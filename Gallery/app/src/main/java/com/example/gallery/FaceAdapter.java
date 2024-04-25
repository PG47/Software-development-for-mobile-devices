package com.example.gallery;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class FaceAdapter extends BaseAdapter {
    private Context context;
    Bitmap[] facesBitmap;
    String[] names;

    public FaceAdapter(Context activityContext, Bitmap[] allFaces, String[] allNames) {
        context = activityContext;
        facesBitmap = allFaces;
        names = allNames;
    }

    @Override
    public int getCount() {
        return facesBitmap.length;
    }

    @Override
    public Object getItem(int i) {
        return facesBitmap[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public String[] getAllNames() {
        return names;
    }
    public Bitmap[] getAllImages() {
        return facesBitmap;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;

        if (view == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            view = inflater.inflate(R.layout.fragment_custom_grid_face, viewGroup, false);

            holder = new ViewHolder();
            holder.imageView = (ImageView) view.findViewById(R.id.image);
            holder.textView = (TextView) view.findViewById(R.id.tvName);
            holder.editText = (EditText) view.findViewById(R.id.name);

            holder.editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String text = editable.toString().trim();
                    names[i] = text;
                }
            });

            int itemWidth = dpToPx(170);
            view.setLayoutParams(new GridView.LayoutParams(itemWidth, GridView.AUTO_FIT));

            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        holder.imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        holder.imageView.setImageBitmap(facesBitmap[i]);
        holder.textView.setText("Name:");
        holder.editText.setText(names[i]);

        return view;
    }
    static class ViewHolder {
        ImageView imageView;
        TextView textView;
        EditText editText;
    }
    private int dpToPx(int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
