package com.wjc.beijingnews2.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.SparseArray;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wjc.beijingnews2.bean.ShoppingCart;
import com.wjc.beijingnews2.bean.ShoppingPagerBean;
import com.wjc.beijingnews2.view.NumberAddReduceView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${万嘉诚} on 2016/10/26.
 * WeChat：wjc398556712
 * Function：购物车存储器类，存取购物车的内容；取内容json数据-->List; 存List-->json数据
 */

public class CartProvider {
    public static final String JSON_CART = "json_cart";
    private final Context context;
    /**
     * SparseArray 替代HashMap,内存效率优于HashMap（执行效率劣于HashMap）
     */
    private SparseArray<ShoppingCart> sparseArray;

    public  CartProvider(Context context){
        this.context  = context;
        sparseArray = new SparseArray<>(10);
        listToSparse();
    }

    /**
     * 从列表中的数据转换成sparseArray
     */
    private void listToSparse() {
        //得到集合
        List<ShoppingCart> shoppingCarts = getAllData();
        if(shoppingCarts != null && shoppingCarts.size() > 0) {
            for (int i = 0; i < shoppingCarts.size();i++){
                ShoppingCart shoppingCart = shoppingCarts.get(i);
                sparseArray.put(shoppingCart.getId(),shoppingCart);
            }
        }
    }

    /**
     * 得到所有的数据
     * @return
     */
    public List<ShoppingCart> getAllData() {
        return getLocalData();
    }

    private List<ShoppingCart> getLocalData() {
        List<ShoppingCart> shoppingCarts = new ArrayList<>();
        //从本地保持中获取数据
        String saveJson = CacheUtils.getString(context,JSON_CART);
        if(!TextUtils.isEmpty(saveJson)) {
            //直接把json数据解析成集合
            shoppingCarts = new Gson().fromJson(saveJson,new TypeToken<List<ShoppingCart>>(){}.getType());
        }
        return shoppingCarts;
    }

    /**
     * 添加数据
     * @param cart
     */
    public void addData(ShoppingCart cart){
        //1、增加数据
        ShoppingCart tempCart = sparseArray.get(cart.getId());//判断是否在列表中存在
        if(tempCart != null) {
            //购物车添加每个物品的最大数目
            int maxCount = new NumberAddReduceView(context).getMaxValue();
            if(tempCart.getCount() < maxCount) {
                tempCart.setCount(tempCart.getCount() + 1);
            }
        } else {
            tempCart = cart;
            tempCart.setCount(1);
        }

        //保持到sparseArray
        sparseArray.put(tempCart.getId(),tempCart);

        //2、保存到本地
        commit();
    }

    /**
     * 把List列表数据-->json文本数据-->保持到本地
     */
    private void commit() {
        List<ShoppingCart> shoppingCarts = sparseTolist();
        String json = new Gson().toJson(shoppingCarts);
        CacheUtils.putString(context,JSON_CART,json);
    }

    private List<ShoppingCart> sparseTolist() {
        List<ShoppingCart> shoppingCarts = new ArrayList<>();
        for(int i = 0; i < sparseArray.size(); i++) {
            ShoppingCart shoppingCart = sparseArray.valueAt(i);
            shoppingCarts.add(shoppingCart);
        }
        return shoppingCarts;
    }

    /**
     * 删除数据
     * @param cart
     */
    public void deleteDeta(ShoppingCart cart){
        //1.删除数据
        sparseArray.delete(cart.getId());

        //2.保持本地
        commit();
    }

    /**
     * 修改数据
     * @param cart
     */
    public void update(ShoppingCart cart){
        //1.修改数据
        sparseArray.put(cart.getId(), cart);

        //2.保持本地
        commit();
    }

    /**
     * 把Wares转换成ShoppingCart
     * @param wares
     * @return
     */
    public ShoppingCart conversion(ShoppingPagerBean.Wares wares) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setId(wares.getId());
        shoppingCart.setCount(1);
        shoppingCart.setIsCheck(true);
        shoppingCart.setDescription(wares.getDescription());
        shoppingCart.setImgUrl(wares.getImgUrl());
        shoppingCart.setPrice(wares.getPrice());
        shoppingCart.setSale(wares.getSale());
        shoppingCart.setName(wares.getName());
        return shoppingCart;
    }

}
