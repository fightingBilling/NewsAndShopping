package com.wjc.beijingnews2.pager;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;

import com.wjc.beijingnews2.base.BasePager;
import com.wjc.beijingnews2.utils.LogUtil;

/**
 * Created by ${万嘉诚} on 2016/10/17.
 * WeChat：wjc398556712
 * Function：
 */
public class HomePager extends BasePager {


    public HomePager(Context context) {
        super(context);
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("主界面被初始化了-------");
        //1.设置标题
        tv_title.setText("主界面");
        //2.联网请求，得到数据，创建视图
        TextView textView = new TextView(context);//动态创建视图
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        textView.setTextSize(25);
        //3.把子视图添加到BasePager的FrameLayout中
        fl_content.addView(textView);
        //4.绑定数据
        textView.setText("主界面内容");
    }
}
