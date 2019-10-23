package com.example.pato.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import androidx.recyclerview.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.pato.R;
import com.example.pato.model.ImagesModel;
import com.example.pato.model.PatchNoteModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class ChampionNoteRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> patchKey;
    private List<PatchNoteModel.Contents> contents;

    private String champion_Name;
    private String year_choice;
    private Activity activity;
    private ImagesModel imagesModel;

    public TextView champion_isEmpty_view;

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private RequestOptions options = new RequestOptions().placeholder(R.drawable.loading_icon).error(R.drawable.error_icon).circleCrop();

    public ChampionNoteRecyclerViewAdapter(final String champion_Name, String year_choice, final Activity activity, final TextView champion_isEmpty_view, List<PatchNoteModel.Contents> contents, List<String> patchKey, ImagesModel imagesModel){
        this.champion_Name = champion_Name;
        this.year_choice = year_choice;
        this.activity = activity;
        this.champion_isEmpty_view = champion_isEmpty_view;
        this.contents = contents;
        this.patchKey = patchKey;
        this.imagesModel = imagesModel;

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference();

        /*databaseReference.child("patchnotes").child(year_choice).orderByChild("patchinfo/"+ champion_Name).equalTo(champion_Name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    patchNoteModels.clear();
                    keys.clear();
                    for(DataSnapshot snapshot: dataSnapshot.getChildren()) {
                        PatchNoteModel patchNoteModel = snapshot.getValue(PatchNoteModel.class);
                        patchNoteModels.add(patchNoteModel);
                        keys.add(snapshot.getKey());
                    }
                    champion_isEmpty_view.setVisibility(View.GONE);
                }else{
                    champion_isEmpty_view.setVisibility(View.VISIBLE);
                }

                Collections.reverse(patchNoteModels);
                Collections.reverse(keys);

                notifyDataSetChanged();
                databaseReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(activity, "오류가 발생하였습니다. 잠시 후 시도해 주세요.", Toast.LENGTH_SHORT).show();
                databaseReference.removeEventListener(this);
            }
        });*/


    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_championnote,parent,false);

        return new ChampionNoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        final ChampionNoteViewHolder championNoteViewHolder = (ChampionNoteViewHolder)holder;

        if(contents.get(position).contentS != null){

            championNoteViewHolder.championS_linear.setVisibility(View.VISIBLE);
            championNoteViewHolder.championS_Content_text.setText(SkillNameModify(contents.get(position).contentS),TextView.BufferType.SPANNABLE);
        }else{
            championNoteViewHolder.championS_linear.setVisibility(View.GONE);
        }

        if(contents.get(position).contentP != null){
            championNoteViewHolder.championP_linear.setVisibility(View.VISIBLE);

            if(contents.get(position).status.length() != 3){
                Glide.with(holder.itemView.getContext())
                        .load(imagesModel.imageUrlP)
                        .apply(options)
                        .into(championNoteViewHolder.championP_ImageView);
            }else{
                Glide.with(holder.itemView.getContext())
                        .load(imagesModel.imageUrlr_P)
                        .apply(options)
                        .into(championNoteViewHolder.championP_ImageView);
            }
            championNoteViewHolder.championP_Content_text.setText(SkillNameModify(contents.get(position).contentP),TextView.BufferType.SPANNABLE);
        }else{
            championNoteViewHolder.championP_linear.setVisibility(View.GONE);
        }

        if(contents.get(position).contentQ != null){
            championNoteViewHolder.championQ_linear.setVisibility(View.VISIBLE);

            if(contents.get(position).status.length() != 3){
                Glide.with(holder.itemView.getContext())
                        .load(imagesModel.imageUrlQ)
                        .apply(options)
                        .into(championNoteViewHolder.championQ_ImageView);
            }else{
                Glide.with(holder.itemView.getContext())
                        .load(imagesModel.imageUrlr_Q)
                        .apply(options)
                        .into(championNoteViewHolder.championQ_ImageView);
            }
            championNoteViewHolder.championQ_Content_text.setText(SkillNameModify(contents.get(position).contentQ),TextView.BufferType.SPANNABLE);
        }else{
            championNoteViewHolder.championQ_linear.setVisibility(View.GONE);
        }

        if(contents.get(position).contentQQ != null){
            championNoteViewHolder.championQQ_linear.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(imagesModel.imageUrlQQ)
                    .apply(options)
                    .into(championNoteViewHolder.championQQ_ImageView);
            championNoteViewHolder.championQQ_Content_text.setText(SkillNameModify(contents.get(position).contentQQ),TextView.BufferType.SPANNABLE);
        }else{
            championNoteViewHolder.championQQ_linear.setVisibility(View.GONE);
        }

        if(contents.get(position).contentW != null){
            championNoteViewHolder.championW_linear.setVisibility(View.VISIBLE);

            if(contents.get(position).status.length() != 3){
                Glide.with(holder.itemView.getContext())
                        .load(imagesModel.imageUrlW)
                        .apply(options)
                        .into(championNoteViewHolder.championW_ImageView);
            }else{
                Glide.with(holder.itemView.getContext())
                        .load(imagesModel.imageUrlr_W)
                        .apply(options)
                        .into(championNoteViewHolder.championW_ImageView);
            }

            championNoteViewHolder.championW_Content_text.setText(SkillNameModify(contents.get(position).contentW),TextView.BufferType.SPANNABLE);
        }else{
            championNoteViewHolder.championW_linear.setVisibility(View.GONE);
        }

        if(contents.get(position).contentWW != null){
            championNoteViewHolder.championWW_linear.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(imagesModel.imageUrlWW)
                    .apply(options)
                    .into(championNoteViewHolder.championWW_ImageView);
            championNoteViewHolder.championWW_Content_text.setText(SkillNameModify(contents.get(position).contentWW),TextView.BufferType.SPANNABLE);
        }else{
            championNoteViewHolder.championWW_linear.setVisibility(View.GONE);
        }

        if(contents.get(position).contentE != null){
            championNoteViewHolder.championE_linear.setVisibility(View.VISIBLE);

            if(contents.get(position).status.length() != 3){
                Glide.with(holder.itemView.getContext())
                        .load(imagesModel.imageUrlE)
                        .apply(options)
                        .into(championNoteViewHolder.championE_ImageView);
            }else{
                Glide.with(holder.itemView.getContext())
                        .load(imagesModel.imageUrlr_E)
                        .apply(options)
                        .into(championNoteViewHolder.championE_ImageView);
            }

            championNoteViewHolder.championE_Content_text.setText(SkillNameModify(contents.get(position).contentE),TextView.BufferType.SPANNABLE);
        }else{
            championNoteViewHolder.championE_linear.setVisibility(View.GONE);
        }

        if(contents.get(position).contentEE != null){
            championNoteViewHolder.championEE_linear.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(imagesModel.imageUrlEE)
                    .apply(options)
                    .into(championNoteViewHolder.championEE_ImageView);
            championNoteViewHolder.championEE_Content_text.setText(SkillNameModify(contents.get(position).contentEE),TextView.BufferType.SPANNABLE);
        }else{
            championNoteViewHolder.championEE_linear.setVisibility(View.GONE);
        }

        if(contents.get(position).contentR != null){
            championNoteViewHolder.championR_linear.setVisibility(View.VISIBLE);

            if(contents.get(position).status.length() != 3){
                Glide.with(holder.itemView.getContext())
                        .load(imagesModel.imageUrlR)
                        .apply(options)
                        .into(championNoteViewHolder.championR_ImageView);
            }else{
                Glide.with(holder.itemView.getContext())
                        .load(imagesModel.imageUrlr_R)
                        .apply(options)
                        .into(championNoteViewHolder.championR_ImageView);
            }

            Glide.with(holder.itemView.getContext())
                    .load(imagesModel.imageUrlR)
                    .apply(options)
                    .into(championNoteViewHolder.championR_ImageView);
            championNoteViewHolder.championR_Content_text.setText(SkillNameModify(contents.get(position).contentR),TextView.BufferType.SPANNABLE);
        }else{
            championNoteViewHolder.championR_linear.setVisibility(View.GONE);
        }

        if(contents.get(position).contentRR != null){
            championNoteViewHolder.championRR_linear.setVisibility(View.VISIBLE);
            Glide.with(holder.itemView.getContext())
                    .load(imagesModel.imageUrlRR)
                    .apply(options)
                    .into(championNoteViewHolder.championRR_ImageView);
            championNoteViewHolder.championRR_Content_text.setText(SkillNameModify(contents.get(position).contentRR),TextView.BufferType.SPANNABLE);
        }else{
            championNoteViewHolder.championRR_linear.setVisibility(View.GONE);
        }

        if(contents.get(position).status != null){
            championNoteViewHolder.championStatus_Text.setVisibility(View.VISIBLE);

            String status;
            if(contents.get(position).status.length() == 3){
                status = contents.get(position).status.substring(0,2);
            }else{
                status = contents.get(position).status;
            }

            if (status.equals("버프") || status.equals("신규")) {
                championNoteViewHolder.championStatus_Text.setBackgroundResource(R.drawable.design_patch_status_green);
            } else if (status.equals("너프")) {
                championNoteViewHolder.championStatus_Text.setBackgroundResource(R.drawable.design_patch_status_red);
            } else if (status.equals("변경") || status.equals("수정")) {
                championNoteViewHolder.championStatus_Text.setBackgroundResource(R.drawable.design_patch_status_blue);
            }
            championNoteViewHolder.championStatus_Text.setText(status);
        }else{
            championNoteViewHolder.championStatus_Text.setVisibility(View.GONE);
        }

        if(patchKey.get(position).contains("a")){
            String patchArray[] = patchKey.get(position).split("-");
            championNoteViewHolder.championName_Text.setText(patchArray[0]+"."+patchArray[2]+" 패치");
        }else{
            String patchArray[] = patchKey.get(position).split("-");
            championNoteViewHolder.championName_Text.setText(patchArray[0]+"."+patchArray[1]+" 패치");
        }

        /*databaseReference.child("patchnotes").child(year_choice).child(keys.get(position)).child("contents").orderByChild("name").equalTo(champion_Name).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        PatchNoteModel.Contents contents = snapshot.getValue(PatchNoteModel.Contents.class);

                    }
                }
                databaseReference.removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(activity, "오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
                databaseReference.removeEventListener(this);
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return contents.size();
    }

    private class ChampionNoteViewHolder extends RecyclerView.ViewHolder {

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

        private TextView championName_Text;
        private TextView championStatus_Text;


        public ChampionNoteViewHolder(View view) {
            super(view);

            championS_linear = view.findViewById(R.id.item_championNote_championS_linear);
            championS_Content_text =  view.findViewById(R.id.item_championNote_championS_content);

            championP_linear =  view.findViewById(R.id.item_championNote_championP_linear);
            championP_ImageView =  view.findViewById(R.id.item_championNote_championP_imageview);
            championP_Content_text =  view.findViewById(R.id.item_championNote_championP_content);

            championQ_linear =  view.findViewById(R.id.item_championNote_championQ_linear);
            championQ_ImageView =  view.findViewById(R.id.item_championNote_championQ_imageview);
            championQ_Content_text =  view.findViewById(R.id.item_championNote_championQ_content);

            championQQ_linear = view.findViewById(R.id.item_championNote_championQQ_linear);
            championQQ_ImageView = view.findViewById(R.id.item_championNote_championQQ_imageview);
            championQQ_Content_text = view.findViewById(R.id.item_championNote_championQQ_content);

            championW_linear = view.findViewById(R.id.item_championNote_championW_linear);
            championW_ImageView = view.findViewById(R.id.item_championNote_championW_imageview);
            championW_Content_text = view.findViewById(R.id.item_championNote_championW_content);

            championWW_linear = view.findViewById(R.id.item_championNote_championWW_linear);
            championWW_ImageView = view.findViewById(R.id.item_championNote_championWW_imageview);
            championWW_Content_text = view.findViewById(R.id.item_championNote_championWW_content);

            championE_linear = view.findViewById(R.id.item_championNote_championE_linear);
            championE_ImageView = view.findViewById(R.id.item_championNote_championE_imageview);
            championE_Content_text = view.findViewById(R.id.item_championNote_championE_content);

            championEE_linear = view.findViewById(R.id.item_championNote_championEE_linear);
            championEE_ImageView = view.findViewById(R.id.item_championNote_championEE_imageview);
            championEE_Content_text =  view.findViewById(R.id.item_championNote_championEE_content);

            championR_linear = view.findViewById(R.id.item_championNote_championR_linear);
            championR_ImageView =  view.findViewById(R.id.item_championNote_championR_imageview);
            championR_Content_text =  view.findViewById(R.id.item_championNote_championR_content);

            championRR_linear = view.findViewById(R.id.item_championNote_championRR_linear);
            championRR_ImageView = view.findViewById(R.id.item_championNote_championRR_imageview);
            championRR_Content_text = view.findViewById(R.id.item_championNote_championRR_content);

            championName_Text = view.findViewById(R.id.item_championNote_champion_name);
            championStatus_Text = view.findViewById(R.id.item_championNote_champion_status);

        }
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
