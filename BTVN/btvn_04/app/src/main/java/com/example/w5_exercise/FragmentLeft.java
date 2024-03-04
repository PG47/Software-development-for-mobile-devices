package com.example.w5_exercise;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class FragmentLeft extends Fragment {
    MainActivity mainActivity;
    Context context = null;
    String message = "";
    ListView listView;
    private final String[] items = {
            "A1_9829,Lê Thị A,A1,8",
            "A1_1809,Lê Thị B,A1,9",
            "A2_3509,Lê Thị C,A1,10",
            "A2_3100,Lê Thị D,A1,7",
            "A1_1120,Lê Thị E,A1,6",
            "A3_4120,Lê Thị F,A1,5",
            "A2_8100,Lê Thị G,A1,4",
            "A4_1160,Lê Thị H,A1,3"} ;

    private int curr_pos=0;
    Integer[] thumbnails = {R.mipmap.m1_foreground, R.mipmap.m1_foreground, R.mipmap.m1_foreground, R.mipmap.m1_foreground, R.mipmap.m1_foreground, R.mipmap.m1_foreground, R.mipmap.m1_foreground, R.mipmap.m1_foreground};
    public static FragmentLeft newInstance(String strArg) {
        FragmentLeft fragment = new FragmentLeft();
        Bundle args = new Bundle();
        args.putString("strArg1", strArg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            context = getActivity();
            mainActivity = (MainActivity) getActivity();
        }
        catch (IllegalStateException e) {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout layoutleft = (LinearLayout) inflater.inflate(R.layout.layout_list_outline, null);
        final TextView txtBlue = (TextView) layoutleft.findViewById(R.id.textMsg);
        listView = (ListView) layoutleft.findViewById(R.id.myList);
        listView.setBackgroundColor(Color.parseColor("#ffccddff"));

        if (getActivity() instanceof MainActivity) {
            mainActivity = (MainActivity) getActivity();
        } else {
            throw new IllegalStateException("MainActivity must implement callbacks");
        }

        MyAdapter adapter = new MyAdapter(context, R.layout.layout_list_outline, thumbnails, items);
        listView.setAdapter(adapter);
        listView.setSelection(0);
        listView.smoothScrollToPosition(0);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                mainActivity.onMsgFromFragToMain("BLUE-FRAG", items[position]);
                curr_pos = position;
                txtBlue.setText("Mã số: " + items[position].split(",")[0]);
                for (int i = 0; i < parent.getChildCount(); i++) {
                    View itemView = parent.getChildAt(i);
                    itemView.setBackgroundColor(Color.WHITE);
                }
                v.setBackgroundColor(Color.rgb(68,85,90));
            }});

        return layoutleft;
    }

    public void navigateToFirstItem() {
        Release(curr_pos);
        curr_pos=0;
        Select(curr_pos);
        listView.setSelection(0);
        listView.smoothScrollToPosition(0);
        mainActivity.onMsgFromFragToMain("BLUE-FRAG", items[0]);
    }

    public void navigateToNextItem() {
        Release(curr_pos);
        if (curr_pos < items.length - 1) {
            curr_pos++; }
        else curr_pos =0;
        listView.setSelection(curr_pos);
        listView.smoothScrollToPosition(curr_pos);
        Select(curr_pos);
        mainActivity.onMsgFromFragToMain("BLUE-FRAG", items[curr_pos]);
    }

    public void Select(int pos) {
        View v = listView.getChildAt(curr_pos);
        v.setBackgroundColor(Color.rgb(68,85,90));
    }
    public void Release(int pos) {
        View v = listView.getChildAt(curr_pos);
        v.setBackgroundColor(Color.rgb(255,255,255));
    }
}
