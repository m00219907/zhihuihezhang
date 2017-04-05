package com.zhihuihezhang.zj.jiaxing.publicFragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.zhihuihezhang.zj.jiaxing.CommonWebView;
import com.zhihuihezhang.zj.jiaxing.Constant;
import com.zhihuihezhang.zj.jiaxing.R;

public class TabPublicFragment extends Fragment implements CommonWebView.Iloadfailed{

    private View view, map_load_failed;
    Activity activity;
    CommonWebView map_webview;

    @Override
    public void onAttach(Activity activity) {
        this.activity = activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.map_webview, null);

        map_load_failed = view.findViewById(R.id.map_load_failed);
        map_load_failed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map_webview.setVisibility(View.VISIBLE);
                map_webview.loadUrl(Constant.BaseAddr + "public.php");
                map_load_failed.setVisibility(View.GONE);
            }
        });

        map_webview = (CommonWebView)view.findViewById(R.id.map_webview);
        map_webview.init(activity);
        map_webview.loadUrl(Constant.BaseAddr + "public.php");
        return view;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        map_webview.setVisibility(View.INVISIBLE);
        map_load_failed.setVisibility(View.VISIBLE);
    }

}
