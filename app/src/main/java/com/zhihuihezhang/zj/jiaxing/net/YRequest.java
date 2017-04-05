package com.zhihuihezhang.zj.jiaxing.net;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class YRequest extends BaseRequest {
    public static final String TAG = "YRequest";
    private static OkHttpClient okHttpClient;
    private static final int TIME_OUT = 15000;//读取、写入超时时间各15秒

    private YRequest() {
        if (okHttpClient == null) {
            X509TrustManager xtm = new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {
                }

                @Override
                public X509Certificate[] getAcceptedIssuers() {
                    X509Certificate[] x509Certificates = new X509Certificate[0];
                    return x509Certificates;
                }
            };

            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance("SSL");

                sslContext.init(null, new TrustManager[]{xtm}, new SecureRandom());

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
            HostnameVerifier DO_NOT_VERIFY = new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            };
            okHttpClient = new OkHttpClient.Builder()
//                    .addInterceptor(interceptor)
                    .sslSocketFactory(sslContext.getSocketFactory())
                    .hostnameVerifier(DO_NOT_VERIFY)
                    .readTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
                    .build();
        }
//            okHttpClient = new OkHttpClient();
    }


    public static YRequest getInstance() {
        return Loader.instance;
    }

    static class Loader {
        private static final YRequest instance = new YRequest();
    }

    @Override
    public void get(Context context, String url, Class<?> _class, HashMap<String, String> map, CallBack handler) {
        super.get(context, url, _class, map, handler);
        Request request = new Request.Builder().url(buildGetUrl(url, map)).addHeader("token", "").build();
        call(context, request, handler, _class);
    }

    @Override
    public void post(Context context, String url, Class<?> _class, HashMap<String, String> map, CallBack handler) {
        super.post(context, url, _class, map, handler);
        Request request = new Request.Builder().url(url).post(buildPostFormBody(url, map)).addHeader("token", "").build();
        call(context, request, handler, _class);
    }

    @Override
    public void get(String url, HashMap<String, String> map, Callback callback) {
        super.get(url, map, callback);
        Request request = new Request.Builder().url(buildGetUrl(url, map)).addHeader("token", "").build();
        call(request, callback);
    }

    @Override
    public void post(String url, HashMap<String, String> map, Callback callback) {
        super.post(url, map, callback);
        Request request = new Request.Builder().url(url).post(buildPostFormBody(url, map)).addHeader("token", "").build();
        call(request, callback);
    }

    @Override
    public void get(String url, HashMap<String, String> map) {
        super.get(url, map);
        get(url, map, null);
    }

    @Override
    public void post(String url, HashMap<String, String> map) {
        super.post(url, map);
        post(url, map, null);
    }

    @Override
    public void postFile(Context context, String url, Class<?> _class, HashMap<String, String> map, List<YFile> fileList, CallBack handler) {
        super.postFile(context, url, _class, map, fileList, handler);
        MultipartBody body = buildMultipartBody(url, map, fileList);
        Request request = new Request.Builder().url(url).addHeader("token", "").post(body)

                .build();
        call(context, request, handler, _class);
    }

    /**
     * 将请求参数拼接在url后，生成get请求url
     *
     * @param url 请求服务url
     * @param map 请求参数
     * @return get请求url
     */
    private String buildGetUrl(String url, HashMap<String, String> map) {
        Iterator iterator = map.entrySet().iterator();
        StringBuilder sb = new StringBuilder(url);
        boolean isFirstParam = true;
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (isFirstParam) {
                sb.append("?");
                isFirstParam = false;
            } else sb.append("&");
            sb.append(entry.getKey()).append("=").append(entry.getValue());
        }
        Log.i(TAG, "getUrl = " + sb.toString());
        return sb.toString();
    }

    /**
     * 生成一个用于post请求的FormBody
     *
     * @param map 请求参数
     * @return
     */
    private FormBody buildPostFormBody(String url, HashMap<String, String> map) {
        FormBody.Builder builder = new FormBody.Builder();
        Iterator iterator = map.entrySet().iterator();
        StringBuilder sb = new StringBuilder(url);
        boolean isFirstParam = true;
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (entry.getValue() != null) {
                builder.add(entry.getKey().toString(), String.valueOf(entry.getValue()));
            }
            if (isFirstParam) {
                sb.append("?");
                isFirstParam = false;
            } else sb.append("&");
            sb.append(entry.getKey()).append("=").append(String.valueOf(entry.getValue()));
        }
        Log.i(TAG, "postUrl=" + sb.toString());
        return builder.build();
    }

    /**
     * 生成多类型的请求体，可包含文件
     *
     * @param url
     * @param map      请求参数
     * @param fileList 文件列表
     * @return
     */
    private MultipartBody buildMultipartBody(String url, HashMap<String, String> map, List<YFile> fileList) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        Iterator iterator = map.entrySet().iterator();
        StringBuilder sb = new StringBuilder(url);
        boolean isFirstParam = true;
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (entry.getValue() != null) {
                builder.addFormDataPart(entry.getKey().toString(), String.valueOf(entry.getValue()));
            }

            if (isFirstParam) {
                sb.append("?");
                isFirstParam = false;
            } else sb.append("&");
            sb.append(entry.getKey()).append("=").append(String.valueOf(entry.getValue()));
        }

        for (int i = 0; i < fileList.size(); i++) {
            YFile yFile = fileList.get(i);
            String contentType = "";
            try {
                contentType = yFile.getFile().toURL().openConnection().getContentType();
            } catch (IOException e) {
                e.printStackTrace();
            }
            RequestBody fileBody = RequestBody.create(MediaType.parse(contentType), yFile.getFile());
            builder.addFormDataPart(yFile.getFileKey(), yFile.getFileName(), fileBody);

        }

        Log.i(TAG, "postUrl=" + sb.toString());
        return builder.build();
    }

    /**
     * 发起请求，并对请求回调结果进行处理
     * 需要注意的是：
     * 1.OkHttp的请求本身回调并不在主线程
     * 2.YRequest已经将Activity中的回调置于主线程中，Service则是在请求子线程中，如要在Service的回调
     * 中进行UI操作（如Toast），需要使用handler等方式实现
     * 3.response.body在调用一次后将会被释放，因此需要对结果进行持久化
     *
     * @param context
     * @param request         OkHttp请求
     * @param responseHandler 用于给client处理请求结果的回调对象
     * @param _class          解析对象目标类
     */
    private void call(final Context context, Request request, CallBack responseHandler, final Class<?> _class) {
        if (responseHandler == null) {
            responseHandler = new CallBack(context);
        }
        final CallBack finalHandler = responseHandler;
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                handleError(context, finalHandler);
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                String resp = response.body().string();
                Log.i(TAG, "response=" + resp);
                resp = handleDecode(finalHandler, resp);
                try {
                    final Object object = new Gson().fromJson(resp, _class);
                    handleResultWithContext(context, object, finalHandler);
                } catch (Exception e) {
                    handleError(context, finalHandler);
                    e.printStackTrace();
                }


            }
        });
    }

    private void handleResult(Object object, CallBack handler) {
        if (object != null && ((BaseBean) object).getSuccess().equals(CODE_SUCCESS)) {
            handler.onResultOk(object);
        } else {
            if (object != null) {
                handler.onResultError(object);
            }
            handler.onNull();
        }
    }

    /**
     * 根据context类型进行响应结果的事件分发处理
     *
     * @param context
     * @param object
     * @param handler
     */
    private void handleResultWithContext(final Context context, final Object object, final CallBack handler) {
        if (context == null) {
            return;
        } else if (context instanceof Service) {
            handleResult(object, handler);
        } else if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    handleResult(object, handler);
                }
            });
        }
    }

    /**
     * 错误、异常事件分发处理
     *
     * @param context
     * @param handler
     */
    private void handleError(final Context context, final CallBack handler) {
        if (context == null) {
            return;
        } else if (context instanceof Service) {
            handler.onNull();
            handler.onNetError();
        } else if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    handler.onNull();
                    handler.onNetError();

                }
            });
        }
    }

    /**
     * 对需要解密的串进行解密操作
     *
     * @param callBack
     * @param decodeString 待解密密文
     * @return
     */
    private String handleDecode(CallBack callBack, String decodeString) {
        if (callBack.shouldDecode()) {
            // 对resp进行解密
            //decodeString = CryptUtil.aesDecodeParameter(decodeString);
        }
        return decodeString;
    }

    /**
     * 发起请求
     *
     * @param request OkHttp请求
     */
    private void call(final Request request, Callback callback) {
        if (callback == null) {
            callback = new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String resp = response.body().string();
                    Log.i(TAG, "response ==" + resp);
                }
            };
        }
        okHttpClient.newCall(request).enqueue(callback);

    }

}
