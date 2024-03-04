package com.example.w5_exercise;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends ArrayAdapter<String> {
    Context context; String[] items; Integer[] thumbnails;
    public MyAdapter(Context context, int layoutToBeInflated, Integer[] thumbnails, String[] items) {
        super(context, R.layout.layout_list, items);
        this.context = context;
        this.items = items;
        this.thumbnails = thumbnails;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row;
        if (convertView == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(R.layout.layout_list, null);
        } else {
            row = (View) convertView;
        }
        TextView label = (TextView) row.findViewById(R.id.info);
        ImageView icon = (ImageView) row.findViewById(R.id.icon);
        label.setText(items[position].split(",")[0]);
        icon.setImageResource(thumbnails[position]);
        return row;
    }
}
