package cn.eejing.colorflower.view.fragment;

import android.content.Intent;
import android.view.View;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.request.GoodsListBean;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.activity.MaGoodsDetailsActivity;
import cn.eejing.colorflower.view.activity.MainActivity;
import cn.eejing.colorflower.view.adapter.TabMallAdapter;
import cn.eejing.colorflower.view.base.BaseFragment;

/**
 * 商城模块
 */

public class TabMallFragment extends BaseFragment {
    private static final String TAG = "TabMallFragment";

    @BindView(R.id.rv_tab_mall)        PullLoadMoreRecyclerView rvTabMall;

    private Gson mGson;
    private List<GoodsListBean.DataBean> mList;
    private TabMallAdapter mAdapter;

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
        OkGo.<String>get(Urls.GET_GOODS_LIST)
                .tag(this)
                .params("token", MainActivity.getAppCtrl().getToken())
                .params("page", 1)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        LogUtil.d(TAG, "商品列表 请求成功: " + body);
                        GoodsListBean bean = mGson.fromJson(body, GoodsListBean.class);
                        mList = bean.getData();
                        // 刷新数据
                        mAdapter.refreshList(mList);
                    }
                });
    }

    private void initRecyclerView() {
        // 设置布局
        rvTabMall.setGridLayout(2);
        // 绑定适配器
        mAdapter = new TabMallAdapter(getContext(), mList);
        mAdapter.setOnClickListener(v -> {
            int position = (int) v.getTag();
            if (mList.get(position).getStore_count().equals("0")) {
                ToastUtil.showShort("商家正在补货中...");
            } else {
            int goodsId = mList.get(position).getId();
            String name = mList.get(position).getGoods_name();

            Intent intent = new Intent(getContext(), MaGoodsDetailsActivity.class);
            intent.putExtra("goods_id", goodsId);
            intent.putExtra("name", name);
            ((MainActivity) Objects.requireNonNull(getActivity())).jumpToActivity(intent);
            }
        });
        rvTabMall.setAdapter(mAdapter);
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
