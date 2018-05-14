package cn.eejing.ejcolorflower.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.app.Urls;
import cn.eejing.ejcolorflower.model.request.GoodsListBean;
import cn.eejing.ejcolorflower.ui.adapter.TabMallAdapter;
import cn.eejing.ejcolorflower.ui.base.BaseFragment;


/**
 * @创建者 Taodaren
 * @描述 商城模块
 */

public class TabMallFragment extends BaseFragment {
    private Gson mGson;
    private List<GoodsListBean.DataBean> mList;

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
    public void initView(View rootView) {// 我封装的 相当于onCreateView
        mGson = new Gson();
        mList = new ArrayList<>();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 在 onActivityCreated 方法中初始化 Toolbar
        setToolbar(R.id.main_toolbar, R.string.mall_name, View.VISIBLE);
        getData();
        //        setRecyclerView();
        //        rvTabMall.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
        //            @Override
        //            public void onRefresh() {
        //                getData();
        //            }
        //
        //            @Override
        //            public void onLoadMore() {
        //
        //            }
        //        });
    }

    private void getData() {
        OkGo.<String>get(Urls.Mall.GOODS_LIST)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "Network request succeeded --->" + body);
                        GoodsListBean bean = mGson.fromJson(body, GoodsListBean.class);
                        mList = bean.getData();
                        Log.e(AppConstant.TAG, "json parse data number --->" + mList.size());
                    }
                });
        // 刷新结束
        rvTabMall.setPullLoadMoreCompleted();
        rvTabMall.setPushRefreshEnable(false);
    }

    private void setRecyclerView() {
        if (mList != null && !mList.isEmpty()) {
            // 设置布局
            rvTabMall.setGridLayout(2);
            // 绑定适配器
            TabMallAdapter mallAdapter = new TabMallAdapter(getContext(), mList);
            rvTabMall.setAdapter(mallAdapter);
        }
    }

}
