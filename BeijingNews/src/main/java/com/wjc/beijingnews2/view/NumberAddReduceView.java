package com.wjc.beijingnews2.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.TintTypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wjc.beijingnews2.R;
import com.wjc.beijingnews2.utils.LogUtil;

/**
 * Created by ${万嘉诚} on 2016/10/26.
 * WeChat：wjc398556712
 * Function：自定义增加删除按钮
 */

public class NumberAddReduceView extends LinearLayout implements View.OnClickListener {

    private final Context context;
    private Button btn_reduce;
    private TextView tv_num;
    private Button btn_add;

    /**
     * 默认值
     */
    private int value = 1;

    /**
     * 最小值
     */
    private int minValue = 1;

    /**
     * 最大值（库存）
     */
    private int maxValue = 10;

    public NumberAddReduceView(Context context) {
        this(context, null);
    }

    public NumberAddReduceView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NumberAddReduceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        initView();
        //得到属性
        if (attrs != null) {
            TintTypedArray array = TintTypedArray.obtainStyledAttributes(context, attrs, R.styleable.NumberAddReduceView);
            int value = array.getInt(R.styleable.NumberAddReduceView_value, 0);
            if (value > 0) {//表示在属性里面给value赋值了
                setValue(value);
            }


            int maxValue = array.getInt(R.styleable.NumberAddReduceView_maxValue, 0);
            if (value > 0) {
                setValue(maxValue);
            }

            int minValue = array.getInt(R.styleable.NumberAddReduceView_minValue, 0);
            if (value > 0) {
                setValue(minValue);
            }

            Drawable btnReduceBackground = array.getDrawable(R.styleable.NumberAddReduceView_btnReduceBackground);
            if (btnReduceBackground != null) {
                btn_reduce.setBackground(btnReduceBackground);
            }

            Drawable btnAddBackground = array.getDrawable(R.styleable.NumberAddReduceView_btnAddBackground);
            if (btnAddBackground != null)
                btn_add.setBackground(btnAddBackground);

            Drawable textViewBackground = array.getDrawable(R.styleable.NumberAddReduceView_textViewBackground);
            if (textViewBackground != null)
                tv_num.setBackground(textViewBackground);

            array.recycle();
        }

    }

    private void initView() {
        //把布局R.layout.add_sub_number_view添加到NumberAddSubView类中
        View.inflate(context, R.layout.add_reduce_number_view, this);
        btn_reduce = (Button) findViewById(R.id.btn_reduce);
        btn_add = (Button) findViewById(R.id.btn_add);
        tv_num = (TextView) findViewById(R.id.tv_num);

        btn_reduce.setOnClickListener(this);
        btn_add.setOnClickListener(this);

    }

    public int getValue() {
        String val = tv_num.getText().toString();
        if (!TextUtils.isEmpty(val)) {
            value = Integer.parseInt(val);
        }
        return value;
    }

    public void setValue(int value) {
        this.value = value;
        tv_num.setText(value + "");
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_reduce) {
//            Toast.makeText(mContext,"减",Toast.LENGTH_SHORT).show();

            reduceNum();
            if (onButtonClickListenter != null) {
                onButtonClickListenter.onButtonReduceClick(v, value);
            }
        } else if (v.getId() == R.id.btn_add) {
//            Toast.makeText(context,"加",Toast.LENGTH_SHORT).show();
            addNum();
            if (onButtonClickListenter != null) {
                onButtonClickListenter.onButtonAddClick(v, value);
            }
        }
    }

    /**
     * 减少数据
     */
    private void reduceNum() {
        if (value > minValue) {
            value = value - 1;
            LogUtil.e("reduceNum-->value=========" + value);
            setValue(value);
        }
    }

    /**
     * 添加数据
     */
    private void addNum() {
        if (value < maxValue) {
            value = value + 1;
            LogUtil.e("addNum-->value=========" + value);
            setValue(value);
        }
    }

    public interface OnButtonClickListenter {
        /**
         * 当增加按钮被点击的时候回调该方法
         *
         * @param view
         * @param value
         */
        void onButtonAddClick(View view, int value);

        /**
         * 当减少按钮被点击的时候回调这个方法
         *
         * @param view
         * @param value
         */
        void onButtonReduceClick(View view, int value);
    }

    private OnButtonClickListenter onButtonClickListenter;

    public void setOnButtonClickListenter(OnButtonClickListenter onButtonClickListenter) {
        this.onButtonClickListenter = onButtonClickListenter;
    }
}
