package com.example.pato;

import android.content.DialogInterface;
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
import android.text.TextUtils;
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
import com.example.pato.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private EditText email_edit, password_edit, passwordCheck_edit, nickname_edit;
    private TextView email_text, password_text, passwordCheck_text, nickname_text;
    private Button join_btn;

    private AlertDialog alertDialog;

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;

    private boolean firstCheck = true;

    final boolean[] Check = {false,false,false,false};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        email_edit = findViewById(R.id.signActivity_email_edittext);
        password_edit = findViewById(R.id.signActivity_password_edittext);
        passwordCheck_edit = findViewById(R.id.signupActivity_passwordCheck_edittext);
        nickname_edit = findViewById(R.id.signupActivity_nickName_edittext);
        join_btn = findViewById(R.id.signupActivity_join_btn);
        email_text = findViewById(R.id.signActivity_email_textview);
        password_text =  findViewById(R.id.signActivity_password_textview);
        passwordCheck_text = findViewById(R.id.signActivity_passwordCheck_textview);
        nickname_text = findViewById(R.id.signupActivity_nickName_textview);

        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("users");


        nickname_edit.setFilters(new InputFilter[]{filter3,new InputFilter.LengthFilter(20)});
        email_edit.setFilters(new InputFilter[]{filter2,new InputFilter.LengthFilter(50)});
        password_edit.setFilters(new InputFilter[]{filter,new InputFilter.LengthFilter(16)});
        passwordCheck_edit.setFilters(new InputFilter[]{filter,new InputFilter.LengthFilter(16)});

        signUpCheck();
        join_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!NetworkCheck.isNetworkCheck(getApplicationContext())){
                    Toast.makeText(SignUpActivity.this, "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                }else if(!signUpCheck()){

                }else{
                    join_btn.setEnabled(false);

                    alertDialog = LoadingDialog.loading_Diaglog(SignUpActivity.this);

                    collectionReference.whereEqualTo("email",email_edit.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                QuerySnapshot queryDocumentSnapshots = task.getResult();
                                if(queryDocumentSnapshots.isEmpty()){
                                    collectionReference.whereEqualTo("nickname",nickname_edit.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if(task.isSuccessful()){
                                                QuerySnapshot queryDocumentSnapshots1 = task.getResult();
                                                if(queryDocumentSnapshots1.isEmpty()){

                                                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email_edit.getText().toString(), SHA256.encrypt(password_edit.getText().toString())).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                                            if(task.isSuccessful()){
                                                                final String userUid = task.getResult().getUser().getUid();
                                                                UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(nickname_edit.getText().toString().trim()).build();

                                                                task.getResult().getUser().updateProfile(userProfileChangeRequest);

                                                                UserModel userModel = new UserModel();
                                                                userModel.email = email_edit.getText().toString().trim();
                                                                userModel.nickname = nickname_edit.getText().toString().trim();
                                                                userModel.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                                                userModel.password = SHA256.encrypt(password_edit.getText().toString().trim());

                                                                collectionReference.document(userUid).set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if(task.isSuccessful()){
                                                                            final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
                                                                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                                                                            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                @Override
                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                    if(task.isSuccessful()){
                                                                                        Toast.makeText(getApplicationContext(), "회원가입이 되었습니다. 이메일을 확인하여 인증버튼을 눌러주시기 바랍니다.", Toast.LENGTH_LONG).show();
                                                                                    }else{
                                                                                        Toast.makeText(getApplicationContext(), "회원가입은 되었으나 '인증 이메일'은 전송할 수 없습니다.", Toast.LENGTH_LONG).show();
                                                                                    }
                                                                                    alertDialog.dismiss();
                                                                                    join_btn.setEnabled(true);
                                                                                    SignUpActivity.this.finish();
                                                                                }
                                                                            });
                                                                        }else{
                                                                            alertDialog.dismiss();
                                                                            join_btn.setEnabled(true);
                                                                            Toast.makeText(getApplicationContext(), "회원가입을 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }
                                                                });


                                                            }else{
                                                                if(!task.getException().equals("null")){
                                                                    alertDialog.dismiss();
                                                                    join_btn.setEnabled(true);
                                                                    Toast.makeText(SignUpActivity.this, "회원가입을 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                                                }

                                                            }
                                                        }
                                                    });
                                                }else{
                                                    alertDialog.dismiss();
                                                    nickname_text.setText("중복된 닉네임이 존재합니다.");
                                                    nickname_edit.setText("");
                                                    nickname_edit.requestFocus();
                                                    join_btn.setEnabled(true);
                                                }

                                            }else{
                                                Toast.makeText(SignUpActivity.this, "닉네임을 체크 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                }else{
                                    alertDialog.dismiss();
                                    email_text.setText("중복된 이메일이 존재합니다.");
                                    email_edit.setText("");
                                    email_edit.requestFocus();

                                    join_btn.setEnabled(true);
                                }
                            }else{
                                Toast.makeText(SignUpActivity.this, "아이디를 체크 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        ActionBar();
    }

    public boolean signUpCheck(){

        email_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    email_text.setVisibility(View.VISIBLE);
                    if(email_edit.getText().toString().length() == 0 && TextUtils.isEmpty(email_edit.getText().toString().trim())){
                        email_text.setText("이메일을 입력해주세요.");
                        Check[0] = false;
                    }else if(!checkEmail(email_edit.getText().toString())) {
                        email_text.setText("이메일형식으로 입력해주세요. (ex. abcd@ef.com)");
                        Check[0] = false;
                    }else if(email_edit.getText().toString().length() >= 50){
                        email_text.setText("50자 미만으로 입력해주세요.");
                        Check[0] = false;
                    }else{

                        collectionReference.whereEqualTo("email",email_edit.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    QuerySnapshot queryDocumentSnapshots = task.getResult();
                                    if(queryDocumentSnapshots.isEmpty()){
                                        email_text.setText("사용 가능한 이메일 입니다.");
                                        Check[0] = true;
                                    }else{
                                        email_text.setText("중복된 이메일이 존재합니다.");
                                        Check[0] = false;
                                    }
                                }else{
                                    Toast.makeText(SignUpActivity.this, "아이디를 체크 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                }
            }
        });

        email_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                email_text.setVisibility(View.VISIBLE);
                if(email_edit.getText().toString().length() == 0 && TextUtils.isEmpty(email_edit.getText().toString().trim())){
                    email_text.setText("이메일을 입력해주세요.");
                    Check[0] = false;
                }else if(!checkEmail(email_edit.getText().toString())) {
                    email_text.setText("이메일형식으로 입력해주세요. (ex. abcd@ef.com)");
                    Check[0] = false;
                }else if(email_edit.getText().toString().length() >= 50){
                    email_text.setText("50자 미만으로 입력해주세요.");
                    Check[0] = false;
                }else {
                    email_text.setText(" ");
                    Check[0] = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        nickname_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                nickname_text.setVisibility(View.VISIBLE);
                if(checkWordCount(nickname_edit.getText().toString()) == 0 || TextUtils.isEmpty(nickname_edit.getText().toString())){
                    nickname_text.setText("닉네임을 입력해주세요.");
                    Check[3] = false;
                }else if(checkWordCount(nickname_edit.getText().toString()) > 16 || checkWordCount(nickname_edit.getText().toString()) < 4){
                    nickname_text.setText("닉네임(영어,숫자,한글)을 2자이상 8자 이하로 입력해주세요.");
                    Check[3] = false;
                }else{
                    nickname_text.setText(" ");
                    Check[3] = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        nickname_edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b){
                    nickname_text.setVisibility(View.VISIBLE);
                    if(checkWordCount(nickname_edit.getText().toString()) == 0 || TextUtils.isEmpty(nickname_edit.getText().toString())){
                        nickname_text.setText("닉네임을 입력해주세요.");
                        Check[3] = false;
                    }else if(checkWordCount(nickname_edit.getText().toString()) > 16 || checkWordCount(nickname_edit.getText().toString()) < 4){
                        nickname_text.setText("닉네임(영어,숫자,한글)을 2자이상 8자 이하로 입력해주세요.");
                        Check[3] = false;
                    }else{

                        collectionReference.whereEqualTo("nickname",nickname_edit.getText().toString()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    QuerySnapshot queryDocumentSnapshots = task.getResult();

                                    if(queryDocumentSnapshots.isEmpty()){
                                        nickname_text.setText("사용 가능한 닉네임입니다.");
                                        Check[3] = true;
                                    }else{
                                        nickname_text.setText("중복된 닉네임이 존재합니다.");
                                        Check[3] = false;
                                    }

                                }else{
                                    Toast.makeText(SignUpActivity.this, "닉네임을 체크 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });


        password_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password_text.setVisibility(View.VISIBLE);
                if(password_edit.getText().toString().length() == 0){
                    password_text.setText("비밀번호를 입력해주세요.");
                    Check[1] = false;
                }else if(password_edit.getText().toString().length() > 16 || 8 > password_edit.getText().toString().length()) {
                    password_text.setText("비밀번호(영어,숫자)를 8자 이상 16자 이하로 입력해주세요");
                    Check[1] = false;
                }else{
                    password_text.setText("사용 가능한 비밀번호 입니다.");
                    Check[1] = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        passwordCheck_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                passwordCheck_text.setVisibility(View.VISIBLE);
                if(passwordCheck_edit.getText().toString().length() == 0 ){
                    passwordCheck_text.setText("비밀번호확인을 입력해주세요.");
                    Check[2] = false;
                }else if(!passwordCheck_edit.getText().toString().equals(password_edit.getText().toString()) ) {
                    passwordCheck_text.setText("비밀번호가 서로 일치하지 않습니다.");
                    Check[2] = false;
                }else{
                    passwordCheck_text.setText("비밀번호가 서로 일치합니다.");
                    Check[2] = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        if(!Check[0]){
            if(firstCheck){
                firstCheck = false;
                return false;
            }else{
                email_edit.requestFocus(email_edit.getText().toString().trim().length());
                Toast.makeText(this, "이메일을 다시 적어주세요.", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else if(!Check[1] || !Check[2]){
            password_edit.requestFocus();
            password_edit.setText("");
            passwordCheck_edit.setText("");
            Toast.makeText(this, "비밀번호를 다시 적어주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(!Check[3]) {
            nickname_edit.requestFocus(nickname_edit.getText().toString().trim().length());
            Toast.makeText(this, "닉네임을 다시 적어주세요.", Toast.LENGTH_SHORT).show();
            return false;
        }else if(Check[0] && Check[1] && Check[2] && Check[3] ){

            if(!checkEmail(email_edit.getText().toString())) {
                email_text.setText("이메일형식으로 입력해주세요. (ex. abcd@ef.com)");
                return false;
            }else if(email_edit.getText().toString().length() >= 50){
                email_text.setText("50자 미만으로 입력해주세요.");
                return false;
            }else if(password_edit.getText().toString().length() > 16 || 8 > password_edit.getText().toString().length()) {
                password_text.setText("비밀번호(영어,숫자)를 8자 이상 16자 이하로 입력해주세요");
                return false;
            }else if(!passwordCheck_edit.getText().toString().equals(password_edit.getText().toString()) ) {
                passwordCheck_text.setText("비밀번호가 서로 일치하지 않습니다.");
                return false;
            }else if(checkWordCount(nickname_edit.getText().toString()) > 16 || checkWordCount(nickname_edit.getText().toString()) < 4){
                nickname_text.setText("닉네임(영어,숫자,한글)을 2자이상 8자 이하로 입력해주세요.");
                return false;
            }else{
                return true;
            }

        }else{
            return false;
        }
    }

    void ActionBar(){
        ActionBar actionBar_pato = getSupportActionBar() ;
        actionBar_pato.setTitle("회원 가입") ;
    }


    public static boolean checkEmail(String email){ //이메일 형식 체크

        String regex = "^[_a-zA-Z0-9-\\.]+@[\\.a-zA-Z0-9-]+\\.[a-zA-Z]+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        boolean isNormal = m.matches();

        return isNormal;
    }

    public static int checkWordCount(String word){ //영어 한글 글자숫 체크
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

    protected InputFilter filter = new InputFilter() { //영어,숫자만 허용

        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    protected InputFilter filter2 = new InputFilter() { //영어,숫자만 허용

        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9@.]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    protected InputFilter filter3 = new InputFilter() { //영어,숫자만 허용

        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9ㄱ-ㅣ가-힣]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };

    @Override
    public void onBackPressed() {
        ExitMessage();
    }

    private void ExitMessage() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
        builder.setCancelable(false).setMessage("회원가입을 그만 하시겠습니까?").setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                SignUpActivity.this.finish();
                dialogInterface.dismiss();
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();
    }

}
