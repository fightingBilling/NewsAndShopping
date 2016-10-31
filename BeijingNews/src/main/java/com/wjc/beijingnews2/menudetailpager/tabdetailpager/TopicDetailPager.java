package com.wjc.beijingnews2.menudetailpager.tabdetailpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.extras.SoundPullEventListener;
import com.wjc.beijingnews2.R;
import com.wjc.beijingnews2.base.MenuDetailBasePager;
import com.wjc.beijingnews2.bean.NewsCenterPagerBean2;
import com.wjc.beijingnews2.bean.TabDetailPagerBean;
import com.wjc.beijingnews2.utils.CacheUtils;
import com.wjc.beijingnews2.utils.Contants;
import com.wjc.beijingnews2.utils.LogUtil;

import org.xutils.common.Callback;
import org.xutils.common.util.DensityUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

/**
 * Created by ${万嘉诚} on 2016/10/19.
 * WeChat：wjc398556712
 * Function：
 */
public class TopicDetailPager extends MenuDetailBasePager{
    private final ImageOptions imageOptions;
    private TabDetailPagerListAdapter adapter;
    @ViewInject(R.id.viewpager)
    private ViewPager viewPager;

    @ViewInject(R.id.tv_title)
    private TextView tv_title;

    private ListView listview;

    @ViewInject(R.id.ll_point_group)
    private LinearLayout ll_point_group;

    /**
     * 顶部专题数据集合
     */
    private List<TabDetailPagerBean.DataEntity.TopnewsData> topNews;
    /**
     * 专题列表数据集合
     */
    private List<TabDetailPagerBean.DataEntity.NewsData> news;

    private final NewsCenterPagerBean2.NewsCenterPagerData.ChildrenData childrenData;

    private String url;
    /**
     * 下一页的联网路径
     */
    private String moreUrl;
    /**
     * 是否加载更多
     */
    private boolean isLoadMore = false;

    private PullToRefreshListView mPullRefreshListView;

    public TopicDetailPager(Context context, NewsCenterPagerBean2.NewsCenterPagerData.ChildrenData childrenData) {
        super(context);
        this.childrenData = childrenData;
        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(100), DensityUtil.dip2px(100))
                .setRadius(DensityUtil.dip2px(5))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop.
                .setCrop(true) // 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                //.setPlaceholderScaleType(ImageView.ScaleType.MATRIX)
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.news_pic_default)
                .setFailureDrawableId(R.drawable.news_pic_default)
                .build();
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.topicdetail_pager, null);
        x.view().inject(this,view);

        mPullRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pull_refresh_list);

        listview = mPullRefreshListView.getRefreshableView();

        /**
         * Add Sound Event Listener
         * 下拉刷新。上拉加载。添加音效
         */
        SoundPullEventListener<ListView> soundListener = new SoundPullEventListener<>(context);
        soundListener.addSoundEvent(PullToRefreshBase.State.PULL_TO_REFRESH, R.raw.pull_event);
        soundListener.addSoundEvent(PullToRefreshBase.State.RESET, R.raw.reset_sound);
        soundListener.addSoundEvent(PullToRefreshBase.State.REFRESHING, R.raw.refreshing_sound);
        mPullRefreshListView.setOnPullEventListener(soundListener);

        View topNewsView = View.inflate(context,R.layout.topnews,null);
        x.view().inject(this,topNewsView);

        //把顶部轮播图部分视图，以头的方式添加到ListView中
        listview.addHeaderView(topNewsView);

        mPullRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                getDataForNet(url);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if(TextUtils.isEmpty(moreUrl)){
                    //没有更多数据
                    Toast.makeText(context, "没有更多数据", Toast.LENGTH_SHORT).show();
//                    listview.onRefreshFinish(false);
                    mPullRefreshListView.onRefreshComplete();
                }else{
                    getMoreDataForNet(moreUrl);
                }
            }
        });

        return view;
    }

