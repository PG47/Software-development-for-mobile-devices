package com.example.w5_exercise;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


public class FragmentRight extends Fragment implements FragmentCallbacks {
    MainActivity main; TextView tv1; TextView tv2; TextView tv3; TextView tv4;
    public static FragmentRight newInstance(String strArg1) {
        FragmentRight fragment = new FragmentRight();
        Bundle bundle = new Bundle(); bundle.putString("arg1", strArg1);
        fragment.setArguments(bundle);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!(getActivity() instanceof MainCallbacks)) {
            throw new IllegalStateException( "Activity must implement MainCallbacks");
        }
        main = (MainActivity) getActivity(); // use this reference to invoke main callbacks
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        LinearLayout view_layout_right = (LinearLayout) inflater.inflate(R.layout.layout_details, null);
        tv1 = (TextView) view_layout_right.findViewById(R.id.userId);
        tv2 = (TextView) view_layout_right.findViewById(R.id.userName);
        tv3 = (TextView) view_layout_right.findViewById(R.id.userClass);
        tv4 = (TextView) view_layout_right.findViewById(R.id.userScore);
        try { Bundle arguments = getArguments(); tv1.setText(arguments.getString("arg1", "")); }
        catch (Exception e) { Log.e("RED BUNDLE ERROR – ", "" + e.getMessage()); }
        return view_layout_right;
    }
    @Override
    public void onMsgFromMainToFragment(String strValue) {
        String[] splitValue = strValue.split(",");
        tv1.setText(splitValue[0]);
        tv2.setText("Họ tên: " + splitValue[1]);
        tv3.setText("Lớp: " + splitValue[2]);
        tv4.setText("Điểm trung bình: " + splitValue[3]);
    }
}
