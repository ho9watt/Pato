package com.example.pato;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pato.adapter.BoardReplyRecyclerViewAdapter;
import com.example.pato.customclass.LoadingDialog;
import com.example.pato.customclass.LongTypeGetTime;
import com.example.pato.customclass.NetworkCheck;
import com.example.pato.model.BoardModel;
import com.example.pato.model.NotificationModel;
import com.example.pato.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BoardActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;
    private CollectionReference collectionReference_user;

    private BoardReplyRecyclerViewAdapter boardReplyRecyclerViewAdapter;

    private RecyclerView board_replys_recyclerView;
    private BoardModel.Board boardModel;

    private Button replys_btn;
    private EditText replys_edit;

    private String boardUid;
    private String userUid;
    private String onResume = "";
    public String board_writer;
    public boolean reGetData = false;
    private boolean modify_check = true;

    private List<BoardModel.Reply> boardreplys = new ArrayList<>();
    private List<String> keys = new ArrayList<>();

    private UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("board");
        collectionReference_user = firebaseFirestore.collection("users");

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            userUid = firebaseUser.getUid();
        }

        boardUid = getIntent().getStringExtra("bid");

        board_replys_recyclerView = findViewById(R.id.boardActivity_replys);
        replys_btn = findViewById(R.id.boardActivity_replys_btn);
        replys_edit = findViewById(R.id.boardNoteActivity_replys_edit);

        replys_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();

                if(!NetworkCheck.isNetworkCheck(getApplicationContext())){
                    Toast.makeText(BoardActivity.this, "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                }else if (user == null) {
                    Toast.makeText(getApplicationContext(), "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(BoardActivity.this, LoginActivity.class);
                    startActivityForResult(intent,8);
                }else if(!user.isEmailVerified()){
                    Toast.makeText(getApplicationContext(), "이메일 인증이 필요한 계정입니다. 'ABOUT PATO' 에서 메일을 전송하시길 바랍니다.", Toast.LENGTH_SHORT).show();
                }else if(replys_edit.getText().toString().trim().length() > 300){
                    Toast.makeText(BoardActivity.this, "300자 이하로 작성해주세요.", Toast.LENGTH_SHORT).show();
                }else{

                    replys_btn.setEnabled(false);

                    userUid = user.getUid();

                    if(!TextUtils.isEmpty(replys_edit.getText().toString().trim())){
                        final AlertDialog alertDialog = LoadingDialog.loading_Diaglog(BoardActivity.this);

                        final BoardModel.Reply boardreply = new BoardModel.Reply();

                        boardreply.uid = userUid;
                        boardreply.content = replys_edit.getText().toString().trim();
                        boardreply.timestamp = LongTypeGetTime.getTime();
                        boardreply.nickname = firebaseAuth.getCurrentUser().getDisplayName();
                        boardreply.removereplys = false;
                        boardreply.part = boardreply.timestamp;
                        boardreply.rpEmpty = false;

                        collectionReference.document(boardUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful()){
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if(documentSnapshot.exists()){

                                        collectionReference.document(boardUid).collection("replys").add(boardreply).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                if(task.isSuccessful()){
                                                    firebaseFirestore.runTransaction(new com.google.firebase.firestore.Transaction.Function<Void>() {
                                                        @Nullable
                                                        @Override
                                                        public Void apply(@NonNull com.google.firebase.firestore.Transaction transaction) throws FirebaseFirestoreException {
                                                            DocumentReference documentReference = collectionReference.document(boardUid);
                                                            DocumentSnapshot documentSnapshot = transaction.get(documentReference);

                                                            double replyscount = documentSnapshot.getDouble("replyscount") + 1;

                                                            transaction.update(documentReference,"replyscount",replyscount);

                                                            return null;
                                                        }
                                                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                userCheck(boardUid,board_writer);
                                                                replys_edit.clearFocus();
                                                                replys_edit.setText("");

                                                                reGetData = true;
                                                                getData();

                                                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                                                imm.hideSoftInputFromWindow(replys_edit.getWindowToken(), 0);
                                                                alertDialog.dismiss();
                                                            }else{
                                                                alertDialog.dismiss();
                                                            }
                                                            replys_btn.setEnabled(true);
                                                        }
                                                    });
                                                }else{
                                                    replys_btn.setEnabled(true);
                                                    alertDialog.dismiss();
                                                    Toast.makeText(BoardActivity.this, "댓글 작성 실패", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }else{
                                        alertDialog.dismiss();
                                        replys_btn.setEnabled(true);
                                        BoardActivity.this.finish();
                                        Toast.makeText(BoardActivity.this, "삭제된 게시물 입니다.", Toast.LENGTH_SHORT).show();
                                    }
                                }else{
                                    alertDialog.dismiss();
                                    Toast.makeText(BoardActivity.this, "댓글 작성 실패", Toast.LENGTH_SHORT).show();
                                    replys_btn.setEnabled(true);
                                }

                            }
                        });
                    }else{
                        Toast.makeText(BoardActivity.this, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        replys_edit.requestFocus();
                        replys_btn.setEnabled(true);
                    }
                }
            }
        });
        getData_board();
        getSupportActionBar().setTitle("글 보기");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                onResume = data.getStringExtra("onresume");
            }
        }else if(requestCode == 8){
            if (resultCode == Activity.RESULT_OK){
                onResume = "logins";
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(onResume.equals("onresume")){

            modify_check = false;
            getData_board();

            Intent intent = new Intent();
            intent.putExtra("onresume","onresume");
            setResult(Activity.RESULT_OK,intent);

            onResume = "";
        }else if(onResume.equals("delete")){
            BoardActivity.this.finish();

        }else if(onResume.equals("logins")){
            if(firebaseAuth.getInstance().getCurrentUser() != null){

                firebaseUser = firebaseAuth.getInstance().getCurrentUser();
                boardReplyRecyclerViewAdapter.userUid = firebaseUser.getUid();
                boardReplyRecyclerViewAdapter.loginView();
                boardReplyRecyclerViewAdapter.firebaseUser_board = firebaseUser;
            }

            onResume = "";
        }
    }

    private void userCheck(final String boardUid,  final String board_useruid){

        collectionReference_user.document(board_useruid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        String uid = documentSnapshot.getString("uid");
                        String pushToken = documentSnapshot.getString("pushToken");
                        if(uid.equals(userUid)){
                        }else{
                            sendGcm(boardUid,pushToken);
                        }
                    }else{

                    }
                }else{

                }
            }
        });
    }

    private void sendGcm(String boardUid, String pushToken){
        Gson gson = new Gson();

        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.to = pushToken;
        notificationModel.data.title = userName;
        notificationModel.data.gcmUid = boardUid;

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"),gson.toJson(notificationModel));

        Request request = new Request.Builder()
                .header("Content-Type","application/json")
                .addHeader("Authorization","key=AIzaSyDWwYEksJpRjkvIw03CIpwiKdPnjp3UGBU")
                .url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .build();

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) {
            }
        });

    }


    public void getData(){

        collectionReference.document(boardUid).collection("replys").orderBy("part").orderBy("timestamp").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot querySnapshot = task.getResult();

                    boardreplys.clear();
                    keys.clear();

                    if(!querySnapshot.isEmpty()){
                        for(QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot){
                            BoardModel.Reply boardreply = queryDocumentSnapshot.toObject(BoardModel.Reply.class);
                            boardreplys.add(boardreply);
                            keys.add(queryDocumentSnapshot.getId());
                        }
                    }

                    if(reGetData){
                        boardReplyRecyclerViewAdapter.notifyDataSetChanged();
                        reGetData = false;
                    }else{
                        boardReplyRecyclerViewAdapter = new BoardReplyRecyclerViewAdapter(boardreplys,keys,boardUid,BoardActivity.this,replys_edit,replys_btn,userUid);
                        board_replys_recyclerView.setAdapter(boardReplyRecyclerViewAdapter);
                        board_replys_recyclerView.setLayoutManager(new LinearLayoutManager(BoardActivity.this));
                        boardReplyRecyclerViewAdapter.boardModel = boardModel;
                    }
                }else{
                    Toast.makeText(BoardActivity.this, "댓글을 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void getData_board(){

        collectionReference.document(boardUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        boardModel = documentSnapshot.toObject(BoardModel.Board.class);
                        if(boardReplyRecyclerViewAdapter != null){
                            boardReplyRecyclerViewAdapter.boardModel = boardModel;
                            boardReplyRecyclerViewAdapter.notifyItemChanged(0);
                        }
                        if(modify_check){
                            getData();
                        }else{
                            modify_check = true;
                        }

                    }else{
                        delete_page_dialog();
                    }
                }else{
                    Toast.makeText(BoardActivity.this, "데이터를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void delete_page_dialog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(BoardActivity.this);
        builder.setCancelable(false).setMessage("삭제된 페이지 입니다.").setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                BoardActivity.this.finish();
                dialogInterface.dismiss();
            }
        }).create().show();

    }

}
