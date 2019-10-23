package com.example.pato;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pato.customclass.LoadingDialog;
import com.example.pato.customclass.NetworkCheck;
import com.example.pato.customclass.SHA256;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PasswordModifyActivity extends AppCompatActivity {

    private EditText pre_password, new_password, new_password_1;
    private TextView pre_password_text, new_password_text,new_password_1_text;
    private Button success_btn;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;

    private String userUid;

    final boolean[] Check = {false,false,false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_modify);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        userUid = firebaseAuth.getCurrentUser().getUid();

        pre_password = findViewById(R.id.passwordActivity_pre_password_edit);
        new_password =  findViewById(R.id.passwordActivity_new_password_edit);
        new_password_1 =  findViewById(R.id.passwordActivity_new_password_1_edit);
        pre_password_text = findViewById(R.id.passwordActivity_pre_password_textview);
        new_password_text =  findViewById(R.id.passwordActivity_new_password_textview);
        new_password_1_text =  findViewById(R.id.passwordActivity_new_password_1_textview);

        success_btn =  findViewById(R.id.passwordActivity_success_btn);

        success_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!NetworkCheck.isNetworkCheck(getApplicationContext())){
                    Toast.makeText(PasswordModifyActivity.this, "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                }else if(signUpCheck() == false){
                    return;
                }else{
                    success_btn.setEnabled(false);

                    final AlertDialog alertDialog;
                    alertDialog = LoadingDialog.loading_Diaglog(PasswordModifyActivity.this);
                    
                    collectionReference.document(userUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful()){
                                DocumentSnapshot documentSnapshot = task.getResult();
                                if(documentSnapshot.exists()){
                                    final String[] password = {documentSnapshot.getString("password")};

                                    if(SHA256.encrypt(pre_password.getText().toString()).equals(password[0])){
                                        firebaseUser.updatePassword(SHA256.encrypt(new_password.getText().toString())).addOnCompleteListener(PasswordModifyActivity.this,new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()){
                                                    password[0] = SHA256.encrypt(new_password.getText().toString());

                                                    Map<String, Object> map = new HashMap<>();
                                                    map.put("password",password[0]);

                                                    collectionReference.document(userUid).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if(task.isSuccessful()){
                                                                Toast.makeText(PasswordModifyActivity.this, "비밀번호 변경에 성공하였습니다.", Toast.LENGTH_SHORT).show();
                                                                PasswordModifyActivity.this.finish();
                                                            }else{
                                                                Toast.makeText(PasswordModifyActivity.this, "비밀번호 변경에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                                            }
                                                            success_btn.setEnabled(true);
                                                            alertDialog.dismiss();
                                                        }
                                                    });
                                                }else{
                                                    Toast.makeText(PasswordModifyActivity.this, "비밀번호 변경에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                                    }else{
                                        success_btn.setEnabled(true);
                                        alertDialog.dismiss();
                                        Toast.makeText(PasswordModifyActivity.this, "현재 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                                    }

                                }else{
                                    Toast.makeText(PasswordModifyActivity.this, "존재하지 않는 유저 입니다.", Toast.LENGTH_SHORT).show();
                                }
                            }else{
                                Toast.makeText(PasswordModifyActivity.this, "유저정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        ActionBar();
        signUpCheck();
    }

    void ActionBar(){
        ActionBar actionBar_pato = getSupportActionBar() ;
        actionBar_pato.setTitle("비밀번호 변경") ;
    }


    public boolean signUpCheck(){

        pre_password.setFilters(new InputFilter[]{filter,new InputFilter.LengthFilter(16)});
        new_password.setFilters(new InputFilter[]{filter,new InputFilter.LengthFilter(16)});
        new_password_1.setFilters(new InputFilter[]{filter,new InputFilter.LengthFilter(16)});

        pre_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(pre_password.getText().toString().length() == 0){
                    pre_password_text.setVisibility(View.VISIBLE);
                    pre_password_text.setText("현재 비밀번호를 입력해주세요.");
                    Check[0] = false;
                }else{
                    pre_password_text.setVisibility(View.GONE);
                    Check[0] = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        new_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                new_password_text.setVisibility(View.VISIBLE);
                if(new_password.getText().toString().length() == 0){
                    new_password_text.setText("새 비밀번호를 입력해주세요.");
                    Check[1] = false;
                }else if(new_password.getText().toString().length() > 16 || 8 > new_password.getText().toString().length()) {
                    new_password_text.setText("비밀번호(영어,숫자)를 8자 이상 16자 이하로 입력해주세요");
                    Check[1] = false;
                }else{
                    new_password_text.setText("사용 가능한 비밀번호 입니다.");
                    Check[1] = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        new_password_1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                new_password_1_text.setVisibility(View.VISIBLE);
                if(new_password_1.getText().toString().length() == 0 ){
                    new_password_1_text.setText("비밀번호 확인을 입력해주세요.");
                    Check[2] = false;
                }else if(!new_password.getText().toString().equals(new_password_1.getText().toString()) ) {
                    new_password_1_text.setText("비밀번호가 서로 일치하지 않습니다.");
                    Check[2] = false;
                }else{
                    new_password_1_text.setText("비밀번호가 서로 일치합니다.");
                    Check[2] = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        if(Check[0] == false){
            pre_password.requestFocus(pre_password.getText().toString().trim().length());
            Toast.makeText(PasswordModifyActivity.this, "현재 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(Check[1]==false || Check[2]==false) {
            new_password.requestFocus();
            new_password.setText("");
            new_password_1.setText("");
            return false;
        }else if(Check[0] == true && Check[1] == true && Check[2] == true){
            if(!new_password_1.getText().toString().equals(new_password.getText().toString()) ) {

                Toast.makeText(this, "비밀번호가 서로 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                new_password.setText("");
                new_password_1.setText("");
                new_password.requestFocus();

                return false;
            }else{
                return true;
            }

        }else{
            return false;
        }
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
}
