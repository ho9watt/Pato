package com.example.pato;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pato.customclass.LoadingDialog;
import com.example.pato.customclass.NetworkCheck;
import com.example.pato.customclass.SHA256;
import com.example.pato.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

public class RemoveUserActivity extends AppCompatActivity {

    private Button remove_btn;
    private EditText email_edit, password_edit;

    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;

    private String userUid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remove_user);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        userUid = firebaseAuth.getCurrentUser().getUid();

        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("users");

        remove_btn = findViewById(R.id.removeUserActivity_remove_btn);
        email_edit =  findViewById(R.id.removeUserActivity_email_edittext);
        password_edit = findViewById(R.id.removeUserActivity_password_edittext);

        remove_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!NetworkCheck.isNetworkCheck(getApplicationContext())){
                    Toast.makeText(RemoveUserActivity.this, "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(email_edit.getText().toString().trim())){
                    Toast.makeText(RemoveUserActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    email_edit.requestFocus();
                }else if(TextUtils.isEmpty(password_edit.getText().toString().trim())){
                    Toast.makeText(RemoveUserActivity.this, "비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    password_edit.requestFocus();
                }else{
                    remove_btn.setEnabled(false);
                    ExitMessage();
                }
            }
        });

        password_edit.setFilters(new InputFilter[]{filter,new InputFilter.LengthFilter(16)});
        ActionBar();
    }

    void ActionBar(){
        ActionBar actionBar_pato = getSupportActionBar() ;
        actionBar_pato.setTitle("회원 탈퇴") ;
    }

    protected InputFilter filter = new InputFilter() { //영어,숫자만 허용

        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };


    private void ExitMessage() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(RemoveUserActivity.this);
        builder.setCancelable(false);
        builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    remove_btn.setEnabled(true);
                    dialogInterface.dismiss();
                    return true;
                }
                return false;
            }
        });

        builder.setMessage("정말 회원 '탈퇴' 하시겠습니까?").setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {

                remove_btn.setEnabled(true);

                final AlertDialog alertDialog;
                alertDialog = LoadingDialog.loading_Diaglog(RemoveUserActivity.this);

                collectionReference.document(userUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if(documentSnapshot.exists()){

                                String email = documentSnapshot.getString("email");
                                String password = documentSnapshot.getString("password");

                                if(email.equals(email_edit.getText().toString()) && password.equals(SHA256.encrypt(password_edit.getText().toString()))){
                                    AuthCredential credential = EmailAuthProvider.getCredential(email_edit.getText().toString(),SHA256.encrypt(password_edit.getText().toString()));
                                    firebaseUser.reauthenticate(credential).addOnCompleteListener(RemoveUserActivity.this, new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                collectionReference.document(userUid).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        firebaseUser.delete().addOnCompleteListener(RemoveUserActivity.this, new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Toast.makeText(RemoveUserActivity.this, "탈퇴 하였습니다.", Toast.LENGTH_SHORT).show();
                                                                    firebaseAuth.signOut();
                                                                    Intent intent = new Intent();
                                                                    setResult(Activity.RESULT_OK,intent);
                                                                    RemoveUserActivity.this.finish();
                                                                    alertDialog.dismiss();
                                                                    dialogInterface.dismiss();
                                                                }else{
                                                                    Toast.makeText(RemoveUserActivity.this, "탈퇴하지 못하였습니다.", Toast.LENGTH_SHORT).show();
                                                                    alertDialog.dismiss();
                                                                    dialogInterface.dismiss();
                                                                }
                                                            }
                                                        });

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(RemoveUserActivity.this, "탈퇴하지 못하였습니다.", Toast.LENGTH_SHORT).show();
                                                        alertDialog.dismiss();
                                                        dialogInterface.dismiss();
                                                    }
                                                });


                                            }else{
                                                alertDialog.dismiss();
                                                dialogInterface.dismiss();
                                                Toast.makeText(RemoveUserActivity.this, "사용자 인증에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else{
                                    alertDialog.dismiss();
                                    dialogInterface.dismiss();
                                    Toast.makeText(RemoveUserActivity.this, "이메일과 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                                    email_edit.setText("");
                                    password_edit.setText("");
                                    email_edit.requestFocus();
                                }
                            }else{
                                Toast.makeText(RemoveUserActivity.this, "이미 탈퇴한 아이디거나 존재 하지 않는 아이디 입니다.", Toast.LENGTH_SHORT).show();
                                dialogInterface.dismiss();
                                alertDialog.dismiss();
                            }
                        }else{
                            Toast.makeText(RemoveUserActivity.this, "로그인정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                remove_btn.setEnabled(true);
                dialogInterface.dismiss();
            }
        }).create().show();
    }

}
