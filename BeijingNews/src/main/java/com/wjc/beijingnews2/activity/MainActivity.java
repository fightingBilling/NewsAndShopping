package com.wjc.beijingnews2.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Window;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.wjc.beijingnews2.R;
import com.wjc.beijingnews2.fragment.ContentFragment;
import com.wjc.beijingnews2.fragment.LeftMenuFragment;

public class MainActivity extends SlidingFragmentActivity {

    public static final String CONTENT_TAG = "content_tag";
    public static final String LEFTMENU_TAG = "leftmenu_tag";

    private int screenWidth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getScreenDimens();
        initView();
        initFragment();
    }

    private void initView() {
        //1.设置主页面
        setContentView(R.layout.activity_main);

        //2.设置左侧菜单
        setBehindContentView(R.layout.leftmenu);
        //3.设置右侧菜单
        SlidingMenu slidingMenu = getSlidingMenu();
//        slidingMenu.setSecondaryMenu(R.layout.rightmenu);
        //4.设置支持滑动的模式：全屏滑动，边缘滑动，不可以滑动
        slidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        //5.设置页面模式：左侧菜单+主页面；左侧菜单+主页面+右侧菜单； 主页面+右侧菜单
        slidingMenu.setMode(SlidingMenu.LEFT);
        //6.设置主页面占的宽度
//        slidingMenu.setBehindOffset(DensityUtil.dip2px(this,200));
        slidingMenu.setBehindOffset((int) (screenWidth*0.625));
    }

    private void getScreenDimens() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
    }

    private void initFragment() {
        //1.开启事务
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //2.添加LeftmenuFragment页面
        transaction.replace(R.id.fl_leftmenu,new LeftMenuFragment(),LEFTMENU_TAG);
        //3.添加主Fragemnt页面
        transaction.replace(R.id.fl_main_content,new ContentFragment(),CONTENT_TAG);
        //4.提交事务
        transaction.commit();
    }

    /**
     * 得到左侧菜单Fragment
     * @return
     */
    public LeftMenuFragment getLeftmenuFragment() {
        return (LeftMenuFragment) getSupportFragmentManager().findFragmentByTag(LEFTMENU_TAG);
    }

    /**
     * 得到正文Fragment
     * @return
     */
    public ContentFragment getContentFragment() {
        return (ContentFragment) getSupportFragmentManager().findFragmentByTag(CONTENT_TAG);
    }


}
