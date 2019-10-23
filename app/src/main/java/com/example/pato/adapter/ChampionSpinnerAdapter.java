package com.example.pato.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.pato.R;

public class ChampionSpinnerAdapter extends BaseAdapter {

    private Context context;
    private String []conson;
    private LayoutInflater inflater;

    public ChampionSpinnerAdapter(Context context, String[] conson) {
        this.context = context;
        this.conson = conson;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(conson!=null) return conson.length;
        else return 0;
    }

    @Override
    public Object getItem(int i) {
        return conson[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = inflater.inflate(R.layout.spinner_board_xml, viewGroup, false);
        }

        if(conson!=null){
            //데이터세팅
            String text = conson[i];
            ((TextView)view.findViewById(R.id.spinner_board_name)).setText(text);
        }

        return view;
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        if(view == null) {
            view = inflater.inflate(R.layout.spinner_board_dropdown, viewGroup, false);
        }

        String text = conson[i];
        ((TextView)view.findViewById(R.id.spinner_board_name)).setText(text);

        return view;
    }
}
