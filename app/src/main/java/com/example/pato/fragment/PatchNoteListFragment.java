package com.example.pato.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pato.MainActivity;
import com.example.pato.PatchNoteActivity;
import com.example.pato.R;
import com.example.pato.customclass.NetworkCheck;
import com.example.pato.model.ImagesModel;
import com.example.pato.model.PatchNoteModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class PatchNoteListFragment extends Fragment {

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private LinearLayout patch_linear;
    private LinearLayout patch_linear_main;
    private ProgressBar progressBar;
    private TextView netCheck;

    private RecyclerView recyclerView;

    private String year = "2018";
    private List<PatchNoteModel.patchinfo> patchinfo = new ArrayList<>();
    private List<PatchNoteModel.readcount> readcount = new ArrayList<>();
    private boolean isViewShown = true;



    public static PatchNoteListFragment newInstance(){
        Bundle bundle = new Bundle();

        PatchNoteListFragment fragment = new PatchNoteListFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_patchnotelist,container,false);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("patchnotes");

        netCheck = view.findViewById(R.id.patchNoteFragment_netCheck);
        progressBar = view.findViewById(R.id.patchNoteFragment_progressbar);
        patch_linear = view.findViewById(R.id.patchnoteActivity_patchlist_linear);
        patch_linear_main = view.findViewById(R.id.patchnoteActivity_patchlist_linear_main);
        recyclerView = view.findViewById(R.id.patchNoteFragment_patch_recycleview);

        return view;
    }

    public class PatchNoteRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public PatchNoteRecyclerViewAdapter(){
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patchnotelist,parent,false);

            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
            final CustomViewHolder customViewHolder = (CustomViewHolder)holder;

            if(NewSign(patchinfo.get(position).timestamp) > 13){
                customViewHolder.patchNote_new.setVisibility(View.GONE);
            }else{
                customViewHolder.patchNote_new.setVisibility(View.VISIBLE);
            }

            customViewHolder.patchNote_readcount.setText("조회수 " + readcount.get(position).readcount);
            customViewHolder.patchNote_replyscount.setText("["+(readcount.get(position).replyscount)+"]");

            customViewHolder.patchNote_timestamp.setText(patchinfo.get(position).timestamp.substring(5,10));
            customViewHolder.patchNote_title.setText(patchinfo.get(position).title);

            customViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    if(!NetworkCheck.isNetworkCheck(getActivity())){
                        Toast.makeText(getActivity(), "인터넷에 접속해주세요.", Toast.LENGTH_SHORT).show();
                    }else{
                        customViewHolder.itemView.setEnabled(false);

                        ((MainActivity)getActivity()).customViewHolder = customViewHolder;

                        databaseReference.child("2018").child(patchinfo.get(position).version).child("readcount").runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                PatchNoteModel.readcount readcount = mutableData.getValue(PatchNoteModel.readcount.class);

                                if(readcount != null){
                                    readcount.readcount = readcount.readcount + 1;
                                    mutableData.setValue(readcount);
                                }else{

                                }
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                            }
                        });
                        Intent intent = new Intent(view.getContext(), PatchNoteActivity.class);
                        intent.putExtra("version",patchinfo.get(position).version);
                        intent.putExtra("title",patchinfo.get(position).title);
                        intent.putExtra("year",year);
                        getActivity().startActivityForResult(intent,11);
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return patchinfo.size();
        }

        public class CustomViewHolder extends RecyclerView.ViewHolder {
            public TextView patchNote_title;
            public TextView patchNote_replyscount;
            public ImageView patchNote_new;
            public TextView patchNote_timestamp;
            public TextView patchNote_readcount;

            public CustomViewHolder(View view) {
                super(view);

                patchNote_title = view.findViewById(R.id.patchNotelist_title_textview);
                patchNote_replyscount =  view.findViewById(R.id.patchNotelist_replyscount_textview);
                patchNote_new =  view.findViewById(R.id.patchNotelist_new_imageview);
                patchNote_timestamp = view.findViewById(R.id.patchNotelist_timestamp_textview);
                patchNote_readcount = view.findViewById(R.id.patchNotelist_readcount_textview);
            }
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser && isViewShown){
            if(NetworkCheck.isNetworkCheck(getActivity())){
                getData();
            }else{
                patch_linear.setVisibility(View.GONE);
                patch_linear_main.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER);
                netCheck.setVisibility(View.VISIBLE);
            }

            isViewShown = false;
        }
    }

    public long NewSign(String beforeDay){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String currentDay = simpleDateFormat.format(new Date());
        long gabDay = 0;
        try {
            Date beforeDate = simpleDateFormat.parse(beforeDay);
            Date currentDate = simpleDateFormat.parse(currentDay);

            gabDay = currentDate.getTime() - beforeDate.getTime();
            gabDay = gabDay / (24 * 60 * 60 * 1000);

        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        return gabDay;
    }

    private void getData(){
        patch_linear.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        patch_linear_main.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER);

        firebaseDatabase.getReference().child("images").child("가가").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    ImagesModel imagesModel = dataSnapshot.getValue(ImagesModel.class);
                    final String[] version = imagesModel.imageUrl.split(",");

                    patchinfo.clear();
                    readcount.clear();

                    for(int i = 0; i < 13; i++){
                        final int finalI = i;

                        databaseReference.child(year).child(version[i]).child("patchinfo").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(dataSnapshot.exists()){
                                    PatchNoteModel.patchinfo patchinfoo = dataSnapshot.getValue(PatchNoteModel.patchinfo.class);
                                    patchinfo.add(patchinfoo);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getActivity(), "오류가 발생하였습니다. 잠시 후 시도해 주세요.!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });

                        databaseReference.child(year).child(version[i]).child("readcount").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                if(dataSnapshot.exists()){
                                    PatchNoteModel.readcount readcounto = dataSnapshot.getValue(PatchNoteModel.readcount.class);
                                    readcount.add(readcounto);

                                    if(finalI == 12){

                                        if(!NetworkCheck.isNetworkCheck(getActivity())){
                                            netCheck.setVisibility(View.VISIBLE);
                                        }else{
                                            patch_linear_main.setGravity(Gravity.NO_GRAVITY);
                                        }

                                        recyclerView.setAdapter(new PatchNoteRecyclerViewAdapter());
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

                                        patch_linear.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }else{
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Toast.makeText(getActivity(), "오류가 발생하였습니다. 잠시 후 시도해 주세요.!", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        });
                    }

                }else{

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(), "오류가 발생하였습니다. 잠시 후 시도해 주세요.!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}

