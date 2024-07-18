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
    private OnItemClickListener onItemClickListener;

    public HomeAdapter(ArrayList<DataClass> dataList, Context context) {
        this.dataList = dataList;
        this.context = context;
    }

    public interface OnItemClickListener {
        void onItemClick(DataClass dataClass);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    @Override
    public int getCount() {
        return dataList.size();
    }

    @Override
    public Object getItem(int i) {
        return dataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (layoutInflater == null) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (view == null) {
            view = layoutInflater.inflate(R.layout.home_items, null);
        }

        ImageView gridImage = view.findViewById(R.id.serviceImage);
        TextView gridServiceName = view.findViewById(R.id.serviceText);

        DataClass dataClass = dataList.get(i);
        Glide.with(context).load(dataClass.getImageURL()).into(gridImage);
        gridServiceName.setText(dataClass.getServicename());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(dataClass);
                }
            }
        });

        return view;
    }
}
