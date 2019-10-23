package com.example.pato;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pato.adapter.PatchNoteRecyclerViewAdapter;
import com.example.pato.customclass.LoadingDialog;
import com.example.pato.customclass.NetworkCheck;
import com.example.pato.model.ImagesModel;
import com.example.pato.model.PatchNoteModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PatchNoteActivity extends AppCompatActivity  {

    private RecyclerView patchNote_recyclerView;
    private EditText patchNote_replys_edit;
    private Button patchNote_replys_btn;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    public LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PatchNoteActivity.this);

    private String userUid;
    private String version;
    private String title;
    private String year;
    private List<PatchNoteModel.Contents> contents = new ArrayList<>();
    private List<PatchNoteModel.Replys> replys = new ArrayList<>();
    private List<String> keys = new ArrayList<>();
    public PatchNoteRecyclerViewAdapter patchNoteRecyclerViewAdapter;
    private String[][] championimage;
    private String onResume = "";
    public boolean reGetdata = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patch_note);

        if(!NetworkCheck.isNetworkCheck(getApplicationContext())){
            Toast.makeText(PatchNoteActivity.this, "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
            PatchNoteActivity.this.finish();
        }
        firebaseAuth = FirebaseAuth.getInstance();

        firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            userUid = firebaseUser.getUid();
        }
        version = getIntent().getStringExtra("version");
        title = getIntent().getStringExtra("title");
        year = getIntent().getStringExtra("year");

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        patchNote_replys_edit = findViewById(R.id.patchNoteActivity_replys_edit);
        patchNote_replys_btn = findViewById(R.id.patchnoteActivity_replys_btn);
        patchNote_recyclerView = findViewById(R.id.patchNoteActivity_recyclerview);

        patchNote_replys_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(!NetworkCheck.isNetworkCheck(getApplicationContext())){
                    Toast.makeText(PatchNoteActivity.this, "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                }else if (user != null) {

                } else {
                    Toast.makeText(getApplicationContext(), "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PatchNoteActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        patchNote_replys_btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(final View view) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(!NetworkCheck.isNetworkCheck(getApplicationContext())){
                    Toast.makeText(PatchNoteActivity.this, "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                }else if(user == null) {
                    Toast.makeText(getApplicationContext(), "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PatchNoteActivity.this, LoginActivity.class);
                    startActivityForResult(intent,8);
                }else if(!firebaseUser.isEmailVerified()){
                    Toast.makeText(getApplicationContext(), "이메일 인증이 필요한 계정입니다. 'ABOUT PATO' 에서 메일을 전송하시길 바랍니다.", Toast.LENGTH_SHORT).show();
                }else if(patchNote_replys_edit.getText().toString().trim().length() > 300){
                    Toast.makeText(PatchNoteActivity.this, "300자 이하로 작성해주세요.", Toast.LENGTH_SHORT).show();
                }else {
                    if(patchNote_replys_edit.getText().toString().length() != 0){
                        patchNote_replys_btn.setEnabled(false);

                        final AlertDialog alertDialog = LoadingDialog.loading_Diaglog(PatchNoteActivity.this);
                        final PatchNoteModel.Replys patchNoteModel = new PatchNoteModel.Replys();

                        patchNoteModel.uid = firebaseAuth.getCurrentUser().getUid();
                        patchNoteModel.content = patchNote_replys_edit.getText().toString();
                        patchNoteModel.timestamp = ServerValue.TIMESTAMP;
                        patchNoteModel.nickname = firebaseAuth.getCurrentUser().getDisplayName();
                        patchNoteModel.removereplys = false;
                        patchNoteModel.part = patchNoteModel.timestamp;
                        patchNoteModel.rpEmpty = false;

                        databaseReference.child("patchnotes").child(year).child(version).child("replys").push().setValue(patchNoteModel).addOnSuccessListener(PatchNoteActivity.this, new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                patchNote_replys_edit.clearFocus();
                                patchNote_replys_edit.setText("");

                                databaseReference.child("patchnotes").child(year).child(version).child("readcount").runTransaction(new Transaction.Handler() {
                                    @Override
                                    public Transaction.Result doTransaction(MutableData mutableData) {
                                        PatchNoteModel.readcount readcount = mutableData.getValue(PatchNoteModel.readcount.class);
                                        if(readcount != null){
                                            readcount.replyscount = readcount.replyscount + 1;
                                            mutableData.setValue(readcount);
                                        }

                                        return Transaction.success(mutableData);
                                    }

                                    @Override
                                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                        reGetdata = true;
                                        replys_getData();

                                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(patchNote_replys_edit.getWindowToken(), 0);
                                        alertDialog.dismiss();
                                        patchNote_replys_btn.setEnabled(true);
                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                alertDialog.dismiss();
                                patchNote_replys_btn.setEnabled(true);
                                Toast.makeText(PatchNoteActivity.this, "댓글 작성 실패", Toast.LENGTH_SHORT).show();
                            }
                        });

                    }else{
                        Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        patchNote_replys_edit.requestFocus();
                    }
                }
            }
        });

        getSupportActionBar().setTitle(title);
        getData();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == 8){
            if (resultCode == Activity.RESULT_OK){
                onResume = "logins";
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(onResume.equals("logins")){
            firebaseUser = firebaseAuth.getInstance().getCurrentUser();
            if(firebaseUser != null){
                userUid = firebaseUser.getUid();
                patchNoteRecyclerViewAdapter.userUid = userUid;
            }

            onResume = "";
        }
    }

    void getData(){
        databaseReference.child("patchnotes").child(year).child(version).child("contents").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                contents.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    PatchNoteModel.Contents patchNoteModel = snapshot.getValue(PatchNoteModel.Contents.class);
                    contents.add(patchNoteModel);
                }
                databaseReference.removeEventListener(this);
                championImage();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PatchNoteActivity.this, "오류가 발생하였습니다. 잠시 후 시도해 주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void replys_getData(){

        databaseReference.child("patchnotes").child(year).child(version).child("replys").orderByChild("part").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                replys.clear();
                keys.clear();
                if(dataSnapshot.exists()){
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        replys.add(snapshot.getValue(PatchNoteModel.Replys.class));
                        keys.add(snapshot.getKey());
                    }

                }else{
                }

                if(reGetdata){
                    patchNoteRecyclerViewAdapter.notifyDataSetChanged();
                    reGetdata = false;
                }else{
                    patchNoteRecyclerViewAdapter = new PatchNoteRecyclerViewAdapter(title,year,version,contents,championimage,PatchNoteActivity.this,patchNote_replys_edit,patchNote_replys_btn,userUid,replys,keys);
                    patchNote_recyclerView.setAdapter(patchNoteRecyclerViewAdapter);
                    patchNote_recyclerView.setLayoutManager(linearLayoutManager);
                }
                databaseReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(PatchNoteActivity.this, "오류가 발생하였습니다. 잠시 후 시도해 주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    void championImage(){
        championimage = new String[contents.size()][15];

        final int size = contents.size();

        for(int i = 0; i < size; i++){
            final int j = i;

            databaseReference.child("images").child(contents.get(i).name).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    ImagesModel imagesModel = dataSnapshot.getValue(ImagesModel.class);
                    if(dataSnapshot.exists()){
                        championimage[j][0] = imagesModel.imageUrl;
                        championimage[j][1] = imagesModel.imageUrlP;
                        championimage[j][2] = imagesModel.imageUrlQ;
                        championimage[j][3] = imagesModel.imageUrlQQ;
                        championimage[j][4] = imagesModel.imageUrlW;
                        championimage[j][5] = imagesModel.imageUrlWW;
                        championimage[j][6] = imagesModel.imageUrlE;
                        championimage[j][7] = imagesModel.imageUrlEE;
                        championimage[j][8] = imagesModel.imageUrlR;
                        championimage[j][9] = imagesModel.imageUrlRR;
                        championimage[j][10] = imagesModel.imageUrlr_P;  championimage[j][11] = imagesModel.imageUrlr_Q; championimage[j][12] = imagesModel.imageUrlr_W;
                        championimage[j][13] = imagesModel.imageUrlr_E; championimage[j][14] = imagesModel.imageUrlr_R;

                    }else{

                    }
                    if(j == size - 1){
                        replys_getData();
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(PatchNoteActivity.this, "오류가 발생하였습니다. 잠시 후 시도해 주세요.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
