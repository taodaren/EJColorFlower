package cn.eejing.colorflower.view.fragment;

import android.content.Intent;
import android.view.View;

import com.lzy.okgo.model.HttpParams;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.http.OkGoBuilder;
import cn.eejing.colorflower.model.request.GoodsListBean;
import cn.eejing.colorflower.presenter.Callback;
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

    @BindView(R.id.rv_tab_mall)        PullLoadMoreRecyclerView rvTabMall;

    private static final String TAG = "TabMallFragment";
    private List<GoodsListBean.DataBean> mList;
    private TabMallAdapter mAdapter;

    public static TabMallFragment newInstance() {
        return new TabMallFragment();
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_tab_mall;
    }

    @Override
    public void initToolbar() {
        setToolbar(R.id.main_toolbar, R.string.mall_name, View.VISIBLE);
    }

    @Override
    public void initData() {
        getDataWithGoodsList();
    }

    @Override
    public void initView(View rootView) {
        mList = new ArrayList<>();
        initRecyclerView();
    }

    @SuppressWarnings("unchecked")
    private void getDataWithGoodsList() {
        OkGoBuilder.getInstance().setToken(MainActivity.getAppCtrl().getToken());
        HttpParams params = new HttpParams();
        params.put("page", 1);

        OkGoBuilder.getInstance().Builder(getActivity())
                .url(Urls.GET_GOODS_LIST)
                .method(OkGoBuilder.POST)
                .params(params)
                .cls(GoodsListBean.class)
                .callback(new Callback<GoodsListBean>() {
                    @Override
                    public void onSuccess(GoodsListBean bean, int id) {
                        LogUtil.d(TAG, "商品列表 请求成功");
                        mList = bean.getData();
                        // 刷新数据
                        mAdapter.refreshList(mList);
                    }

                    @Override
                    public void onError(Throwable e, int id) {
                    }
                }).build();
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
