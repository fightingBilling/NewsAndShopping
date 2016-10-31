package com.wjc.beijingnews2.menudetailpager;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.wjc.beijingnews2.R;
import com.wjc.beijingnews2.base.MenuDetailBasePager;
import com.wjc.beijingnews2.bean.NewsCenterPagerBean2;
import com.wjc.beijingnews2.bean.PhotosDetailPagerBean;
import com.wjc.beijingnews2.utils.BitmapCacheUtils;
import com.wjc.beijingnews2.utils.CacheUtils;
import com.wjc.beijingnews2.utils.Contants;
import com.wjc.beijingnews2.utils.LogUtil;
import com.wjc.beijingnews2.utils.NetCacheUtils;
import com.wjc.beijingnews2.volley.VolleyManager;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.util.List;

import okhttp3.Call;

/**
 * Created by ${万嘉诚} on 2016/10/17.
 * WeChat：wjc398556712
 * Function：互动详情页面
 */
public class InteractMenuDetailPager extends MenuDetailBasePager {

    private final NewsCenterPagerBean2.NewsCenterPagerData newsCenterPagerData;
    @ViewInject(R.id.listview)
    private ListView listView;

    @ViewInject(R.id.gridview)
    private GridView gridView;

    private String url;
    private List<PhotosDetailPagerBean.DataBean.NewsBean> news;
    private PhotosDetailPagerAdapter adapter;
    /***
     * 图片三级缓存工具类：
     */
    private BitmapCacheUtils bitmapCacheUtils;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            switch (msg.what) {
                case NetCacheUtils.SUCESS ://图片请求成功
                    int position = msg.arg1;
                    Bitmap bitmap = (Bitmap) msg.obj;

                    if(listView != null & news.size()>0) {
                        ImageView iv_icon = (ImageView) listView.findViewWithTag(position);
                        if(iv_icon != null && bitmap != null) {
                            iv_icon.setImageBitmap(bitmap);
                        }
                    }
                    LogUtil.e("请求图片成功=="+position);
                    break;
                case NetCacheUtils.FAIL :
                    position = msg.arg1;
                    LogUtil.e("请求图片失败=="+position);
                    break;
            }

        }
    };

    public InteractMenuDetailPager(Context context, NewsCenterPagerBean2.NewsCenterPagerData newsCenterPagerData) {
        super(context);
        this.newsCenterPagerData = newsCenterPagerData;
        bitmapCacheUtils = new BitmapCacheUtils(handler);
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.photo_menu_detailpager, null);
        x.view().inject(this, view);
        return view;
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("互动详情页面数据被初始化了..");

        url = Contants.BASE_URL + newsCenterPagerData.getUrl();
        String saveJson = CacheUtils.getString(context, url);
        if (!TextUtils.isEmpty(saveJson)) {
            processData(saveJson);
        }
