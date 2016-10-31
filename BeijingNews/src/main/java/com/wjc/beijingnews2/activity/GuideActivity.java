package com.wjc.beijingnews2.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.wjc.beijingnews2.R;
import com.wjc.beijingnews2.SplashActivity;
import com.wjc.beijingnews2.utils.CacheUtils;
import com.wjc.beijingnews2.utils.DensityUtil;

import java.util.ArrayList;

public class GuideActivity extends Activity implements View.OnClickListener {

    private ViewPager viewpager;
    private Button btn_start_main;
    private LinearLayout ll_point_group;
    private ImageView iv_red_point;

    private ArrayList<ImageView> imageViews;

    /**
     * 两点的间距
     */
    private int leftmax;

    private int widthdpi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        viewpager = (ViewPager) findViewById(R.id.viewpager);
        btn_start_main = (Button) findViewById(R.id.btn_start_main);
        ll_point_group = (LinearLayout) findViewById(R.id.ll_point_group);
        iv_red_point = (ImageView) findViewById(R.id.iv_red_point);

        //准备数据
        int[] ids = new int[]{
                R.drawable.guide_1,
                R.drawable.guide_2,
                R.drawable.guide_3
        };

        widthdpi = DensityUtil.dip2px(this,10);

        imageViews = new ArrayList<>();
        for(int i = 0; i < ids.length; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setBackgroundResource(ids[i]);
            imageViews.add(imageView);

            ImageView imagePoint = new ImageView(this);
            imagePoint.setBackgroundResource(R.drawable.point_normal);

            /**
             * 单位是像数
             * 把单位当成dp转成对应的像数
             */
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(widthdpi,widthdpi);
            if(i != 0) {
                params.leftMargin = widthdpi;//每个小点的左边距
            }
            imagePoint.setLayoutParams(params);//动态设置imagePoint的布局
            ll_point_group.addView(imagePoint);
        }

        //设置ViewPager的适配器
        viewpager.setAdapter(new MyPagerAdapter());
        //根据View的生命周期，当视图执行到onLayout或者onDraw的时候执行onGlobalLayout()得到两个小圆点之间的间距
        iv_red_point.getViewTreeObserver().addOnGlobalLayoutListener(new MyOnGlobalLayoutListener());
        //得到屏幕滑动的百分比
        viewpager.addOnPageChangeListener( new MyOnPageChangeListener());

        btn_start_main.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        CacheUtils.putBoolean(GuideActivity.this, SplashActivity.START_MAIN,true);

        //2.跳转到主页面
        Intent intent = new Intent(GuideActivity.this,MainActivity.class);
        startActivity(intent);

        //3.关闭引导页面
        finish();

    }

    class MyOnGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {

        @Override
        public void onGlobalLayout() {
            //执行不只一次
//            iv_red_point.getViewTreeObserver().removeGlobalOnLayoutListener(MyOnGlobalLayoutListener.this);
            //间距  = 第1个点距离左边的距离 - 第0个点距离左边的距离
            leftmax = ll_point_group.getChildAt(1).getLeft() - ll_point_group.getChildAt(0).getLeft();

        }
    }

    class MyPagerAdapter extends PagerAdapter {

        /**
         * 返回数据的总个数
         * @return
         */
        @Override
        public int getCount() {
            return imageViews.size();
        }

        /**
         * 作用，getView
         *
         * @param container ViewPager
         * @param position 要创业页面的位置
         * @return 返回和创建当前页面有关系的值
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            ImageView imageView = imageViews.get(position);
            container.addView(imageView);
            return imageView;
        }

        /**
         * 判断
         * @param view 当前创建的视图
         * @param object 上面instantiateItem返回的结果值
         * @return
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return  view == object;
        }

        /**
         * 销毁页面
         * @param container ViewPager
         * @param position 要销毁页面的位置
         * @param object 要销毁的页面
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {
        /**
         * 当页面滑动了会回调这个方法
         * @param position 当前滑动页面的位置
         * @param positionOffset 页面滑动的百分比
         * @param positionOffsetPixels 滑动的像数
         */
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            //两点间滑动距离对应的坐标 = 原来的起始位置 +  两点间移动的距离
            int leftMargin = (int) (position*leftmax + positionOffset*leftmax);

            //小红点的外层布局是RelativeLayout，所以是RelativeLayout.LayoutParams params
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) iv_red_point.getLayoutParams();
            params.leftMargin = leftMargin;
            iv_red_point.setLayoutParams(params);
        }

        /**
         * 当页面被选中的时候，回调这个方法
         * @param position 被选中页面的对应的位置
         */
        @Override
        public void onPageSelected(int position) {
            if(position == imageViews.size() - 1) {
                btn_start_main.setVisibility(View.VISIBLE);
            }else {
                btn_start_main.setVisibility(View.GONE);
            }

        }

        /**
         * 当ViewPager页面滑动状态发生变化的时候
         * @param state
         */
        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }
}
