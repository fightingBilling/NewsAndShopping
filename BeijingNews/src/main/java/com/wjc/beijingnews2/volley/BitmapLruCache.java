package com.wjc.beijingnews2.volley;

import android.graphics.Bitmap;

import com.android.volley.toolbox.ImageLoader;

import org.xutils.cache.LruCache;

/**
 * Created by ${万嘉诚} on 2016/10/21.
 * WeChat：wjc398556712
 * Function：
 */
public class BitmapLruCache extends LruCache<String,Bitmap> implements ImageLoader.ImageCache {
    /**
     * @param maxSize for caches that do not override {@link #sizeOf}, this is
     *                the maximum number of entries in the cache. For all other caches,
     *                this is the maximum sum of the sizes of the entries in this cache.
     */
    public BitmapLruCache(int maxSize) {
        super(maxSize);
    }

    /**
     * 计算每张图片的大小
     * @param key
     * @param value
     * @return
     */
    @Override
    protected int sizeOf(String key, Bitmap value) {
        return value.getRowBytes() * value.getHeight();
    }

    @Override
    public Bitmap getBitmap(String url) {
        return get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        put(url,bitmap);
    }
}
