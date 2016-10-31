package com.wjc.beijingnews2.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by ${万嘉诚} on 2016/10/22.
 * WeChat：wjc398556712
 * Function：网络缓存工具类
 */
public class NetCacheUtils {
    /**
     * 请求图片成功
     */
    public static final int SUCESS = 1;
    /**
     * 请求图片失败
     */
    public static final int FAIL = 2;

    private final Handler handler;
    /**
     * 线程池类
     */
    private ExecutorService service;
    /**
     * 本地缓存工具类
     */
    private final LocalCacheUtils localCacheUtils;
    /**
     * 内存缓存工具类
     */
    private final MemoryCacheUtils memoryCacheUtils;

    public NetCacheUtils(Handler handler, LocalCacheUtils localCacheUtils, MemoryCacheUtils memoryCacheUtils) {
        this.handler = handler;
        this.localCacheUtils = localCacheUtils;
        this.memoryCacheUtils = memoryCacheUtils;
        service = Executors.newFixedThreadPool(10);
    }

    //联网请求得到图片
    public void getBitmapFomNet(String imageUrl, int position) {
//        new Thread(new MyRunnable(imageUrl,position)).start();
        //效率高，节约资源
        service.execute(new MyRunnable(imageUrl, position));
    }

    class MyRunnable implements Runnable {
        private final String imageUrl;
        private final int position;

        public MyRunnable(String imageUrl, int position) {
            this.imageUrl = imageUrl;
            this.position = position;
        }

        @Override
        public void run() {
            //子线程
            //请求网络图片
            InputStream is = null;
            try {
                URL url = new URL(imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                int reponseCode = connection.getResponseCode();
                if(reponseCode == 200) {//联网成功
                    LogUtil.e("从网络获取图片联网成功");
                    is = connection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(is);

                    //显示到控件上,发消息吧Bitmap和position发出去
                    Message message = Message.obtain();
                    message.what = SUCESS;
                    message.obj = bitmap;
                    message.arg1 = position;
                    handler.sendMessage(message);
                    LogUtil.e("向handler发送message.what = SUCESS");

                    //在内存中缓存一份
                    memoryCacheUtils.putBitmap(imageUrl,bitmap);
                    //在本地中缓存一份
                    localCacheUtils.putBitmap(imageUrl,bitmap);

                }

            } catch (Exception e) {
                e.printStackTrace();
                Message message = Message.obtain();
                message.what = FAIL;
                message.arg1 = position;
                handler.sendMessage(message);
                LogUtil.e("向handler发送message.what = FAIL");
            } finally {
                if(is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

}
