package cn.eejing.ejcolorflower.view.fragment;

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
import cn.eejing.ejcolorflower.model.request.GoodsListBean;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.view.adapter.TabMallAdapter;
import cn.eejing.ejcolorflower.view.base.BaseFragment;

/**
 * 商城模块
 */

public class TabMallFragment extends BaseFragment {

    @BindView(R.id.rv_tab_mall)        PullLoadMoreRecyclerView rvTabMall;

    private Gson mGson;
    private List<GoodsListBean.DataBean> mList;
    private TabMallAdapter mMallAdapter;

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
        initRecyclerView();
    }

    @Override
    public void initToolbar() {
        setToolbar(R.id.main_toolbar, R.string.mall_name, View.VISIBLE);
    }

    @Override
    public void initData() {
        getDataWithGoodsList();
    }

    private void getDataWithGoodsList() {
        OkGo.<String>get(Urls.GOODS_LIST)
                .tag(this)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "Network request succeeded --->" + body);
                        GoodsListBean bean = mGson.fromJson(body, GoodsListBean.class);
                        mList = bean.getData();
                        // 刷新数据
                        mMallAdapter.refreshList(mList);
                    }
                });
    }

    private void initRecyclerView() {
        // 设置布局
        rvTabMall.setGridLayout(2);
        // 绑定适配器
        mMallAdapter = new TabMallAdapter(getContext(), mList);
        rvTabMall.setAdapter(mMallAdapter);
        // 不需要上拉刷新
        rvTabMall.setPushRefreshEnable(false);
        // 调用下拉刷新和加载更多
        rvTabMall.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                getDataWithGoodsList();
                // 刷新结束
                rvTabMall.setPullLoadMoreCompleted();
            }

            @Override
            public void onLoadMore() {
            }
        });
        // 刷新结束
        rvTabMall.setPullLoadMoreCompleted();
    }

}
