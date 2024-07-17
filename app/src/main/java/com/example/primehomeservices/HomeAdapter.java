package com.example.primehomeservices;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class HomeAdapter extends BaseAdapter {
    private ArrayList<DataClass> dataList;
    private Context context;
    LayoutInflater layoutInflater;

    public HomeAdapter(ArrayList<DataClass> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup ViewGroup) {
        if (layoutInflater == null){
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view == null ){
            view = layoutInflater.inflate(R.layout.home_items, null);
        }
        ImageView gridImage = view.findViewById(R.id.serviceImage);
        TextView gridServiceName = view.findViewById(R.id.serviceText);

        Glide.with(context).load(dataList.get(i).getImageURL()).into(gridImage);
        gridServiceName.setText(dataList.get(i).getServicename());


        return view;
    }
}
