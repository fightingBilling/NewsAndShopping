package com.wjc.beijingnews2.menudetailpager;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.viewpagerindicator.TabPageIndicator;
import com.wjc.beijingnews2.R;
import com.wjc.beijingnews2.activity.MainActivity;
import com.wjc.beijingnews2.base.MenuDetailBasePager;
import com.wjc.beijingnews2.bean.NewsCenterPagerBean2;
import com.wjc.beijingnews2.menudetailpager.tabdetailpager.TabDetailPager;
import com.wjc.beijingnews2.utils.LogUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${万嘉诚} on 2016/10/17.
 * WeChat：wjc398556712
 * Function：新闻详情页面
 */
public class NewsMenuDetailPager extends MenuDetailBasePager {

    /**
     * 页签页面的数据的集合-数据
     */
    private List<NewsCenterPagerBean2.NewsCenterPagerData.ChildrenData> childrens;
    /**
     * 页签页面的集合-页面
     */
    private ArrayList<TabDetailPager> tabDetailPagers;

    @ViewInject(R.id.viewpager)
    private ViewPager viewPager;

    @ViewInject(R.id.tabPageIndicator)
    private TabPageIndicator tabPageIndicator;

    @ViewInject(R.id.ib_tab_next)
    private ImageButton ib_tab_next;

    /**
     * 新闻详情界面的数据
     *
     * @param context
     * @param detailPagerData
     */
    public NewsMenuDetailPager(Context context, NewsCenterPagerBean2.NewsCenterPagerData detailPagerData) {
        super(context);
        childrens = detailPagerData.getChildren();
    }

    @Override
    public View initView() {
        View view = View.inflate(context, R.layout.newsmenu_detail_pager, null);
        x.view().inject(NewsMenuDetailPager.this, view);
        //设置点击事件
        ib_tab_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            }
        });

        return view;
    }

    @Override
    public void initData() {
        super.initData();

        LogUtil.e("新闻详情页面数据被初始化了..");
        //准备新闻详情页面的数据
        tabDetailPagers = new ArrayList<>();
        for (int i = 0; i < childrens.size(); i++) {
            tabDetailPagers.add(new TabDetailPager(context, childrens.get(i)));
        }
        //设置ViewPager的适配器
        viewPager.setAdapter(new MyNewsMenuDetailPagerAdapter());
        //ViewPager和TabPageIndicator关联  需要重写getPageTitle()方法
        tabPageIndicator.setViewPager(viewPager);
        //主页以后监听页面的变化 ，TabPageIndicator监听页面的变化
        tabPageIndicator.setOnPageChangeListener(new MyOnPageChangeListener());
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        /**
         * 屏蔽左侧菜单被拽出来
         * @param position
         */
        @Override
        public void onPageSelected(int position) {
            if(position == 0) {
                isEnableSlidingMenu(SlidingMenu.TOUCHMODE_FULLSCREEN);
            } else {
                isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     根据传入的参数设置是否让SlidingMenu可以滑动
     */
    private void isEnableSlidingMenu(int touchmodeFullscreen) {
        MainActivity mainActivity = (MainActivity) context;
        mainActivity.getSlidingMenu().setTouchModeAbove(touchmodeFullscreen);
    }


    class MyNewsMenuDetailPagerAdapter extends PagerAdapter {
        /**
         * ViewPager和TabPageIndicator关联之后需要重写的方法
         * @param position
         * @return
         */
        @Override
        public CharSequence getPageTitle(int position) {
            LogUtil.e("MyNewsMenuDetailPagerAdapter-->getPageTitle" + childrens.get(position).getTitle());
            return childrens.get(position).getTitle();
        }

        @Override
        public int getCount() {
            return tabDetailPagers.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            TabDetailPager tabDetailPager = tabDetailPagers.get(position);
            View rootView = tabDetailPager.rootView;
            tabDetailPager.initData();
            container.addView(rootView);//初始化数据
            return rootView;
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
}
