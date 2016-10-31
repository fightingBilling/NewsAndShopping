package com.wjc.beijingnews2.pager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.wjc.beijingnews2.R;
import com.wjc.beijingnews2.adapter.ShoppingCartPagerAdapter;
import com.wjc.beijingnews2.alipay.PayResult;
import com.wjc.beijingnews2.alipay.SignUtils;
import com.wjc.beijingnews2.base.BasePager;
import com.wjc.beijingnews2.bean.ShoppingCart;
import com.wjc.beijingnews2.utils.CartProvider;
import com.wjc.beijingnews2.utils.LogUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Created by ${万嘉诚} on 2016/10/17.
 * WeChat：wjc398556712
 * Function：
 */
public class ShoppingCartPager extends BasePager {

    private RecyclerView recyclerview;
    private CheckBox checkbox_all;
    private TextView tv_total_price;
    private Button btn_order;
    private Button btn_delete;
    private ShoppingCartPagerAdapter adapter;
    private TextView tv_shopping_cart_empty;
    /**
     * 编辑状态
     */
    public static final int ACTION_EDIT = 1;
    /**
     * 完成状态
     */
    public static final int ACTION_COMPLETE = 2;

    // 商户PID
    public static final String PARTNER = "2088911876712776";
    // 商户收款账号
    public static final String SELLER = "chenlei@atguigu.com";
    // 商户私钥，pkcs8格式   一般放在服务器上，我们这里就放在代码里简化操作
    public static final String RSA_PRIVATE = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJRNvuqc9B73oT54\n" +
            "GmOPng8qiq1IlhLWItWIs0rZLjSacT+VKjVdxAJBbxbTpF61aMhtWu6RwAd+8qBU\n" +
            "5AeQFNs4zU5CTjFFjdGPpm8ROGBYar0C1WzJdkjkqAvLaXG0rJeWP+EIT+FzyAbA\n" +
            "qAXXzWbdKgLdTXtxZb01MDl8mTQjAgMBAAECgYAz29/n4IyJC6Sp2Iu5xu3JdeHa\n" +
            "aGh6G8FAlDXF9Z3vrRXu2vVQhgJVm5YCEG4I5DzI4VyL0hGpTV4AbM70ShDQc4v+\n" +
            "iZh3lTmzv4rhUDz3BrxbwR96ebtITTqvBKDQgXDpLKiJocwvlrd5EFgAJYpGZqRA\n" +
            "GCIBbHftZ5UvMyySIQJBAMT27wKk8xoW4BhcIdma+NOQqONeWX5VatqW8qH/dZTC\n" +
            "98gWYgvUwADxtKdqlNphoDHlBlVF5JUEVWfVsMTQAlECQQDAwRGJ0toVVQpRcqnD\n" +
            "+70F78zb/cEefidkU5ssszgCF90/zikoXJjA1KXFACzug7cyztvXa0VU2mLWLuGW\n" +
            "n14zAkAr++S113X+LnuOlQxuFqBYRmagl5Iulw6Mj8bRDEYKmVtR0EXG1JSn4VHx\n" +
            "TOi+t6xZWAaJBlmcOWKFFIAsAzNxAkA1e1xqaV6pXJcoUjBYeJjR9N9aiuXyl/5G\n" +
            "EAyWMoPv0L9K3OD+mfKoTlhQeOP+qf1C07Kb6t+p045o70kYic+RAkA12/woH7cC\n" +
            "JyDmHW1/HZR/rk/ilM+verss0mCzTNLFYtGh1pu09HyvN5XwkRf36CoE0QYEsC4c\n" +
            "sHjoR3G47/2T";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCUTb7qnPQe96E+eBpjj54PKoqtSJYS1iLViLNK2S40mnE/lSo1XcQCQW8W06RetWjIbVrukcAHfvKgVOQHkBTbOM1OQk4xRY3Rj6ZvEThgWGq9AtVsyXZI5KgLy2lxtKyXlj/hCE/hc8gGwKgF181m3SoC3U17cWW9NTA5fJk0IwIDAQAB";
    private static final int SDK_PAY_FLAG = 1;

    private CartProvider cartProvider;
    private List<ShoppingCart> shoppingCarts;

    public ShoppingCartPager(Context context) {
        super(context);
        cartProvider = new CartProvider(context);
    }

