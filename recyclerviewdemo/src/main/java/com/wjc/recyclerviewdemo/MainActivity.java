package com.wjc.recyclerviewdemo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener {

    private RecyclerView recyclerview;

    private ArrayList<String> datas;

    private RecyclerViewAdapter adapter;

    private Button btn_add;
    private Button btn_remove;
    private Button btn_list;
    private Button btn_grid;
    private SwipeRefreshLayout swipe_refresh_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerview = (RecyclerView) findViewById(R.id.recyclerview);
        btn_add = (Button) findViewById(R.id.btn_add);
        btn_remove = (Button) findViewById(R.id.btn_remove);
        btn_list = (Button) findViewById(R.id.btn_list);
        btn_grid = (Button) findViewById(R.id.btn_grid);
        swipe_refresh_layout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_layout);

        initRefresh();

        //设置点击事件
        btn_add.setOnClickListener(this);
        btn_remove.setOnClickListener(this);
        btn_list.setOnClickListener(this);
        btn_grid.setOnClickListener(this);

        //添加数据
        datas = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            datas.add("Data_" + i);
        }

        //设置适配器
        adapter = new RecyclerViewAdapter(this, datas);
        recyclerview.setAdapter(adapter);

        //设置布局管理器
        /**
         * 第一个参数：上下文
         * 第二参数：方向，水平和竖直
         * 第三个参数：是否倒序
         */
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false);
        /**
         * 第一个参数：上下文
         * 第二参数：设置列数
         * 第三个参数：方向，水平和竖直
         * 第四个参数：是否倒序
         */
        GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this,2,GridLayoutManager.VERTICAL,false);
        //瀑布流
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerview.setLayoutManager(linearLayoutManager);

        //设置分割线
        recyclerview.addItemDecoration(new MyItemDecoration(MainActivity.this,MyItemDecoration.VERTICAL_LIST));

        //设置点击某一条
        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, String data) {
                Toast.makeText(MainActivity.this, "data==" + data+",position=="+position, Toast.LENGTH_SHORT).show();

            }
        });

        //设置动画
        recyclerview.setItemAnimator(new DefaultItemAnimator());

    }

    private void initRefresh() {
        //设置刷新控件圈圈的颜色
        swipe_refresh_layout.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_orange_light, android.R.color.holo_red_light, android.R.color.holo_green_light);
        //设置刷新控件背景色
        swipe_refresh_layout.setProgressBackgroundColorSchemeColor(getResources().getColor(android.R.color.white));
        //设置滑动距离
        swipe_refresh_layout.setDistanceToTriggerSync(100);
        //设置大小模式
        swipe_refresh_layout.setSize(SwipeRefreshLayout.DEFAULT);
        //设置下拉刷新的监听
        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //主线程
                        adapter.removeAllData();
                        refreshData();
                        adapter.addAllData(datas);
                        //设置下拉刷新控件状态隐藏
                        swipe_refresh_layout.setRefreshing(false);
                        //刷新数据
//                       adapter.notifyItemRangeChanged(0,50);
                        recyclerview.scrollToPosition(0);
                    }
                },2000);
            }
        });

    }

    private void refreshData() {
//        datas.clear();
        //添加数据
        datas = new ArrayList<>();
        for (int i=0;i<50;i++){
            datas.add(0,"new Data_"+i);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_add:
                adapter.addData(0,"new Data");
                recyclerview.scrollToPosition(0);
                break;
            case R.id.btn_remove:
                adapter.removeData(0);
                recyclerview.scrollToPosition(0);
                break;
            case R.id.btn_list:
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MainActivity.this,LinearLayoutManager.VERTICAL,false);

                recyclerview.setLayoutManager(linearLayoutManager);
                break;
            case R.id.btn_grid:

                GridLayoutManager gridLayoutManager = new GridLayoutManager(MainActivity.this,2,GridLayoutManager.VERTICAL,false);
                recyclerview.setLayoutManager(gridLayoutManager);
                break;
        }

    }
}
