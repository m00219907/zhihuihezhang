package com.zhihuihezhang.zj.jiaxing.net;

import android.content.Context;

/**
 * @param <T>
 */
public class CallBack<T> implements ResponseCall<T> {
    private Context mContext;
    private boolean shouldDecode = false;

    private CallBack() {
    }

    public CallBack(Context context) {
        mContext = context;
    }

    public CallBack(Context context, boolean shouldDecode) {
        mContext = context;
        this.shouldDecode = shouldDecode;
    }

    public boolean shouldDecode() {
        return shouldDecode;
    }

    @Override
    public void onNull() {
        if (mContext == null) return;
    }

    @Override
    public void onResultOk(T result) {
        if (mContext == null) return;
    }

    @Override
    public void onNetError() {
        if (mContext == null) return;
    }

    @Override
    public void onResultError(T result) {
        if (mContext == null) return;
        if (result instanceof BaseBean) {
        }
    }

}
