package com.example.plantscanapp.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.plantscanapp.R;
import com.example.plantscanapp.model.HistoryModel;

import java.util.List;

public class HistoryAdapter extends BaseAdapter {
    Activity activity;
    LayoutInflater layoutInflater;
    List<HistoryModel> list;

    public HistoryAdapter(Activity activity,List<HistoryModel> list){
        this.activity=activity;
        this.list=list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(layoutInflater==null)
            layoutInflater=(LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(view==null)
            view=layoutInflater.inflate(R.layout.history_model,null);
        TextView name=(TextView)view.findViewById(R.id.name);
        TextView date=(TextView)view.findViewById(R.id.date);

        final HistoryModel hm=list.get(i);

        name.setText(hm.getName());
        date.setText(hm.getDate());

        return view;
    }
}
