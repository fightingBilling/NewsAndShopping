package com.wjc.beijingnews2.fragment;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.wjc.beijingnews2.R;
import com.wjc.beijingnews2.activity.MainActivity;
import com.wjc.beijingnews2.base.BaseFragment;
import com.wjc.beijingnews2.bean.NewsCenterPagerBean2;
import com.wjc.beijingnews2.utils.DensityUtil;
import com.wjc.beijingnews2.utils.LogUtil;

import java.util.List;

/**
 * Created by ${万嘉诚} on 2016/10/16.
 * 微信：wjc398556712
 * 作用：左侧菜单的Fragment
 */
public class LeftMenuFragment extends BaseFragment {

    private List<NewsCenterPagerBean2.NewsCenterPagerData> data;

    private ListView listView;

    private LeftmenuFragmentAdapter adapter;

    /**
     * 点击的位置
     */
    private int prePosition;

    @Override
    public View initView() {
        LogUtil.e("左侧菜单的视图被初始化了...");
        listView = new ListView(context);
        listView.setPadding(0, DensityUtil.dip2px(context,40),0,0);
        listView.setDividerHeight(0);//设置分割线高度为0
        listView.setCacheColorHint(Color.TRANSPARENT);//低版本的手机ListView有颜色

        //设置按下listView的item不变色
        listView.setSelection(android.R.color.transparent);

        //设置item的点击事件
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //1.记录点击的位置，变成红色
                prePosition = position;
                adapter.notifyDataSetChanged();//执行getCount()-->getView()
                //2.把左侧菜单关闭
                ((MainActivity)context).getSlidingMenu().toggle();

                //3.切换到对应的详情页面：新闻详情页面，专题详情页面，图组详情页面，互动详情页面
                switchPager(prePosition);
            }
        });


        return listView;
    }

    private void switchPager(int prePosition) {
        ((MainActivity)context).getContentFragment().getNewsCenterPager().switchThisPager(prePosition);
    }

    @Override
    protected void initData() {
        super.initData();
        LogUtil.e("左侧菜单的数据被初始化了...");

    }

    /**
     * 接收从NewsCenterPager传过来的数据
     * @param data
     */
    public void setData(List<NewsCenterPagerBean2.NewsCenterPagerData> data) {
        LogUtil.e("接收从NewsCenterPager传过来的数据====");
        this.data = data;
        for(int i = 0; i < data.size(); i++) {
            LogUtil.e("title == " + data.get(i).getTitle());
        }

        //设置适配器
        adapter = new LeftmenuFragmentAdapter();
        listView.setAdapter(adapter);
        //设置默认页面
        switchPager(prePosition);
    }

    private class LeftmenuFragmentAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return data != null? data.size() : 0;
        }

        @Override
        public Object getItem(int position) {
            return data != null ? data.get(position) : null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) View.inflate(context, R.layout.item_leftmenu,null);
            textView.setText(data.get(position).getTitle());
            textView.setEnabled(position == prePosition);
            return textView;
        }
    }
}
