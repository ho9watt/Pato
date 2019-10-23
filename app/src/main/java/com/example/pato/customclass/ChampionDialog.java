package com.example.pato.customclass;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.pato.ChampionNoteActivity;
import com.example.pato.R;
import com.example.pato.adapter.ChampionSpinnerAdapter;

public class ChampionDialog extends Dialog {

    private ChampionSpinnerAdapter championSpinnerAdapter,championSpinnerAdapter2;

    private Spinner conson,name;
    private RecyclerView recyclerView;
    private Activity activity;
    private Context context;
    private androidx.appcompat.app.ActionBar actionBar;

    private TextView champion_isEmpty_view;

    private int clickCheck = 0;

    private String[] conson_str = {"ㄱ - ㄴ","ㄷ - ㄹ","ㅁ - ㅂ","ㅅ -","ㅇ -","ㅈ - ㅊ","ㅋ - ㅌ","ㅍ - ㅎ"};
    private String[] name_gana = {"선택없음","가렌","갈리오","갱플랭크","그라가스","그레이브즈","나르","나미","나서스","노틸러스","녹턴","누누","누누와 윌럼프","니달리","니코"};
    private String[] name_dara = {"선택없음","다리우스","다이애나","드레이븐","라이즈","라칸","람머스","럭스","럼블","레넥톤","레오나","렉사이","렝가","루시안","룰루","르블랑","리신","리븐","리산드라"};
    private String[] name_maba = {"선택없음","마스터이","마오카이","말자하","말파이트","모데카이저","모르가나","문도 박사","미스포츈","바드","바루스","바이","베이가","베인","벨코즈","볼리베어",
            "브라움","브랜드","블라디미르","블리츠크랭크","빅토르","뽀삐"};
    private String[] name_sa = {"선택없음","사이온","사일러스", "샤코","세주아니","소나","소라카","쉔","쉬바나","스웨인","스카너","시비르","신짜오","신드라","신지드","쓰레쉬"};
    private String[] name_a = {"선택없음","아리","아무무","아우렐리온 솔","아이번","아지르","아칼리","아트록스","알리스타","애니","애니비아","애쉬","야스오","에코","엘리스","오공","오른","오리아나"
            ,"올라프","요릭","우디르","우르곳","유미","워윅","이렐리아","이블린","이즈리얼","일라오이"};
    private String[] name_jacha = {"선택없음","자르반","자야","자이라","자크","잔나","잭스","제드","제라스","제이스","조이","직스","진","질리언","징크스","초가스"};

    private String[] name_kata = {"선택없음","카르마","카밀","카사딘","카서스","카시오페아","카이사","카직스","카타리나","칼리스타","케넨","케이틀린","케인","케일","코그모","코르키","퀸","클레드", "키아나",
            "킨드레드","타릭","탈론","탈리야","탐켄치","트런들","트리스타나","트린다미어","트위스티드 페이트","트위치","티모"};

    private String[] name_paha = {"선택없음","파이크","판테온","피들스틱","피오라","피즈","하이머딩거","헤카림"};


    public ChampionDialog(@NonNull Context context, RecyclerView recyclerView, Activity activity, androidx.appcompat.app.ActionBar actionBar, TextView champion_isEmpty_view) {
        super(context);
        this.context = context;
        this.recyclerView = recyclerView;
        this.activity = activity;
        this.actionBar = actionBar;
        this.champion_isEmpty_view = champion_isEmpty_view;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_champion_choice);

        conson = findViewById(R.id.championNoteActivity_champion_conson);
        name = findViewById(R.id.championNoteActivity_champion_name);

        championSpinnerAdapter = new ChampionSpinnerAdapter(context,conson_str);
        championSpinnerAdapter2 = new ChampionSpinnerAdapter(context,name_gana);

        conson.setAdapter(championSpinnerAdapter);
        name.setAdapter(championSpinnerAdapter2);

        conson.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(i == 0){
                    championSpinnerAdapter2 = new ChampionSpinnerAdapter(context,name_gana);
                    name.setAdapter(championSpinnerAdapter2);
                }else if(i == 1){
                    championSpinnerAdapter2 = new ChampionSpinnerAdapter(context,name_dara);
                    name.setAdapter(championSpinnerAdapter2);
                }else if(i == 2){
                    championSpinnerAdapter2 = new ChampionSpinnerAdapter(context,name_maba);
                    name.setAdapter(championSpinnerAdapter2);
                }else if(i == 3){
                    championSpinnerAdapter2 = new ChampionSpinnerAdapter(context,name_sa);
                    name.setAdapter(championSpinnerAdapter2);
                }else if(i == 4){
                    championSpinnerAdapter2 = new ChampionSpinnerAdapter(context,name_a);
                    name.setAdapter(championSpinnerAdapter2);
                }else if(i == 5){
                    championSpinnerAdapter2 = new ChampionSpinnerAdapter(context,name_jacha);
                    name.setAdapter(championSpinnerAdapter2);
                }else if(i == 6){
                    championSpinnerAdapter2 = new ChampionSpinnerAdapter(context,name_kata);
                    name.setAdapter(championSpinnerAdapter2);
                }else if(i == 7){
                    championSpinnerAdapter2 = new ChampionSpinnerAdapter(context,name_paha);
                    name.setAdapter(championSpinnerAdapter2);
                }
                if(clickCheck != 0){
                    clickCheck = 1;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(clickCheck > 1){
                    ((ChampionNoteActivity)activity).champion_Name = adapterView.getAdapter().getItem(i).toString();
                    ((ChampionNoteActivity)activity).getData();

                    actionBar.setTitle(adapterView.getAdapter().getItem(i).toString()+"의 패치노트");
                    ChampionDialog.this.dismiss();
                }else{
                    clickCheck++;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }
}
