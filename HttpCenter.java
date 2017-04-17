package com.welink.worker.http;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ImageView;

import com.welink.worker.BuildConfig;
import com.welink.worker.R;
import com.welink.worker.application.MyApplication;
import com.welink.worker.utils.LogUtil;
import com.welink.worker.utils.NetWorkUtil;
import com.welink.worker.utils.PropertyUtil;
import com.welink.worker.utils.TimeUtil;
import com.welink.worker.utils.VersionInfoUtil;

import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cc.cloudist.acplibrary.ACProgressFlower;


/**
 * Created by liuchao on 16/10/13.
 */
public class HttpCenter {

    private static String  methodKey = "method";
    private static String  timestampKey = "timestamp";
    private static String  formatKey = "format";
    private static String  formatValue = "json";
    private static String  versionKey = "version";
    private static String  versionValue = "1.0";
    private static String  charsetKey = "charset";
    private static String  charsetValue = "UTF-8";
    private static String  platformKey = "platform";
    private static String  platformValue = "android";
    private static String  paramsKey = "params";
    private volatile static HttpCenter instance;
    private Handler handler;
    private ImageOptions options;
    private static Context mContext;

    private HttpCenter(){
        handler = new Handler(Looper.getMainLooper());
    }

    /**
     * 单例模式
     *
     * @return
     */
    public static HttpCenter getInstance() {
        if (instance == null) {
            synchronized (HttpCenter.class) {
                if (instance == null) {
                    instance = new HttpCenter();
                }
            }
        }
        return instance;
    }

