package com.example.pato;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pato.customclass.LoadingDialog;
import com.example.pato.customclass.NetworkCheck;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordFindActivity extends AppCompatActivity {

    private EditText email_edit;
    private Button success_btn;
    private TextView email_text;

    private boolean Check[] = {false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_find);

        email_edit = findViewById(R.id.passwordFindActivity_email_edittext);
        success_btn = findViewById(R.id.passwordFindActivity_success_btn);
        email_text = findViewById(R.id.passwordFindActivity_email_textview);

        success_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!NetworkCheck.isNetworkCheck(getApplicationContext())){
                    Toast.makeText(PasswordFindActivity.this, "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                }else if(email_edit.getText().length() == 0 ){
                    Toast.makeText(PasswordFindActivity.this, "이메일을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if(signUpCheck() == true){
                    success_btn.setEnabled(false);

                    final AlertDialog alertDialog;
                    alertDialog = LoadingDialog.loading_Diaglog(PasswordFindActivity.this);

                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    String emailAddress = email_edit.getText().toString();

                    auth.sendPasswordResetEmail(emailAddress)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(PasswordFindActivity.this, "메일을 전송하였습니다.", Toast.LENGTH_SHORT).show();
                                        PasswordFindActivity.this.finish();
                                    }else{
                                        Toast.makeText(PasswordFindActivity.this, "등록되지 않은 메일입니다.", Toast.LENGTH_SHORT).show();
                                    }
                                    success_btn.setEnabled(true);
                                    alertDialog.dismiss();
                                }
                            });
                }else{
                    return;
                }
            }
        });

        signUpCheck();
        ActionBar();
    }

    void ActionBar(){
        ActionBar actionBar_pato = getSupportActionBar() ;
        actionBar_pato.setTitle("비밀번호 찾기") ;
    }

    public boolean signUpCheck(){

        email_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                email_text.setVisibility(View.VISIBLE);
                if(email_edit.getText().toString().length() == 0){
                    email_text.setText("이메일을 입력해주세요.");
                    Check[0] = false;
                }else if(checkEmail(email_edit.getText().toString()) == false) {
                    email_text.setText("이메일형식으로 입력해주세요.");
                    Check[0] = false;
                }else{
                    email_text.setVisibility(View.GONE);
                    Check[0] = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return Check[0];
    }

    public static boolean checkEmail(String email){ //이메일 형식 체크

        String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        boolean isNormal = m.matches();

        return isNormal;
    }

}
