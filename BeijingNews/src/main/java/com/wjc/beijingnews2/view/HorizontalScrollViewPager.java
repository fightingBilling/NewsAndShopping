package com.wjc.beijingnews2.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by ${万嘉诚} on 2016/10/18.
 * WeChat：wjc398556712
 * Function：水平方向滑动的ViewPager
 */
public class HorizontalScrollViewPager extends ViewPager{

    public HorizontalScrollViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalScrollViewPager(Context context) {
        super(context);
    }

    /**
     * 起始坐标
     */
    private float startX;
    private float startY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN :
                getParent().requestDisallowInterceptTouchEvent(true);
                startX = ev.getX();
                startY = ev.getY();

                break;
            case MotionEvent.ACTION_MOVE :
                float endX = ev.getX();
                float endY = ev.getY();

                float distanceX = endX - startX;
                float distanceY = endY - startY;
                //判断滑动方向
                if(Math.abs(distanceX) > Math.abs(distanceY)) {
                    //水平方向滑动
                    // 2.1，当滑动到ViewPager的第0个页面，并且是从左到右滑动
                    if(getCurrentItem() == 0 & distanceX > 0) {
                         getParent().requestDisallowInterceptTouchEvent(false);
                    }// 2.2，当滑动到ViewPager的最后一个页面，并且是从右到左滑动
                    else if(getCurrentItem() == (getAdapter().getCount()-1) && distanceX < 0) {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }else { //2.3，其他,中间部分
                        getParent().requestDisallowInterceptTouchEvent(true);
                    }
                }else {
                    //竖直方向滑动
                    getParent().requestDisallowInterceptTouchEvent(false);
                }

                break;
            case MotionEvent.ACTION_UP :

                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
