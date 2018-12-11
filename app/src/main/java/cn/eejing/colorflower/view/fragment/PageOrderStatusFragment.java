package cn.eejing.colorflower.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.lzy.okgo.model.HttpParams;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.http.OkGoBuilder;
import cn.eejing.colorflower.model.request.OrderListBean;
import cn.eejing.colorflower.presenter.Callback;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.view.activity.MainActivity;
import cn.eejing.colorflower.view.adapter.OrderStatusAdapter;
import cn.eejing.colorflower.view.base.BaseFragment;

import static cn.eejing.colorflower.app.AppConstant.ARG_TYPE;
import static cn.eejing.colorflower.app.AppConstant.TYPE_COMPLETE_GOODS;
import static cn.eejing.colorflower.app.AppConstant.TYPE_WAIT_PAYMENT;
import static cn.eejing.colorflower.app.AppConstant.TYPE_WAIT_RECEIPT;
import static cn.eejing.colorflower.app.AppConstant.TYPE_WAIT_SHIP;

/**
 * 订单状态
 */

public class PageOrderStatusFragment extends BaseFragment {
    @BindView(R.id.rv_mi_order_status)    PullLoadMoreRecyclerView rvOrderStatus;
    @BindView(R.id.ll_no_order)           LinearLayout llNoOrder;

    private static final String TAG = "PageOrderStatusFragment";
    private List<OrderListBean.DataBean> mList;
    private OrderStatusAdapter mAdapter;
    private String mType;

    public static PageOrderStatusFragment newInstance(String mTitle) {
        PageOrderStatusFragment fragment = new PageOrderStatusFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_TYPE, mTitle);
        fragment.setArguments(bundle);
        return fragment;
    }

    public PageOrderStatusFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mType = bundle.getString(ARG_TYPE);
        }
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_page_order_status;
    }

    @Override
    public void initView(View rootView) {
        mList = new ArrayList<>();
        initRecyclerView();
    }

    @Override
    public void initData() {
        judgingStateType();
    }

    private void initRecyclerView() {
        rvOrderStatus.setLinearLayout();
        mAdapter = new OrderStatusAdapter(getActivity(), mList, mType);
        rvOrderStatus.setAdapter(mAdapter);
        rvOrderStatus.setPushRefreshEnable(false);
        rvOrderStatus.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                judgingStateType();
            }

            @Override
            public void onLoadMore() {
            }
        });
        // 刷新结束
        rvOrderStatus.setPullLoadMoreCompleted();
    }

    private void judgingStateType() {
        switch (mType) {
            case TYPE_WAIT_PAYMENT:
                getDataWithOrderList("0");
                break;
            case TYPE_WAIT_SHIP:
                getDataWithOrderList("1");
                break;
            case TYPE_WAIT_RECEIPT:
                getDataWithOrderList("2");
                break;
            case TYPE_COMPLETE_GOODS:
                getDataWithOrderList("3");
                break;
        }
    }

    @SuppressWarnings("unchecked")
    private void getDataWithOrderList(String status) {
        OkGoBuilder<OrderListBean> builder = new OkGoBuilder<>();
        builder.setToken(MainActivity.getAppCtrl().getToken());
        HttpParams params = new HttpParams();
        params.put("status", status);

        builder.Builder(getActivity())
                .url(Urls.ORDER_LIST)
                .method(OkGoBuilder.POST)
                .params(params)
                .cls(OrderListBean.class)
                .callback(new Callback<OrderListBean>() {
                    @Override
                    public void onSuccess(OrderListBean bean, int id) {
                        LogUtil.d(TAG, "订单列表 请求成功");

                        switch (bean.getCode()) {
                            case 1:
                                rvOrderStatus.setVisibility(View.VISIBLE);
                                llNoOrder.setVisibility(View.GONE);
                                mList = bean.getData();
                                // 刷新数据
                                mAdapter.refreshList(mList);
                                // 刷新结束
                                rvOrderStatus.setPullLoadMoreCompleted();
                                break;
                            case 0:
                                rvOrderStatus.setVisibility(View.GONE);
                                llNoOrder.setVisibility(View.VISIBLE);
                                // 刷新结束
                                rvOrderStatus.setPullLoadMoreCompleted();
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e, int id) {
                    }
                }).build();
    }

}
