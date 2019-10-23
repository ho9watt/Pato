package com.example.pato;

import android.app.Activity;
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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.pato.customclass.LoadingDialog;
import com.example.pato.customclass.NetworkCheck;
import com.example.pato.customclass.SHA256;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {

    private EditText email_edit, password_edit;
    private Button login_btn, signup_btn, password_find_btn;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        email_edit = findViewById(R.id.loginActivity_email_edittext);
        password_edit = findViewById(R.id.loginActivity_password_edittext);
        login_btn =  findViewById(R.id.loginActivity_login_btn);
        signup_btn = findViewById(R.id.loginActivity_signup_btn);
        password_find_btn = findViewById(R.id.loginActivity_password_find_btn);

        email_edit.requestFocus();
        email_edit.setFilters(new InputFilter[]{filter2,new InputFilter.LengthFilter(50)});
        password_edit.setFilters(new InputFilter[]{filter,new InputFilter.LengthFilter(16)});

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                login_btn.setEnabled(false);

                if(!NetworkCheck.isNetworkCheck(getApplicationContext())){
                    Toast.makeText(LoginActivity.this, "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                    login_btn.setEnabled(true);
                }else if(!TextUtils.isEmpty(email_edit.getText().toString().trim()) && !TextUtils.isEmpty(password_edit.getText().toString().trim())){
                    loginEvent();
                }else{
                    Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
                    login_btn.setEnabled(true);
                }
            }
        });

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,tosCheckActivity.class));
            }
        });

        password_find_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, PasswordFindActivity.class);
                startActivity(intent);
            }
        });

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull final FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){ //로그인 됐을떄
                    Intent intent = new Intent();
                    intent.putExtra("login","login");
                    setResult(Activity.RESULT_OK,intent);
                    LoginActivity.this.finish();

                }else{ //로그아웃 일떄

                }
            }
        };

        ActionBar();
    }

    void ActionBar(){
        ActionBar actionBar_pato = getSupportActionBar() ;
        actionBar_pato.setTitle("로그인") ;
    }


    void loginEvent(){
        alertDialog = LoadingDialog.loading_Diaglog(LoginActivity.this);
        firebaseAuth.signInWithEmailAndPassword(email_edit.getText().toString(), SHA256.encrypt(password_edit.getText().toString())).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){ //로그인 실패 했을떄
                    String message = task.getException().getMessage();
                    if(message.equals("The email address is badly formatted.") || message.equals("The password is invalid or the user does not have a password.")) {
                        Toast.makeText(LoginActivity.this, "아이디가 없거나 잘못된 비밀번호입니다.", Toast.LENGTH_SHORT).show();
                    }else if(message.equals("The user account has been disabled by an administrator.")){
                        Toast.makeText(LoginActivity.this, "정지된 계정입니다.", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(LoginActivity.this, "로그인 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }

                }else{

                }
                login_btn.setEnabled(true);
                alertDialog.dismiss();
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    protected InputFilter filter2 = new InputFilter() { //영어,숫자만 허용

        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9@.]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

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
