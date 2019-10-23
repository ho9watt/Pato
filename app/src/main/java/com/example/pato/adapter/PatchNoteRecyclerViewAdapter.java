package com.example.pato.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ImageSpan;
import android.text.style.StyleSpan;
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
import com.bumptech.glide.request.RequestOptions;
import com.example.pato.LoginActivity;
import com.example.pato.PatchNoteActivity;
import com.example.pato.R;
import com.example.pato.customclass.CharacterWrapTextView;
import com.example.pato.customclass.LoadingDialog;
import com.example.pato.customclass.NetworkCheck;
import com.example.pato.model.NotificationPatchModel;
import com.example.pato.model.PatchNoteModel;
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
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PatchNoteRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_REPLYS = 1;

    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
    private SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy.MM.dd");

    private FirebaseAuth firebaseAuth;

    private EditText replys_edit;
    private Button replys_btn;
    private Activity activity;

    private List<PatchNoteModel.Contents> contents;
    private List<PatchNoteModel.Replys> replys;
    private List<String> keys;
    private String version;
    private String year;
    private String title;
    private String championimage[][];
    public String userUid;
    private int contents_size;

    private UserModel userModel;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private RequestOptions options = new RequestOptions().placeholder(R.drawable.loading_icon).error(R.drawable.error_icon).circleCrop();

    public PatchNoteRecyclerViewAdapter(String title,String year,String version, List<PatchNoteModel.Contents> contents, String championimage[][], Activity activity,
                                        EditText editText, Button button, String userUid, List<PatchNoteModel.Replys> replys, List<String> keys ) {
        this.title = title;
        this.version = version;
        this.year = year;
        this.contents = contents;
        this.championimage = championimage;
        this.activity = activity;
        this.replys_btn = button;
        this.replys_edit = editText;
        this.userUid = userUid;
        this.replys = replys;
        this.keys = keys;
        this.contents_size = contents.size();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_patchnote, parent, false);
            return new PatchNoteViewHolder(view);

        }else if (viewType == TYPE_REPLYS) {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_patchnote_replys, parent, false);
            return new PatchNoteReplysViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof PatchNoteViewHolder) {
            final PatchNoteViewHolder patchNoteViewHolder = (PatchNoteViewHolder) holder;

            if(championimage[position][0] != null){
                patchNoteViewHolder.champion_ImageView.setVisibility(View.VISIBLE);
                Glide.with(patchNoteViewHolder.itemView.getContext())
                        .load(championimage[position][0])
                        .apply(options)
                        .into(patchNoteViewHolder.champion_ImageView);
            }else{
                patchNoteViewHolder.champion_ImageView.setVisibility(View.GONE);
            }

            if(contents.get(position).contentS != null){
                patchNoteViewHolder.championS_linear.setVisibility(View.VISIBLE);
                patchNoteViewHolder.championS_Content_text.setText(SkillNameModify(contents.get(position).contentS),TextView.BufferType.SPANNABLE);
            }else{
                patchNoteViewHolder.championS_linear.setVisibility(View.GONE);
            }

            if(contents.get(position).contentP != null){
                patchNoteViewHolder.championP_linear.setVisibility(View.VISIBLE);

                if (contents.get(position).status.length() != 3){
                    Glide.with(patchNoteViewHolder.itemView.getContext())
                            .load(championimage[position][1])
                            .apply(options)
                            .into(patchNoteViewHolder.championP_ImageView);
                }else{
                    Glide.with(patchNoteViewHolder.itemView.getContext())
                            .load(championimage[position][10])
                            .apply(options)
                            .into(patchNoteViewHolder.championP_ImageView);
                }
                patchNoteViewHolder.championP_Content_text.setText(SkillNameModify(contents.get(position).contentP),TextView.BufferType.SPANNABLE);
            }else{
                patchNoteViewHolder.championP_linear.setVisibility(View.GONE);
            }

            if(contents.get(position).contentQ != null){
                patchNoteViewHolder.championQ_linear.setVisibility(View.VISIBLE);

                if (contents.get(position).status.length() != 3){
                    Glide.with(patchNoteViewHolder.itemView.getContext())
                            .load(championimage[position][2])
                            .apply(options)
                            .into(patchNoteViewHolder.championQ_ImageView);
                }else{
                    Glide.with(patchNoteViewHolder.itemView.getContext())
                            .load(championimage[position][11])
                            .apply(options)
                            .into(patchNoteViewHolder.championQ_ImageView);
                }

                patchNoteViewHolder.championQ_Content_text.setText(SkillNameModify(contents.get(position).contentQ), TextView.BufferType.SPANNABLE);
            }else{
                patchNoteViewHolder.championQ_linear.setVisibility(View.GONE);
            }

            if(contents.get(position).contentQQ != null){
                patchNoteViewHolder.championQQ_linear.setVisibility(View.VISIBLE);

                Glide.with(patchNoteViewHolder.itemView.getContext())
                        .load(championimage[position][3])
                        .apply(options)
                        .into(patchNoteViewHolder.championQQ_ImageView);

                patchNoteViewHolder.championQQ_Content_text.setText(SkillNameModify(contents.get(position).contentQQ), TextView.BufferType.SPANNABLE);
            }else{
                patchNoteViewHolder.championQQ_linear.setVisibility(View.GONE);
            }

            if(contents.get(position).contentW != null){
                patchNoteViewHolder.championW_linear.setVisibility(View.VISIBLE);

                if (contents.get(position).status.length() != 3){
                    Glide.with(patchNoteViewHolder.itemView.getContext())
                            .load(championimage[position][4])
                            .apply(options)
                            .into(patchNoteViewHolder.championW_ImageView);
                }else{
                    Glide.with(patchNoteViewHolder.itemView.getContext())
                            .load(championimage[position][12])
                            .apply(options)
                            .into(patchNoteViewHolder.championW_ImageView);
                }

                patchNoteViewHolder.championW_Content_text.setText(SkillNameModify(contents.get(position).contentW), TextView.BufferType.SPANNABLE);
            }else{
                patchNoteViewHolder.championW_linear.setVisibility(View.GONE);
            }

            if(contents.get(position).contentWW != null){
                patchNoteViewHolder.championWW_linear.setVisibility(View.VISIBLE);

                Glide.with(patchNoteViewHolder.itemView.getContext())
                        .load(championimage[position][5])
                        .apply(options)
                        .into(patchNoteViewHolder.championWW_ImageView);
                patchNoteViewHolder.championWW_Content_text.setText(SkillNameModify(contents.get(position).contentWW), TextView.BufferType.SPANNABLE);
            }else{
                patchNoteViewHolder.championWW_linear.setVisibility(View.GONE);
            }

            if(contents.get(position).contentE != null){
                patchNoteViewHolder.championE_linear.setVisibility(View.VISIBLE);

                if (contents.get(position).status.length() != 3){
                    Glide.with(patchNoteViewHolder.itemView.getContext())
                            .load(championimage[position][6])
                            .apply(options)
                            .into(patchNoteViewHolder.championE_ImageView);
                }else{
                    Glide.with(patchNoteViewHolder.itemView.getContext())
                            .load(championimage[position][13])
                            .apply(options)
                            .into(patchNoteViewHolder.championE_ImageView);
                }

                patchNoteViewHolder.championE_Content_text.setText(SkillNameModify(contents.get(position).contentE), TextView.BufferType.SPANNABLE);
            }else{
                patchNoteViewHolder.championE_linear.setVisibility(View.GONE);
            }

            if(contents.get(position).contentEE != null){
                patchNoteViewHolder.championEE_linear.setVisibility(View.VISIBLE);

                Glide.with(patchNoteViewHolder.itemView.getContext())
                        .load(championimage[position][7])
                        .apply(options)
                        .into(patchNoteViewHolder.championEE_ImageView);

                patchNoteViewHolder.championEE_Content_text.setText(SkillNameModify(contents.get(position).contentEE), TextView.BufferType.SPANNABLE);
            }else{
                patchNoteViewHolder.championEE_linear.setVisibility(View.GONE);
            }

            if(contents.get(position).contentR != null){
                patchNoteViewHolder.championR_linear.setVisibility(View.VISIBLE);

                if (contents.get(position).status.length() != 3){
                    Glide.with(patchNoteViewHolder.itemView.getContext())
                            .load(championimage[position][8])
                            .apply(options)
                            .into(patchNoteViewHolder.championR_ImageView);
                }else{
                    Glide.with(patchNoteViewHolder.itemView.getContext())
                            .load(championimage[position][14])
                            .apply(options)
                            .into(patchNoteViewHolder.championR_ImageView);
                }

                patchNoteViewHolder.championR_Content_text.setText(SkillNameModify(contents.get(position).contentR), TextView.BufferType.SPANNABLE);
            }else{
                patchNoteViewHolder.championR_linear.setVisibility(View.GONE);
            }

            if(contents.get(position).contentRR != null){
                patchNoteViewHolder.championRR_linear.setVisibility(View.VISIBLE);

                Glide.with(patchNoteViewHolder.itemView.getContext())
                        .load(championimage[position][9])
                        .apply(options)
                        .into(patchNoteViewHolder.championRR_ImageView);
                patchNoteViewHolder.championRR_Content_text.setText(SkillNameModify(contents.get(position).contentRR), TextView.BufferType.SPANNABLE);
            }else{
                patchNoteViewHolder.championRR_linear.setVisibility(View.GONE);
            }

            if(contents.get(position).status != null){
                patchNoteViewHolder.championStatus_Text.setVisibility(View.VISIBLE);

                String status;
                if(contents.get(position).status.length() == 3){
                    status = contents.get(position).status.substring(0,2);
                }else{
                    status = contents.get(position).status;
                }
                patchNoteViewHolder.championStatus_Text.setText(status);

                if (status.equals("버프") || status.equals("신규")) {
                    patchNoteViewHolder.championStatus_Text.setBackgroundResource(R.drawable.design_patch_status_green);
                } else if (status.equals("너프") || status.equals("삭제")) {
                    patchNoteViewHolder.championStatus_Text.setBackgroundResource(R.drawable.design_patch_status_red);
                } else if (status.equals("변경") || status.equals("수정")) {
                    patchNoteViewHolder.championStatus_Text.setBackgroundResource(R.drawable.design_patch_status_blue);
                }

            }else{
                patchNoteViewHolder.championStatus_Text.setVisibility(View.GONE);
            }

            patchNoteViewHolder.championName_Text.setText(contents.get(position).name);

        } else if(holder instanceof PatchNoteReplysViewHolder) {
            final PatchNoteReplysViewHolder patchNoteReplyViewHolder = (PatchNoteReplysViewHolder)holder;

            patchNoteReplyViewHolder.replys_nickname.setText(replys.get(position - contents_size).nickname);
            patchNoteReplyViewHolder.replys_content.setText(replys.get(position - contents_size).content);

            if(_timestamp(replys.get(position - contents_size).timestamp)){
                simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                long unixTime = (long) replys.get(position - contents_size).timestamp;
                Date date = new Date(unixTime);
                patchNoteReplyViewHolder.replys_timestamp.setText(simpleDateFormat.format(date));
            }else{
                simpleDateFormat2.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
                long unixTime = (long) replys.get(position - contents_size).timestamp;
                Date date = new Date(unixTime);
                patchNoteReplyViewHolder.replys_timestamp.setText(simpleDateFormat2.format(date));
            }

            if(replys.get(position - contents_size).rrp) {
                patchNoteReplyViewHolder.replys_icon_linear.setVisibility(View.VISIBLE);
                if(replys.get(position - contents_size).opname != null){
                    patchNoteReplyViewHolder.replys_oppname.setVisibility(View.VISIBLE);
                    patchNoteReplyViewHolder.replys_oppname.setText((replys.get(position - contents_size).opname));
                }else{
                    patchNoteReplyViewHolder.replys_oppname.setVisibility(View.GONE);
                }
            }else{
                patchNoteReplyViewHolder.replys_oppname.setVisibility(View.GONE);
                patchNoteReplyViewHolder.replys_icon_linear.setVisibility(View.GONE);
            }

            if(replys.get(position - contents_size).removereplys){
                if(replys.get(position - contents_size).rpEmpty){
                    patchNoteReplyViewHolder.linearLayout.setVisibility(View.GONE);
                    patchNoteReplyViewHolder.linearLayout2.setVisibility(View.VISIBLE);
                }
            }else{
                patchNoteReplyViewHolder.linearLayout.setVisibility(View.VISIBLE);
                patchNoteReplyViewHolder.linearLayout2.setVisibility(View.GONE);
            }

            patchNoteReplyViewHolder.menuMore_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MoreMenu(keys.get(position - contents_size),position - contents_size,replys.get(position - contents_size));
                }
            });
        }
    }

    private void MoreMenu(final String replysUid, final int position, final PatchNoteModel.Replys replys_1) {

        final Button remove_btn;
        Button report_btn;
        Button dialog_replys_button;

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
        report_btn =  view.findViewById(R.id.dialog_moreMenu_report);
        dialog_replys_button = view.findViewById(R.id.dialog_moreMenu_replys);

        final FirebaseUser firebaseUser;
        String current_uid = "";

        firebaseUser = firebaseAuth.getInstance().getCurrentUser();
        if(firebaseUser != null){
            current_uid = firebaseUser.getUid();
        }

        if(!current_uid.equals(replys_1.uid)){
            remove_btn.setVisibility(View.GONE);
        }

        dialog_replys_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    Toast.makeText(activity, "로그인이 필요한 서비스입니다.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(activity, LoginActivity.class);
                    activity.startActivity(intent);
                } else {
                    int stringSize;

                    ColorGenerator colorGenerator = ColorGenerator.MATERIAL;

                    Drawable d = TextDrawable.builder().buildRoundRect(replys.get(position).nickname,colorGenerator.getRandomColor(),10);
                    stringSize = checkWordCount(replys.get(position).nickname.toString()) * 18 + 30;
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

                        FirebaseUser firebaseUser_1 = firebaseAuth.getInstance().getCurrentUser();
                        userUid = firebaseUser_1.getUid();

                        if(!NetworkCheck.isNetworkCheck(activity)) {
                            Toast.makeText(activity, "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                        }else if(!firebaseUser_1.isEmailVerified()){
                            Toast.makeText(activity, "이메일 인증이 필요한 계정입니다. 'ABOUT PATO' 에서 메일을 전송하시길 바랍니다.", Toast.LENGTH_SHORT).show();
                        }else if(replys_edit.getText().toString().trim().length() > 300){
                            Toast.makeText(activity, "300자 이하로 작성해주세요.", Toast.LENGTH_SHORT).show();
                        }else if(!TextUtils.isEmpty(replys_edit.getText().toString().trim())){

                            replys_btn.setEnabled(false);

                            final AlertDialog alertDialog = LoadingDialog.loading_Diaglog(activity);

                            if(hasImageSpan(replys_edit)){

                                final PatchNoteModel.Replys patchreplys = new PatchNoteModel.Replys();

                                patchreplys.content = replys_edit.getText().toString().trim();
                                patchreplys.timestamp = ServerValue.TIMESTAMP;
                                patchreplys.uid = firebaseAuth.getCurrentUser().getUid();
                                patchreplys.nickname = firebaseAuth.getCurrentUser().getDisplayName();
                                patchreplys.rrp = true;
                                patchreplys.removereplys = false;

                                if(replys_1.rrp){
                                    patchreplys.opname = replys_1.nickname;
                                    patchreplys.part = replys_1.part;
                                    patchreplys.pId = replys_1.pId;
                                }else{
                                    patchreplys.part = replys_1.timestamp;
                                    patchreplys.pId = replysUid;
                                }

                                databaseReference.child("patchnotes").child(year).child(version).child("replys").push().setValue(patchreplys).addOnSuccessListener(activity, new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        databaseReference.child("patchnotes").child(year).child(version).child("replys").child(replysUid).runTransaction(new Transaction.Handler() {
                                            @NonNull
                                            @Override
                                            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                                PatchNoteModel.Replys replys = mutableData.getValue(PatchNoteModel.Replys.class);
                                                if(replys != null){
                                                    replys.rpEmpty = true;
                                                    mutableData.setValue(replys);
                                                }

                                                return Transaction.success(mutableData);
                                            }

                                            @Override
                                            public void onComplete(@Nullable DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {

                                            }
                                        });

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
                                                replyAlarm(replys.get(position).uid);
                                                replys_edit.setText("");
                                                replys_edit.clearFocus();

                                                ((PatchNoteActivity)activity).reGetdata = true;
                                                ((PatchNoteActivity)activity).replys_getData();

                                                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                                imm.hideSoftInputFromWindow(replys_edit.getWindowToken(), 0);
                                                alertDialog.dismiss();
                                                replys_btn.setEnabled(true);
                                            }
                                        });
                                    }
                                }).addOnFailureListener(activity, new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        alertDialog.dismiss();
                                        replys_btn.setEnabled(true);
                                        Toast.makeText(activity, "댓글 작성 실패", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }else{
                                PatchNoteModel.Replys patchreplys = new PatchNoteModel.Replys();

                                patchreplys.uid = firebaseAuth.getCurrentUser().getUid();
                                patchreplys.content = replys_edit.getText().toString().trim();
                                patchreplys.timestamp = ServerValue.TIMESTAMP;
                                patchreplys.nickname = firebaseAuth.getCurrentUser().getDisplayName();
                                patchreplys.removereplys = false;
                                patchreplys.part = patchreplys.timestamp;
                                patchreplys.rpEmpty = false;

                                databaseReference.child("patchnotes").child(year).child(version).child("replys").push().setValue(patchreplys).addOnSuccessListener(activity, new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        replys_edit.setText("");
                                        replys_edit.clearFocus();

                                        ((PatchNoteActivity)activity).reGetdata = true;
                                        ((PatchNoteActivity)activity).replys_getData();

                                        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                        imm.hideSoftInputFromWindow(replys_edit.getWindowToken(), 0);

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
                                                alertDialog.dismiss();
                                                replys_btn.setEnabled(true);
                                            }
                                        });

                                    }
                                }).addOnFailureListener(activity, new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        alertDialog.dismiss();
                                        replys_btn.setEnabled(true);
                                        Toast.makeText(activity, "댓글 작성 실패", Toast.LENGTH_SHORT).show();
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
                remove_replys_Diaglog(replysUid,replys_1);
                Optiondialog.dismiss();
            }
        });

        report_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReportReason(replysUid);
                Optiondialog.dismiss();
            }
        });
    }

    private void remove_replys_Diaglog(final String replyUid, final PatchNoteModel.Replys replys){

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
                        databaseReference.child("patchnotes").child(year).child(version).child("replys").child(replyUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    databaseReference.child("patchnotes").child(year).child(version).child("replys").orderByChild("pId").equalTo(replys.pId).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()){

                                            }else{
                                                databaseReference.child("patchnotes").child(year).child(version).child("replys").child(replys.pId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                        if(dataSnapshot.exists()){
                                                            PatchNoteModel.Replys replys1 = dataSnapshot.getValue(PatchNoteModel.Replys.class);
                                                            if(replys1.removereplys){
                                                                databaseReference.child("patchnotes").child(year).child(version).child("replys").child(replys.pId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                    }
                                                                });
                                                            }else{
                                                                replys1.rpEmpty = false;
                                                                databaseReference.child("patchnotes").child(year).child(version).child("replys").child(replys.pId).setValue(replys1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {

                                                                    }
                                                                });
                                                            }
                                                        }else{
                                                        }
                                                        databaseReference.removeEventListener(this);
                                                    }

                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError databaseError) {
                                                        Toast.makeText(activity, "오류가 발생하였습니다. 잠시 후 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                                        databaseReference.removeEventListener(this);
                                                    }
                                                });
                                            }
                                            databaseReference.removeEventListener(this);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(activity, "오류가 발생하였습니다. 잠시 후 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                            databaseReference.removeEventListener(this);
                                        }
                                    });

                                    databaseReference.child("patchnotes").child(year).child(version).child("readcount").runTransaction(new Transaction.Handler() {
                                        @NonNull
                                        @Override
                                        public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                            PatchNoteModel.readcount readcount = mutableData.getValue(PatchNoteModel.readcount.class);

                                            if(readcount != null){
                                                readcount.replyscount = readcount.replyscount - 1;
                                                mutableData.setValue(readcount);
                                            }else{

                                            }
                                            return Transaction.success(mutableData);
                                        }

                                        @Override
                                        public void onComplete(@Nullable final DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                            ((PatchNoteActivity)activity).reGetdata = true;
                                            ((PatchNoteActivity)activity).replys_getData();
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
                        if(replys.rpEmpty){
                            replys.removereplys = true;
                            databaseReference.child("patchnotes").child(year).child(version).child("replys").child(replyUid).setValue(replys).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        databaseReference.child("patchnotes").child(year).child(version).child("readcount").runTransaction(new Transaction.Handler() {
                                            @NonNull
                                            @Override
                                            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                                PatchNoteModel.readcount readcount = mutableData.getValue(PatchNoteModel.readcount.class);

                                                if(readcount != null){
                                                    readcount.replyscount = readcount.replyscount - 1;
                                                    mutableData.setValue(readcount);
                                                }else{

                                                }
                                                return Transaction.success(mutableData);
                                            }

                                            @Override
                                            public void onComplete(@Nullable final DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                ((PatchNoteActivity)activity).reGetdata = true;
                                                ((PatchNoteActivity)activity).replys_getData();
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
                            databaseReference.child("patchnotes").child(year).child(version).child("replys").child(replyUid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        databaseReference.child("patchnotes").child(year).child(version).child("readcount").runTransaction(new Transaction.Handler() {
                                            @NonNull
                                            @Override
                                            public Transaction.Result doTransaction(@NonNull MutableData mutableData) {
                                                PatchNoteModel.readcount readcount = mutableData.getValue(PatchNoteModel.readcount.class);

                                                if(readcount != null){
                                                    readcount.replyscount = readcount.replyscount - 1;
                                                    mutableData.setValue(readcount);
                                                }else{

                                                }
                                                return Transaction.success(mutableData);
                                            }

                                            @Override
                                            public void onComplete(@Nullable final DatabaseError databaseError, boolean b, @Nullable DataSnapshot dataSnapshot) {
                                                ((PatchNoteActivity)activity).reGetdata = true;
                                                ((PatchNoteActivity)activity).replys_getData();
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

    @Override
    public int getItemCount() {
        return contents_size + replys.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionReplys(position)) {
            return TYPE_ITEM;
        }
        return TYPE_REPLYS;
    }

    private boolean isPositionReplys(int position) {
        return position <= contents_size - 1;
    }

    private class PatchNoteViewHolder extends RecyclerView.ViewHolder {

        private ImageView champion_ImageView;
        private TextView championName_Text;
        private CharacterWrapTextView championStatus_Text;

        private LinearLayout championS_linear;
        private TextView championS_Content_text;

        private ImageView championP_ImageView;
        private LinearLayout championP_linear;
        private TextView championP_Content_text;

        private ImageView championQ_ImageView;
        private LinearLayout championQ_linear;
        private TextView championQ_Content_text;

        private ImageView championQQ_ImageView;
        private LinearLayout championQQ_linear;
        private TextView championQQ_Content_text;

        private ImageView championW_ImageView;
        private LinearLayout championW_linear;
        private TextView championW_Content_text;

        private ImageView championWW_ImageView;
        private LinearLayout championWW_linear;
        private TextView championWW_Content_text;

        private ImageView championE_ImageView;
        private LinearLayout championE_linear;
        private TextView championE_Content_text;

        private ImageView championEE_ImageView;
        private LinearLayout championEE_linear;
        private TextView championEE_Content_text;

        private ImageView championR_ImageView;
        private LinearLayout championR_linear;
        private TextView championR_Content_text;

        private ImageView championRR_ImageView;
        private LinearLayout championRR_linear;
        private TextView championRR_Content_text;



        public PatchNoteViewHolder(View view) {
            super(view);

            champion_ImageView = view.findViewById(R.id.item_patchnote_champion_imageview);
            championName_Text = view.findViewById(R.id.item_patchnote_champion_name);
            championStatus_Text = view.findViewById(R.id.item_patchnote_champion_status);

            championS_linear = view.findViewById(R.id.item_patchnote_championS_linear);
            championS_Content_text = view.findViewById(R.id.item_patchnote_championS_content);

            championP_linear = view.findViewById(R.id.item_patchnote_championP_linear);
            championP_ImageView = view.findViewById(R.id.item_patchnote_championP_imageview);
            championP_Content_text = view.findViewById(R.id.item_patchnote_championP_content);

            championQ_linear = view.findViewById(R.id.item_patchnote_championQ_linear);
            championQ_ImageView =  view.findViewById(R.id.item_patchnote_championQ_imageview);
            championQ_Content_text = view.findViewById(R.id.item_patchnote_championQ_content);

            championQQ_linear = view.findViewById(R.id.item_patchnote_championQQ_linear);
            championQQ_ImageView =  view.findViewById(R.id.item_patchnote_championQQ_imageview);
            championQQ_Content_text = view.findViewById(R.id.item_patchnote_championQQ_content);

            championW_linear = view.findViewById(R.id.item_patchnote_championW_linear);
            championW_ImageView =  view.findViewById(R.id.item_patchnote_championW_imageview);
            championW_Content_text =  view.findViewById(R.id.item_patchnote_championW_content);

            championWW_linear = view.findViewById(R.id.item_patchnote_championWW_linear);
            championWW_ImageView =  view.findViewById(R.id.item_patchnote_championWW_imageview);
            championWW_Content_text =  view.findViewById(R.id.item_patchnote_championWW_content);

            championE_linear =  view.findViewById(R.id.item_patchnote_championE_linear);
            championE_ImageView =  view.findViewById(R.id.item_patchnote_championE_imageview);
            championE_Content_text =  view.findViewById(R.id.item_patchnote_championE_content);

            championEE_linear =  view.findViewById(R.id.item_patchnote_championEE_linear);
            championEE_ImageView =  view.findViewById(R.id.item_patchnote_championEE_imageview);
            championEE_Content_text =  view.findViewById(R.id.item_patchnote_championEE_content);

            championR_linear = view.findViewById(R.id.item_patchnote_championR_linear);
            championR_ImageView = view.findViewById(R.id.item_patchnote_championR_imageview);
            championR_Content_text = view.findViewById(R.id.item_patchnote_championR_content);

            championRR_linear = view.findViewById(R.id.item_patchnote_championRR_linear);
            championRR_ImageView = view.findViewById(R.id.item_patchnote_championRR_imageview);
            championRR_Content_text = view.findViewById(R.id.item_patchnote_championRR_content);



        }
    }

    public class PatchNoteReplysViewHolder extends RecyclerView.ViewHolder {

        private TextView replys_nickname;
        private TextView replys_timestamp;
        private TextView replys_oppname;
        private CharacterWrapTextView replys_content;
        private ImageButton menuMore_btn;
        private LinearLayout linearLayout, linearLayout2, replys_icon_linear;


        public PatchNoteReplysViewHolder(View view) {
            super(view);

            replys_nickname = view.findViewById(R.id.patchnoteActivity_replys_nickname);
            replys_timestamp =  view.findViewById(R.id.patchnoteActivity_replys_timestamp);
            replys_content = view.findViewById(R.id.patchnoteActivity_replys_content);
            menuMore_btn = view.findViewById(R.id.patchnoteActivity_moreMenu_btn);
            linearLayout = view.findViewById(R.id.patchnoteActivity_replys_linear);
            linearLayout2 = view.findViewById(R.id.patchnoteActivity_replys_linear2);
            replys_icon_linear = view.findViewById(R.id.patchnoteActivity_replys_iV);
            replys_oppname = view.findViewById(R.id.patchnoteActivity_opponickname);

        }
    }


    public boolean hasImageSpan(EditText editText) {
        Editable text  = editText.getEditableText();
        ImageSpan[] spans = text.getSpans(0, text.length(), ImageSpan.class);
        return !(spans.length == 0);
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

    private void Report(String replysUid, String reason, final AlertDialog alertDialog){
        ReportModel reportModel = new ReportModel();

        TimeZone tz;
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        tz = TimeZone.getTimeZone("Asia/Seoul");
        df.setTimeZone(tz);

        reportModel.uid = firebaseAuth.getCurrentUser().getUid();
        reportModel.bid = version;
        reportModel.time = df.format(date);
        reportModel.rs = reason;

        if(replysUid != null || !replysUid.equals("") || replysUid.length() > 0){
            reportModel.repU = replysUid;
        }

        databaseReference.child("report").push().setValue(reportModel).addOnSuccessListener(activity, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(activity, "신고가 성공적으로 접수 되었습니다.", Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        }).addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(activity, "신고 실패", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void ReportReason(final String replysUid){

        RadioGroup radioGroup;
        RadioButton rb1, rb2, rb3;
        Button report_btn;

        final String[] reason = {""};

        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_report_radiobutton_xml,null);

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final AlertDialog Optiondialog = builder.create();

        Optiondialog.setView(view);
        Optiondialog.setCancelable(true);
        Optiondialog.show();

        radioGroup = view.findViewById(R.id.dialog_report_radioGroup);
        rb1 = view.findViewById(R.id.dialog_report_rb1);
        rb2 = view.findViewById(R.id.dialog_report_rb2);
        rb3 = view.findViewById(R.id.dialog_report_rb3);
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
                        Report(replysUid,reason[0],Optiondialog);
                    }

                }

            }
        });
    }

    private void replyAlarm(String replysUid){

        FirebaseFirestore.getInstance().collection("users").document(replysUid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists()){
                        String uid = documentSnapshot.getString("uid");
                        if(!uid.equals(userUid)){
                            if(documentSnapshot.getString("pushToken") != null){
                                sendGcm(version,documentSnapshot.getString("pushToken"));
                            }
                        }
                    }
                }
            }
        });
    }

    private void sendGcm(String patchNoteUid, String pushToken){
        Gson gson = new Gson();

        String userName = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();

        NotificationPatchModel notificationPatchModel = new NotificationPatchModel();
        notificationPatchModel.to = pushToken;
        notificationPatchModel.data.writer = userName;
        notificationPatchModel.data.noteVersion = patchNoteUid;
        notificationPatchModel.data.year = year;
        notificationPatchModel.data.title = title;

        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf8"),gson.toJson(notificationPatchModel));

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

    private SpannableStringBuilder SkillNameModify(String text) {

        text = text.replace(" ", "\u00A0").replace("-","\u2011");

        int startIndex = text.indexOf("^");
        int lastIndex = text.indexOf("&");

        text = text.replace("^","");
        text = text.replace("&","");

        SpannableStringBuilder sp = new SpannableStringBuilder(text);

        if((startIndex+lastIndex) > 1){
            sp.setSpan(new AbsoluteSizeSpan(20, true), startIndex, lastIndex,0);
            sp.setSpan(new StyleSpan(Typeface.BOLD),startIndex,lastIndex,0);
        }

        return sp;
    }
}

