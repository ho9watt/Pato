package com.example.pato.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SwitchCompat;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pato.LoginActivity;
import com.example.pato.MainActivity;
import com.example.pato.OptionDatabase;
import com.example.pato.PasswordModifyActivity;
import com.example.pato.R;
import com.example.pato.TosBoardActivity;
import com.example.pato.customclass.CharacterWrapTextView;
import com.example.pato.customclass.ClearEditText;
import com.example.pato.customclass.LoadingDialog;
import com.example.pato.customclass.NetworkCheck;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;


public class MyFragment extends Fragment{

    private LinearLayout logout_linear;
    private Button login_btn;
    private LinearLayout modify_linear;
    private LinearLayout password_modify_linear;

    private Button about_btn;
    private Button about_pato_btn;
    private SwitchCompat Board_Switch;
    private SwitchCompat Contest_Switch;
    private LinearLayout linearLayout_main;
    private LinearLayout linearLayout_logout;
    private LinearLayout linearlayout_login;
    private TextView netCheck;
    //private RecyclerView recyclerView;
    private AlertDialog alertDialog;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;

    private String userUid;
    final Boolean[] nicknameCheck = {false};

    private OptionDatabase optionDatabase;
    private SQLiteDatabase database;
    private String databaseName = "option.db";
    private String tableName = "option";

