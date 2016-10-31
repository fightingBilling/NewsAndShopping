package com.wjc.beijingnews2.volley;

import android.app.ActivityManager;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by ${万嘉诚} on 2016/10/21.
 * WeChat：wjc398556712
 * Function：对Volley的封装
 */
public class VolleyManager {
    /**
     * 请求队列
     */
    private static RequestQueue requestQueue;
    /**
     * 图片加载工具类
     */
    private static ImageLoader imageLoader;

    public VolleyManager() {
    }

    /**
     * 使用Volley请求网络和图片是首先调用该方法
     * 建议在Application 的onCreater中调用
     *
     * @param context
     */
    public static void init(Context context) {
        requestQueue = Volley.newRequestQueue(context);
        //告诉你你的机器还有多少内存，在计算缓存大小的时候会比较有用
        ActivityManager activityManager = (ActivityManager)(context.getSystemService(Context.ACTIVITY_SERVICE));
        int memClass =  activityManager.getMemoryClass();
        // Use 1/8th of the available memory for this memory cache.
        int cacheSize = 1024 * 1024 * memClass / 8;//图片缓存的空间
        imageLoader = new ImageLoader(requestQueue,new BitmapLruCache(cacheSize));

    }

    /**
     * 得到消息队列
     * @return
     */
    public static RequestQueue getRequestQueue() {
        if(requestQueue != null) {
            return requestQueue;
        } else {
            throw new IllegalStateException("RequestQueue not initialized");
        }
    }

    /**
     * 把请求添加到队列中
     * @param request
     * @param tag
     */
    public static void addRequest(Request<?> request, Object tag) {
        if(tag != null) {
            request.setTag(tag);
        }
        requestQueue.add(request);
    }

    /**
     * 请求的取消
     * @param tag
     */
    public static void cancelAll(Object tag) {
        requestQueue.cancelAll(tag);
    }

    /**
     * Returns instance of ImageLoader initialized with {@see FakeImageCache}
     * which effectively means that no memory caching is used. This is useful
     * for images that you know that will be show only once.
     *
     * @return
     */
    public static ImageLoader getImageLoader() {
        if(imageLoader != null) {
            return imageLoader;
        } else {
            throw new IllegalStateException("ImageLoader not initialized");
        }
    }
}
