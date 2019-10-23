package com.example.pato;

import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pato.adapter.ChampionNoteRecyclerViewAdapter;
import com.example.pato.customclass.ChampionDialog;
import com.example.pato.customclass.NetworkCheck;
import com.example.pato.model.ImagesModel;
import com.example.pato.model.PatchNoteModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ChampionNoteActivity extends AppCompatActivity {

    public RecyclerView championNote_recyclerView;
    public String champion_Name = "가렌";
    private String year_choice = "2018";
    private List<String> patchKeyList = new ArrayList<>();
    private List<PatchNoteModel.Contents> contents = new ArrayList<>();

    private ImagesModel imagesModel;

    private TextView champion_isEmpty_view;

    private ActionBar actionBar;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_champion_note);

        championNote_recyclerView =  findViewById(R.id.championNoteActivity_recyclerview);
        champion_isEmpty_view = findViewById(R.id.championNoteActivity_isempty_text);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        actionBar = getSupportActionBar();
        actionBar.setTitle(champion_Name +"의 패치노트") ;

        if(!NetworkCheck.isNetworkCheck(getApplicationContext())) {
            Toast.makeText(ChampionNoteActivity.this, "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
            ChampionNoteActivity.this.finish();
        }

        if(getIntent().getStringExtra("champion_Name") != null){
            champion_Name = getIntent().getStringExtra("champion_Name");
        }

        getData();
    }

    public void getData(){

        patchKeyList.clear();
        contents.clear();

        databaseReference.child("images").child(champion_Name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    imagesModel = dataSnapshot.getValue(ImagesModel.class);

                    if(imagesModel.patchKey != null){

                        final String patchKey[] = imagesModel.patchKey.split(",");

                        for(int i = 0; i < patchKey.length; i++){
                            final int finalI = i;

                            patchKeyList.add(patchKey[i]);

                            databaseReference.child("patchnotes").child(year_choice).child(patchKey[i]).child("contents").orderByChild("name").equalTo(champion_Name).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if(dataSnapshot.exists()){

                                        contents.add(dataSnapshot.getChildren().iterator().next().getValue(PatchNoteModel.Contents.class));

                                        if(finalI == patchKey.length - 1){
                                            champion_isEmpty_view.setVisibility(View.GONE);

                                            Collections.reverse(patchKeyList);
                                            Collections.reverse(contents);

                                            championNote_recyclerView.setLayoutManager(new LinearLayoutManager(ChampionNoteActivity.this));
                                            championNote_recyclerView.setAdapter(new ChampionNoteRecyclerViewAdapter(champion_Name,year_choice,ChampionNoteActivity.this,champion_isEmpty_view,contents,patchKeyList,imagesModel));
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    Toast.makeText(ChampionNoteActivity.this, "오류가 발생하였습니다. 잠시 후 시도해 주세요.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }else{
                        champion_isEmpty_view.setVisibility(View.VISIBLE);
                        championNote_recyclerView.setLayoutManager(new LinearLayoutManager(ChampionNoteActivity.this));
                        championNote_recyclerView.setAdapter(new ChampionNoteRecyclerViewAdapter(champion_Name,year_choice,ChampionNoteActivity.this,champion_isEmpty_view,contents,patchKeyList,imagesModel));
                    }

                }else{
                    champion_isEmpty_view.setVisibility(View.VISIBLE);
                    championNote_recyclerView.setLayoutManager(new LinearLayoutManager(ChampionNoteActivity.this));
                    championNote_recyclerView.setAdapter(new ChampionNoteRecyclerViewAdapter(champion_Name,year_choice,ChampionNoteActivity.this,champion_isEmpty_view,contents,patchKeyList,imagesModel));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ChampionNoteActivity.this, "오류가 발생하였습니다. 잠시 후 시도해 주세요.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_championlist,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.championNoteActivity_championlist_btn:
                item.setEnabled(false);

                if(!NetworkCheck.isNetworkCheck(getApplicationContext())) {
                    Toast.makeText(ChampionNoteActivity.this, "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    ChampionDialog championDialog = new ChampionDialog(this,championNote_recyclerView,ChampionNoteActivity.this,actionBar,champion_isEmpty_view);
                    championDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    championDialog.show();
                    item.setEnabled(true);
                }
        }

        return super.onOptionsItemSelected(item);
    }


}
