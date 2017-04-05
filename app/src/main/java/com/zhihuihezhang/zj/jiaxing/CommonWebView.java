package com.zhihuihezhang.zj.jiaxing;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.webkit.GeolocationPermissions;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class CommonWebView extends WebView {
    private Activity activity;

    public static final int FILECHOOSER_RESULTCODE = 1;
    protected static final int REQ_CAMERA = FILECHOOSER_RESULTCODE + 1;
    protected static final int REQ_CHOOSE = REQ_CAMERA + 1;
    protected static final int REQ_CHOOSES = REQ_CHOOSE + 1;
    ValueCallback<Uri> mUploadMessage;

    private ValueCallback<Uri[]> mUploadMessageForAndroid5;
    private int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 5;

    public CommonWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CommonWebView(Context context) {
        super(context);
    }

    public void init(Activity activity) {
        this.activity = activity;

        WebSettings settings = this.getSettings();
        settings.setDefaultTextEncodingName("utf-8");
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setAllowFileAccess(true);// 设置允许访问文件数据
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setUseWideViewPort(true);
        settings.setLoadWithOverviewMode(true);
        // 设置支持缩放
        settings.setBuiltInZoomControls(false);
        settings.setDatabaseEnabled(true);
        // 使用localStorage则必须打开
        settings.setDomStorageEnabled(true);
        settings.setGeolocationEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        //设置UA
        String ua = settings.getUserAgentString();
        settings.setUserAgentString(ua+"; ZhiHuiYunHeZhang/ZJ/JiaXing/"+ CommonTools.getVersionNum(activity));

        this.setWebChromeClient(new MyWebChromeClient());
        this.setWebViewClient(new CommonWebViewClient());
    }


    private class MyWebChromeClient extends WebChromeClient {

        //方案2
        //3.0++版本
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            openFileChooserImpl(uploadMsg);
        }

        //3.0--版本
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooserImpl(uploadMsg);
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            openFileChooserImpl(uploadMsg);
        }

        // For Android > 5.0
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> uploadMsg, WebChromeClient.FileChooserParams fileChooserParams) {
            openFileChooserImplForAndroid5(uploadMsg);
            return true;
        }

        @Override
        public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
            // Always grant permission since the app itself requires location
            // permission and the user has therefore already granted it
            callback.invoke(origin, true, false);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            switch (newProgress) {
                case 0:
                    handler.sendEmptyMessage(0);
                    break;
                case 100:
                    handler.sendEmptyMessage(1);
                    break;
            }
        }
    }

    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        activity.startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
    }

    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
        mUploadMessageForAndroid5 = uploadMsg;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");

        activity.startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    DialogUtils.showWaitDialog(activity, "加载中...", 1000);
                    break;
                case 1:
                    DialogUtils.dismissDialog();
                    break;
            }
        }
    };

    public class CommonWebViewClient extends WebViewClient {

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed(); // Ignore SSL certificate errors
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            if(iloadfailed!=null){
                iloadfailed.onReceivedError(view, errorCode, description, failingUrl);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return false;
        }
    }

    public interface Iloadfailed{
        void onReceivedError(WebView view, int errorCode, String description, String failingUrl);
    }

    Iloadfailed iloadfailed;

    public Iloadfailed getIloadfailed() {
        return iloadfailed;
    }

    public void setIloadfailed(Iloadfailed iloadfailed) {
        this.iloadfailed = iloadfailed;
    }
}
