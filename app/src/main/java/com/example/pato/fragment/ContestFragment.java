package com.example.pato.fragment;

import android.app.Fragment;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.pato.R;
import com.example.pato.adapter.ContestSpinnerAdapter;
import com.example.pato.customclass.NetworkCheck;
import com.example.pato.model.ContestModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class ContestFragment extends Fragment {

    private static final int TYPE_LCK_HEAD = 0;
    private static final int TYPE_LCK_RANK = 1;
    private static final int TYPE_LCK_MVP_TITLE = 2;
    private static final int TYPE_LCK_MVP_LIST = 3;

    private LinearLayout main_linear;
    private TextView netCheck;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ContestSpinnerAdapter contestSpinnerAdapter;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private List<String> contest_name_list;
    private List<String> contest_name_list_2;

    private String contest_name = "";
    private int spinner_number = 0;
    private boolean isViewShown = true;
    private boolean visiblePass = false;

    private RequestOptions options = new RequestOptions().placeholder(R.drawable.loading_icon).error(R.drawable.error_icon).centerCrop();

    public static ContestFragment newInstance(){
        Bundle bundle = new Bundle();

        ContestFragment fragment = new ContestFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contest,container,false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("contest");

        main_linear = view.findViewById(R.id.contestFragment_main_linear_1);
        netCheck = view.findViewById(R.id.contestFragment_netCheck);
        recyclerView = view.findViewById(R.id.contestFragment_lck_recyclerview);
        progressBar = view.findViewById(R.id.contestFragment_progressbar);

        if(visiblePass){
            setView();
        }


        return view;
    }

    private class LCKRecyclerView extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private List<ContestModel.RANKS> lcklist = new ArrayList<>();
        private List<ContestModel.MVP_RANKS> lck_mvp_list = new ArrayList<>();
        private int lck_mvp_list_size;

        public LCKRecyclerView() {

            main_linear.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

            databaseReference.child("LCK").child("season").child(contest_name).child("RANKS").orderByChild("RANK").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    lcklist.clear();
                    if(dataSnapshot.exists()){
                        for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                            ContestModel.RANKS lck_rank = snapshot.getValue(ContestModel.RANKS.class);
                            lcklist.add(lck_rank);
                        }
                        lck_mvp_list_size = lcklist.size();
                    }

                    main_linear.setGravity(Gravity.NO_GRAVITY);
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "오류가 발생하였습니다. 잠시 후 시도해 주세요.", Toast.LENGTH_SHORT).show();
                }
            });

            databaseReference.child("LCK").child("season").child(contest_name).child("MVP").orderByChild("point").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    lck_mvp_list.clear();
                    if(dataSnapshot.exists()){
                        for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                            ContestModel.MVP_RANKS lck_mvp_rank = snapshot.getValue(ContestModel.MVP_RANKS.class);
                            lck_mvp_list.add(lck_mvp_rank);
                        }
                        Collections.reverse(lck_mvp_list);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(getActivity(), "오류가 발생하였습니다. 잠시 후 시도해 주세요.", Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == TYPE_LCK_HEAD){
                 View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lck_head,parent,false);
                 return new LCK_HEADViewHolder(view);
            }else if(viewType == TYPE_LCK_RANK){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lck_rank,parent,false);
                return new LCKViewHolder(view);
            }else if(viewType == TYPE_LCK_MVP_TITLE){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lck_mvp_list,parent,false);
                return new LCK_MVP_TITLEViewHolder(view);
            }else if(viewType == TYPE_LCK_MVP_LIST){
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lck_mvp,parent,false);
                return new LCK_MVP_LISTViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if(holder instanceof LCK_HEADViewHolder){

                final LCK_HEADViewHolder lck_headViewHolder = (LCK_HEADViewHolder)holder;

                TimeZone tz;
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date date = new Date();
                tz = TimeZone.getTimeZone("Asia/Seoul");
                simpleDateFormat.setTimeZone(tz);
                String current_time = simpleDateFormat.format(date);

                databaseReference.child("LCK").child("schedule").child(current_time).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()){
                            lck_headViewHolder.schedule_on.setVisibility(View.VISIBLE);
                            lck_headViewHolder.schedyle_off.setVisibility(View.GONE);

                            ContestModel.scheDule scheDule = dataSnapshot.getValue(ContestModel.scheDule.class);

                            lck_headViewHolder.firstteam.setText(scheDule.ft);
                            lck_headViewHolder.firstteam2.setText(scheDule.ft2);
                            lck_headViewHolder.firstgameTime.setText(scheDule.fgt);

                            if(scheDule.st != null){
                                lck_headViewHolder.secondteam2.setText(scheDule.st2);
                                lck_headViewHolder.secondteam.setText(scheDule.st);
                                lck_headViewHolder.secondgameTime.setText(scheDule.sgt);
                            }else{
                                lck_headViewHolder.second_linear.setVisibility(View.GONE);
                            }

                            if(scheDule.tgt != null){
                                lck_headViewHolder.thirdteam.setText(scheDule.tt);
                                lck_headViewHolder.thirdteam2.setText(scheDule.tt2);
                                lck_headViewHolder.thirdgameTime.setText(scheDule.tgt);
                            }else{
                                lck_headViewHolder.thirdgame_linear.setVisibility(View.GONE);
                            }
                        }else{
                            lck_headViewHolder.schedyle_off.setVisibility(View.VISIBLE);
                            lck_headViewHolder.schedule_on.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(getActivity(), "오류가 발생하였습니다. 잠시 후 시도해 주세요.", Toast.LENGTH_SHORT).show();
                    }
                });
                lck_headViewHolder.contest_Name_Spinner.setSelection(spinner_number);
                lck_headViewHolder.contest_Name_Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if(!NetworkCheck.isNetworkCheck(getActivity())){
                            main_linear.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
                            netCheck.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }else if(!contest_name.equals(contest_name_list_2.get(position))) {
                            contest_name = contest_name_list_2.get(position);
                            recyclerView.setAdapter(new LCKRecyclerView());
                            spinner_number = position;
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }else if(holder instanceof LCKViewHolder) {

                LCKViewHolder lckViewHolder = (LCKViewHolder) holder;

                lckViewHolder.rank_view.setText(String.valueOf(lcklist.get(position-1).RANK));
                lckViewHolder.win_view.setText(lcklist.get(position-1).WIN);
                lckViewHolder.lose_view.setText(lcklist.get(position-1).LOSE);
                lckViewHolder.gain_view.setText(lcklist.get(position-1).GAIN);
                lckViewHolder.remark_view.setText(lcklist.get(position-1).REMARK);

                Glide.with(getActivity().getApplicationContext())
                        .load("https://firebasestorage.googleapis.com/v0/b/pato-102e1.appspot.com/o/TEAM_LOGO%2F"+lcklist.get(position-1).logoImage)
                        .apply(options)
                        .into(lckViewHolder.logo_View);

            }else if(holder instanceof LCK_MVP_TITLEViewHolder){

                LCK_MVP_TITLEViewHolder lck_mvp_titleViewHolder = (LCK_MVP_TITLEViewHolder)holder;
                lck_mvp_titleViewHolder.textView.setText(contest_name.substring(4) + " MVP");

            }else if(holder instanceof LCK_MVP_LISTViewHolder){
                LCK_MVP_LISTViewHolder lck_mvp_listViewHolder = (LCK_MVP_LISTViewHolder)holder;

                lck_mvp_listViewHolder.team_view.setText(lck_mvp_list.get(position - 2 - lck_mvp_list_size).team);
                lck_mvp_listViewHolder.rank_view.setText(String.valueOf(position - 2 - lck_mvp_list_size + 1));
                lck_mvp_listViewHolder.position_view.setText(lck_mvp_list.get(position - 2 - lck_mvp_list_size).position);
                lck_mvp_listViewHolder.point_view.setText(String.valueOf(lck_mvp_list.get(position - 2 - lck_mvp_list_size).point));
                lck_mvp_listViewHolder.id_view.setText(lck_mvp_list.get(position - 2 - lck_mvp_list_size).id);
            }
        }

        @Override
        public int getItemCount() {
            return 1 + lcklist.size() + 1 + lck_mvp_list.size();
        }

        @Override
        public int getItemViewType(int position) {
            if(isPositionHeader(position)){
                return TYPE_LCK_HEAD;
            }else if(isPositionRank(position)){
                return TYPE_LCK_RANK;
            }else if (isPositionMVP_TITLE(position)) {
                return TYPE_LCK_MVP_TITLE;
            }else{
                return TYPE_LCK_MVP_LIST;
            }
        }

        private boolean isPositionHeader(int position) {
            return position == 0;
        }
        private boolean isPositionRank(int position) {
            return position >= 1 && position <= lcklist.size();
        }
        private boolean isPositionMVP_TITLE(int position) {
            return position == lcklist.size() + 1;
        }

        private class LCK_HEADViewHolder extends RecyclerView.ViewHolder {

            private TextView firstteam, firstteam2, firstgameTime;
            private TextView secondteam, secondteam2, secondgameTime;
            private TextView thirdteam, thirdteam2, thirdgameTime;
            private TextView relay;
            private LinearLayout schedule_on, schedyle_off, second_linear, thirdgame_linear,all_linear,main_linear;
            private Spinner contest_Name_Spinner;

            public LCK_HEADViewHolder(View view) {
                super(view);

                schedule_on = view.findViewById(R.id.contestFragment_schedule_layout_on);
                schedyle_off = view.findViewById(R.id.contestFragment_schedule_layout_off);
                firstteam = view.findViewById(R.id.contestFragment_first_team);
                firstteam2 =  view.findViewById(R.id.contestFragment_first_team2);
                firstgameTime =  view.findViewById(R.id.contestFragment_firstGame_time);
                secondteam =  view.findViewById(R.id.contestFragment_second_team);
                secondteam2 =  view.findViewById(R.id.contestFragment_second_team2);
                secondgameTime = view.findViewById(R.id.contestFragment_SecondGame_time);
                second_linear = view.findViewById(R.id.contestFragment_second_linear);
                thirdteam =  view.findViewById(R.id.contestFragment_third_team);
                thirdteam2 = view.findViewById(R.id.contestFragment_third_team2);
                thirdgameTime =  view.findViewById(R.id.contestFragment_ThirdGame_time);
                thirdgame_linear = view.findViewById(R.id.contestFragment_thirdgame_linear);
                all_linear =  view.findViewById(R.id.contestFragment_all_linear);
                main_linear = view.findViewById(R.id.contestFragment_main_linear);
                relay = view.findViewById(R.id.contestFragment_relay);
                contest_Name_Spinner = view.findViewById(R.id.contestFragment_Spinner);

                contest_Name_Spinner.setAdapter(contestSpinnerAdapter);
                contest_Name_Spinner.setGravity(Gravity.CENTER_HORIZONTAL);
            }
        }

        private class LCKViewHolder extends RecyclerView.ViewHolder {

            private TextView win_view;
            private TextView lose_view;
            private TextView gain_view;
            private TextView rank_view;
            private TextView remark_view;
            private ImageView logo_View;

            public LCKViewHolder(View view) {
                super(view);

                win_view =  view.findViewById(R.id.contestFragment_win);
                lose_view = view.findViewById(R.id.contestFragment_lose);
                gain_view = view.findViewById(R.id.contestFragment_gain);
                rank_view = view.findViewById(R.id.contestFragment_rank);
                remark_view = view.findViewById(R.id.contestFragment_remark);
                logo_View = view.findViewById(R.id.contestFragment_teamLogo);
            }
        }

        private class LCK_MVP_TITLEViewHolder extends RecyclerView.ViewHolder {

            private TextView textView;

            public LCK_MVP_TITLEViewHolder(View view) {
                super(view);

                textView = view.findViewById(R.id.contestFragment_MVP_name);
            }
        }

        private class LCK_MVP_LISTViewHolder extends RecyclerView.ViewHolder {

            private TextView team_view;
            private TextView rank_view;
            private TextView point_view;
            private TextView position_view;
            private TextView id_view;

            public LCK_MVP_LISTViewHolder(View view) {
                super(view);

                team_view = view.findViewById(R.id.contestFragment_team);
                rank_view = view.findViewById(R.id.contestFragment_rank);
                point_view = view.findViewById(R.id.contestFragment_point);
                position_view = view.findViewById(R.id.contestFragment_position);
                id_view = view.findViewById(R.id.contestFragment_id);
            }
        }
    }



    private void start_contest_name(){

        databaseReference.child("LCK").child("season").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    contest_name_list = new ArrayList<>();
                    contest_name_list_2 = new ArrayList<>();

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        contest_name_list.add(snapshot.getKey().substring(4));
                        contest_name_list_2.add(snapshot.getKey());
                    }

                    Collections.reverse(contest_name_list);
                    Collections.reverse(contest_name_list_2);
                    contest_name = contest_name_list_2.get(0);

                    contestSpinnerAdapter = new ContestSpinnerAdapter(getActivity(), contest_name_list);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(new LCKRecyclerView());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getActivity(), "오류가 발생하였습니다. 잠시 후 시도해 주세요.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser && isViewShown){
            if(getView() != null){
                setView();
            }else{
                visiblePass = true;
            }
            isViewShown = false;
        }else{

        }
    }

    private void setView(){
        if(!NetworkCheck.isNetworkCheck(getActivity())){
            main_linear.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            netCheck.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }else{
            main_linear.setGravity(Gravity.NO_GRAVITY);
            netCheck.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            start_contest_name();
        }
    }



}
