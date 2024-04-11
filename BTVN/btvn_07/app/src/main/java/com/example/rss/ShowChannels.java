package com.example.rss;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ShowChannels extends AppCompatActivity {

    ArrayAdapter<String> adapterMainSubjects;
    ListView myMainListView;
    Context context;
    SingleItem selectedNewsItem;
    String [][] vnexpress_url = {
            {"https://vnexpress.net/rss/the-gioi.rss", "Thế giới"},
            {"https://vnexpress.net/rss/thoi-su.rss", "Thời sự"},
            {"https://vnexpress.net/rss/gia-dinh.rss", "Đời sống"},
            {"https://vnexpress.net/rss/suc-khoe.rss", "Sức khỏe"},
            {"https://vnexpress.net/rss/giai-tri.rss", "Giải trí"},
            {"https://vnexpress.net/rss/the-thao.rss", "Thể thao"},
            {"https://vnexpress.net/rss/giao-duc.rss", "Giáo dục"}
    };
    String [][] dantri_url = {
            {"https://dantri.com.vn/rss/su-kien.rss", "Sự kiện"},
            {"https://dantri.com.vn/rss/xa-hoi.rss", "Xã hội"},
            {"https://dantri.com.vn/rss/the-gioi.rss", "Thế giới"},
            {"https://dantri.com.vn/rss/van-hoa.rss", "Văn hóa"},
            {"https://dantri.com.vn/rss/doi-song.rss", "Đời sống"},
            {"https://dantri.com.vn/rss/the-thao.rss", "Thể thao"},
            {"https://dantri.com.vn/rss/lao-dong-viec-lam.rss", "Lao động việc làm"}
    };

    String [][] thanhnien_url = {
            {"https://thanhnien.vn/rss/thoi-su.rss", "Thời sự"},
            {"https://thanhnien.vn/rss/kinh-te.rss", "Kinh tế"},
            {"https://thanhnien.vn/rss/the-gioi.rss", "Thế giới"},
            {"https://thanhnien.vn/rss/doi-song.rss", "Đời sống"},
            {"https://thanhnien.vn/rss/suc-khoe.rss", "Sức khỏe"},
            {"https://thanhnien.vn/rss/giao-duc.rss", "Giáo dục"},
            {"https://thanhnien.vn/rss/du-lich.rss", "Du lịch"}
    };

    String [][] tuoitre_url = {
            {"https://tuoitre.vn/rss/the-gioi.rss", "Thế giới"},
            {"https://tuoitre.vn/rss/thoi-su.rss", "Thời sự"},
            {"https://tuoitre.vn/rss/phap-luat.rss", "Pháp luật"},
            {"https://tuoitre.vn/rss/kinh-doanh.rss", "Kinh doanh"},
            {"https://tuoitre.vn/rss/giai-tri.rss", "Giải trí"},
            {"https://tuoitre.vn/rss/nhip-song-so.rss", "Nhịp sống"},
            {"https://tuoitre.vn/rss/giao-duc.rss", "Giáo dục"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_channels);

        String [][] myUrlCaptionMenu;
        Intent intent = getIntent();
        Bundle myBundle = intent.getExtras();
        String title = myBundle.getString("urlTitle");
        Integer logo = myBundle.getInt("urlImage");

        if("VNEXPRESS".equals(title)) {
            myUrlCaptionMenu = vnexpress_url;
        } else if ("DAN TRI".equals(title)) {
            myUrlCaptionMenu = dantri_url;
        }  else if ("THANH NIEN".equals(title)) {
            myUrlCaptionMenu = thanhnien_url;
        } else {
            myUrlCaptionMenu = tuoitre_url;
        }
        String[] myUrlCaption = new String[myUrlCaptionMenu.length];
        String[] myUrlAddress = new String[myUrlCaptionMenu.length];

        for (int i=0; i<myUrlAddress.length; i++) {
            myUrlAddress[i] = myUrlCaptionMenu[i][0]; myUrlCaption[i] = myUrlCaptionMenu[i][1];
        }
        context = getApplicationContext();
        this.setTitle("CHANNELS IN " + title);
        myMainListView = (ListView)this.findViewById(R.id.myListView);
        myMainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> _av, View _v, int _index, long _id) {
                String urlAddress = myUrlAddress[_index], urlCaption = myUrlCaption[_index];
                Intent callShowHeadlines = new Intent(ShowChannels.this, ShowHeadlines.class);
                Bundle myData = new Bundle();
                myData.putString("urlAddress", urlAddress);
                myData.putString("urlCaption", urlCaption);
                myData.putString("urlTitle", title);
                myData.putInt("urlImage", logo);
                callShowHeadlines.putExtras(myData); startActivity(callShowHeadlines);
            }
        });
        adapterMainSubjects = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, myUrlCaption);
        myMainListView.setAdapter(adapterMainSubjects);
    }
}