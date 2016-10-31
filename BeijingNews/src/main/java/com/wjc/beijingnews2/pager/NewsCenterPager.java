package com.wjc.beijingnews2.pager;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wjc.beijingnews2.activity.MainActivity;
import com.wjc.beijingnews2.base.BasePager;
import com.wjc.beijingnews2.base.MenuDetailBasePager;
import com.wjc.beijingnews2.bean.NewsCenterPagerBean2;
import com.wjc.beijingnews2.fragment.LeftMenuFragment;
import com.wjc.beijingnews2.menudetailpager.InteractMenuDetailPager;
import com.wjc.beijingnews2.menudetailpager.NewsMenuDetailPager;
import com.wjc.beijingnews2.menudetailpager.PhotosMenuDetailPager;
import com.wjc.beijingnews2.menudetailpager.TopicMenuDetailPager;
import com.wjc.beijingnews2.utils.CacheUtils;
import com.wjc.beijingnews2.utils.Contants;
import com.wjc.beijingnews2.utils.LogUtil;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ${万嘉诚} on 2016/10/17.
 * WeChat：wjc398556712
 * Function：新闻中心页
 */
public class NewsCenterPager extends BasePager {
    /**
     * 左侧菜单对应的数据集合
     */
    private List<NewsCenterPagerBean2.NewsCenterPagerData> data;

    /**
     * 详情页面的集合
     */
    private ArrayList<MenuDetailBasePager> detailBasePagers;


    public NewsCenterPager(Context context) {
        super(context);
    }

    @Override
    public void initData() {
        super.initData();
        LogUtil.e("新闻界面被初始化了-------");
        ib_menu.setVisibility(View.VISIBLE);
        //1.设置标题
        tv_title.setText("新闻界面");
        //2.联网请求，得到数据，创建视图
        TextView textView = new TextView(context);//动态创建视图
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        textView.setTextSize(25);
        //3.把子视图添加到BasePager的FrameLayout中
        fl_content.addView(textView);
        //4.绑定数据
        textView.setText("新闻界面内容");

        //得到缓存Json数据
        String saveJson = CacheUtils.getString(context, Contants.NEWSCENTER_PAGER_URL);
        LogUtil.e("saveJson====" + saveJson);
        if(!TextUtils.isEmpty(saveJson)) {//不能用 saveJson!=null,因为""也满足
            processData(saveJson);
        }

        //联网请求数据
        getDataFromNet();
    }

    /**
     * 使用xUtils3联网请求数据
     */
    private void getDataFromNet() {

        x.http().get(new RequestParams(Contants.NEWSCENTER_PAGER_URL), new Callback.CommonCallback<String>() {


            @Override
            public void onSuccess(String result) {
                LogUtil.e("使用xUtils3联网请求成功==" + result);
                //将联网请求的Json数据存到内存
                CacheUtils.putString(context,Contants.NEWSCENTER_PAGER_URL,result);

                processData(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                LogUtil.e("使用xUtils3联网请求出错==" + ex.getMessage());

            }

            @Override
            public void onCancelled(CancelledException cex) {
                LogUtil.e("使用xUtils3联网请求取消==" + cex.getMessage());
            }

            @Override
            public void onFinished() {
                LogUtil.e("使用xUtils3联网请求结束==");

            }
        });
    }


    private void processData(String json) {
        NewsCenterPagerBean2 bean = parsedJson(json);
        String title = bean.getData().get(0).getChildren().get(1).getTitle();
        LogUtil.e("使用Gson解析json数据成功-title==" + title);

        //给左侧菜单传递的数据
        data = bean.getData();
        LogUtil.e("给左侧菜单传递的数据" + data);

        //得到左侧菜单
        LeftMenuFragment leftMenuFragment = ((MainActivity) context).getLeftmenuFragment();
        //添加详情页面
        // 要放在把数据传递给左侧菜单(leftmenuFragment.setData(data))的前面 ，否则有空指针异常
        detailBasePagers = new ArrayList<>();
        detailBasePagers.add(new NewsMenuDetailPager(context,data.get(0)));
        detailBasePagers.add(new TopicMenuDetailPager(context,data.get(0)));
        detailBasePagers.add(new PhotosMenuDetailPager(context,data.get(2)));
        detailBasePagers.add(new InteractMenuDetailPager(context,data.get(2)));
        //把数据传递给左侧菜单
        leftMenuFragment.setData(data);

    }

    /**
     * 解析json数据：1,使用系统的API解析json；2,使用第三方框架解析json数据，例如Gson,fastjson
     *
     * @param json
     * @return
     */
    private NewsCenterPagerBean2 parsedJson(String json) {
        return new Gson().fromJson(json, NewsCenterPagerBean2.class);
    }

    /**
     * 根据位置切换详情页面
     *
     * @param position
     */
    public void switchThisPager(int position) {
        if(position < detailBasePagers.size()) {
            //1.设置标题
            tv_title.setText(data.get(position).getTitle());
            //2.移除之前视图
            fl_content.removeAllViews();
            //3.添加新内容
            MenuDetailBasePager detailBasePager = detailBasePagers.get(position);
            View rootView = detailBasePager.rootView;
            //初始化数据
            detailBasePager.initData();
            fl_content.addView(rootView);

            if(position == 2) {
                ib_swich_list_grid.setVisibility(View.VISIBLE);
                ib_swich_list_grid.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //1.得到图组详情页面对象
                        PhotosMenuDetailPager photosMenuDetailPager = (PhotosMenuDetailPager) detailBasePagers.get(2);
                        //2.调用图组对象的切换ListView和GridView的方法
                        photosMenuDetailPager.swichListAndGrid(ib_swich_list_grid);

                    }
                });
            } else {
                ib_swich_list_grid.setVisibility(View.GONE);
            }
        } else {
            Toast.makeText(context, "抱歉，该功能还未实现", Toast.LENGTH_SHORT).show();
        }
    }

