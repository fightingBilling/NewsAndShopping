package com.wjc.beijingnews2.fragment;

import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioGroup;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.wjc.beijingnews2.R;
import com.wjc.beijingnews2.activity.MainActivity;
import com.wjc.beijingnews2.adapter.ContentFragmentAdapter;
import com.wjc.beijingnews2.base.BaseFragment;
import com.wjc.beijingnews2.base.BasePager;
import com.wjc.beijingnews2.pager.HomePager;
import com.wjc.beijingnews2.pager.NewsCenterPager;
import com.wjc.beijingnews2.pager.SettingPager;
import com.wjc.beijingnews2.pager.ShoppingCartPager;
import com.wjc.beijingnews2.pager.ShoppingPager;
import com.wjc.beijingnews2.utils.LogUtil;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by ${万嘉诚} on 2016/10/16.
 * 微信：wjc398556712
 * 作用：主页面
 */
public class ContentFragment extends BaseFragment {

    @ViewInject(R.id.rg_main)
    private RadioGroup rg_main;

    @ViewInject(R.id.viewpager)
    private ViewPager viewPager;

    /**
     * 装五个页面的集合
     */

    private ArrayList<BasePager> basePagers;

    @Override
    public View initView() {
        LogUtil.e("主页面的视图被初始化了...");
        View view = View.inflate(context,R.layout.fragment_content,null);
        x.view().inject(ContentFragment.this,view);
        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        LogUtil.e("主页面的数据被初始化了...");

        //初始化五个页面，并且放入集合中
        basePagers = new ArrayList<>();
        basePagers.add(new HomePager(context));
        basePagers.add(new NewsCenterPager(context));
        basePagers.add(new ShoppingPager(context));
        basePagers.add(new ShoppingCartPager(context));
        basePagers.add(new SettingPager(context));

        //设置ViewPager的适配器
        viewPager.setAdapter(new ContentFragmentAdapter(basePagers));

        //设置RadioGroup的选中状态改变的监听
        rg_main.setOnCheckedChangeListener(new MyOnCheckedChangeListener());

        //监听某个页面被选中，初始对应的页面的数据
        viewPager.addOnPageChangeListener(new MyOnPageChangeListener());
        //设置默认选中首页
        rg_main.check(R.id.rb_home);
        basePagers.get(0).initData();
        //设置模式SlidingMenu不可以滑动
        isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);


    }

    /**
     * 得到新闻中心
     * @return
     */
    public NewsCenterPager getNewsCenterPager() {
        return (NewsCenterPager) basePagers.get(1);
    }


    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener{

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        /**
         * 当某个页面被选中的时候回调这个方法
         * @param position 被选中页面的位置
         */
        @Override
        public void onPageSelected(int position) {
            //调用被选中的页面的initData方法
            basePagers.get(position).initData();
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    class MyOnCheckedChangeListener implements RadioGroup.OnCheckedChangeListener {

        /**
         *
         * @param group RadioGroup
         * @param checkedId 被选中的RadioButton的id
         */
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            isEnableSlidingMenu(SlidingMenu.TOUCHMODE_NONE);
            switch (checkedId) {
                case R.id.rb_home :
                    viewPager.setCurrentItem(0,false);//false表示没有动画
                    break;
                case R.id.rb_news :
                    viewPager.setCurrentItem(1,false);
                    isEnableSlidingMenu(SlidingMenu.TOUCHMODE_FULLSCREEN);
                    break;
                case R.id.rb_shopping :
                    viewPager.setCurrentItem(2,false);
                    break;
                case R.id.rb_shopping_cart :
                    viewPager.setCurrentItem(3,false);
                    break;
                case R.id.rb_setting :
                    viewPager.setCurrentItem(4,false);
                    break;
            }

        }
    }

    /**
     根据传人的参数设置是否让SlidingMenu可以滑动
     */
    private void isEnableSlidingMenu(int touchmodeNone) {
        MainActivity mainActivity = (MainActivity)context;
        mainActivity.getSlidingMenu().setTouchModeAbove(touchmodeNone);

    }
}
