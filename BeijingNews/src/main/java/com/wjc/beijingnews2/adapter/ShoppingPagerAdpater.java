package com.wjc.beijingnews2.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.wjc.beijingnews2.R;
import com.wjc.beijingnews2.bean.ShoppingCart;
import com.wjc.beijingnews2.bean.ShoppingPagerBean;
import com.wjc.beijingnews2.utils.CartProvider;

import java.util.List;

/**
 * Created by ${万嘉诚} on 2016/10/26.
 * WeChat：wjc398556712
 * Function：购物车界面适配器
 */

public class ShoppingPagerAdpater extends RecyclerView.Adapter<ShoppingPagerAdpater.ViewHolder> {

    private Context context;
    private List<ShoppingPagerBean.Wares> datas;
    private CartProvider cartProvider;

    public ShoppingPagerAdpater(Context context, List<ShoppingPagerBean.Wares> list) {
        this.context = context;
        this.datas = list;
        cartProvider = new CartProvider(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(context, R.layout.item_shopping_pager,null);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //1.根据位置得到对应的数据
        ShoppingPagerBean.Wares wares = datas.get(position);
        Glide
                .with(context)
                .load(wares.getImgUrl())
                .centerCrop()
                .placeholder(R.drawable.pic_item_list_default)
                .crossFade()
                .into(holder.iv_icon);
        holder.tv_name.setText(wares.getName());
        holder.tv_price.setText("￥"+wares.getPrice());

        //2.绑定数据
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    /**
     * 清除数据
     */
    public void clearData() {
        datas.clear();
        notifyItemRangeRemoved(0, datas.size());
    }

    /**
     * 添加数据
     * @param count
     * @param list
     */
    public void addData(int count, List<ShoppingPagerBean.Wares> list) {
        datas.addAll(count,list);
        notifyItemRangeChanged(count,datas.size());
    }

    /**
     * 得到多少条数据
     * @return
     */
    public int getCount() {
        return  datas.size();
    }

    public void addData(List<ShoppingPagerBean.Wares> list) {
        addData(0,list);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView iv_icon;
        private TextView tv_name;
        private TextView tv_price;
        private Button btn_buy;


        public ViewHolder(View itemView) {
            super(itemView);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            tv_price = (TextView) itemView.findViewById(R.id.tv_price);
            btn_buy = (Button) itemView.findViewById(R.id.btn_buy);

            btn_buy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Toast.makeText(context, "败家", Toast.LENGTH_SHORT).show();
                    ShoppingPagerBean.Wares wares = datas .get(getLayoutPosition());
                    ShoppingCart shoppingCart = cartProvider.conversion(wares);
                    cartProvider.addData(shoppingCart);
                }
            });
        }
    }
}
