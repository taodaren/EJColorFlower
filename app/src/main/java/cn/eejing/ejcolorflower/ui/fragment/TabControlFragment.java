package cn.eejing.ejcolorflower.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.gson.Gson;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.model.request.GoodsListBean;
import cn.eejing.ejcolorflower.ui.base.BaseFragment;


/**
 * @创建者 Taodaren
 * @描述 控制模块
 */

public class TabControlFragment extends BaseFragment {
    private Gson mGson;
    private List<GoodsListBean.DataBean> mList;

    @BindView(R.id.rv_tab_control)
    PullLoadMoreRecyclerView rvTabControl;

    public static TabControlFragment newInstance() {
        return new TabControlFragment();
    }

    public TabControlFragment() {
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_tab_control;
    }

    @Override
    public void initView(View rootView) {
        mGson = new Gson();
        mList = new ArrayList<>();
        setRecyclerView();
        rvTabControl.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {

            }
        });
        rvTabControl.setPushRefreshEnable(false);
    }

    private void setRecyclerView() {

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 在 onActivityCreated 方法中初始化 Toolbar
        setToolbar(R.id.main_toolbar, R.string.control_name, View.VISIBLE);
    }

}
