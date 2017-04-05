package com.zhihuihezhang.zj.jiaxing.net;

public interface ResponseCall<T> {

    /**
     * 成功从请求中接收到正确的数据
     *
     * @param result
     */
    void onResultOk(T result);

    /**
     * 没有从服务器获取到json数据,网络或者服务器异常
     */
    void onNetError();

    /**
     * 从请求中接收到了数据，但是数据异常，可以得到服务端返回的错误码和提示信息
     *
     * @param result
     */
    void onResultError(T result);

    /**
     * 没有从服务器获取到json数据，或者从请求中接收到了数据，但是数据异常、解析出错
     */
    void onNull();

}
