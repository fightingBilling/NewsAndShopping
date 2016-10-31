package com.wjc.beijingnews2.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by ${万嘉诚} on 2016/10/22.
 * WeChat：wjc398556712
 * Function：本地缓存工具类
 */
public class LocalCacheUtils {

    private final MemoryCacheUtils memoryCacheUtils;

    public LocalCacheUtils(MemoryCacheUtils memoryCacheUtils) {
        this.memoryCacheUtils = memoryCacheUtils;
    }


    /**
     * 根据Url获取图片
     *
     * @param imageUrl
     * @return
     */
    public Bitmap getBitmapFromUrl(String imageUrl) {
        //判断sdcard是否挂载
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //保存图片在/mnt/sdcard/beijingnews/http://192.168.21.165:8080/xsxxxx.png
            //保存图片在/mnt/sdcard/beijingnews/llkskljskljklsjklsllsl
            try {
                String fileName = MD5Encoder.encode(imageUrl);//llkskljskljklsjklsllsl
                ///mnt/sdcard/beijingnews/llkskljskljklsjklsllsl

                File file = new File(Environment.getExternalStorageDirectory() + "/beijingnews", fileName);
                String filePath = file.getAbsolutePath();

                if (file.exists()) {
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);
                    if (bitmap != null) {
                        LogUtil.e("把图片从本地保存到内存中");
                        memoryCacheUtils.putBitmap(imageUrl, bitmap);
                    }
                    return bitmap;
                }

            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("图片本地获取失败");
            }
        }
        return null;
    }



    /**
     * 根据Url保存图片
     *
     * @param imageUrl url
     * @param bitmap   图片
     */
    public void putBitmap(String imageUrl, Bitmap bitmap) {
        //判断sdcard是否挂载
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //保存图片在/mnt/sdcard/beijingnews/http://192.168.21.165:8080/xsxxxx.png
            //保存图片在/mnt/sdcard/beijingnews/llkskljskljklsjklsllsl
            try {
                String fileName = MD5Encoder.encode(imageUrl);//llkskljskljklsjklsllsl
                ///mnt/sdcard/beijingnews/llkskljskljklsjklsllsl
                File file = new File(Environment.getExternalStorageDirectory() + "/beijingnews", fileName);
                File parentFile = file.getParentFile();//mnt/sdcard/beijingnews
                if (!parentFile.exists()) {
                    //创建目录
                    parentFile.mkdirs();
                }

                if (!file.exists()) {
                    file.createNewFile();
                }
                //保存图片
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
                LogUtil.e("把图片保存到本地中");

            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e("图片本地缓存失败");
            }
        }

    }
}
