package com.example.pato;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import com.google.android.material.tabs.TabLayout;
import androidx.legacy.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.pato.adapter.BoardListRecyclerViewAdapter;
import com.example.pato.adapter.TapPagerAdapter;
import com.example.pato.customclass.BackPress;
import com.example.pato.customclass.NetworkCheck;
import com.example.pato.fragment.PatchNoteListFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 8;

    private BackPress backPressCloseHandler;

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private AdView adView;

    private MenuItem menu_1;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    private String onResume = "";
    public boolean writeCheck = false;
    private boolean alarmcheck = false;
    private boolean contestalarmcheck = false;
    private boolean patchnotecheck = false;

    public TapPagerAdapter pagerAdapter;

    public BoardListRecyclerViewAdapter.BoardViewHolder boardViewHolder;
    public PatchNoteListFragment.PatchNoteRecyclerViewAdapter.CustomViewHolder customViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        tabLayout =  findViewById(R.id.tablayoutttt);
        viewPager = findViewById(R.id.Viewpage_main);
        adView = findViewById(R.id.mainActivity_adview);

        tabLayout.setupWithViewPager(viewPager);

        pagerAdapter = new TapPagerAdapter(getFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        viewPager.setOffscreenPageLimit(3);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        backPressCloseHandler = new BackPress(MainActivity.this);

        Permission();

        /*AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);*/

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                onResume = data.getStringExtra("onresume");
            }
            if(menu_1 != null){
                menu_1.setEnabled(true);
            }

        }else if(requestCode == 5){
            menu_1.setEnabled(true);
        }else if(requestCode == 9){
            if (resultCode == Activity.RESULT_OK) {
                onResume = data.getStringExtra("onresume");
            }
            boardViewHolder.itemView.setEnabled(true);
        }else if(requestCode == 11){
            customViewHolder.itemView.setEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        firebaseUser = firebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null){
            firebaseUser.reload();
            passPushTokenToServer();
            getSupportActionBar().setTitle(firebaseUser.getDisplayName()+"님");
        }else{
            getSupportActionBar().setTitle("LoL");
        }

        alarmcheck = getIntent().getBooleanExtra("alarmcheck",false);
        contestalarmcheck = getIntent().getBooleanExtra("contestalarmcheck",false);
        patchnotecheck = getIntent().getBooleanExtra("patchnotealarmcheck", false);

        if(onResume.equals("onresume")){
            writeCheck = true;

            onResume = "";
        }else if(alarmcheck){
            String boardUid = getIntent().getStringExtra("bid");

            viewPager.setCurrentItem(0);

            Intent intent = new Intent(this,BoardActivity.class);
            intent.putExtra("bid",boardUid);
            startActivityForResult(intent,1);

        }else if(contestalarmcheck){
            viewPager.setCurrentItem(2);
        }else if(patchnotecheck){
            String noteVersion = getIntent().getStringExtra("noteVersion");
            String title = getIntent().getStringExtra("title");
            String year = getIntent().getStringExtra("year");

            viewPager.setCurrentItem(0);

            Intent intent = new Intent(this,PatchNoteActivity.class);
            intent.putExtra("version",noteVersion);
            intent.putExtra("title",title);
            intent.putExtra("year",year);

            startActivityForResult(intent,2);

        }
        getIntent().removeExtra("alarmcheck");
        getIntent().removeExtra("contestalarmcheck");
        getIntent().removeExtra("patchnotealarmcheck");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.actionbar_mainactivity,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        menu_1 = item;

        switch (item.getItemId()){
            case R.id.mainActivity_write_btn:

                FirebaseUser user = firebaseAuth.getInstance().getCurrentUser();

                if(!NetworkCheck.isNetworkCheck(getApplicationContext())){
                    Toast.makeText(MainActivity.this, "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();

                }else if(user == null) {
                    item.setEnabled(false);
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(intent, 5);
                }else if(!firebaseUser.isEmailVerified()) {
                    Toast.makeText(getApplicationContext(), "이메일 인증이 필요한 계정입니다. 'ABOUT PATO' 에서 메일을 전송하시길 바랍니다.", Toast.LENGTH_SHORT).show();
                }else {
                    item.setEnabled(false);
                    Intent intent = new Intent(MainActivity.this, BoardWriteActivity.class);
                    startActivityForResult(intent, 1);
                }
                break;
            case R.id.mainActivity_championlist_btn:
                item.setEnabled(false);

                if(!NetworkCheck.isNetworkCheck(getApplicationContext())){
                    Toast.makeText(MainActivity.this, "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                    item.setEnabled(true);
                }else{
                    Intent intent = new Intent(MainActivity.this, ChampionNoteActivity.class);
                    startActivityForResult(intent,5);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 권한 허가
                    // 해당 권한을 사용해서 작업을 진행할 수 있습니다

                } else {
                    // 권한 거부
                    // 사용자가 해당권한을 거부했을때 해주어야 할 동작을 수행합니다

                }
                return;
        }
    }

    private void Permission(){

        int permissionCheck = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE);
        if(permissionCheck== PackageManager.PERMISSION_DENIED){
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED) {

                // 이 권한을 필요한 이유를 설명해야하는가?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)) {

                    // 다이어로그같은것을 띄워서 사용자에게 해당 권한이 필요한 이유에 대해 설명합니다

                    // 해당 설명이 끝난뒤 requestPermissions()함수를 호출하여 권한허가를 요청해야 합니다

                } else {

                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            }
        }else{

        }
    }

    private void passPushTokenToServer(){
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String token = FirebaseInstanceId.getInstance().getToken();

        Map<String, Object> map = new HashMap<>();
        map.put("pushToken",token);

        FirebaseFirestore.getInstance().collection("users").document(uid).update(map);

    }

    @Override public void onBackPressed() {
        backPressCloseHandler.onBackPressed();
    }

}