    @Override
    public void initData() {
        super.initData();
        btn_cart.setVisibility(View.VISIBLE);

        LogUtil.e("购物车界面被初始化了-------");
        //1.设置标题
        tv_title.setText("购物车界面");
        initSubView();

        //设置默认是编辑tag状态
        btn_cart.setTag(ACTION_EDIT);
        btn_cart.setText("编辑");
        btn_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int action = (int) btn_cart.getTag();
                //1.如果是编译状态就改为完成
                if (action == ACTION_EDIT) {

                    showDeleteControl();

                    //2.如果是完成就改为编辑
                } else if (action == ACTION_COMPLETE) {
                    hideDeleteControl();
                }
            }
        });

        //设置点击删除
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkbox_all.isChecked()) {
                    checkbox_all.setChecked(false);
                }
                //只是把选择的删除
                adapter.deleteCart();
                if(shoppingCarts.size() == 0) {
                    tv_shopping_cart_empty.setVisibility(View.VISIBLE);
                    tv_total_price.setText("合计￥0.0");
                    checkbox_all.setChecked(false);
                }
            }
        });

        //去结算
        btn_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(shoppingCarts.size() > 0) {
                    pay(v);
                } else {
                    Toast.makeText(context, "没有物品可以结算", Toast.LENGTH_SHORT).show();
                }
            }
        });
        showData();
    }


    /**
     * call alipay sdk pay. 调用SDK支付
     *
     */
    public void pay(View v) {
        if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE) || TextUtils.isEmpty(SELLER)) {
            new AlertDialog.Builder(context).setTitle("警告").setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialoginterface, int i) {
                            //
//                            finish();
                        }
                    }).show();
            return;
        }
        // 取出所选商品的总价格
        String price = String.valueOf(adapter.getTotalPrice());
        String orderInfo = getOrderInfo("测试的商品", "该测试商品的详细描述", price);
//        String orderInfo = getOrderInfo("测试的商品", "该测试商品的详细描述", "0.01");

        /**
         * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
         */
        String sign = sign(orderInfo);
        try {
            /**
             * 仅需对sign 做URL编码
             */
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        /**
         * 完整的符合支付宝参数规范的订单信息
         */
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask((Activity) context);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(context, "支付成功", Toast.LENGTH_SHORT).show();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(context, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(context, "支付失败", Toast.LENGTH_SHORT).show();

                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * create the order info. 创建订单信息
     *
     */
    private String getOrderInfo(String subject, String body, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm" + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }


    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content
     *            待签名订单信息
     */
    private String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     *
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }
    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     *
     */
    private String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + r.nextInt();
        key = key.substring(0, 15);
        return key;
    }




    /**
     * 隐藏删除按钮相关处理
     */
    private void hideDeleteControl() {
        //按钮状态设置为编辑
        btn_cart.setText("编辑");
        //总价格显示
        tv_total_price.setVisibility(View.VISIBLE);
        //全选按钮设置勾选
        if(shoppingCarts.size() > 0) {
            checkbox_all.setChecked(true);
        }else {
            checkbox_all.setChecked(false);
        }
        //删除按钮隐藏
        btn_delete.setVisibility(View.GONE);
        //显示去结算按钮
        btn_order.setVisibility(View.VISIBLE);
        //把所有的设置为勾选
        adapter.checkAll_none(true);
        //设置tag
        btn_cart.setTag(ACTION_EDIT);
    }

    /**
     * 显示删除按钮相关处理
     */
    private void showDeleteControl() {
        //按钮状态设置为完成
        btn_cart.setText("完成");
        //总价格隐藏
        tv_total_price.setVisibility(View.GONE);
        //全选按钮设置非勾选
        checkbox_all.setChecked(false);
        //删除按钮显示
        btn_delete.setVisibility(View.VISIBLE);
        //隐藏去结算按钮
        btn_order.setVisibility(View.GONE);
        //把所有的设置为非勾选
        adapter.checkAll_none(false);
        //设置tag
        btn_cart.setTag(ACTION_COMPLETE);
    }

    /**
     * 创建子页面的视图
     */
    private void initSubView() {
        View view = View.inflate(context, R.layout.shopping_cart_pager, null);
        recyclerview = (RecyclerView) view.findViewById(R.id.recyclerview);
        checkbox_all = (CheckBox) view.findViewById(R.id.checkbox_all);
        tv_total_price = (TextView) view.findViewById(R.id.tv_total_price);
        btn_order = (Button) view.findViewById(R.id.btn_order);
        btn_delete = (Button) view.findViewById(R.id.btn_delete);
        tv_shopping_cart_empty = (TextView) view.findViewById(R.id.tv_shopping_cart_empty);

        //3.把子视图添加到BasePager的FrameLayout中
        fl_content.removeAllViews();
        fl_content.addView(view);

    }

    private void showData() {
        shoppingCarts = cartProvider.getAllData();

        if (shoppingCarts.size() > 0) {
            //设置为全选
            checkbox_all.setChecked(true);
        } else {//购物车为空，显示文本
            tv_shopping_cart_empty.setVisibility(View.VISIBLE);
        }

        adapter = new ShoppingCartPagerAdapter(context, shoppingCarts, checkbox_all, tv_total_price);
        recyclerview.setAdapter(adapter);
        recyclerview.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerview.setItemAnimator(new DefaultItemAnimator());
    }
}
