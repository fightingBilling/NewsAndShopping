package com.wjc.beijingnews2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.wjc.beijingnews2.R;
import com.wjc.beijingnews2.bean.ShoppingCart;
import com.wjc.beijingnews2.utils.CartProvider;
import com.wjc.beijingnews2.utils.LogUtil;
import com.wjc.beijingnews2.view.NumberAddReduceView;

import java.util.Iterator;
import java.util.List;

/**
 * Created by ${万嘉诚} on 2016/10/27.
 * WeChat：wjc398556712
 * Function：购物车界面适配器
 */

public class ShoppingCartPagerAdapter extends RecyclerView.Adapter<ShoppingCartPagerAdapter.ViewHolder> {

    private Context context;
    private List<ShoppingCart> shoppingCarts;
    private CheckBox checkbox_all;
    private TextView tv_total_price;
    private CartProvider cartProvider;

    public ShoppingCartPagerAdapter(Context context, final List<ShoppingCart> shoppingCarts, final CheckBox checkbox_all, TextView tv_total_price) {
        this.context = context;
        this.shoppingCarts = shoppingCarts;
        this.checkbox_all = checkbox_all;
        this.tv_total_price = tv_total_price;
        cartProvider = new CartProvider(context);
        showTotalPrice();
        setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                //1.设置点击item的状态
                ShoppingCart wares = shoppingCarts.get(position);
                wares.setIsCheck(!wares.isCheck());
                notifyItemChanged(position);//刷新
                //保存选中的状态
                cartProvider.update(wares);
                //2.设置全选和非全选
                checkListener();
                //3.显示总价格
                showTotalPrice();
            }
        });

        checkbox_all.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //全选和反选
                checkAll_none(checkbox_all.isChecked());
                showTotalPrice();
            }
        });
    }

    /**
     * 全选和反选
     *
     * @param ischeck
     */
    public void checkAll_none(boolean ischeck) {
        if (shoppingCarts != null && shoppingCarts.size() > 0) {
            for (int i = 0; i < shoppingCarts.size(); i++) {
                shoppingCarts.get(i).setIsCheck(ischeck);
                notifyItemChanged(i);
            }
        }
    }


    /**
     * 全选的监听
     */
    private void checkListener() {
        int num = 0;
        if (shoppingCarts != null && shoppingCarts.size() > 0) {

            for (int i = 0; i < shoppingCarts.size(); i++) {
                ShoppingCart cart = shoppingCarts.get(i);
                //只要有一个没有被选中就把全选设置为未勾选
                if (!cart.isCheck()) {
                    checkbox_all.setChecked(false);
                } else {
                    num++;
                }
            }
            if (shoppingCarts.size() == num) {
                checkbox_all.setChecked(true);
            }
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(context, R.layout.item_shoppingcart, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final ShoppingCart wares = shoppingCarts.get(position);
        Glide.with(context)
                .load(wares.getImgUrl())
                .placeholder(R.drawable.news_pic_default)
                .error(R.drawable.news_pic_default)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.iv_icon);

        holder.checkbox.setChecked(wares.isCheck());
        holder.tv_name.setText(wares.getName());
        holder.tv_price.setText("￥" + wares.getPrice());
        holder.number_add_reduce.setValue(wares.getCount());

        holder.number_add_reduce.setOnButtonClickListenter(new NumberAddReduceView.OnButtonClickListenter() {
            @Override
            public void onButtonAddClick(View view, int value) {
                wares.setCount(value);
                cartProvider.update(wares);
                showTotalPrice();
            }

            @Override
            public void onButtonReduceClick(View view, int value) {
                wares.setCount(value);
                cartProvider.update(wares);
                showTotalPrice();
            }
        });
    }

    @Override
    public int getItemCount() {
        return shoppingCarts.size();
    }

    /**
     * 购物车中删除选中的商品
     */
    public void deleteCart() {
        if (shoppingCarts != null && shoppingCarts.size() > 0) {
            //方法一:有bug ，全选删除崩溃，下标越界
//            for(int i = 0; i < shoppingCarts.size(); i++) {
//                ShoppingCart wares = shoppingCarts.get(i);
//                if(wares.isCheck()) {
//                    shoppingCarts.remove(i);
//                    cartProvider.deleteDeta(wares);
//                    notifyItemChanged(i);
//                      i--; //解决bug
//                }
//            }
            //  方法二
            for (Iterator iterator = shoppingCarts.iterator(); iterator.hasNext(); ) {
                ShoppingCart wares = (ShoppingCart) iterator.next();
                if (wares.isCheck()) {
                    //根据对象找到在列表中的位置
                    int position = shoppingCarts.indexOf(wares);
                    //从本地中删除
                    iterator.remove();
                    cartProvider.deleteDeta(wares);
                    //刷新页面
                    notifyItemRemoved(position);
                }
            }
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkbox;
        private ImageView iv_icon;
        private TextView tv_name;
        private TextView tv_price;
        private NumberAddReduceView number_add_reduce;

        public ViewHolder(View itemView) {
            super(itemView);
            checkbox = (CheckBox) itemView.findViewById(R.id.checkbox);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            number_add_reduce = (NumberAddReduceView) itemView.findViewById(R.id.number_add_reduce);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(v, getLayoutPosition());
                    }
                }
            });
        }
    }

    /**
     * 显示总商品价格
     */
    private void showTotalPrice() {
        tv_total_price.setText("合计￥" + getTotalPrice());
    }

    /**
     * 计算购物车里面所选商品总架构
     *
     * @return
     */
    public double getTotalPrice() {
        double num = 0;
        if (shoppingCarts != null && shoppingCarts.size() > 0) {

            for (int i = 0; i < shoppingCarts.size(); i++) {
                ShoppingCart cart = shoppingCarts.get(i);
                //判断是否选中的商品
                if (cart.isCheck()) {
                    num = num + cart.getPrice() * cart.getCount();
                }

            }
        }
        LogUtil.e("总价格=========" + num);
        return num;
    }

    //设置点击某个item的监听
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener onItemClickListener;
    /**
     * 设置某条的监听
     *
     * @param onItemClickListener
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
