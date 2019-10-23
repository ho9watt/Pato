package com.example.pato.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.pato.BoardActivity;
import com.example.pato.BoardModifyActivity;
import com.example.pato.LoginActivity;
import com.example.pato.R;
import com.example.pato.customclass.CharacterWrapTextView;
import com.example.pato.customclass.LoadingDialog;
import com.example.pato.customclass.LongTypeGetTime;
import com.example.pato.customclass.NetworkCheck;
import com.example.pato.model.BoardModel;
import com.example.pato.model.NotificationModel;
import com.example.pato.model.ReportModel;
import com.example.pato.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;



public class BoardReplyRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEAD = 0;
    private static final int TYPE_ITEM = 1;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy.MM.dd");

    private BoardHeadReplyViewHolder boardHeadReplyViewHolder_login;

    private EditText replys_edit;
    private Button replys_btn;
    private Activity activity;
    private DisplayMetrics metrics;

    private List<BoardModel.Reply> boardreplys;
    private List<String> keys;
    private String boardUid;
    public String userUid;
    private Map<String,Boolean> likeUsers = null;

    private UserModel userModel;
    public BoardModel.Board boardModel;
    private FirebaseAuth firebaseAuth;
    public FirebaseUser firebaseUser_board;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;
    private CollectionReference collectionReference_users;

    private String ImagePath = "https://firebasestorage.googleapis.com/v0/b/pato-102e1.appspot.com/o/Board_Image%2F";

    private RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.NONE).error(R.drawable.error_icon).centerCrop().skipMemoryCache(true).priority(Priority.HIGH);

    public BoardReplyRecyclerViewAdapter(final List<BoardModel.Reply> boardreplys, final List<String> keys, String boardUid, final Activity activity, EditText editText, Button button, String userUid) {
        this.activity = activity;
        this.boardreplys = boardreplys;
        this.keys = keys;
        this.boardUid = boardUid;
        this.replys_edit = editText;
        this.replys_btn = button;
        this.userUid = userUid;

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser_board = firebaseAuth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("board");
        collectionReference_users = firebaseFirestore.collection("users");

        metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) activity.getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == TYPE_HEAD){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_board_head, parent, false);
            return new BoardHeadReplyViewHolder(view);
        }else if(viewType == TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_board_replys, parent, false);
            return new BoardReplyViewHolder(view);
        }
        return  null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if(holder instanceof BoardHeadReplyViewHolder) {

            final BoardHeadReplyViewHolder boardHeadReplyViewHolder = (BoardHeadReplyViewHolder)holder;
            boardHeadReplyViewHolder_login = boardHeadReplyViewHolder;

            ((BoardActivity)activity).board_writer = boardModel.uid;

            if(boardModel.bI != null){

                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) boardHeadReplyViewHolder.imageView1.getLayoutParams();
                String imageSize[] = boardModel.bIS.split(",");
                double imageSizeInt[] = {Double.parseDouble(imageSize[0]), Double.parseDouble(imageSize[1]),Double.parseDouble(imageSize[2])};
                double mag = 0.0f;

                if(imageSizeInt[0] > imageSizeInt[1]){
                    mag = imageSizeInt[0] / imageSizeInt[1];
                    mag = Math.round(mag*100)/100.0;

                    params.width = metrics.widthPixels;
                    params.height = (int) Math.round(metrics.widthPixels * mag);
                }else{
                    if(imageSizeInt[2] == 6.0 || imageSizeInt[2] == 8.0){
                        mag = imageSizeInt[1] / imageSizeInt[0];
                        mag = Math.round(mag*100)/100.0;
                    }else{
                        mag = imageSizeInt[0] / imageSizeInt[1];
                        mag = Math.round(mag*100)/100.0;
                    }
                    params.width = metrics.widthPixels;
                    params.height = (int) (metrics.widthPixels * mag);
                }

                boardHeadReplyViewHolder.imageView1.setLayoutParams(params);
                boardHeadReplyViewHolder.imageView1.setVisibility(View.VISIBLE);
                Glide.with(activity).load(ImagePath + boardModel.bI)
                        .apply(options).into(boardHeadReplyViewHolder.imageView1);
            }else{
                boardHeadReplyViewHolder.imageView1.setVisibility(View.GONE);
            }

            if(boardModel.bI2 != null){
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) boardHeadReplyViewHolder.imageView2.getLayoutParams();
                String imageSize[] = boardModel.bIS2.split(",");
                double imageSizeInt[] = {Double.parseDouble(imageSize[0]), Double.parseDouble(imageSize[1]),Double.parseDouble(imageSize[2])};
                double mag = 0.0d;

                if(imageSizeInt[0] > imageSizeInt[1]){
                    mag = imageSizeInt[0] / imageSizeInt[1];
                    mag = Math.round(mag*100)/100.0;

                    params.width = metrics.widthPixels;
                    params.height = (int) Math.round(metrics.widthPixels * mag);
                }else{
                    if(imageSizeInt[2] == 6.0 || imageSizeInt[2] == 8.0){
                        mag = imageSizeInt[1] / imageSizeInt[0];
                        mag = Math.round(mag*100)/100.0;
                    }else{
                        mag = imageSizeInt[0] / imageSizeInt[1];
                        mag = Math.round(mag*100)/100.0;
                    }
                    params.width = metrics.widthPixels;
                    params.height = (int) (metrics.widthPixels * mag);
                }

                boardHeadReplyViewHolder.imageView2.setLayoutParams(params);
                boardHeadReplyViewHolder.imageView2.setVisibility(View.VISIBLE);
                Glide.with(activity).load(ImagePath + boardModel.bI2)
                        .apply(options).into(boardHeadReplyViewHolder.imageView2);
            }else{
                boardHeadReplyViewHolder.imageView2.setVisibility(View.GONE);
            }

            if(boardModel.bI3 != null){
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) boardHeadReplyViewHolder.imageView3.getLayoutParams();
                String imageSize[] = boardModel.bIS3.split(",");
                double imageSizeInt[] = {Double.parseDouble(imageSize[0]), Double.parseDouble(imageSize[1]),Double.parseDouble(imageSize[2])};
                double mag = 0.0d;

                if(imageSizeInt[0] > imageSizeInt[1]){
                    mag = imageSizeInt[0] / imageSizeInt[1];
                    mag = Math.round(mag*100)/100.0;

                    params.width = metrics.widthPixels;
                    params.height = (int) Math.round(metrics.widthPixels * mag);
                }else{
                    if(imageSizeInt[2] == 6.0 || imageSizeInt[2] == 8.0){
                        mag = imageSizeInt[1] / imageSizeInt[0];
                        mag = Math.round(mag*100)/100.0;
                    }else{
                        mag = imageSizeInt[0] / imageSizeInt[1];
                        mag = Math.round(mag*100)/100.0;
                    }
                    params.width = metrics.widthPixels;
                    params.height = (int) (metrics.widthPixels * mag);
                }

                boardHeadReplyViewHolder.imageView3.setLayoutParams(params);
                boardHeadReplyViewHolder.imageView3.setVisibility(View.VISIBLE);
                Glide.with(activity).load(ImagePath + boardModel.bI3)
                        .apply(options).into(boardHeadReplyViewHolder.imageView3);
            }else{
                boardHeadReplyViewHolder.imageView3.setVisibility(View.GONE);
            }

            if(boardModel.bI4 != null){
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) boardHeadReplyViewHolder.imageView4.getLayoutParams();
                String imageSize[] = boardModel.bIS4.split(",");
                double imageSizeInt[] = {Double.parseDouble(imageSize[0]), Double.parseDouble(imageSize[1]),Double.parseDouble(imageSize[2])};
                double mag = 0.0d;

                if(imageSizeInt[0] > imageSizeInt[1]){
                    mag = imageSizeInt[0] / imageSizeInt[1];
                    mag = Math.round(mag*100)/100.0;

                    params.width = metrics.widthPixels;
                    params.height = (int) Math.round(metrics.widthPixels * mag);
                }else{
                    if(imageSizeInt[2] == 6.0 || imageSizeInt[2] == 8.0){
                        mag = imageSizeInt[1] / imageSizeInt[0];
                        mag = Math.round(mag*100)/100.0;
                    }else{
                        mag = imageSizeInt[0] / imageSizeInt[1];
                        mag = Math.round(mag*100)/100.0;
                    }
                    params.width = metrics.widthPixels;
                    params.height = (int) (metrics.widthPixels * mag);
                }

                boardHeadReplyViewHolder.imageView4.setLayoutParams(params);
                boardHeadReplyViewHolder.imageView4.setVisibility(View.VISIBLE);
                Glide.with(activity).load(ImagePath + boardModel.bI4)
                        .apply(options).into(boardHeadReplyViewHolder.imageView4);
            }else{
                boardHeadReplyViewHolder.imageView4.setVisibility(View.GONE);
            }

            if(boardModel.bI5 != null){
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) boardHeadReplyViewHolder.imageView5.getLayoutParams();
                String imageSize[] = boardModel.bIS5.split(",");
                double imageSizeInt[] = {Double.parseDouble(imageSize[0]), Double.parseDouble(imageSize[1]),Double.parseDouble(imageSize[2])};
                double mag ;

                if(imageSizeInt[0] > imageSizeInt[1]){
                    mag = imageSizeInt[0] / imageSizeInt[1];
                    mag = Math.round(mag*100)/100.0;

                    params.width = metrics.widthPixels;
                    params.height = (int) Math.round(metrics.widthPixels * mag);
                }else{
                    if(imageSizeInt[2] == 6.0 || imageSizeInt[2] == 8.0){
                        mag = imageSizeInt[1] / imageSizeInt[0];
                        mag = Math.round(mag*100)/100.0;
                    }else{
                        mag = imageSizeInt[0] / imageSizeInt[1];
                        mag = Math.round(mag*100)/100.0;
                    }
                    params.width = metrics.widthPixels;
                    params.height = (int) (metrics.widthPixels * mag);
                }

                boardHeadReplyViewHolder.imageView5.setLayoutParams(params);
                boardHeadReplyViewHolder.imageView5.setVisibility(View.VISIBLE);
                Glide.with(activity).load(ImagePath + boardModel.bI5)
                        .apply(options).into(boardHeadReplyViewHolder.imageView5);
            }else{
                boardHeadReplyViewHolder.imageView5.setVisibility(View.GONE);
            }

            boardHeadReplyViewHolder.title_text.setText(boardModel.title);
            boardHeadReplyViewHolder.content_text.setText(boardModel.content);
            boardHeadReplyViewHolder.readcount_view.setText("조회수:"+ boardModel.readcount);
            boardHeadReplyViewHolder.nickname_view.setText(boardModel.nickname);
            boardHeadReplyViewHolder.like_btn.setText(String.valueOf(boardModel.likeUsers.size()));

            boardHeadReplyViewHolder.like_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!NetworkCheck.isNetworkCheck(activity)) {
                        Toast.makeText(activity, "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                    }else if(firebaseUser_board == null){
                        Toast.makeText(activity, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(activity, LoginActivity.class);
                        activity.startActivityForResult(intent,8);
                    }else if(!firebaseUser_board.isEmailVerified()) {
                        Toast.makeText(activity, "이메일 인증이 필요한 계정입니다. 'ABOUT PATO' 에서 메일을 전송하시길 바랍니다.", Toast.LENGTH_SHORT).show();
                    }else if(userUid != null){

                        final DocumentReference documentReference = collectionReference.document(boardUid);

                        firebaseFirestore.runTransaction(new Transaction.Function<Void>() {
                            @Nullable
                            @Override
                            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {

                                DocumentSnapshot documentSnapshot = transaction.get(documentReference);
                                likeUsers = (Map<String, Boolean>) documentSnapshot.get("likeUsers");
                                String category = null;

                                if(likeUsers.containsKey(userUid)){
                                    likeUsers.remove(userUid);
                                }else{
                                    likeUsers.put(userUid,true);
                                }
                                if(likeUsers.size() > 10){
                                    category = documentSnapshot.getString("category");
                                    if(!category.equals("fam")){
                                        category = "fam";
                                        transaction.update(documentReference,"category",category);
                                    }
                                }
                                transaction.update(documentReference,"likeUsers",likeUsers);
                                return null;

                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                boardHeadReplyViewHolder.like_btn.setText(String.valueOf(likeUsers.size()));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(activity, "좋아요 실패!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        Intent intent = new Intent(activity,LoginActivity.class);
                        activity.startActivityForResult(intent,8);
                    }
                }
            });

            if(_timestamp(boardModel.timestamp)){
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                long unixTime = boardModel.timestamp;
                Date date = new Date(unixTime);
                boardHeadReplyViewHolder.timestamp_view.setText(simpleDateFormat.format(date));
            }else{
                simpleDateFormat2.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                long unixTime = boardModel.timestamp;
                Date date = new Date(unixTime);
                boardHeadReplyViewHolder.timestamp_view.setText(simpleDateFormat2.format(date));
            }
            loginView();
            boardHeadReplyViewHolder.head_layout.setVisibility(View.VISIBLE);

        }else if(holder instanceof BoardReplyViewHolder){
            final BoardReplyViewHolder boardReplyViewHolder = (BoardReplyViewHolder) holder;
            boardReplyViewHolder.main_linear.setVisibility(View.VISIBLE);
            boardReplyViewHolder.content_view.setText(boardreplys.get(position-1).content);
            boardReplyViewHolder.nickname_view.setText(boardreplys.get(position-1).nickname);

            if(_timestamp(boardreplys.get(position-1).timestamp)){
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                long unixTime = boardreplys.get(position-1).timestamp;
                Date date = new Date(unixTime);
                boardReplyViewHolder.timestamp_view.setText(simpleDateFormat.format(date));
            }else{
                simpleDateFormat2.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                long unixTime = boardreplys.get(position-1).timestamp;
                Date date = new Date(unixTime);
                boardReplyViewHolder.timestamp_view.setText(simpleDateFormat2.format(date));
            }

            if(boardreplys.get(position-1).rrp) {
                boardReplyViewHolder.linearLayout_imageview.setVisibility(View.VISIBLE);
                if(boardreplys.get(position-1).opname != null){
                    boardReplyViewHolder.opponickname.setVisibility(View.VISIBLE);
                    boardReplyViewHolder.opponickname.setText((boardreplys.get(position-1).opname));
                }else{
                    boardReplyViewHolder.opponickname.setVisibility(View.GONE);
                }
            }else{
                boardReplyViewHolder.opponickname.setVisibility(View.GONE);
                boardReplyViewHolder.linearLayout_imageview.setVisibility(View.GONE);
            }

            if(boardreplys.get(position-1).removereplys){
                if(boardreplys.get(position-1).rpEmpty){
                    boardReplyViewHolder.replys_linear.setVisibility(View.GONE);
                    boardReplyViewHolder.replys_linear2.setVisibility(View.VISIBLE);
                }
            }else{
                boardReplyViewHolder.replys_linear.setVisibility(View.VISIBLE);
                boardReplyViewHolder.replys_linear2.setVisibility(View.GONE);
            }

            boardReplyViewHolder.moreMenu_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MoreMenu(keys.get(position-1),position,boardreplys.get(position-1));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return boardreplys.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            return TYPE_HEAD;
        }
        return TYPE_ITEM ;
    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

    private void MoreMenu(final String replysUid, final int position, final BoardModel.Reply replys) {

        final Button remove_btn;
        Button report_btn;
        final Button dialog_replys_btn;

        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_more_menu,null);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final AlertDialog Optiondialog = builder.create();
        Optiondialog.setView(view);
        Optiondialog.setCancelable(true);
        Optiondialog.show();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int displayWidth = displayMetrics.widthPixels;

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(Optiondialog.getWindow().getAttributes());
        layoutParams.width = (int) (displayWidth * 0.5f);
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

        Window window = Optiondialog.getWindow();
        window.setAttributes(layoutParams);

        remove_btn = view.findViewById(R.id.dialog_moreMenu_remove);
        report_btn = view.findViewById(R.id.dialog_moreMenu_report);
        dialog_replys_btn = view.findViewById(R.id.dialog_moreMenu_replys);

        final FirebaseUser firebaseUser;
        String current_uid = "";

        firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            current_uid = firebaseUser.getUid();
        }

        if(!current_uid.equals(replys.uid)){
            remove_btn.setVisibility(View.GONE);
        }

        dialog_replys_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user == null) {
                    Toast.makeText(activity, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activity, LoginActivity.class);
                    activity.startActivityForResult(intent,8);
                } else {

                    int stringSize ;
                    ColorGenerator colorGenerator = ColorGenerator.MATERIAL;

                    Drawable d = TextDrawable.builder().buildRoundRect(boardreplys.get(position-1).nickname,colorGenerator.getRandomColor(),10);
                    stringSize = checkWordCount(boardreplys.get(position-1).nickname) * 18 + 30;
                    d.setBounds(0,22,stringSize,80);
                    replys_edit.setText("  ");

                    ImageSpan imageSpan = new ImageSpan(d,ImageSpan.ALIGN_BASELINE);
                    replys_edit.getText().setSpan(imageSpan,0,1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    replys_edit.requestFocus();
                    replys_edit.setSelection(2);

                }

                replys_btn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(final View view) {

                        final String replys_1 = replys_edit.getText().toString().trim();
                        FirebaseUser firebaseUser_1 = firebaseAuth.getInstance().getCurrentUser();
                        userUid = firebaseUser_1.getUid();

                        replys_btn.setEnabled(false);

                        if(!NetworkCheck.isNetworkCheck(activity)) {
                            Toast.makeText(activity, "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                        }else if(!firebaseUser_1.isEmailVerified()) {
                            Toast.makeText(activity, "이메일 인증이 필요한 계정입니다. 'ABOUT PATO' 에서 메일을 전송하시길 바랍니다.", Toast.LENGTH_SHORT).show();
                        }else if(replys_edit.getText().toString().trim().length() > 300){
                            Toast.makeText(activity, "300자 이하로 작성해주세요.", Toast.LENGTH_SHORT).show();
                        }else if(!TextUtils.isEmpty(replys_edit.getText().toString().trim())) {
                            final AlertDialog alertDialog = LoadingDialog.loading_Diaglog(activity);

                            if (hasImageSpan(replys_edit)) {

                                final BoardModel.Reply boardreplys = new BoardModel.Reply();

                                boardreplys.content = replys_1;
                                boardreplys.timestamp = LongTypeGetTime.getTime();
                                boardreplys.uid = userUid;
                                boardreplys.nickname = firebaseAuth.getCurrentUser().getDisplayName();
                                boardreplys.rrp = true;

                                if(replys.rrp){
                                    boardreplys.opname = replys.nickname;
                                    boardreplys.part = replys.part;
                                    boardreplys.pId = replys.pId;
                                }else{
                                    boardreplys.pId = replysUid;
                                    boardreplys.part = replys.timestamp;
                                }

                                collectionReference.document(boardUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            if(documentSnapshot.exists()){

                                                collectionReference.document(boardUid).collection("replys").add(boardreplys).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        if(task.isSuccessful()){

                                                            firebaseFirestore.runTransaction(new com.google.firebase.firestore.Transaction.Function<Void>() {
                                                                @Nullable
                                                                @Override
                                                                public Void apply(@NonNull com.google.firebase.firestore.Transaction transaction) throws FirebaseFirestoreException {
                                                                    DocumentReference documentReference = collectionReference.document(boardUid);
                                                                    DocumentSnapshot documentSnapshot = transaction.get(documentReference);

                                                                    DocumentReference documentReference1 = documentReference.collection("replys").document(replysUid);
                                                                    DocumentSnapshot documentSnapshot1 = transaction.get(documentReference1);

                                                                    boolean rpEmpty = documentSnapshot1.getBoolean("rpEmpty");
                                                                    double replyscount = documentSnapshot.getDouble("replyscount") + 1;

                                                                    if(!rpEmpty && !replys.rrp){
                                                                        rpEmpty = true;
                                                                    }

                                                                    if(documentSnapshot1.exists()){
                                                                        transaction.update(documentReference1,"rpEmpty",rpEmpty);
                                                                    }else{

                                                                    }
                                                                    transaction.update(documentReference,"replyscount",replyscount);

                                                                    return null;
                                                                }
                                                            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if(task.isSuccessful()){
                                                                        boardAlarm(boardModel.uid);
                                                                        replyAlarm(keys.get(position-1));

                                                                        replys_edit.setText("");
                                                                        replys_edit.clearFocus();

                                                                        ((BoardActivity)activity).reGetData = true;
                                                                        ((BoardActivity)activity).getData();

                                                                        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                                                        imm.hideSoftInputFromWindow(replys_edit.getWindowToken(), 0);

                                                                        alertDialog.dismiss();
                                                                        replys_btn.setEnabled(true);
                                                                    }else{

                                                                        replys_btn.setEnabled(true);
                                                                        alertDialog.dismiss();
                                                                        if(task.getException().toString().equals("java.lang.NullPointerException")){
                                                                            activity.finish();
                                                                            Toast.makeText(activity, "삭제된 게시물 입니다.", Toast.LENGTH_SHORT).show();
                                                                        }else{
                                                                            Toast.makeText(activity, "댓글 작성 실패", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                }
                                                            });
                                                        }else{
                                                            alertDialog.dismiss();
                                                            replys_btn.setEnabled(true);
                                                            Toast.makeText(activity, "댓글 작성 실패", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }else{
                                                alertDialog.dismiss();
                                                replys_btn.setEnabled(true);
                                                activity.finish();
                                                Toast.makeText(activity, "삭제된 게시물 입니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        }else{
                                            alertDialog.dismiss();
                                            replys_btn.setEnabled(true);
                                        }
                                    }
                                });
                            } else {
                                collectionReference.document(boardUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            DocumentSnapshot documentSnapshot = task.getResult();
                                            if(documentSnapshot.exists()){
                                                BoardModel.Reply boardreply = new BoardModel.Reply();

                                                boardreply.uid = userUid;
                                                boardreply.content = replys_1;
                                                boardreply.timestamp = LongTypeGetTime.getTime();
                                                boardreply.nickname = firebaseAuth.getCurrentUser().getDisplayName();
                                                boardreply.removereplys = false;
                                                boardreply.part = boardreply.timestamp;
                                                boardreply.rpEmpty = false;

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

                                                                        boardAlarm(boardModel.uid);

                                                                        replys_edit.setText("");
                                                                        replys_edit.clearFocus();
                                                                        ((BoardActivity)activity).reGetData = true;
                                                                        ((BoardActivity)activity).getData();

                                                                        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                                                        imm.hideSoftInputFromWindow(replys_edit.getWindowToken(), 0);
                                                                        alertDialog.dismiss();
                                                                        replys_btn.setEnabled(true);
                                                                    }else{

                                                                    }
                                                                }
                                                            });
                                                        }else{
                                                            alertDialog.dismiss();
                                                            replys_btn.setEnabled(true);
                                                            Toast.makeText(activity, "댓글 작성 실패", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                            }else{
                                                alertDialog.dismiss();
                                                replys_btn.setEnabled(true);
                                                activity.finish();
                                                Toast.makeText(activity, "삭제된 게시물 입니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        }else{
                                            alertDialog.dismiss();
                                            replys_btn.setEnabled(true);
                                        }
                                    }
                                });
                            }
                        }else{
                            Toast.makeText(activity, "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                            replys_edit.requestFocus();
                        }
                    }
                });
                Optiondialog.dismiss();
            }
        });

        remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remove_replys_Diaglog(replysUid, replys);
                Optiondialog.dismiss();
            }
        });

        report_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firebaseAuth.getCurrentUser() != null){
                    ReportReason(replysUid);
                }else{
                    Toast.makeText(activity, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activity, LoginActivity.class);
                    activity.startActivityForResult(intent,8);
                }
                Optiondialog.dismiss();
            }
        });
    }

    private void remove_replys_Diaglog(final String replyUid, final BoardModel.Reply replys){

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("댓글을 삭제 하시겠습니까?").setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int k) {
                dialogInterface.dismiss();
                final AlertDialog alertDialog = LoadingDialog.loading_Diaglog(activity);

                if(!NetworkCheck.isNetworkCheck(activity)) {
                    Toast.makeText(activity, "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }else{
                    if(replys.rrp){
                        collectionReference.document(boardUid).collection("replys").document(replyUid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    collectionReference.document(boardUid).collection("replys").whereEqualTo("pId",replys.pId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                QuerySnapshot querySnapshot = task.getResult();
                                                if(!querySnapshot.isEmpty()){

                                                }else{
                                                    collectionReference.document(boardUid).collection("replys").document(replys.pId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                            if(task.isSuccessful()){
                                                                DocumentSnapshot documentSnapshot = task.getResult();
                                                                if(documentSnapshot.exists()){
                                                                    BoardModel.Reply reply = documentSnapshot.toObject(BoardModel.Reply.class);
                                                                    if(reply.removereplys){
                                                                        collectionReference.document(boardUid).collection("replys").document(replys.pId).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                            }
                                                                        });
                                                                    }else{
                                                                        reply.rpEmpty = false;
                                                                        collectionReference.document(boardUid).collection("replys").document(replys.pId).set(reply).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                            }
                                                                        });
                                                                    }
                                                                }else{
                                                                }
                                                            }else{
                                                            }
                                                            alertDialog.dismiss();
                                                        }
                                                    });
                                                }
                                            }else{

                                            }
                                            alertDialog.dismiss();
                                        }
                                    });
                                    firebaseFirestore.runTransaction(new com.google.firebase.firestore.Transaction.Function<Void>() {
                                        @Nullable
                                        @Override
                                        public Void apply(@NonNull com.google.firebase.firestore.Transaction transaction) throws FirebaseFirestoreException {

                                            DocumentReference documentReference = collectionReference.document(boardUid);
                                            DocumentSnapshot documentSnapshot = transaction.get(documentReference);

                                            double replyscount = documentSnapshot.getDouble("replyscount") - 1;

                                            transaction.update(documentReference,"replyscount",replyscount);

                                            return null;
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){

                                            }else{

                                            }
                                            Toast.makeText(activity, "댓글을 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                                            ((BoardActivity)activity).reGetData = true;
                                            ((BoardActivity)activity).getData();
                                            alertDialog.dismiss();
                                        }
                                    });

                                }else{
                                    Toast.makeText(activity, "댓글 삭제 실패!", Toast.LENGTH_SHORT).show();
                                    alertDialog.dismiss();
                                }
                                alertDialog.dismiss();
                            }
                        });
                    }else{
                        if(replys.rpEmpty){
                            replys.removereplys = true;
                            collectionReference.document(boardUid).collection("replys").document(replyUid).set(replys).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        firebaseFirestore.runTransaction(new com.google.firebase.firestore.Transaction.Function<Void>() {
                                            @Nullable
                                            @Override
                                            public Void apply(@NonNull com.google.firebase.firestore.Transaction transaction) throws FirebaseFirestoreException {

                                                DocumentReference documentReference = collectionReference.document(boardUid);
                                                DocumentSnapshot documentSnapshot = transaction.get(documentReference);
                                                double replyscount = documentSnapshot.getDouble("replyscount") - 1;

                                                transaction.update(documentReference,"replyscount",replyscount);

                                                return null;
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){

                                                }else{

                                                }
                                                ((BoardActivity)activity).reGetData = true;
                                                ((BoardActivity)activity).getData();
                                                Toast.makeText(activity, "댓글을 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                                                alertDialog.dismiss();
                                            }
                                        });
                                    }else{
                                        Toast.makeText(activity, "댓글 삭제 실패!", Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();
                                    }
                                }
                            });
                        }else{
                            collectionReference.document(boardUid).collection("replys").document(replyUid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        firebaseFirestore.runTransaction(new com.google.firebase.firestore.Transaction.Function<Void>() {
                                            @Nullable
                                            @Override
                                            public Void apply(@NonNull com.google.firebase.firestore.Transaction transaction) throws FirebaseFirestoreException {

                                                DocumentReference documentReference = collectionReference.document(boardUid);
                                                DocumentSnapshot documentSnapshot = transaction.get(documentReference);

                                                double replyscount = documentSnapshot.getDouble("replyscount") - 1;

                                                transaction.update(documentReference,"replyscount",replyscount);

                                                return null;
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){

                                                }else{

                                                }
                                                ((BoardActivity)activity).reGetData = true;
                                                ((BoardActivity)activity).getData();
                                                Toast.makeText(activity, "댓글을 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                                                alertDialog.dismiss();
                                            }
                                        });
                                    }else{
                                        Toast.makeText(activity, "댓글 삭제 실패!", Toast.LENGTH_SHORT).show();
                                        alertDialog.dismiss();
                                    }
                                }
                            });
                        }
                    }
                }
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();
    }


    private class BoardReplyViewHolder extends RecyclerView.ViewHolder {

        private CharacterWrapTextView content_view;
        private TextView nickname_view;
        private TextView timestamp_view;
        private TextView opponickname;

        private ImageButton moreMenu_btn;
        private LinearLayout replys_linear, replys_linear2,  linearLayout_imageview, main_linear;

        public BoardReplyViewHolder(View view) {
            super(view);

            content_view =  view.findViewById(R.id.boardActivity_replys_content);
            nickname_view = view.findViewById(R.id.boardActivity_replys_nickname);
            timestamp_view = view.findViewById(R.id.boardActivity_replys_timestamp);
            moreMenu_btn =  view.findViewById(R.id.boardActivity_moreMenu_btn);

            opponickname = view.findViewById(R.id.boardActivity_replys_opponickname);

            replys_linear2 = view.findViewById(R.id.BoardActivity_replys_linear2);
            replys_linear = view.findViewById(R.id.BoardActivity_replys_linear);
            linearLayout_imageview = view.findViewById(R.id.boardActivity_replys_iV);
            main_linear = view.findViewById(R.id.BoardActivity_replys_main_linear);
        }
    }

    private class BoardHeadReplyViewHolder extends RecyclerView.ViewHolder {

        private Button modify_btn, remove_btn, like_btn, report_btn;
        private CharacterWrapTextView content_text,title_text;
        private TextView   timestamp_view, readcount_view, nickname_view;
        public LinearLayout linearLayout,head_layout;
        private ImageView imageView1,imageView2,imageView3,imageView4,imageView5;

        public BoardHeadReplyViewHolder(View view) {
            super(view);

            title_text = view.findViewById(R.id.boardActivity_title);
            content_text = view.findViewById(R.id.boardActivity_content);
            timestamp_view = view.findViewById(R.id.boardActivity_timestamp);
            readcount_view = view.findViewById(R.id.boardActivity_readCount);
            nickname_view = view.findViewById(R.id.boardActivity_nickname);

            modify_btn = view.findViewById(R.id.boardActivity_modify_btn);
            remove_btn = view.findViewById(R.id.boardActivity_remove_btn);
            like_btn = view.findViewById(R.id.boardActivity_like_btn);
            report_btn = view.findViewById(R.id.boardActivity_report_btn);

            linearLayout = view.findViewById(R.id.boardActivity_btn_layout);
            head_layout = view.findViewById(R.id.boardActivity_head_layout);

            imageView1 = view.findViewById(R.id.boardActivity_image1);
            imageView2 = view.findViewById(R.id.boardActivity_image2);
            imageView3 =  view.findViewById(R.id.boardActivity_image3);
            imageView4 = view.findViewById(R.id.boardActivity_image4);
            imageView5 = view.findViewById(R.id.boardActivity_image5);

            modify_btn.setPaintFlags(modify_btn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            remove_btn.setPaintFlags(remove_btn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            report_btn.setPaintFlags(report_btn.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

            modify_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    modifyDiaglog();
                }
            });
            remove_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    removeDiaglog();
                }
            });
            report_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(firebaseAuth.getCurrentUser() != null){
                        ReportReason("");

                    }else{
                        Toast.makeText(activity, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(activity, LoginActivity.class);
                        activity.startActivityForResult(intent,8);
                    }

                }
            });
        }
    }

    private void removeDiaglog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("게시물을 삭제 하시겠습니까?").setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int k) {
                if(!NetworkCheck.isNetworkCheck(activity)) {
                    Toast.makeText(activity, "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    final AlertDialog alertDialog = LoadingDialog.loading_Diaglog(activity);

                    collectionReference.document(boardUid).collection("replys").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            QuerySnapshot queryDocumentSnapshots = task.getResult();

                            for(QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots){
                                queryDocumentSnapshot.getReference().delete();
                            }
                        }
                    });

                    if(boardModel.bI != null){
                        storageReference.child("Board_Image").child(boardModel.bI.substring(0,22)).delete().addOnSuccessListener(activity, new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                if(boardModel.bI2 != null){
                                    storageReference.child("Board_Image").child(boardModel.bI2.substring(0,22)).delete().addOnSuccessListener(activity, new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            if(boardModel.bI3 != null){
                                                storageReference.child("Board_Image").child(boardModel.bI3.substring(0,22)).delete().addOnSuccessListener(activity, new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        if(boardModel.bI4 != null){
                                                            storageReference.child("Board_Image").child(boardModel.bI4.substring(0,22)).delete().addOnSuccessListener(activity, new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {

                                                                    if(boardModel.bI5 != null){
                                                                        storageReference.child("Board_Image").child(boardModel.bI5.substring(0,22)).delete().addOnSuccessListener(activity, new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                boardDelete(dialogInterface,alertDialog);

                                                                            }
                                                                        }).addOnFailureListener(activity, new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                alertDialog.dismiss();
                                                                                Toast.makeText(activity, "게시물 삭제 실패", Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                                    }else{
                                                                        boardDelete(dialogInterface,alertDialog);
                                                                    }
                                                                }
                                                            }).addOnFailureListener(activity, new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    alertDialog.dismiss();
                                                                    Toast.makeText(activity, "게시물 삭제 실패", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                        }else{
                                                            boardDelete(dialogInterface,alertDialog);
                                                        }
                                                    }
                                                }).addOnFailureListener(activity, new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        alertDialog.dismiss();
                                                        Toast.makeText(activity, "게시물을 삭제 실패", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }else{
                                                boardDelete(dialogInterface,alertDialog);
                                            }
                                        }
                                    }).addOnFailureListener(activity, new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            alertDialog.dismiss();
                                            Toast.makeText(activity, "게시물을 삭제 실패", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }else{
                                    boardDelete(dialogInterface,alertDialog);
                                }
                            }
                        }).addOnFailureListener(activity, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                alertDialog.dismiss();
                                Toast.makeText(activity, "게시물 삭제 실패", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else{
                        boardDelete(dialogInterface,alertDialog);
                    }
                }
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();
    }

    private void boardDelete(final DialogInterface dialogInterface, final AlertDialog alertDialog) {

        collectionReference.document(boardUid).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    dialogInterface.dismiss();
                    Intent intent = new Intent();
                    intent.putExtra("onresume","onresume");
                    activity.setResult(Activity.RESULT_OK,intent);
                    alertDialog.dismiss();
                    activity.finish();
                    Toast.makeText(activity, "게시물을 삭제하였습니다.", Toast.LENGTH_SHORT).show();
                }else{
                    alertDialog.dismiss();
                    Toast.makeText(activity, "게시물 삭제 실패", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void modifyDiaglog(){

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("게시물을 수정 하시겠습니까?").setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int k) {
                dialogInterface.dismiss();

                if(!NetworkCheck.isNetworkCheck(activity)) {
                    Toast.makeText(activity, "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(activity, BoardModifyActivity.class);
                    intent.putExtra("bid",boardUid);
                    activity.startActivityForResult(intent,2);
                }
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();

    }

    private boolean hasImageSpan(EditText editText) {
        Editable text  = editText.getEditableText();
        ImageSpan[] spans = text.getSpans(0, text.length(), ImageSpan.class);
        return !(spans.length == 0);
    }

    private static int checkWordCount(String word){ //영어 한글 글자숫 체크
        if(word.length() < 0){
            return 0;
        }
        int length = word.length();
        int charLength = 0;
        for (int i = 0; i < length; i++) {
            charLength += word.codePointAt(i) > 0x00ff ? 2 : 1;
        }
        return charLength;
    }

    private boolean _timestamp(Object timestamp){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");

        long write_time = (long)timestamp;
        long current_time = System.currentTimeMillis();
        Date date = new Date(write_time);
        Date date2 = new Date(current_time);

        String write_time_st = simpleDateFormat.format(date);
        String current_time_st = simpleDateFormat.format(date2);

        if(write_time_st.equals(current_time_st)){
            return true;
        }else{
            return false;
        }
    }

    private void boardAlarm(final String boardUid8){

        collectionReference_users.document(boardUid8).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        String uid = documentSnapshot.getString("uid");
                        if(!uid.equals(userUid)){
                            if(documentSnapshot.getString("pushToken") != null){
                                sendGcm(boardUid,documentSnapshot.getString("pushToken"));
                            }
                        }
                    }
                }
            }
        });
    }

    private void replyAlarm(String replys_writer){

        collectionReference_users.document(replys_writer).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        String uid = documentSnapshot.getString("uid");
                        if(!uid.equals(userUid)){
                            if(documentSnapshot.getString("pushToken") != null){
                                sendGcm(boardUid,documentSnapshot.getString("pushToken"));
                            }
                        }
                    }
                }
            }
        });
    }


    private void sendGcm(String boardUid, String pushToken){
        Gson gson = new Gson();

        String userName = firebaseAuth.getCurrentUser().getDisplayName();
        NotificationModel notificationModel = new NotificationModel();
        notificationModel.to = pushToken;
        notificationModel.data.title = userName;
        notificationModel.data.gcmUid = boardUid;

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"),gson.toJson(notificationModel));

        Request request = new Request.Builder()
                .header("Content-Type","application/json")
                .addHeader("Authorization","key=AIzaSyDWwYEksJpRjkvIw03CIpwiKdPnjp3UGBU")
                .url("https://gcm-http.googleapis.com/gcm/send")
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            }
        });
    }

    private void Report(String replysUid, String reason, final AlertDialog Optiondialog, final Button report_btn){
        ReportModel reportModel = new ReportModel();

        TimeZone tz;
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        tz = TimeZone.getTimeZone("Asia/Seoul");
        df.setTimeZone(tz);

        reportModel.uid = userUid;
        reportModel.bid = boardUid;
        reportModel.time = df.format(date);
        reportModel.rs = reason;

        if(replysUid != null || !replysUid.equals("") || replysUid.length() > 0){
            reportModel.repU = replysUid;
        }

        databaseReference.child("report").push().setValue(reportModel).addOnCompleteListener(activity, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(activity, "신고가 성공적으로 접수 되었습니다.", Toast.LENGTH_SHORT).show();
                    Optiondialog.dismiss();
                }else{
                    Toast.makeText(activity, "신고 실패", Toast.LENGTH_SHORT).show();
                    Optiondialog.dismiss();
                }
                report_btn.setEnabled(true);
            }
        });
    }


    private void ReportReason(final String replysUid){

        RadioGroup radioGroup;
        RadioButton rb1, rb2, rb3;
        final Button report_btn;

        final String[] reason = {""};

        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_report_radiobutton_xml,null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final AlertDialog Optiondialog = builder.create();

        Optiondialog.setView(view);
        Optiondialog.setCancelable(true);
        Optiondialog.show();

        radioGroup = view.findViewById(R.id.dialog_report_radioGroup);
        rb1 =  view.findViewById(R.id.dialog_report_rb1);
        rb2 =  view.findViewById(R.id.dialog_report_rb2);
        rb3 =  view.findViewById(R.id.dialog_report_rb3);
        report_btn = view.findViewById(R.id.dialog_report_button);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.dialog_report_rb1:
                        reason[0] = "음란물";
                        break;
                    case R.id.dialog_report_rb2:
                        reason[0] = "비하";
                        break;
                    case R.id.dialog_report_rb3:
                        reason[0] = "홍보";
                        break;
                }
            }
        });

        report_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(reason[0].equals("")){
                    Toast.makeText(activity, "신고 사유를 선택해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }else{
                    if(!NetworkCheck.isNetworkCheck(activity)) {
                        Toast.makeText(activity, "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                    }else{
                        report_btn.setEnabled(false);
                        Report(replysUid,reason[0],Optiondialog,report_btn);
                    }
                }
            }
        });
    }


    public void loginView(){
        if(boardModel.uid.equals(userUid)){
            boardHeadReplyViewHolder_login.linearLayout.setVisibility(View.VISIBLE);
        }
    }
}
