package com.example.pato.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pato.Interface.LoadMore;
import com.example.pato.MainActivity;
import com.example.pato.R;
import com.example.pato.adapter.BoardListRecyclerViewAdapter;
import com.example.pato.customclass.NetworkCheck;
import com.example.pato.model.BoardModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class BoardListFragment extends Fragment {

    private Button allboardlist_btn;
    private Button contestboardlist_btn;
    private Button freeboardlist_btn;
    private Button lolboardlist_btn;
    private Button famboardlist_btn;
    private RecyclerView board_recyclerview;
    private ProgressBar board_ProgressBar, board_ProgressBar2;
    private TextView netCheck;
    private RelativeLayout main_relative;
    private LinearLayout menu_linear;

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;

    private SwipeRefreshLayout swipeRefreshLayout;

    private BoardListRecyclerViewAdapter boardListRecyclerViewAdapter;

    private long endKey;
    private List<BoardModel.Board> boardModels = new ArrayList<>();
    private List<BoardModel.Board> boardModelsTemp = new ArrayList<>();
    private List<String> boardKey = new ArrayList<>();
    private List<String> boardTempKey = new ArrayList<>();
    private List<Long> getKey = new ArrayList<>();
    private Boolean endCheck = true;
    private String boardCheck = "all";

    public BoardListRecyclerViewAdapter.BoardViewHolder boardViewHolder;

    public static BoardListFragment newInstance(){
        Bundle bundle = new Bundle();

        BoardListFragment fragment = new BoardListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_boardlist,container,false);

        allboardlist_btn = view.findViewById(R.id.boardFragment_allboardlist_btn);
        contestboardlist_btn = view.findViewById(R.id.boardFragment_contestboardlist_btn);
        freeboardlist_btn = view.findViewById(R.id.boardFragment_freeboardlist_btn);
        lolboardlist_btn = view.findViewById(R.id.boardFragment_lolboardlist_btn);
        famboardlist_btn = view.findViewById(R.id.boardFragment_famboardlist_btn);
        board_ProgressBar =  view.findViewById(R.id.boardFragment_progressbar);
        board_ProgressBar2 = view.findViewById(R.id.boardfragment_progressbar2);
        swipeRefreshLayout = view.findViewById(R.id.boardFragment_refreshLayout);
        netCheck =  view.findViewById(R.id.boardFragment_netCheck);
        main_relative =  view.findViewById(R.id.boardFragment_main_relative);
        menu_linear = view.findViewById(R.id.boardFragment_menu_linear);
        board_recyclerview = view.findViewById(R.id.BoardFragment_recycleview);

        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("board");

        swipeRefreshLayout.setColorSchemeResources( android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(!NetworkCheck.isNetworkCheck(getActivity())){
                    Toast.makeText(getActivity(), "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    getData();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        allboardlist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                boardCheck = "all";

                if (!NetworkCheck.isNetworkCheck(getActivity())) {
                    Toast.makeText(getActivity(), "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    getData();
                }
            }
        });

        lolboardlist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boardCheck = "lol";

                if(!NetworkCheck.isNetworkCheck(getActivity())){
                    Toast.makeText(getActivity(), "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    getData();
                }
            }
        });

        contestboardlist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boardCheck = "con";

                if(!NetworkCheck.isNetworkCheck(getActivity())){
                    Toast.makeText(getActivity(), "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    getData();
                }
            }
        });

        freeboardlist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boardCheck = "fre";

                if(!NetworkCheck.isNetworkCheck(getActivity())){
                    Toast.makeText(getActivity(), "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    getData();
                }
            }
        });

        famboardlist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boardCheck = "fam";

                if(!NetworkCheck.isNetworkCheck(getActivity())){
                    Toast.makeText(getActivity(), "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    getData();
                }
            }
        });

        if(!NetworkCheck.isNetworkCheck(getActivity())){
            swipeRefreshLayout.setVisibility(View.GONE);
            netCheck.setVisibility(View.VISIBLE);
            main_relative.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            menu_linear.setVisibility(View.GONE);
        }else{
            netCheck.setVisibility(View.GONE);
            getData();
        }
        main_relative.setVisibility(View.VISIBLE);


        return view;
    }

    public void getData(){

        endCheck = true;
        boardModels.clear();
        boardKey.clear();
        boardTempKey.clear();
        boardModelsTemp.clear();
        getKey.clear();

        board_ProgressBar2.setVisibility(View.VISIBLE);
        board_recyclerview.setVisibility(View.GONE);

        allboardlist_btn.setEnabled(false);
        contestboardlist_btn.setEnabled(false);
        lolboardlist_btn.setEnabled(false);
        famboardlist_btn.setEnabled(false);
        freeboardlist_btn.setEnabled(false);

        endKey = 0;

        if(boardCheck.equals("all")) {

            collectionReference.orderBy("timestamp",Query.Direction.DESCENDING).limit(21).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        QuerySnapshot querySnapshot = task.getResult();
                        if(!querySnapshot.isEmpty()){
                            for(QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot){
                                BoardModel.Board board = queryDocumentSnapshot.toObject(BoardModel.Board.class);
                                boardModels.add(board);
                                getKey.add(board.timestamp);
                                boardKey.add(queryDocumentSnapshot.getId());
                            }

                            if (boardModels.size() > 20) {
                                boardModels.remove(20);
                                boardKey.remove(20);
                                endKey = getKey.get(20);
                                getKey.clear();
                            } else {
                                endCheck = false;
                            }
                            boardListRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }else{
                        Toast.makeText(getActivity(),"게시물을 불러 올 수 없습니다.",Toast.LENGTH_SHORT).show();
                    }
                    layout_Visible();
                }
            });
        }else if(boardCheck.equals("con") || boardCheck.equals("fre") || boardCheck.equals("lol") || boardCheck.equals("fam") ){
            collectionReference.whereEqualTo("category",boardCheck).orderBy("timestamp",Query.Direction.DESCENDING).limit(21).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        QuerySnapshot querySnapshot = task.getResult();
                        if(!querySnapshot.isEmpty()){

                            for(QueryDocumentSnapshot queryDocumentSnapshot: querySnapshot){
                                BoardModel.Board board = queryDocumentSnapshot.toObject(BoardModel.Board.class);
                                boardModels.add(board);
                                getKey.add(board.timestamp);
                                boardKey.add(queryDocumentSnapshot.getId());
                            }

                            if (boardModels.size() > 20) {
                                endKey = getKey.get(20);
                                boardModels.remove(20);
                                boardKey.remove(20);
                                getKey.clear();
                            } else {
                                endCheck = false;
                            }
                            boardListRecyclerViewAdapter.notifyDataSetChanged();
                        }
                    }else{
                        Toast.makeText(getActivity(),"게시물을 불러 올 수 없습니다.",Toast.LENGTH_SHORT).show();
                    }
                    layout_Visible();
                }
            });
        }

        getRecyclerView();
    }


    private void boardPage(){

        if(boardCheck.equals("all")){
            if(endCheck){
                board_ProgressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        collectionReference.whereLessThanOrEqualTo("timestamp",endKey).orderBy("timestamp",Query.Direction.DESCENDING).limit(21).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if(!querySnapshot.isEmpty()){
                                        for(QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot){
                                            BoardModel.Board board = queryDocumentSnapshot.toObject(BoardModel.Board.class);
                                            boardModelsTemp.add(board);
                                            getKey.add(board.timestamp);
                                            boardTempKey.add(queryDocumentSnapshot.getId());
                                        }

                                        if(boardModelsTemp.size() > 20){
                                            boardModelsTemp.remove(20);
                                            boardTempKey.remove(20);
                                            endKey = getKey.get(20);
                                        }else{
                                            endCheck = false;
                                        }

                                        boardModels.addAll(boardModelsTemp);
                                        boardKey.addAll(boardTempKey);

                                        boardModelsTemp.clear();
                                        boardTempKey.clear();
                                        getKey.clear();

                                        boardListRecyclerViewAdapter.notifyDataSetChanged();
                                        boardListRecyclerViewAdapter.setLoaded();
                                        board_ProgressBar.setVisibility(View.GONE);
                                    }
                                }else{
                                    Toast.makeText(getActivity(),"게시물을 불러 올 수 없습니다.",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                },300);
            }else{
                Toast.makeText(getActivity(),"마지막 페이지 입니다.",Toast.LENGTH_SHORT).show();
            }
        }else if(boardCheck.equals("con") || boardCheck.equals("fre") || boardCheck.equals("lol") || boardCheck.equals("fam")) {
            if (endCheck) {
                board_ProgressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        collectionReference.whereEqualTo("category",boardCheck).whereLessThanOrEqualTo("timestamp",endKey).orderBy("timestamp",Query.Direction.DESCENDING).limit(21).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    QuerySnapshot querySnapshot = task.getResult();
                                    if(!querySnapshot.isEmpty()){
                                        for(QueryDocumentSnapshot queryDocumentSnapshot : querySnapshot){
                                            BoardModel.Board board = queryDocumentSnapshot.toObject(BoardModel.Board.class);
                                            boardModelsTemp.add(board);
                                            getKey.add(board.timestamp);
                                            boardTempKey.add(queryDocumentSnapshot.getId());
                                        }

                                        if(boardModelsTemp.size() > 20){
                                            endKey = getKey.get(20);
                                            boardModelsTemp.remove(20);
                                            boardTempKey.remove(20);
                                        }else{
                                            endCheck = false;
                                        }

                                        boardModels.addAll(boardModelsTemp);
                                        boardKey.addAll(boardTempKey);
                                        boardModelsTemp.clear();
                                        boardTempKey.clear();

                                        getKey.clear();
                                        boardListRecyclerViewAdapter.notifyDataSetChanged();
                                        boardListRecyclerViewAdapter.setLoaded();
                                        board_ProgressBar.setVisibility(View.GONE);
                                    }else{
                                        board_ProgressBar.setVisibility(View.GONE);
                                    }
                                }else{
                                    Toast.makeText(getActivity(),"게시물을 불러 올 수 없습니다.",Toast.LENGTH_SHORT).show();
                                    board_ProgressBar.setVisibility(View.GONE);
                                }
                            }
                        });
                    }
                }, 300);
            } else {
                Toast.makeText(getActivity(), "마지막 페이지 입니다.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void getRecyclerView(){

        board_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        boardListRecyclerViewAdapter = new BoardListRecyclerViewAdapter(board_recyclerview,getActivity(),boardModels,boardKey);
        board_recyclerview.setAdapter(boardListRecyclerViewAdapter);

        boardListRecyclerViewAdapter.setLoadMore(new LoadMore() {
            @Override
            public void onLoadMore() {

                if(!NetworkCheck.isNetworkCheck(getActivity())) {
                    Toast.makeText(getActivity(), "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                }else{
                    boardPage();
                }
            }
        });
    }



    @Override
    public void onResume() {
        super.onResume();

        if(((MainActivity)getActivity()).writeCheck){
            main_relative.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
            menu_linear.setVisibility(View.GONE);
            getData();
            ((MainActivity)getActivity()).writeCheck = false;
        }
    }

    public void button_eabled(){
        allboardlist_btn.setEnabled(true);
        contestboardlist_btn.setEnabled(true);
        lolboardlist_btn.setEnabled(true);
        famboardlist_btn.setEnabled(true);
        freeboardlist_btn.setEnabled(true);
    }

    public void layout_Visible(){
        menu_linear.setVisibility(View.VISIBLE);
        main_relative.setGravity(Gravity.NO_GRAVITY);
        board_ProgressBar2.setVisibility(View.GONE);
        board_recyclerview.setVisibility(View.VISIBLE);
        button_eabled();
    }

}