//    class MyOnRefreshListener implements RefreshListView.OnRefreshListener {
//
//        @Override
//        public void onPullDownRefresh() {
//            getDataForNet(url);
//        }
//
//        @Override
//        public void onLoadMore() {
//            if(TextUtils.isEmpty(moreUrl)) {
//                Toast.makeText(context, "没有更多的数据", Toast.LENGTH_SHORT).show();
//                listview.onRefreshFinish(true);
//            } else {
//                getMoreDataForNet(moreUrl);
//            }
//
//        }
//    }

    private void getMoreDataForNet(String moreUrl) {
        RequestParams params = new RequestParams(moreUrl);
        params.setConnectTimeout(4000);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                LogUtil.e("加载更多联网成功=="+result);
                mPullRefreshListView.onRefreshComplete();
                //把这个放在前面
                isLoadMore = true;
                //解析数据
                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("加载更多联网失败onError=="+ex.getMessage());
                mPullRefreshListView.onRefreshComplete();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("加载更多联网onCancelled"+cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("加载更多联网onFinished");
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        url = Contants.BASE_URL + childrenData.getUrl();
        LogUtil.e("TabDetailPager-->url-----------" + url);

        String savedUrl = CacheUtils.getString(context, url);
        if(!TextUtils.isEmpty(savedUrl)) {
            processData(savedUrl);
        }
        //请求网络
        getDataForNet(url);
    }

    private void getDataForNet(final String url) {
        RequestParams requestParams = new RequestParams(url);
        requestParams.setConnectTimeout(5000);
        x.http().get(requestParams, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {
                LogUtil.e("TabDetailPager联网请求成功==" + result);
                //缓存数据
                CacheUtils.putString(context, url, result);

                processData(result);
                //隐藏下拉刷新控件-重写显示数据，更新时间
                mPullRefreshListView.onRefreshComplete();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("TabDetailPager联网请求失败==" + ex.getMessage());
                mPullRefreshListView.onRefreshComplete();
            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("onCancelled==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("onFinished==");
            }
        });
    }

    /**
     * 之前被高亮显示的点
     */
    private int prePosition;

    private void processData(String json) {
        TabDetailPagerBean tabDetailPagerBean = paeseJson(json);

        moreUrl = "";
        String moreUrlData = tabDetailPagerBean.getData().getMore();
        if(TextUtils.isEmpty(moreUrlData)) {
            moreUrl = "";
        } else {
            moreUrl = Contants.BASE_URL + moreUrlData;
        }
        LogUtil.e("加载更多的地址==="+moreUrl);
        //默认和加载更多
        if(!isLoadMore) {
            //顶部新闻数据集合
            topNews = tabDetailPagerBean.getData().getTopnews();
            if (topNews != null && topNews.size() > 0) {
                viewPager.setAdapter(new TabDetailPagerAdapter());

                //添加红点
                addPoint();

                //监听页面的改变，设置红点变化和文本变化
                viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
                tv_title.setText(topNews.get(prePosition) .getTitle());

                //准备ListView对应的集合数据
                news = tabDetailPagerBean.getData().getNews();
                LogUtil.e("准备ListView对应的集合数据个数---->" + news.size());
                //设置ListView的适配器
                adapter = new TabDetailPagerListAdapter();
                listview.setAdapter(adapter);
            }
        } else {//加载更多
            isLoadMore = false;
            //添加到原来的集合中
            news.addAll(tabDetailPagerBean.getData().getNews());
            //刷新适配器
            adapter.notifyDataSetChanged();
        }

    }

    class  TabDetailPagerListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return news.size();
        }

        @Override
        public Object getItem(int position) {
            return news.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if(convertView ==null){
                convertView = View.inflate(context,R.layout.item_tabdetail_pager,null);
                viewHolder = new ViewHolder();
                viewHolder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
                viewHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
                viewHolder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);

                convertView.setTag(viewHolder);

            }else{
                viewHolder = (ViewHolder) convertView.getTag();

            }

            String imageUrl = Contants.BASE_URL + news.get(position).getListimage();
            x.image().bind(viewHolder.iv_icon,imageUrl,imageOptions);
            //设置标题
            viewHolder.tv_title.setText(news.get(position).getTitle());
            //设置更新时间
            viewHolder.tv_time.setText(news.get(position).getPubdate());

            return convertView;
        }
    }

    private void addPoint() {
        //移除所有的红点
        ll_point_group.removeAllViews();

        for (int i = 0; i < topNews.size(); i++) {
            ImageView redPoint = new ImageView(context);
            redPoint.setBackgroundResource(R.drawable.point_selector);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(DensityUtil.dip2px(5), DensityUtil.dip2px(5));
            if (i == 0) {
                redPoint.setEnabled(true);
            }else {
                redPoint.setEnabled(false);
                layoutParams.leftMargin = DensityUtil.dip2px(8);
            }

            redPoint.setLayoutParams(layoutParams);

            ll_point_group.addView(redPoint);
        }
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //2.对应页面的点高亮-红色
            //把之前的变成灰色
            ll_point_group.getChildAt(prePosition).setEnabled(false);
            //把当前设置红色
            ll_point_group.getChildAt(position).setEnabled(true);

            //把现在的位置变为之前的位置
            prePosition = position;
        }

        @Override
        public void onPageSelected(int position) {
            //1.设置文本
            tv_title.setText(topNews.get(position).getTitle());

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    class TabDetailPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return topNews.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = new ImageView(context);
            imageView.setBackgroundResource(R.drawable.pic_item_list_default);
            //x轴和Y轴拉伸
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            //请求图片XUtils3
//            x.image().bind(imageView,Contants.BASE_URL + topNews.get(position).getTopimage(),imageOptions);
            ////请求图片使用glide
            Glide.with(context)
                    .load(Contants.BASE_URL + topNews.get(position).getTopimage())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imageView);

            container.addView(imageView);
            return imageView;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    static class ViewHolder{
        ImageView iv_icon;
        TextView tv_title;
        TextView tv_time;
    }
    /**
     * json解析数据
     *
     * @param json
     * @return
     */
    private TabDetailPagerBean paeseJson(String json) {
        return new Gson().fromJson(json, TabDetailPagerBean.class);
    }
}
