package com.zhihuihezhang.zj.jiaxing;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.zhihuihezhang.zj.jiaxing.mainfragment.TabMapFragment;
import com.zhihuihezhang.zj.jiaxing.mainfragment.TabNewsFragment;
import com.zhihuihezhang.zj.jiaxing.mainfragment.TabRiverFragment;
import com.zhihuihezhang.zj.jiaxing.publicFragment.TabPublicFragment;
import com.zhihuihezhang.zj.jiaxing.rivermasterfragment.TabRivermasterFragment;
import com.zhihuihezhang.zj.jiaxing.videofragment.TabVideofragment;

import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements View.OnClickListener{

    public ArrayList<Fragment> fragments = new ArrayList<>();
    TextView main_toplayout_text;

    View main_secondlayout, main_map_view, main_riverinfo_view, main_news_view;

    View tab_home_layout, tab_rivermaster_layout, tab_video_layout, tab_public_layout;

    public int curIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        main_toplayout_text = (TextView)findViewById(R.id.main_toplayout_text);

        main_secondlayout = findViewById(R.id.main_secondlayout);
        main_map_view = findViewById(R.id.main_map_view);
        main_riverinfo_view = findViewById(R.id.main_riverinfo_view);
        main_news_view = findViewById(R.id.main_news_view);

        tab_home_layout = findViewById(R.id.tab_home_layout);
        tab_home_layout.setOnClickListener(this);
        tab_rivermaster_layout = findViewById(R.id.tab_rivermaster_layout);
        tab_rivermaster_layout.setOnClickListener(this);
        tab_video_layout = findViewById(R.id.tab_video_layout);
        tab_video_layout.setOnClickListener(this);
        tab_public_layout = findViewById(R.id.tab_public_layout);
        tab_public_layout.setOnClickListener(this);

        findViewById(R.id.main_map_layout).setOnClickListener(this);
        findViewById(R.id.main_riverinfo_layout).setOnClickListener(this);
        findViewById(R.id.main_news_layout).setOnClickListener(this);

        setChangeView();
        initTopTab(0);
    }

    public void setChangeView() {
        synchronized(this) {

            if (fragments.isEmpty()) {
                TabMapFragment mapFragment = new TabMapFragment();
                fragments.add(0, mapFragment);

                TabRiverFragment riverFragment = new TabRiverFragment();
                fragments.add(1, riverFragment);

                TabNewsFragment newsFragment = new TabNewsFragment();
                fragments.add(2, newsFragment);

                TabRivermasterFragment rivermasterFragment = new TabRivermasterFragment();
                fragments.add(3, rivermasterFragment);

                TabVideofragment videoFragment = new TabVideofragment();
                fragments.add(4, videoFragment);

                TabPublicFragment publicFragment = new TabPublicFragment();
                fragments.add(5, publicFragment);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.main_map_layout:
                initTopTab(0);
                break;
            case R.id.main_riverinfo_layout:
                initTopTab(1);
                break;
            case R.id.main_news_layout:
                initTopTab(2);
                break;
            case R.id.tab_home_layout:
                initTopTab(curIndex);
                break;
            case R.id.tab_rivermaster_layout:
                initTopTab(3);
                break;
            case R.id.tab_video_layout:
                initTopTab(4);
                break;
            case R.id.tab_public_layout:
                initTopTab(5);
                break;
            default:
                break;
        }
    }

    public void initTopTab(int selectIndex) {
        if(selectIndex < 3){
            curIndex = selectIndex;
            main_secondlayout.setVisibility(View.VISIBLE);
            if(selectIndex == 0){
                main_map_view.setVisibility(View.VISIBLE);
            }else{
                main_map_view.setVisibility(View.GONE);
            }
            if(selectIndex == 1){
                main_riverinfo_view.setVisibility(View.VISIBLE);
            }else{
                main_riverinfo_view.setVisibility(View.GONE);
            }
            if(selectIndex == 2){
                main_news_view.setVisibility(View.VISIBLE);
            }else{
                main_news_view.setVisibility(View.GONE);
            }
        }else{
            main_secondlayout.setVisibility(View.GONE);
        }

        initFragmentData(selectIndex);
        tab_home_layout.setBackgroundColor(0xffffffff);
        tab_rivermaster_layout.setBackgroundColor(0xffffffff);
        tab_video_layout.setBackgroundColor(0xffffffff);
        tab_public_layout.setBackgroundColor(0xffffffff);

        if(selectIndex < 3){
            tab_home_layout.setBackgroundColor(0xff66bb6a);
            main_toplayout_text.setText("嘉兴智慧河长");
        }else if(selectIndex == 3){
            tab_rivermaster_layout.setBackgroundColor(0xff66bb6a);
            main_toplayout_text.setText("河长中心");
        }else if(selectIndex == 4){
            tab_video_layout.setBackgroundColor(0xff66bb6a);
            main_toplayout_text.setText("河道在线");
        }else if(selectIndex == 5){
            tab_public_layout.setBackgroundColor(0xff66bb6a);
            main_toplayout_text.setText("公众参与");
        }
    }

    public void initFragmentData(int index) {

        while (getSupportFragmentManager().popBackStackImmediate()) {
            getSupportFragmentManager().popBackStack();
        }

        Fragment fragmentss = fragments.get(index);
        FragmentTransaction fts = getSupportFragmentManager().beginTransaction();

        if(!fragmentss.isAdded()){
            fts.add(R.id.main_fragment, fragmentss);
        }

        for(int i = 0; i < fragments.size(); i++){
            Fragment fragment = fragments.get(i);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            if(index == i && fragment.isAdded()){
                ft.show(fragment);
            }else if(fragment.isAdded()){
                ft.hide(fragment);
            }
            ft.commit();
        }
        fts.commit();

    }
}