//        getDataForNet();
        getDataFromNetByOkHttpUtils();
    }

    private void getDataFromNetByOkHttpUtils() {
        OkHttpUtils
                .get()
                .url(url)
                .id(100)
                .build()
                .execute(new MyStringCallback());
    }

    public class MyStringCallback extends StringCallback {

        @Override
        public void onBefore(okhttp3.Request request, int id) {
            super.onBefore(request, id);
        }

        @Override
        public void onAfter(int id) {
            super.onAfter(id);
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            LogUtil.e("okhttpUtils互动请求数据失败==" + e.getMessage());
            e.printStackTrace();
        }

        @Override
        public void onResponse(String response, int id) {
            LogUtil.e("okhttpUtils互动请求数据成功==" + response);
            //解析json数据
            CacheUtils.putString(context, url, response);
            processData(response);
            switch (id)
            {
                case 100:
                    Toast.makeText(context, "http", Toast.LENGTH_SHORT).show();
                    break;
                case 101:
                    Toast.makeText(context, "https", Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        @Override
        public void inProgress(float progress, long total, int id) {
            super.inProgress(progress, total, id);
        }
    }

    private void getDataForNet() {
        StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                LogUtil.e("使用Volley联网请求成功==" + result);
                //缓存数据
                CacheUtils.putString(context, url, result);

                processData(result);
                //设置适配器

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                LogUtil.e("使用Volley联网请求失败==" + volleyError.getMessage());
            }
        }) {//解决中文字符乱码
            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                try {
                    String parsed = new String(response.data, "UTF-8");
                    return Response.success(parsed, HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                return super.parseNetworkResponse(response);
            }
        };
        //添加到队列
        VolleyManager.getRequestQueue().add(request);

    }

    /**
     * true,显示ListView，隐藏GridView
     * false,显示GridView,隐藏ListView
     */
    private boolean isShowListView = true;

    public void swichListAndGrid(ImageButton ib_swich_list_grid) {
        if(isShowListView) {
            isShowListView = false;
            //显示GridView,隐藏ListView
            gridView.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            gridView.setAdapter(new PhotosDetailPagerAdapter());
            //按钮显示--ListView
            ib_swich_list_grid.setImageResource(R.drawable.icon_pic_list_type);
        }else {
            isShowListView = true;
            //隐藏GridView,显示ListView
            gridView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            listView.setAdapter(new PhotosDetailPagerAdapter());
            //按钮显示--GridView
            ib_swich_list_grid.setImageResource(R.drawable.icon_pic_grid_type);
        }
    }

    /**
     * 解析和显示数据
     *
     * @param json
     */
    private void processData(String json) {
        PhotosDetailPagerBean bean = parseJson(json);
        LogUtil.e("图组解析成功==" + bean.getData().getNews().get(0).getTitle());

        isShowListView = true;
        //设置适配器
        news = bean.getData().getNews();
        adapter = new PhotosDetailPagerAdapter();
        listView.setAdapter(adapter);
    }


    class PhotosDetailPagerAdapter extends BaseAdapter {

        private DisplayImageOptions options;

        public PhotosDetailPagerAdapter(){
            options = new DisplayImageOptions.Builder()
                    .showImageOnLoading(R.drawable.pic_item_list_default)
                    .showImageForEmptyUri(R.drawable.pic_item_list_default)
                    .showImageOnFail(R.drawable.pic_item_list_default)
                    .cacheInMemory(true)
                    .cacheOnDisk(true)
                    .considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .displayer(new RoundedBitmapDisplayer(20))//矩形圆角图片
                    .build();
        }

        @Override
        public int getCount() {
            return news != null ? news.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return news != null ? news.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = View.inflate(context, R.layout.item_photos_menudetail_pager, null);
                viewHolder = new ViewHolder();
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            //根据位置得到对应的数据
            PhotosDetailPagerBean.DataBean.NewsBean newsBean = news.get(position);
            viewHolder.tv_title.setText(newsBean.getTitle());
            String imageUrl = Contants.BASE_URL + newsBean.getSmallimage();
            //1、使用Volley请求图片-设置图片了
//            loaderImager(viewHolder, imageUrl);
            //2.使用自定义的三级缓存请求图片
            viewHolder.iv_icon.setTag(position);
            Bitmap bitmap = bitmapCacheUtils.getBitmap(imageUrl,position);//内存或者本地
            if(bitmap != null) {
                viewHolder.iv_icon.setImageBitmap(bitmap);
            }
            //2.使用Picasso请求图片
//            Picasso.with(context)
//                    .load(Constants.BASE_URL + item.getListimage())
//                    .placeholder(R.drawable.pic_item_list_default)
//                    .error(R.drawable.pic_item_list_default)
//                    .into(viewHolder.iv_icon);

            //3.使用Glide请求图片
//            Glide
//                    .with(context)
//                    .load(Constants.BASE_URL + item.getListimage())
//                    .centerCrop()
//                    .placeholder(R.drawable.pic_item_list_default)
//                    .crossFade()
//                    .into(viewHolder.iv_icon);


            //4.使用ImageLoader加载网络图片
            com.nostra13.universalimageloader.core.ImageLoader
                    .getInstance().displayImage(Contants.BASE_URL + newsBean.getListimage()
                    , viewHolder.iv_icon, options);
            return convertView;
        }
    }

    /**
     * 使用Volley请求图片
     *
     * @param viewHolder
     * @param imageUrl
     */
    private void loaderImager(final ViewHolder viewHolder, String imageUrl) {
        //设置tag
        viewHolder.iv_icon.setTag(imageUrl);
        //直接在这里请求会乱位置
        ImageLoader.ImageListener imageListener = new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer imageContainer, boolean b) {
                if (imageContainer != null) {
                    if (viewHolder.iv_icon != null) {
                        if (imageContainer.getBitmap() != null) {
                            //设置图片
                            viewHolder.iv_icon.setImageBitmap(imageContainer.getBitmap());
                        } else {
                            //设置默认图片
                            viewHolder.iv_icon.setImageResource(R.drawable.home_scroll_default);
                        }

                    }
                }
            }

            @Override
            public void onErrorResponse(VolleyError volleyError) {
                //如果出错，则说明都不显示（简单处理），最好准备一张出错图片
                viewHolder.iv_icon.setImageResource(R.drawable.home_scroll_default);
            }
        };
        VolleyManager.getImageLoader().get(imageUrl, imageListener);
    }

    static class ViewHolder {
        ImageView iv_icon;
        TextView tv_title;
    }


    private PhotosDetailPagerBean parseJson(String json) {
        return new Gson().fromJson(json, PhotosDetailPagerBean.class);
    }

}
