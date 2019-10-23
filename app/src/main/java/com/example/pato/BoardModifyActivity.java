package com.example.pato;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.pato.adapter.BoardSpinnerAdapter;
import com.example.pato.customclass.ClearEditText;
import com.example.pato.customclass.ImageSize;
import com.example.pato.customclass.LoadingDialog;
import com.example.pato.customclass.NetworkCheck;
import com.example.pato.customclass.RandomWord;
import com.example.pato.model.BoardModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BoardModifyActivity extends AppCompatActivity {

    private static final int PICK_FROM_ALBUM = 10;
    private RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.error_icon).centerCrop();

    private FirebaseAuth firebaseAuth;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;

    private ClearEditText title_edit;
    private EditText content_edit;
    private LinearLayout linearLayout;
    private LinearLayout linearLayout_1;
    private ImageView imageView_1;
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private Menu Mmenu;
    private Spinner spinner;
    private AlertDialog alertDialog;
    private beforeImageViewAdapter beforeImageViewAdapter = new beforeImageViewAdapter();

    private List<String> imageview = new ArrayList<>();
    private List<String> imagesize = new ArrayList<>();
    private List<String> imagepath = new ArrayList<>();
    private List<String> imagepath_new = new ArrayList<>();
    private List<String> imagepath_delete = new ArrayList<>();
    private List<String> imageurl = new ArrayList<>();
    private String board_category = "";
    private String boardUid;
    private int firstSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_modify);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("Board_Image");

        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("board");

        title_edit = findViewById(R.id.BoardModify_title_edit);
        content_edit = findViewById(R.id.BoardModify_content_edit);
        recyclerView = findViewById(R.id.BoardModify_recyclerview);
        linearLayout_1 =  findViewById(R.id.BoardModify_contents_linear);
        imageView_1 = findViewById(R.id.BoardModify_before_imageview);
        spinner = findViewById(R.id.BoardModify_spinner);

        boardUid = getIntent().getStringExtra("bid");

        linearLayout_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                content_edit.requestFocus();
            }
        });

        imageView_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(imageview.size() > 4){
                    Toast.makeText(BoardModifyActivity.this, "최대 5개까지 업로드 가능 합니다.", Toast.LENGTH_SHORT).show();
                }else{
                    if(!NetworkCheck.isNetworkCheck(getApplicationContext())){
                        Toast.makeText(BoardModifyActivity.this, "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                    }else{
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                        startActivityForResult(intent,PICK_FROM_ALBUM);
                    }

                }
            }
        });

        spinner();
        getData();
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
    }

    private void getData(){

        collectionReference.document(boardUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){

                    BoardModel.Board boardModel = documentSnapshot.toObject(BoardModel.Board.class);
                    title_edit.setText(boardModel.title);
                    content_edit.setText(boardModel.content);
                    board_category = boardModel.category.substring(0,3);

                    if(board_category.equals("lol")){
                        spinner.setSelection(0);
                    }else if(board_category.equals("con")){
                        spinner.setSelection(1);
                    }else if(board_category.equals("fre")){
                        spinner.setSelection(2);
                    }

                    if(boardModel.bI != null){
                        imageurl.add(boardModel.bI);
                        imageview.add(boardModel.bI);
                        imagesize.add(boardModel.bIS);
                    }
                    if(boardModel.bI2 != null){
                        imageurl.add(boardModel.bI2);
                        imageview.add(boardModel.bI2);
                        imagesize.add(boardModel.bIS2);
                    }
                    if(boardModel.bI3 != null){
                        imageurl.add(boardModel.bI3);
                        imageview.add(boardModel.bI3);
                        imagesize.add(boardModel.bIS3);
                    }
                    if(boardModel.bI4 != null){
                        imageurl.add(boardModel.bI4);
                        imageview.add(boardModel.bI4);
                        imagesize.add(boardModel.bIS4);
                    }
                    if(boardModel.bI5 != null){
                        imageurl.add(boardModel.bI5);
                        imageview.add(boardModel.bI5);
                        imagesize.add(boardModel.bIS5);
                    }

                    firstSize = imageurl.size();
                    recyclerView.setAdapter(new beforeImageViewAdapter());
                    recyclerView.setLayoutManager(layoutManager);
                    content_edit.requestFocus(boardModel.content.length());

                }else{

                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(BoardModifyActivity.this, "오류가 발생하였습니다. 잠시 후 시도해 주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        ActionBar();
    }

    void ActionBar(){
        ActionBar actionBar_pato = getSupportActionBar() ;
        actionBar_pato.setTitle("글 수정") ;

        title_edit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {

                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (i == KeyEvent.KEYCODE_ENTER)) {
                    content_edit.requestFocus(content_edit.getText().toString().trim().length());
                    return true;
                }
                return false;
            }
        });

        content_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(content_edit.getText().toString().length() > 500){
                    Toast.makeText(BoardModifyActivity.this, "내용은 최대 500자까지 작성 가능합니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        title_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(title_edit.getText().toString().length() > 50){
                    Toast.makeText(BoardModifyActivity.this, "제목은 최대 50자까지 작성 가능합니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_boardwrite,menu);
        Mmenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.boardWrite_success:
                if(!NetworkCheck.isNetworkCheck(getApplicationContext())){
                    Toast.makeText(BoardModifyActivity.this, "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                }else if(title_edit.getText().toString().trim().length() > 50 && content_edit.getText().toString().trim().length() > 500){
                    Toast.makeText(this, "제목은 50자, 내용은 500자까지 작성 가능합니다.", Toast.LENGTH_SHORT).show();
                }else if(!TextUtils.isEmpty(title_edit.getText().toString().trim()) && !TextUtils.isEmpty(content_edit.getText().toString().trim()) ){

                    collectionReference.document(boardUid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()){
                                alertDialog = LoadingDialog.loading_Diaglog(BoardModifyActivity.this);

                                BoardModel.Board boardModel = documentSnapshot.toObject(BoardModel.Board.class);

                                boardModel.title = title_edit.getText().toString();
                                boardModel.content = content_edit.getText().toString();
                                boardModel.category = board_category + boardModel.category.substring(3);

                                if(firstSize != imageurl.size()){
                                    boardModelReset(boardModel);
                                }

                                if(!imageurl.isEmpty()){

                                    for(int i = 0; i < imageurl.size(); i++){
                                        final int finalI = i;

                                        if(finalI == 0){
                                            boardModel.bI = imageurl.get(0);
                                            boardModel.bIS = imagesize.get(0);
                                        }else if(finalI == 1){
                                            boardModel.bI2 = imageurl.get(1);
                                            boardModel.bIS2 = imagesize.get(1);
                                        }else if(finalI == 2){
                                            boardModel.bI3 = imageurl.get(2);
                                            boardModel.bIS3 = imagesize.get(2);
                                        }else if(finalI == 3){
                                            boardModel.bI4 = imageurl.get(3);
                                            boardModel.bIS4 = imagesize.get(3);
                                        }else if(finalI == 4){
                                            boardModel.bI5 = imageurl.get(4);
                                            boardModel.bIS5 = imagesize.get(4);
                                        }

                                        if(finalI == imageurl.size()-1){
                                            boardUpload(boardModel);
                                        }
                                    }
                                }else{
                                    boardUpload(boardModel);
                                }
                            }else{
                                Toast.makeText(BoardModifyActivity.this, "게시물이 존재하지 않습니다.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent();
                                intent.putExtra("onresume","delete");
                                setResult(Activity.RESULT_OK,intent);
                                BoardModifyActivity.this.finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(BoardModifyActivity.this, "오류가 발생하였습니다. 잠시 후 시도해 주세요.", Toast.LENGTH_SHORT).show();
                        }
                    });

                }else{
                    if(TextUtils.isEmpty(title_edit.getText().toString().trim())){
                        Toast.makeText(getApplicationContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        title_edit.requestFocus();
                    }else{
                        Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        content_edit.requestFocus();
                    }
                }
        }

        return super.onOptionsItemSelected(item);
    }

    private void boardModelReset(BoardModel.Board boardModel) {

        boardModel.bI = null;
        boardModel.bIS = null;

        boardModel.bI2 = null;
        boardModel.bIS2 = null;

        boardModel.bI3 = null;
        boardModel.bIS3 = null;

        boardModel.bI4 = null;
        boardModel.bIS4 = null;

        boardModel.bI5 = null;
        boardModel.bIS5 = null;
    }

    private class beforeImageViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.item_before_imageview, parent, false);
            return new BeforeImageViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            BeforeImageViewHolder beforeImageViewHolder = (BeforeImageViewHolder)holder;

            int imageviewSize = imageview.size();

            if(imageview.get(imageviewSize-1).equals("progress") && position == imageviewSize-1){
                beforeImageViewHolder.progressBar.setVisibility(View.VISIBLE);
                beforeImageViewHolder.imageView.setVisibility(View.GONE);
            }else{
                beforeImageViewHolder.progressBar.setVisibility(View.GONE);
                beforeImageViewHolder.imageView.setVisibility(View.VISIBLE);
                Glide.with(getApplicationContext()).load(imageview.get(position)).apply(options).into(beforeImageViewHolder.imageView);
            }

            beforeImageViewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Mmenu.findItem(R.id.boardWrite_success).setEnabled(false);

                    imagepath_delete.add(imagepath.get(position));
                    imageview.remove(position);
                    imagesize.remove(position);
                    imagepath.remove(position);
                    imageurl.remove(position);

                    Mmenu.findItem(R.id.boardWrite_success).setEnabled(true);
                    notifyDataSetChanged();

                }
            });
        }


        @Override
        public int getItemCount() {
            return imageview.size();
        }

        private class BeforeImageViewHolder extends RecyclerView.ViewHolder {

            private ImageView imageView;
            private ProgressBar progressBar;

            public BeforeImageViewHolder(View view) {
                super(view);

                imageView = (ImageView) view.findViewById(R.id.BoardWrite_before_imageview);
                progressBar = view.findViewById(R.id.BoardWrite_before_progress);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if(requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK){

            Mmenu.findItem(R.id.boardWrite_success).setEnabled(false);
            imageView_1.setEnabled(false);
            imageview.add("progress");

            recyclerView.setAdapter(beforeImageViewAdapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(BoardModifyActivity.this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);

            final String filePath = getPathFromUri(data.getData());
            String extension = filePath.substring(filePath.lastIndexOf(".") + 1,filePath.length());

            final File file = new File(filePath);
            long fileSize = file.length();

            if(fileSize > 5242880 ){
                Toast.makeText(this, "5Mb 이하만 업로드 가능합니다.", Toast.LENGTH_SHORT).show();
                Mmenu.findItem(R.id.boardWrite_success).setEnabled(true);
                imageView_1.setEnabled(true);
                return;
            }else{
                if(extension.equals("jpg") || extension.equals("png") || extension.equals("jpeg")){

                    final String randomWord = "pato_" + RandomWord.getRandomWord(25) + "_";

                    storageReference.child(randomWord).putFile(Uri.parse(String.valueOf(data.getData()))).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            imageview.remove(imageview.size()-1);

                            if(task.isSuccessful()){

                                storageReference.child(randomWord).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if(task.isSuccessful()){
                                            String  imageUri = task.getResult().toString();
                                            imageurl.add(imageUri.substring(83));
                                            imagepath.add(randomWord);
                                            imagepath_new.add(randomWord);
                                            imageview.add(String.valueOf(data.getData()));
                                            imagesize.add(ImageSize.ImageSizeMethod(filePath,file));
                                            beforeImageViewAdapter.notifyDataSetChanged();
                                        }else{
                                            Toast.makeText(BoardModifyActivity.this, "올린 파일을 불러 올 수 없습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else{
                                Toast.makeText(BoardModifyActivity.this, "파일을 올릴 수 없습니다.", Toast.LENGTH_SHORT).show();
                            }
                            Mmenu.findItem(R.id.boardWrite_success).setEnabled(true);
                            imageView_1.setEnabled(true);
                        }
                    });


                }else{
                    Toast.makeText(this, "jpg, jpeg, png 형식의 이미지 파일만 업로드 가능합니다.", Toast.LENGTH_SHORT).show();
                    Mmenu.findItem(R.id.boardWrite_success).setEnabled(true);
                    imageView_1.setEnabled(true);
                    return;
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        ExitMessage();
    }

    private void ExitMessage() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(BoardModifyActivity.this);
        builder.setMessage("글 수정을 그만 하시겠습니까?").setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int k) {

                if(!imagepath_new.isEmpty()){
                    for(int i = 0; i< imagepath_new.size(); i++){
                        final int finalI = i;
                        storageReference.child(imagepath_new.get(i)).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful()){
                                    Toast.makeText(BoardModifyActivity.this, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                                    dialogInterface.dismiss();
                                    finish();
                                }
                                if(finalI == imagepath_new.size() - 1){
                                    dialogInterface.dismiss();
                                    finish();
                                }
                            }
                        });
                    }
                }else{
                    dialogInterface.dismiss();
                    finish();
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
    protected void onDestroy() {
        super.onDestroy();
    }

    public String getPathFromUri(Uri uri){

        Cursor cursor = getContentResolver().query(uri, null, null, null, null );
        cursor.moveToNext();
        String path = cursor.getString( cursor.getColumnIndex( "_data" ) );
        cursor.close();

        return path;
    }

    private void boardUpload(BoardModel.Board boardModel){

        collectionReference.document(boardUid).set(boardModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    if(!imagepath_delete.isEmpty()){
                        for(int i = 0; i < imagepath_delete.size(); i++){
                            final int finalI = i;
                            storageReference.child(imagepath_delete.get(i)).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        if(finalI == imagepath_delete.size() - 1 ){
                                            Intent intent = new Intent();
                                            intent.putExtra("onresume","onresume");
                                            setResult(Activity.RESULT_OK,intent);

                                            alertDialog.dismiss();
                                            BoardModifyActivity.this.finish();
                                        }
                                    }else{
                                        Toast.makeText(BoardModifyActivity.this, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });
                        }
                    }else {
                        Intent intent = new Intent();
                        intent.putExtra("onresume","onresume");
                        setResult(Activity.RESULT_OK,intent);

                        alertDialog.dismiss();
                        BoardModifyActivity.this.finish();
                    }

                }else{
                    alertDialog.dismiss();
                    Toast.makeText(BoardModifyActivity.this, "잠시 후 다시 이용해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }





    private void spinner(){
        final ArrayList<String> list = new ArrayList<>();
        list.add("롤 게시판");
        list.add("대회 게시판");
        list.add("자유 게시판");

        BoardSpinnerAdapter boardSpinnerAdapter = new BoardSpinnerAdapter(this,list);
        spinner.setAdapter(boardSpinnerAdapter);
        spinner.setGravity(Gravity.CENTER_HORIZONTAL);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    board_category = "lol";
                }else if(position == 1){
                    board_category = "con";
                }else {
                    board_category = "fre";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