    public static MyFragment newInstance(){
        Bundle bundle = new Bundle();

        MyFragment fragment = new MyFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my,container,false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("users");

        logout_linear = view.findViewById(R.id.myFragment_logout_linear);
        modify_linear = view.findViewById(R.id.myFragment_modify_nickname_linear);
        password_modify_linear = view.findViewById(R.id.myFragment_modify_password_linear);

        about_btn = view.findViewById(R.id.myFragment_about_pato);
        Board_Switch = view.findViewById(R.id.myFragment_board_switch);
        Contest_Switch =  view.findViewById(R.id.myFragment_contest_switch);
        linearLayout_main = view.findViewById(R.id.myFragment_linearlayout_main);
        linearLayout_logout = view.findViewById(R.id.myFragment_linearlayout_not_login);
        linearlayout_login =  view.findViewById(R.id.myFragment_linearlayout_login);
        login_btn = view.findViewById(R.id.myFragment_login_btn);
        netCheck = view.findViewById(R.id.myFragment_netCheck);
        //recyclerView = view.findViewById(R.id.MyFragment_Myboard_recyclerview);
        about_pato_btn = view.findViewById(R.id.myFragment_about_pato_btn);

        firebaseUser = firebaseAuth.getInstance().getCurrentUser();

        DatabaseOpen(databaseName);
        DatabaseOpen_Contest(databaseName);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent,66);
            }
        });

        logout_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout_linear.setEnabled(false);

                if(!NetworkCheck.isNetworkCheck(getActivity())){
                    Toast.makeText(getActivity(), "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    alertDialog = LoadingDialog.loading_Diaglog(getActivity());

                    Map<String, Object> map = new HashMap<>();
                    map.put("pushToken","");

                    collectionReference.document(firebaseUser.getUid()).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                firebaseAuth.signOut();
                                Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
                                loginView();
                                alertDialog.dismiss();
                                logout_linear.setEnabled(true);
                            }else{
                                Toast.makeText(getActivity(), "로그아웃을 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                                alertDialog.dismiss();
                                logout_linear.setEnabled(true);
                            }
                        }
                    });
                }
            }
        });

        password_modify_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PasswordModifyActivity.class);
                startActivity(intent);
            }
        });

        modify_linear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modify_linear.setEnabled(false);
                modifyDiaglog();
            }
        });

        about_btn.setPaintFlags(about_btn.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        about_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),TosBoardActivity.class);
                startActivity(intent);
            }
        });

        about_pato_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),TosBoardActivity.class);
                startActivityForResult(intent,45);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 66 || requestCode == 45) {
            if (resultCode == Activity.RESULT_OK) {
                loginView();
            }
        }
    }

    /*private class MyBoardRecyclerviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private List<BoardModel.Board> boardList = new ArrayList<>();
        private List<String> boardKey = new ArrayList<>();

        public MyBoardRecyclerviewAdapter() {

            databaseReference.child("board").child("boardinfo").orderByChild("uid").equalTo(userUid).limitToLast(10).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    boardList.clear();
                    boardKey.clear();
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                        BoardModel.Board boardmodels = snapshot.getValue(BoardModel.Board.class);
                        boardList.add(boardmodels);
                        boardKey.add(snapshot.getKey());
                    }
                    Collections.reverse(boardList);
                    Collections.reverse(boardKey);

                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "오류가 발생하였습니다. 잠시 후 시도해 주세요.", Toast.LENGTH_SHORT).show();
                }
            });

        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_myboard,parent,false);

            return new MyBoardViewHolder(view);
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            MyBoardViewHolder myBoardViewHolder = (MyBoardViewHolder) holder;

            myBoardViewHolder.Myboard_timestamp.setText(TimeGab((long) boardList.get(position).timestamp));
            if(boardList.get(position).title.length() > 41){
                boardList.get(position).title = boardList.get(position).title.substring(0,40) + "...";
            }
            myBoardViewHolder.Myboard_readcount.setText(String.valueOf(boardList.get(position).readcount));

            int replysCountSize = 0;
            String replysCount = "["+String.valueOf(boardList.get(position).replyscount)+"]";

            String text = boardList.get(position).title+"  "+replysCount+"   ";
            SpannableStringBuilder ssBuilder = new SpannableStringBuilder(text);

            if(replysCount.length() == 3){
                replysCountSize = 6;
            }else if(replysCount.length() == 4){
                replysCountSize = 7;
            }else if(replysCount.length() == 5){
                replysCountSize = 8;
            }else if(replysCount.length() == 6){
                replysCountSize = 9;
            }

            ssBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#ffff8800")),text.length() - replysCountSize ,text.length() - 3 ,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ssBuilder.setSpan(new AbsoluteSizeSpan(20,true),text.length() - replysCountSize ,text.length() - 3 ,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            myBoardViewHolder.Myboard_title.setText(ssBuilder);

            myBoardViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!NetworkCheck.isNetworkCheck(getActivity())){
                        Toast.makeText(getActivity(), "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                    }else{
                        databaseReference.child("board").child("boardinfo").child(boardKey.get(position)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                BoardModel.Board boardModels = dataSnapshot.getValue(BoardModel.Board.class);

                                boardModels.readcount = boardModels.readcount + 1;

                                databaseReference.child("board").child("boardinfo").child(boardKey.get(position)).setValue(boardModels).addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Intent intent = new Intent(getActivity().getApplicationContext(), BoardActivity.class);
                                        intent.putExtra("bid", boardKey.get(position));
                                        getActivity().startActivityForResult(intent, 1);

                                    }
                                });
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Toast.makeText(getActivity(), "오류가 발생하였습니다. 잠시 후 시도해 주세요.", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return boardList.size();
        }


        private class MyBoardViewHolder extends RecyclerView.ViewHolder {

            private TextView Myboard_title;
            private TextView Myboard_timestamp;
            private TextView Myboard_readcount;

            public MyBoardViewHolder(View view) {
                super(view);

                Myboard_title = view.findViewById(R.id.myFragment_title);
                Myboard_timestamp = view.findViewById(R.id.myFragment_timestamp);
                Myboard_readcount = view.findViewById(R.id.myFragment_readcount);
            }
        }
    }

    public String TimeGab(long beforeTime){

        long gabDay;
        long gabDate;
        String dayString;

        gabDate = System.currentTimeMillis() + 2600 - beforeTime;
        gabDay = gabDate / (1000);

        if(gabDay>59){
            gabDay = gabDate/ (60 * 1000);
            dayString = "분 전";
            if(gabDay>59){
                gabDay = gabDate / (60* 60 * 1000);
                dayString = "시간 전";
                if(gabDay>23){
                    gabDay = gabDate / (24 * 60 * 60 * 1000);
                    dayString = "일 전";
                    if(gabDay>30){
                        gabDay = (gabDate / (30 * 24 * 60 * 60 *1000)) * -1;
                        dayString = "달 전";
                        if(gabDay>11){
                            gabDay = gabDate / (12 * 30 * 24 * 60 + 60 * 1000);
                            dayString = "년 전";
                        }
                    }
                }
            }
        }else{
            return "방금 전";
        }
        return gabDay+dayString;
    }*/

    private void modifyDiaglog(){

        LayoutInflater layoutInflater_ld = getActivity().getLayoutInflater();
        View view_ld = layoutInflater_ld.inflate(R.layout.dialog_loading,null);

        AlertDialog.Builder builder_ld = new AlertDialog.Builder(getActivity());
        final AlertDialog Optiondialog_ld = builder_ld.create();
        Optiondialog_ld.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Optiondialog_ld.setView(view_ld);
        Optiondialog_ld.setCancelable(true);

        final CharacterWrapTextView nicknameCheck_text;
        final Button yes_btn, no_btn;
        final ClearEditText nickname_edit;

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_change_nickname,null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        final AlertDialog Optiondialog = builder.create();

        Optiondialog.setView(view);
        Optiondialog.setCancelable(false);
        Optiondialog.setCanceledOnTouchOutside(false);
        Optiondialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK) {
                    modify_linear.setEnabled(true);
                    Optiondialog.dismiss();
                    return true;
                }
                return false;
            }
        });
        Optiondialog.show();

        nicknameCheck_text = view.findViewById(R.id.dialog_nicknameCheck_text);
        yes_btn = view.findViewById(R.id.dialog_exitYes_btn);
        no_btn = view.findViewById(R.id.dialog_exitNo_btn);
        nickname_edit = view.findViewById(R.id.dialog_nickname_edittext);

        nickname_edit.setFilters(new InputFilter[]{filter,new InputFilter.LengthFilter(16)});

        nicknameCheck_text.setText("닉네임을 입력해주세요.");
        nicknameCheck(nickname_edit,nicknameCheck_text);
        nickname_edit.requestFocus();


        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                modify_linear.setEnabled(true);

                if(!NetworkCheck.isNetworkCheck(getActivity())){
                    Toast.makeText(getActivity(), "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                }else if(nickname_edit.getText().toString().length() == 0){
                    Toast.makeText(getActivity(), "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }else if(nicknameCheck(nickname_edit,nicknameCheck_text)){
                    yes_btn.setEnabled(false);

                    collectionReference.whereEqualTo("nickname",nickname_edit.getText().toString().trim()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful()){
                                QuerySnapshot queryDocumentSnapshots = task.getResult();
                                if(queryDocumentSnapshots.isEmpty()){
                                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                    Optiondialog.dismiss();
                                    Optiondialog_ld.show();

                                    UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(nickname_edit.getText().toString().trim()).build();
                                    user.updateProfile(userProfileChangeRequest).addOnCompleteListener(getActivity(),new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()){
                                                Map<String, Object> map = new HashMap<>();
                                                map.put("nickname",nickname_edit.getText().toString().trim());

                                                collectionReference.document(userUid).update(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task){
                                                        if(task.isSuccessful()){
                                                            Toast.makeText(getActivity(), "닉네임 변경을 완료하였습니다. ", Toast.LENGTH_SHORT).show();
                                                            ((MainActivity)getActivity()).getSupportActionBar().setTitle(nickname_edit.getText().toString().trim() + "님");
                                                        }else{
                                                            Toast.makeText(getActivity(), "닉네임 변경을 실패 하였습니다. ", Toast.LENGTH_SHORT).show();
                                                        }
                                                        Optiondialog_ld.dismiss();
                                                        yes_btn.setEnabled(true);
                                                    }
                                                });
                                            }else{
                                                yes_btn.setEnabled(true);
                                                Toast.makeText(getActivity(), "닉네임 변경을 실패 하였습니다. ", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }else{
                                    nicknameCheck_text.setText("중복된 닉네임이 존재합니다.");
                                    yes_btn.setEnabled(true);
                                }
                            }else{
                                Toast.makeText(getActivity(), "닉네임 중복확인을 할 수 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    Toast.makeText(getActivity(), "닉네임 변경에 실패 하였습니다. ", Toast.LENGTH_SHORT).show();
                }
            }
        });
        no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                modify_linear.setEnabled(true);
                Optiondialog.dismiss();
            }
        });
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


    private Boolean nicknameCheck(final ClearEditText nickname_edit, final TextView nickname_text){

        nickname_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(checkWordCount(nickname_edit.getText().toString().trim()) == 0 && TextUtils.isEmpty(nickname_edit.getText().toString().trim())){
                    nickname_text.setVisibility(View.VISIBLE);
                    nickname_text.setText("닉네임을 입력해주세요.");
                    nicknameCheck[0] = false;
                }else if(checkWordCount(nickname_edit.getText().toString().trim()) > 16 || checkWordCount(nickname_edit.getText().toString().trim()) < 4){
                    nickname_text.setVisibility(View.VISIBLE);
                    nickname_text.setText("닉네임(영어,숫자,한글)을 2자이상 8자 이하로 입력해주세요.");
                    nicknameCheck[0] = false;
                }else {
                    nickname_text.setVisibility(View.GONE);
                    nicknameCheck[0] = true;
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        if(nicknameCheck[0] == true){
            return true;
        }else{
            return false;
        }
    }

    private void loginView(){

        if(!NetworkCheck.isNetworkCheck(getActivity())){
            linearLayout_main.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            netCheck.setVisibility(View.VISIBLE);
            linearlayout_login.setVisibility(View.GONE);
            linearLayout_logout.setVisibility(View.GONE);
        }else{
            linearLayout_main.setGravity(Gravity.NO_GRAVITY);
            netCheck.setVisibility(View.GONE);
            linearlayout_login.setVisibility(View.VISIBLE);

            firebaseUser = firebaseAuth.getInstance().getCurrentUser();

            if(firebaseUser != null){
                userUid = firebaseUser.getUid();
               /* recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                recyclerView.setAdapter(new MyBoardRecyclerviewAdapter());*/
                firebaseAuth = FirebaseAuth.getInstance();

                linearLayout_main.setGravity(Gravity.NO_GRAVITY);
                linearlayout_login.setVisibility(View.VISIBLE);
                linearLayout_logout.setVisibility(View.GONE);
            }else{
                ((MainActivity)getActivity()).getSupportActionBar().setTitle("LoL");
                linearLayout_main.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
                linearlayout_login.setVisibility(View.GONE);
                linearLayout_logout.setVisibility(View.VISIBLE);
            }
        }
        linearLayout_main.setVisibility(View.VISIBLE);
    }

    private void DatabaseOpen(String databaseName){
        optionDatabase = new OptionDatabase(getActivity(), databaseName,null,1);
        database = optionDatabase.getWritableDatabase();

        if(selectOption(tableName)){
            Board_Switch.setChecked(true);
        }else{
            Board_Switch.setChecked(false);
        }

        Board_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String onoff = "";
                if(b){
                    onoff = "on";

                }else{
                    onoff = "off";

                }
                updateOption(tableName,onoff);
            }
        });
    }

    private boolean selectOption(String tableName){
        String alarm = "";
        if(database != null){
            String sql = "select boardalarm from " + tableName;
            Cursor cursor = database.rawQuery(sql,null);
            cursor.moveToNext();
            alarm = cursor.getString(0);
        }

        if(alarm.equals("on")){
            return true;
        }else{
            return false;
        }
    }

    private void updateOption(String tableName, String onoff){
        if(database !=null){
            ContentValues contentValues = new ContentValues();
            contentValues.put("boardalarm",onoff);
            database.update(tableName,contentValues,"_id=?",new String[]{"option"});
        }
    }

    private void DatabaseOpen_Contest(String databaseName){
        optionDatabase = new OptionDatabase(getActivity(), databaseName,null,1);
        database = optionDatabase.getWritableDatabase();

        FirebaseMessaging firebaseMessaging = FirebaseMessaging.getInstance();

        if(selectOption_Contest(tableName)){
            Contest_Switch.setChecked(true);
            firebaseMessaging.unsubscribeFromTopic("optionOff");
            firebaseMessaging.subscribeToTopic("optionOn");
        }else{
            Contest_Switch.setChecked(false);
            firebaseMessaging.unsubscribeFromTopic("optionOn");
            firebaseMessaging.subscribeToTopic("optionOff");
        }

        Contest_Switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                String onoff;
                if(b){
                    onoff = "on";
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("optionOff");
                    FirebaseMessaging.getInstance().subscribeToTopic("optionOn");
                }else{
                    onoff = "off";
                    FirebaseMessaging.getInstance().unsubscribeFromTopic("optionOn");
                    FirebaseMessaging.getInstance().subscribeToTopic("optionOff");
                }
                updateOption_Contest(tableName,onoff);
            }
        });
    }

    private boolean selectOption_Contest(String tableName){
        String alarm = "";
        if(database != null){
            String sql = "select contestalarm from " + tableName;
            Cursor cursor = database.rawQuery(sql,null);
            cursor.moveToNext();
            alarm = cursor.getString(0);
        }

        if(alarm.equals("on")){
            return true;
        }else{
            return false;
        }
    }

    private void updateOption_Contest(String tableName, String onoff){

        if(database !=null){
            ContentValues contentValues = new ContentValues();
            contentValues.put("contestalarm",onoff);
            database.update(tableName,contentValues,"_id=?",new String[]{"option"});
        }
    }

    protected InputFilter filter = new InputFilter() { //영어,숫자만 허용

        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            Pattern ps = Pattern.compile("^[a-zA-Z0-9ㄱ-ㅣ가-힣]+$");
            if (!ps.matcher(source).matches()) {
                return "";
            }
            return null;
        }
    };


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser){
            loginView();
        }
    }
}
