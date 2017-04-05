package com.zhihuihezhang.zj.jiaxing;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

public class WebviewActivity extends Activity implements CommonWebView.Iloadfailed{

    String url;
    private View map_load_failed;
    CommonWebView map_webview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_webview);
        url = getIntent().getStringExtra("url");

        map_load_failed = findViewById(R.id.map_load_failed);
        map_load_failed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                map_webview.setVisibility(View.VISIBLE);
                map_webview.loadUrl(url);
                map_load_failed.setVisibility(View.GONE);
            }
        });

        map_webview = (CommonWebView)findViewById(R.id.map_webview);
        map_webview.init(this);
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        map_webview.setVisibility(View.INVISIBLE);
        map_load_failed.setVisibility(View.VISIBLE);
    }
}
