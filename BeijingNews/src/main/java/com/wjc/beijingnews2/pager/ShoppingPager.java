package com.wjc.beijingnews2.pager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.google.gson.Gson;
import com.wjc.beijingnews2.R;
import com.wjc.beijingnews2.adapter.ShoppingPagerAdpater;
import com.wjc.beijingnews2.base.BasePager;
import com.wjc.beijingnews2.bean.ShoppingPagerBean;
import com.wjc.beijingnews2.utils.CacheUtils;
import com.wjc.beijingnews2.utils.Contants;
import com.wjc.beijingnews2.utils.LogUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by ${万嘉诚} on 2016/10/17.
 * WeChat：wjc398556712
 * Function：商城页面
 */
public class ShoppingPager extends BasePager {

    private MaterialRefreshLayout refresh;
    private RecyclerView recyclerview;
    private ProgressBar progressbar;
    private String url;

    /**
     * 每页要求10个数据
     */
    private int pageSize = 10;
    /**
     * 第几页
     */
    private int curPage = 1;
    /**
      *总的多少页
      */
    private int totalPager;
    /**
     * 商城热卖的数据集合
     */
    private List<ShoppingPagerBean.Wares> wares;
    private ShoppingPagerAdpater adpater;
    /**
     * 默认状态
     */
    private static  final int STATE_NORMAL = 1;

    /**
     * 下拉刷新
     */
    private static  final int STATE_REFRESH = 2;

    /**
     * 上拉刷新
     */
    private static  final int STATE_LOADMORE = 3;
    /**
     * 当前状态
     */
    private int currentState = STATE_NORMAL;

    public ShoppingPager(Context context) {
        super(context);
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("购物界面被初始化了-------");
        //设置标题
        tv_title.setText("商城");
        //创建子页面的视图
        View view = View.inflate(context, R.layout.shopping_pager, null);
        refresh = (MaterialRefreshLayout) view.findViewById(R.id.refresh);
        recyclerview = (RecyclerView) view.findViewById(R.id.recyclerview);
        progressbar = (ProgressBar) view.findViewById(R.id.progressbar);

        //子页面的视图和FrameLayout结合在一起，形成一个新的页面
        fl_content.removeAllViews();
        fl_content.addView(view);

        //设置下拉刷新和上拉刷新的监听
        initRefreshLayout();

        getDataFromNet();//默认请求

    }

    private void initRefreshLayout() {
        refresh.setMaterialRefreshListener(new MyMaterialRefreshListener());
    }

    class MyMaterialRefreshListener extends MaterialRefreshListener {

        /**
         * 下拉加载
         * @param materialRefreshLayout
         */
        @Override
        public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
            refreshData();
        }

        /**
         * 加载更多
         * @param materialRefreshLayout
         */
        @Override
        public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
            super.onRefreshLoadMore(materialRefreshLayout);
            loadMoreData();
        }
    }

    private void loadMoreData() {
        currentState = STATE_LOADMORE;
        if(curPage < totalPager) {
            curPage ++;
            //加载更多
            url = Contants.WARES_HOT_URL + "pageSize=" + pageSize + "&curPage=" + curPage;
            OkHttpUtils
                    .get()
                    .url(url)
                    .id(100)
                    .build()
                    .execute(new MyStringCallback());
        } else {
            //没有数据
            refresh.finishRefreshLoadMore();
            Toast.makeText(context, "没有更多数据", Toast.LENGTH_SHORT).show();
        }

    }

    private void refreshData() {
        curPage = 1;
        currentState = STATE_REFRESH;
        url = Contants.WARES_HOT_URL + "pageSize=" + pageSize + "&curPage=" + curPage;
        OkHttpUtils
                .get()
                .url(url)
                .id(100)
                .build()
                .execute(new MyStringCallback());
    }

    private void getDataFromNet() {
        currentState = STATE_NORMAL;
        curPage = 1;
        url = Contants.WARES_HOT_URL + "pageSize=" + pageSize + "&curPage=" + curPage;

        String saveJson = CacheUtils.getString(context, Contants.WARES_HOT_URL);
        if (!TextUtils.isEmpty(saveJson)) {
            processData(saveJson);
        }

        OkHttpUtils
                .get()
                .url(url)
                .id(100)
                .build()
                .execute(new MyStringCallback());

    }

    public class  MyStringCallback extends StringCallback {

        @Override
        public void onBefore(Request request, int id) {
            super.onBefore(request, id);
        }

        @Override
        public void onAfter(int id) {
            super.onAfter(id);
        }

        @Override
        public void onError(Call call, Exception e, int id) {
            Toast.makeText(context, "您的网络开了小差~", Toast.LENGTH_SHORT).show();
            LogUtil.e("okhttpUtils互动请求数据失败==" + e.getMessage());
            refresh.finishRefreshLoadMore();
            refresh.finishRefresh();
        }

        @Override
        public void onResponse(String response, int id) {
            //缓存数据
            CacheUtils.putString(context, Contants.WARES_HOT_URL, response);

            LogUtil.e("okhttpUtils互动请求数据成功==" + response);
            //解析json数据
            processData(response);
//
//            switch (id) {
//                case 100:
//                    Toast.makeText(context, "http", Toast.LENGTH_SHORT).show();
//                    break;
//                case 101:
//                    Toast.makeText(context, "https", Toast.LENGTH_SHORT).show();
//                    break;
//            }
        }

        @Override
        public void inProgress(float progress, long total, int id) {
            super.inProgress(progress, total, id);
        }
    }


    /**
     * 解析和显示数据
     * @param json
     */
    private void processData(String json) {
        ShoppingPagerBean bean = new Gson().fromJson(json, ShoppingPagerBean.class);
        curPage = bean.getCurrentPage();
        pageSize = bean.getPageSize();
        totalPager = bean.getTotalPage();
        wares = bean.getList();
        if (wares != null && wares.size() > 0) {
            LogUtil.e("curPage==" + curPage + ",pageSize==" + pageSize + ",totalPager==" + totalPager + ",name==" + bean.getList().get(0).getName());
            showData();

        }
        progressbar.setVisibility(View.GONE);
    }

    private void showData() {
        switch (currentState) {
            case STATE_NORMAL :
                //有数据`
                adpater = new ShoppingPagerAdpater(context, wares);
                //设置适配器
                recyclerview.setAdapter(adpater);
                //设置ListView样式
                recyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

                break;
            case STATE_REFRESH :
                adpater.clearData();
                adpater.addData(wares);
                refresh.finishRefresh();//下拉刷新状态还原

                break;
            case STATE_LOADMORE :
                adpater.addData(adpater.getCount(), wares);
                refresh.finishRefreshLoadMore();//加载更多状态还原

                break;
        }

    }
}