//    /**
//     * 手动解析json数据
//     *
//     * @param json
//     * @return
//     */
//    private NewsCenterPagerBean2 parsedJson2(String json) {
//
//        NewsCenterPagerBean2 bean2 = new NewsCenterPagerBean2();
//        try {
//            JSONObject jsonObject = new JSONObject(json);
//            int retcode = jsonObject.optInt("retcode");
//            bean2.setRetcode(retcode);
//            JSONArray jsonArray = jsonObject.optJSONArray("data");
//
//            if (jsonArray != null) {
//                //创建集合装数据
//                List<NewsCenterPagerBean2.NewsCenterPagerData> list = new ArrayList<>();
//                //把集合关联到Bean对象中
//                bean2.setData(list);
//
//                for (int i = 0; i < jsonArray.length(); i++) {
//
//                    JSONObject itemData = (JSONObject) jsonArray.get(i);
//                    if (itemData != null) {
//                        NewsCenterPagerBean2.NewsCenterPagerData newsCenterPagerData = new NewsCenterPagerBean2.NewsCenterPagerData();
//
//                        int id = itemData.optInt("id");
//                        newsCenterPagerData.setId(id);
//                        int type = itemData.optInt("type");
//                        newsCenterPagerData.setType(type);
//                        String title = itemData.optString("title");
//                        newsCenterPagerData.setTitle(title);
//                        String url = itemData.optString("url");
//                        newsCenterPagerData.setUrl(url);
//                        String url1 = itemData.optString("url1");
//                        newsCenterPagerData.setUrl1(url1);
//                        String dayurl = itemData.optString("dayurl");
//                        newsCenterPagerData.setDayurl(dayurl);
//                        String excurl = itemData.optString("excurl");
//                        newsCenterPagerData.setExcurl(excurl);
//                        String weekurl = itemData.optString("weekurl");
//                        newsCenterPagerData.setWeekurl(weekurl);
//
//                        JSONArray childrenjsonArray = itemData.optJSONArray("children");
//                        if (childrenjsonArray != null) {
//                            List<NewsCenterPagerBean2.NewsCenterPagerData.ChildrenData> childrenDatas = new ArrayList<>();
//                            //设置children的数据
//                            newsCenterPagerData.setChildren(childrenDatas);
//                            for (int j = 0; j < childrenjsonArray.length(); j++) {
//                               JSONObject chilrenItemData = (JSONObject) childrenjsonArray.get(j);
//                                if(chilrenItemData != null) {
//                                    NewsCenterPagerBean2.NewsCenterPagerData.ChildrenData childrenData = new NewsCenterPagerBean2.NewsCenterPagerData.ChildrenData();
//                                    //添加数据
//                                    childrenData.setId(chilrenItemData.optInt("id"));
//                                    childrenData.setType(chilrenItemData.optInt("type"));
//                                    childrenData.setTitle(chilrenItemData.optString("title"));
//                                    childrenData.setUrl(chilrenItemData.optString("url"));
//                                    //添加到集合中
//                                    childrenDatas.add(childrenData);
//                                }
//                            }
//                        }
//                        //把数据添加到集合中
//                        list.add(newsCenterPagerData);
//                    }
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return bean2;
//    }
}
