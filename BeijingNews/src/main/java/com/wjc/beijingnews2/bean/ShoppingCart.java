package com.wjc.beijingnews2.bean;

/**
 * Created by ${万嘉诚} on 2016/10/26.
 * WeChat：wjc398556712
 * Function：购物车类，表示在购物的状态，继承商品类；例如，数量，和是否选中
 */
public class ShoppingCart extends ShoppingPagerBean.Wares{
    /**
     * 商品的个数
     */
    private int count = 1;

    /**
     * 商品是否被选中
     */
    private boolean isCheck = true;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }
}

