package com.example.pato;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.legacy.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
import com.example.pato.customclass.LongTypeGetTime;
import com.example.pato.customclass.NetworkCheck;
import com.example.pato.customclass.RandomWord;
import com.example.pato.model.BoardModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BoardWriteActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 8;

    private static final int PICK_FROM_ALBUM = 10;
    private RequestOptions options = new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL).error(R.drawable.error_icon).centerCrop();

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;

    private ClearEditText title_edit;
    private EditText content_edit;
    private ImageView imageView_1;
    private RecyclerView recyclerView;
    private Menu Mmenu;
    private Spinner spinner;
    private LinearLayout linearLayout;
    private AlertDialog alertDialog;
    private beforeImageViewAdapter beforeImageViewAdapter = new beforeImageViewAdapter();

    private String board_category = "lol";
    private List<String> imageview = new ArrayList<>();
    private List<String> imagesize = new ArrayList<>();
    private List<String> imageurl = new ArrayList<>();
    private List<String> imagepath = new ArrayList<>();
    private InputMethodManager imm;
    private FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_write);

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("Board_Image");

        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("board");

        title_edit = findViewById(R.id.BoardWrite_title_edit);
        content_edit = findViewById(R.id.BoardWrite_content_edit);
        imageView_1 = findViewById(R.id.BoardWrite_before_imageview);
        recyclerView = findViewById(R.id.BoardWrite_recyclerview);
        spinner =  findViewById(R.id.BoardWrite_spinner);
        linearLayout =  findViewById(R.id.BoardWrite_contents_linear);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                content_edit.requestFocus();
                imm.showSoftInput(content_edit,0);
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        imageView_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageView_1.setEnabled(false);
                if(imageview.size() > 4){
                    Toast.makeText(BoardWriteActivity.this, "최대 5개까지 업로드 가능 합니다.", Toast.LENGTH_SHORT).show();
                }else{
                    if(!NetworkCheck.isNetworkCheck(getApplicationContext())){
                        Toast.makeText(BoardWriteActivity.this, "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                    }else{
                        Permission();
                        imageView_1.setEnabled(true);
                    }
                }
            }
        });

        spinner();
        ActionBar();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_boardwrite,menu);
        Mmenu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    void ActionBar(){
        ActionBar actionBar_pato = getSupportActionBar() ;
        actionBar_pato.setTitle("글 작성") ;

        title_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(title_edit.getText().toString().length() > 50){
                    Toast.makeText(BoardWriteActivity.this, "제목은 최대 50자까지 작성 가능합니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        
        content_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(content_edit.getText().toString().length() > 500){
                    Toast.makeText(BoardWriteActivity.this, "내용은 최대 500자까지 작성 가능합니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.boardWrite_success:
                if(!NetworkCheck.isNetworkCheck(getApplicationContext())){
                    Toast.makeText(BoardWriteActivity.this, "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                }else if(title_edit.getText().toString().trim().length() > 50 && content_edit.getText().toString().trim().length() > 500){
                    Toast.makeText(this, "제목은 50자, 내용은 500자까지 작성 가능합니다.", Toast.LENGTH_SHORT).show();
                } else if(!TextUtils.isEmpty(title_edit.getText().toString().trim()) && !TextUtils.isEmpty(content_edit.getText().toString().trim())){

                    item.setEnabled(false);

                    alertDialog = LoadingDialog.loading_Diaglog(BoardWriteActivity.this);

                    final BoardModel.Board boardModel = new BoardModel.Board();

                    boardModel.title = title_edit.getText().toString().trim();
                    boardModel.content = content_edit.getText().toString().trim();
                    boardModel.nickname = firebaseAuth.getInstance().getCurrentUser().getDisplayName();
                    boardModel.category = board_category;
                    boardModel.uid = firebaseAuth.getInstance().getCurrentUser().getUid();
                    boardModel.readcount = 0;
                    boardModel.replyscount = 0;
                    boardModel.timestamp = LongTypeGetTime.getTime();

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
                                boardUpload(boardModel,item);
                            }
                        }
                    }else{
                        boardUpload(boardModel,item);
                    }

                }else{
                    if(TextUtils.isEmpty(title_edit.getText().toString().trim())){
                        Toast.makeText(getApplicationContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        title_edit.requestFocus();
                    }else{
                        Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
                        content_edit.requestFocus();
                    }
                    item.setEnabled(true);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        if(requestCode == PICK_FROM_ALBUM && resultCode == RESULT_OK){

            Mmenu.findItem(R.id.boardWrite_success).setEnabled(false);
            imageView_1.setEnabled(false);

            imageview.add("progress");
            recyclerView.setAdapter(beforeImageViewAdapter);
            LinearLayoutManager layoutManager = new LinearLayoutManager(BoardWriteActivity.this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);

            final String filePath = getPathFromUri(data.getData());
            String extension = filePath.substring(filePath.lastIndexOf(".") + 1);

            final File file = new File(filePath);
            long fileSize = file.length();

            if(fileSize > 5242880 ){
                Toast.makeText(this, "5Mb 이하만 업로드 가능합니다.", Toast.LENGTH_SHORT).show();
                Mmenu.findItem(R.id.boardWrite_success).setEnabled(true);
                imageView_1.setEnabled(true);
                return;
            }else{
                if(extension.equals("jpg") || extension.equals("png") || extension.equals("jpeg")){

                    final String randomWord = "pato_" + RandomWord.getRandomWord(3) + "_" + System.currentTimeMillis();

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
                                            imageview.add(String.valueOf(data.getData()));
                                            imagesize.add(ImageSize.ImageSizeMethod(filePath, file));
                                            beforeImageViewAdapter.notifyDataSetChanged();
                                        }else{
                                            Toast.makeText(BoardWriteActivity.this, "올린 파일을 불러 올 수 없습니다.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }else{
                                Toast.makeText(BoardWriteActivity.this, "파일을 올릴 수 없습니다.", Toast.LENGTH_SHORT).show();
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
                    storageReference.child(imagepath.get(position)).delete().addOnSuccessListener(BoardWriteActivity.this, new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            imageview.remove(position);
                            imagesize.remove(position);
                            imagepath.remove(position);
                            imageurl.remove(position);

                            Mmenu.findItem(R.id.boardWrite_success).setEnabled(true);
                            notifyDataSetChanged();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Mmenu.findItem(R.id.boardWrite_success).setEnabled(true);
                            notifyDataSetChanged();
                        }
                    });
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

                imageView =  view.findViewById(R.id.BoardWrite_before_imageview);
                progressBar = view.findViewById(R.id.BoardWrite_before_progress);

            }
        }
    }


    @Override
    public void onBackPressed() {
        ExitMessage();
    }

    private void boardUpload(final BoardModel.Board boardModel, final MenuItem MenuItem){

        collectionReference.add(boardModel).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent();
                    intent.putExtra("onresume","onresume");
                    setResult(Activity.RESULT_OK,intent);
                    BoardWriteActivity.this.finish();
                    MenuItem.setEnabled(true);
                }else{
                    Toast.makeText(BoardWriteActivity.this, "잠시 후 다시 이용해주세요.", Toast.LENGTH_SHORT).show();
                    MenuItem.setEnabled(true);
                    alertDialog.dismiss();
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

    private void ExitMessage() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(BoardWriteActivity.this);
        builder.setMessage("글 작성을 그만 하시겠습니까?").setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int k) {
                if(!imageview.isEmpty()){
                    for(int i = 0; i < imageview.size(); i++){
                        final int finalI = i;
                        storageReference.child(imagepath.get(i)).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(!task.isSuccessful()){
                                    Toast.makeText(BoardWriteActivity.this, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                                    dialogInterface.dismiss();
                                    BoardWriteActivity.this.finish();
                                }

                                if(finalI == imageview.size() - 1){
                                    dialogInterface.dismiss();
                                    BoardWriteActivity.this.finish();
                                }
                            }
                        });
                    }
                }else{
                    dialogInterface.dismiss();
                    BoardWriteActivity.this.finish();
                }
            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        }).create().show();

    }

    public String getPathFromUri(Uri uri){

        Cursor cursor = getContentResolver().query(uri, null, null, null, null );
        cursor.moveToNext();
        String path = cursor.getString( cursor.getColumnIndex( "_data" ) );
        cursor.close();

        return path;
    }

    private void Permission(){

        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);

        if(permissionCheck == PackageManager.PERMISSION_DENIED){
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {

                // 이 권한을 필요한 이유를 설명해야하는가?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    // 다이어로그같은것을 띄워서 사용자에게 해당 권한이 필요한 이유에 대해 설명합니다

                    // 해당 설명이 끝난뒤 requestPermissions()함수를 호출하여 권한허가를 요청해야 합니다
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                } else {

                    final AlertDialog.Builder builder = new AlertDialog.Builder(BoardWriteActivity.this);
                    builder.setMessage("권한이 없습니다. 권한을 설정하러 가시겠습니까?")
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                            .setData(Uri.parse("package:" + BoardWriteActivity.this.getPackageName()));
                                    startActivity(intent);
                                }
                            }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    }).create().show();
                }
            }
        }else{
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
            startActivityForResult(intent,PICK_FROM_ALBUM);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                    startActivityForResult(intent,PICK_FROM_ALBUM);
                } else {

                }
                return;
        }
    }

}
