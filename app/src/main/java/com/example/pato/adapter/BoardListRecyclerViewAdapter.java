package com.example.pato.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pato.BoardActivity;
import com.example.pato.Interface.LoadMore;
import com.example.pato.MainActivity;
import com.example.pato.R;
import com.example.pato.customclass.NetworkCheck;
import com.example.pato.model.BoardModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class BoardListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LoadMore loadMore;
    private boolean isLoading;
    private Activity activity;
    private List<BoardModel.Board> boardModels;
    private List<String> boardKeys;
    private int lastVisibleItem,totalItemCount,currentItems;

    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;

    public BoardListRecyclerViewAdapter(RecyclerView recyclerView, final Activity activity, List<BoardModel.Board> boardModels, List<String> boardKeys){
        this.boardModels = boardModels;
        this.activity = activity;
        this.boardKeys = boardKeys;

        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection("board");

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager)recyclerView.getLayoutManager();
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isLoading = true;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                currentItems = linearLayoutManager.getChildCount();
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

                if(isLoading && (currentItems + lastVisibleItem == totalItemCount)){
                    isLoading = false;
                    loadMore.onLoadMore();
                }
            }
        });

    }

    public void setLoadMore(LoadMore loadMore){
        this.loadMore = loadMore;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_boardlist,parent,false);

        return new BoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final BoardViewHolder customViewHolder = (BoardViewHolder)holder;

        customViewHolder.board_nickname.setText(boardModels.get(position).nickname);
        customViewHolder.board_timestamp.setText(TimeGab((long)boardModels.get(position).timestamp));

        if(boardModels.get(position).title.length() > 41){
            boardModels.get(position).title = boardModels.get(position).title.substring(0,40) + "...";
        }

        int replysCountSize = 0;
        String replysCount = "[" + boardModels.get(position).replyscount + "]";

        String text = boardModels.get(position).title+"  "+replysCount+"   ";
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

        if(NewSign(boardModels.get(position).timestamp) < 1){
            Drawable d = activity.getResources().getDrawable(R.drawable.new_icon);

            d.setBounds(0,0,80,70);

            ImageSpan image = new ImageSpan(d,ImageSpan.ALIGN_BOTTOM);
            ssBuilder.setSpan(image, text.length() - 1, // Start of the span (inclusive)
                    text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        ssBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#ffff8800")),text.length() - replysCountSize ,text.length() - 3 ,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        ssBuilder.setSpan(new AbsoluteSizeSpan(20,true),text.length() - replysCountSize ,text.length() - 3 ,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        customViewHolder.board_title.setText(ssBuilder);
        customViewHolder.board_readcount.setText("조회수 "+String.valueOf(boardModels.get(position).readcount));

        customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!NetworkCheck.isNetworkCheck(activity)) {
                        Toast.makeText(activity, "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                    }else{
                        customViewHolder.itemView.setEnabled(false);

                        ((MainActivity)activity).boardViewHolder = customViewHolder;

                        Intent intent = new Intent(activity.getApplicationContext(), BoardActivity.class);
                        intent.putExtra("bid",boardKeys.get(position));
                        activity.startActivityForResult(intent,9);

                        final DocumentReference documentReference = collectionReference.document(boardKeys.get(position));

                        firebaseFirestore.runTransaction(new com.google.firebase.firestore.Transaction.Function<Void>() {
                            @Nullable
                            @Override
                            public Void apply(@NonNull com.google.firebase.firestore.Transaction transaction) throws FirebaseFirestoreException {
                                DocumentSnapshot documentSnapshot = transaction.get(documentReference);
                                double readcount = documentSnapshot.getDouble("readcount") + 1;
                                transaction.update(documentReference,"readcount",readcount);

                                return null;
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });


                    }
                }
            });
    }

    @Override
    public int getItemCount() {
        return boardModels.size();
    }

    public class BoardViewHolder extends RecyclerView.ViewHolder {

        private TextView board_title;
        private TextView board_nickname;
        private TextView board_timestamp;
        private TextView board_readcount;


        public BoardViewHolder(View view) {
            super(view);

            board_title = view.findViewById(R.id.boardFragment_title);
            board_nickname = view.findViewById(R.id.boardFragment_nickname);
            board_timestamp = view.findViewById(R.id.boardFragment_timestamp);
            board_readcount = view.findViewById(R.id.boardFragment_readcount);

        }
    }

    public void setLoaded() {
        isLoading = false;
    }

    public String TimeGab(long beforeTime){

        long gabDay ;
        long gabDate ;
        String dayString ;

        gabDate = System.currentTimeMillis() + 8000 - beforeTime;
        gabDay = gabDate / (1000);

        if(gabDay>59){
            gabDay = gabDate/ (60 * 1000);
            dayString = "분 전";
            if(gabDay>59){
                gabDay = gabDate / (60 * 60 * 1000);
                dayString = "시간 전";
                if(gabDay>23){
                    gabDay = gabDate / (24 * 60 * 60 * 1000);
                    dayString = "일 전";
                    if(gabDay>30){
                        gabDay = (gabDate / (30 * 24 * 60 * 60 * 1000)) * -1;
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
    }


    public long NewSign(long beforeDay){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDay = simpleDateFormat.format(new Date());

        long gabDay = 0;

        try {
            Date currentDate = simpleDateFormat.parse(currentDay);

            gabDay = currentDate.getTime() - beforeDay;
            gabDay = gabDay / (24 * 60 * 60 * 1000);

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }

        return gabDay;
    }


}


