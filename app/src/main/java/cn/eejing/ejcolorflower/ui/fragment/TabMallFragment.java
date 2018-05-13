package cn.eejing.ejcolorflower.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.Urls;
import cn.eejing.ejcolorflower.model.request.ControlBean;
import cn.eejing.ejcolorflower.model.request.GoodsListBean;
import cn.eejing.ejcolorflower.ui.adapter.TabMallAdapter;
import cn.eejing.ejcolorflower.ui.base.BaseFragment;


/**
 * @创建者 Taodaren
 * @描述 商城模块
 */

public class TabMallFragment extends BaseFragment {
    private Gson mGson;
    private List<GoodsListBean.Data> mList;

    @BindView(R.id.rv_tab_mall)
    PullLoadMoreRecyclerView rvTabMall;

    public static TabMallFragment newInstance() {
        return new TabMallFragment();
    }

    public TabMallFragment() {
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_tab_mall;
    }

    @Override
    public void initView(View rootView) {
        mGson = new Gson();
        mList = new ArrayList<>();
        setRecyclerView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 在 onActivityCreated 方法中初始化 Toolbar
        setToolbar(R.id.main_toolbar, R.string.mall_name, View.VISIBLE);
    }

    @Override
    public void onStart() {
        super.onStart();
        getData();
    }

    private void getData() {
        OkGo.<String>get(Urls.Mall.GOODS_LIST)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e("GOODS_LIST", "Network request succeeded --->" + body);
                        GoodsListBean bean = mGson.fromJson(body, GoodsListBean.class);
                        mList = bean.getData();
                        Log.e("GOODS_LIST", "json parse data number --->" + mList.size());
                        if (mList != null && !mList.isEmpty()) {
                            // 刷新数据
//                            rvTabMall.refreshList(mBeanList);
                            // 刷新结束
                            rvTabMall.setPullLoadMoreCompleted();
                        }
                    }
                });
    }

    private void setRecyclerView() {
        // 设置布局
        rvTabMall.setGridLayout(2);
        // 绑定适配器
        TabMallAdapter mallAdapter = new TabMallAdapter();
        rvTabMall.setAdapter(mallAdapter);
        // 调用下拉刷新和加载更多
        rvTabMall.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {

            }
        });
        // 刷新结束
        rvTabMall.setPullLoadMoreCompleted();
    }

}
