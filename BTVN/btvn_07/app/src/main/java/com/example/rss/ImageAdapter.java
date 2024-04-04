package com.example.rss;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageAdapter extends BaseAdapter {
    Integer[] images;
    Activity context;

    public ImageAdapter(Integer[] IMAGES, Activity CONTEXT) {
        this.images = IMAGES;
        this.context = CONTEXT;
    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public Object getItem(int i) {
        return images[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ImageView imageView;

        if (view == null) {
            imageView = (ImageView) LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.image_view, viewGroup, false);
        } else {
            imageView = (ImageView) view;
        }

        Glide.with(context).load(images[i]).centerCrop().into(imageView);
        return imageView;
    }
}
