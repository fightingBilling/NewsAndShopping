package com.wjc.recyclerviewdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by ${万嘉诚} on 2016/10/25.
 * WeChat：wjc398556712
 * Function：RecyclerView的适配器
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    private final Context context;
    private final ArrayList<String> datas;

    public RecyclerViewAdapter(Context context, ArrayList<String> datas) {
        this.context = context;
        this.datas = datas;
    }

    /**
     * 相当于getView方法中创建ViewHolder
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = View.inflate(context,R.layout.item,null);
        return new ViewHolder(itemView);
    }

    /**
     * 相当于getView方法中绑定数据
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //1.根据位置得到数据
        String data = datas.get(position);
        //2.绑定数据
        holder.tv_title.setText(data);
        //3.绑定图片
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    /**
     * 添加数据
     * @param position
     * @param data
     */
    public void addData(int position, String data) {
        datas.add(position,data);
        //添加数据的刷新
        notifyItemInserted(position);
    }

    /**
     * 移除数据
     * @param position
     */
    public void removeData(int position) {
        datas.remove(position);
        //移除的刷新
        notifyItemRemoved(position);
    }

    public void removeAllData() {
        datas.clear();
        notifyItemRangeChanged(0, datas.size());
    }

    public void addAllData(ArrayList<String> data) {
        datas.addAll(data);
        notifyItemRangeChanged(0, datas.size());
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView iv_icon;
        private TextView tv_title;

        public ViewHolder(View itemView) {
            super(itemView);
            iv_icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            tv_title = (TextView) itemView.findViewById(R.id.tv_title);

            //设置点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(onItemClickListener != null){
                        onItemClickListener.onItemClick(v,getLayoutPosition(),datas.get(getLayoutPosition()));
                    }
//                    Toast.makeText(context, "data=="+datas.get(getLayoutPosition()), Toast.LENGTH_SHORT).show();
                }
            });

            iv_icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "图片被点击了data==" + datas.get(getLayoutPosition()), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public interface OnItemClickListener{
        /**
         * 当点击某条的时候回调这方法
         * @param view
         * @param position
         * @param data
         */
        public void onItemClick(View view,int position,String data);
    }

    private OnItemClickListener onItemClickListener;

    /**
     * 设置item点击监听
     * @param l
     */
    public void setOnItemClickListener(OnItemClickListener l){
        this.onItemClickListener = l;
    }
}