    /**
     * 数转换成jsonbody
     *
     * @param parame
     * @return
     * @throws Exception
     */
    private static JSONObject encodeJsonString(HashMap<String, Object> parame) throws Exception{
        JSONObject object = new JSONObject();
        Iterator<Map.Entry<String, Object>> iter = parame.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Object> info = iter.next();
            String key = info.getKey();//获取健
            Object content = info.getValue();//获取值
            object.put(key, content);
        }
        return object;
    }

    /**
     * 组织业务参数
     *
     * @param params
     * @return
     */
    private static String encodeRequestParamsBody(HashMap<String,Object> params){
        String request=null;
        HashMap<String,Object> localParams=new HashMap<>();

        try {
            localParams.put(methodKey,"POST");
            localParams.put(timestampKey, TimeUtil.getCurrentFormatDate());
            localParams.put(formatKey,formatValue);
            localParams.put(versionKey, VersionInfoUtil.getVersionName(MyApplication.getContext()));
            localParams.put(charsetKey,charsetValue);
            localParams.put(platformKey,platformValue);
            localParams.put(paramsKey,encodeJsonString(params));

            request=encodeJsonString(localParams).toString();
        }catch (Exception e){
            LogUtil.e("[异常] \n 异常原因:"+e.getMessage()+"\n异常信息位置:class:HttpCenter--mothod:encodeRequestParam");
            return request;

        }
        return request;
    }

    /**
     * 数据组织
     *
     * @param url
     * @param params
     * @return
     */
    public static RequestParams encodeRequestParams(String url,HashMap<String,Object> params){
        RequestParams requestParams=new RequestParams(url);
        requestParams.setConnectTimeout(BuildConfig.APP_TIME_OUT);
        requestParams.setAsJsonContent(true);
        requestParams.setBodyContent(encodeRequestParamsBody(params));
        return requestParams;
    }

    /**
     * 异步get请求
     *
     * @param url
     * @param maps
     * @param callBack
     * @param channelMark
     */
    public void get(String url, HashMap<String, Object> maps, final XCallBack callBack,final int channelMark) {

        RequestParams requestParams= HttpCenter.encodeRequestParams(url, maps);

        x.http().get(requestParams, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                onSuccessResponse(result, callBack, channelMark);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                onErrorResponse(ex, callBack, channelMark);
                ex.printStackTrace();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                onCancelledResponse(callBack, channelMark);
            }

            @Override
            public void onFinished() {

            }


        });

    }

    /**
     * 异步post请求
     *
     * @param url
     * @param maps
     * @param callback
     * @param channelMark
     */
    public void post(String url, HashMap<String, Object> maps, final XCallBack callback,final int channelMark) {

        RequestParams requestParams= HttpCenter.encodeRequestParams(url, maps);
        LogUtil.e("请求地址:\n" + url);
        LogUtil.e("请求参数:\n" + requestParams.getBodyContent());
        x.http().post(requestParams, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                LogUtil.e("返回结果:\n" + result);
                onSuccessResponse(result, callback, channelMark);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("抛出异常:\n" + ex.getMessage());
                onErrorResponse(ex, callback, channelMark);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                onCancelledResponse(callback, channelMark);
            }

            @Override
            public void onFinished() {

            }
        });
    }


    /**
     * 带缓存数据的异步 get请求
     *
     * @param url
     * @param maps
     * @param pnewCache
     * @param callback
     */
    public void getCache(String url, HashMap<String, Object> maps, final boolean pnewCache, final XCallBack callback,final int channelmark) {

        RequestParams requestParams= HttpCenter.encodeRequestParams(url, maps);

        x.http().get(requestParams, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String result) {
                onSuccessResponse(result, callback, channelmark);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                onErrorResponse(ex, callback, channelmark);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                onCancelledResponse(callback, channelmark);
            }

            @Override
            public void onFinished() {

            }

            @Override
            public boolean onCache(String result) {
                boolean newCache = pnewCache;
                if (newCache) {
                    newCache = !newCache;
                }
                if (!newCache) {
                    newCache = !newCache;
                    onSuccessResponse(result, callback, channelmark);
                }
                return newCache;
            }
        });
    }

    /**
     * 带缓存数据的异步 post请求
     *
     * @param url
     * @param maps
     * @param pnewCache
     * @param callback
     */
    public void postCache(String url, HashMap<String, Object> maps, final boolean pnewCache, final XCallBack callback,final int channelmark) {
        RequestParams requestParams= HttpCenter.encodeRequestParams(url, maps);
        LogUtil.e("请求地址:\n" + url);
        LogUtil.e("请求参数:\n" + requestParams.getBodyContent());
        x.http().post(requestParams, new Callback.CacheCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("返回结果:\n" + result);
                onSuccessResponse(result, callback, channelmark);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("抛出异常:\n" + ex.getMessage());
                onErrorResponse(ex, callback, channelmark);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                onCancelledResponse(callback, channelmark);
            }


            @Override
            public void onFinished() {

            }

            @Override
            public boolean onCache(String result) {
                boolean newCache = pnewCache;
                if (newCache) {
                    newCache = !newCache;
                }
                if (!newCache) {
                    newCache = !newCache;
                    onSuccessResponse(result, callback, channelmark);
                }
                return newCache;
            }
        });
    }

    /**
     * 正常图片显示
     *
     * @param iv
     * @param url
     * @param option
     */
    public void bindCommonImage(ImageView iv, String url, boolean option) {
        if (option) {
            options = new ImageOptions.Builder().setLoadingDrawableId(R.mipmap.no_pic).setFailureDrawableId(R.mipmap.no_pic).build();
            x.image().bind(iv, url, options);
        } else {
            x.image().bind(iv, url);
        }
    }

    /**
     * 圆形图片显示
     *
     * @param iv
     * @param url
     * @param option
     */
    public void bindCircularImage(ImageView iv, String url, boolean option) {
        if (option) {
            options = new ImageOptions.Builder().setLoadingDrawableId(R.mipmap.no_pic).setFailureDrawableId(R.mipmap.no_pic).setCircular(true).build();
            x.image().bind(iv, url, options);
        } else {
            x.image().bind(iv, url);
        }
    }

    /**
     * 文件上传
     *
     * @param url
     * @param maps
     * @param allFile
     * @param callback
     */
    public void upLoadFile(String url, Map<String, String> maps, List<File> allFile, final XCallBack callback,final int channelMark) {
        RequestParams params = new RequestParams(url);

        if (maps != null && !maps.isEmpty()) {
            for (Map.Entry<String, String> entry : maps.entrySet()) {
                params.addBodyParameter(entry.getKey(), (String) entry.getValue());
            }
        }

        if (allFile != null) {
            for (File file:allFile) {
                params.addBodyParameter("pictures", file);
            }
        }

        // 有上传文件时使用multipart表单, 否则上传原始文件流.
        params.setMultipart(true);
        LogUtil.e("请求地址:\n" + url);
        LogUtil.e("请求参数:\n" + params.getBodyContent());
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("返回结果:\n" + result);
                onSuccessResponse(result, callback,channelMark);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                onErrorResponse(ex,callback,channelMark);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                onCancelledResponse(callback,channelMark);
            }

            @Override
            public void onFinished() {

            }
        });

    }


    /**
     * 文件下载
     *
     * @param url
     * @param maps
     * @param callBack
     */
    public void downLoadFile(String url, String path,Map<String, String> maps, final XDownLoadCallBack callBack) {

        RequestParams params = new RequestParams(url);
        if (maps != null && !maps.isEmpty()) {
            for (Map.Entry<String, String> entry : maps.entrySet()) {
                params.addBodyParameter(entry.getKey(), entry.getValue());
            }
        }
        params.setAutoRename(true);// 断点续传
        params.setSaveFilePath(path);
        x.http().post(params, new Callback.ProgressCallback<File>() {
            @Override
            public void onSuccess(final File result) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callBack != null) {
                            callBack.onResponse(result);
                        }
                    }
                });
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callBack != null) {
                            callBack.onFinished();
                        }
                    }
                });
            }

            @Override
            public void onWaiting() {

            }

            @Override
            public void onStarted() {

            }

            @Override
            public void onLoading(final long total, final long current, final boolean isDownloading) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (callBack != null) {
                            callBack.onLoading(total, current, isDownloading);
                        }
                    }
                });
            }
        });

    }


    /**
     * 异步get请求返回结果,json字符串
     *
     * @param result
     * @param callBack
     */
    private void onSuccessResponse(final String result, final XCallBack callBack, final int channelMark) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onSuccess(result,channelMark);
                }
            }
        });
    }

    /**
     * 错误信息返回
     * @param e
     * @param callBack
     * @param channelMark
     */
    private void onErrorResponse(final Throwable e, final XCallBack callBack,final int channelMark) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onError(e,channelMark);
                }
            }
        });
    }

    /**
     * 请求取消
     * @param callBack
     * @param channelMark
     */
    private void onCancelledResponse(final XCallBack callBack,final int channelMark) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (callBack != null) {
                    callBack.onCancelled(channelMark);
                }
            }
        });
    }

    public interface XDownLoadCallBack extends XCallBack {
        void onResponse(File result);
        void onLoading(long total, long current, boolean isDownloading);
        void onFinished();
    }



    //通用回调方法
    public interface XCallBack {
        void onSuccess(String result,final int channelMark);
        void onError(Throwable e,final int channelMark);
        void onCancelled(final int channelMark);
    }

}
