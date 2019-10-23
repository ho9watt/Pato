package com.example.pato.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.pato.R;

import java.util.List;

public class BoardSpinnerAdapter extends BaseAdapter{

    private Context context;
    private List<String> data;
    private LayoutInflater inflater;


    public BoardSpinnerAdapter(Context context, List<String> data){
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if(data!=null) return data.size();
        else return 0;

    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null) {
            view = inflater.inflate(R.layout.spinner_board_xml, viewGroup, false);
        }

        if(data!=null){
            //데이터세팅
            String text = data.get(i);
            ((TextView)view.findViewById(R.id.spinner_board_name)).setText(text);
        }

        return view;
    }

    @Override
    public View getDropDownView(int i, View view, ViewGroup viewGroup) {
        if(view==null) {
            view = inflater.inflate(R.layout.spinner_board_dropdown, viewGroup, false);
        }

        String text = data.get(i);
        ((TextView)view.findViewById(R.id.spinner_board_name)).setText(text);

        return view;
    }
}
