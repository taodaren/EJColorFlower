package cn.eejing.colorflower.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

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
import cn.eejing.colorflower.app.AppConstant;
import cn.eejing.colorflower.model.request.OrderPagerBean;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.MySettings;
import cn.eejing.colorflower.view.adapter.OrderStatusAdapter;
import cn.eejing.colorflower.view.base.BaseFragment;

import static cn.eejing.colorflower.app.AppConstant.ARG_TYPE;

/**
 * 订单状态
 */

public class PageOrderStatusFragment extends BaseFragment {

    @BindView(R.id.rv_mi_order_status)    PullLoadMoreRecyclerView rvOrderStatus;
    @BindView(R.id.ll_no_order)           LinearLayout llNoOrder;

    private Gson mGson;
    private List<OrderPagerBean.DataBean> mList;
    private OrderStatusAdapter mAdapter;
    private String mMemberId, mToken;
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
        mGson = new Gson();
        mList = new ArrayList<>();
        mMemberId = String.valueOf(MySettings.getLoginSessionInfo(Objects.requireNonNull(getActivity())).getMember_id());
        mToken = MySettings.getLoginSessionInfo(getActivity()).getToken();
        initRecyclerView();
    }

    @Override
    public void initData() {
        switch (mType) {
            case AppConstant.TYPE_WAIT_PAYMENT:
                getDataWithWaitPayment();
                break;
            case AppConstant.TYPE_WAIT_SHIP:
                getDataWithWaitGoods();
                break;
            case AppConstant.TYPE_WAIT_RECEIPT:
                getDataWithAlreadyGoods();
                break;
            case AppConstant.TYPE_COMPLETE_GOODS:
                getDataWithCompleted();
                break;
            default:
                break;
        }
    }

    private void initRecyclerView() {
        // 设置布局
        rvOrderStatus.setLinearLayout();
        // 绑定适配器
        mAdapter = new OrderStatusAdapter(getContext(), mList, mType, mMemberId, mToken);
        rvOrderStatus.setAdapter(mAdapter);
        // 不需要上拉刷新
        rvOrderStatus.setPushRefreshEnable(false);
        // 调用下拉刷新和加载更多
        rvOrderStatus.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                switch (mType) {
                    case AppConstant.TYPE_WAIT_PAYMENT:
                        getDataWithWaitPayment();
                        break;
                    case AppConstant.TYPE_WAIT_SHIP:
                        getDataWithWaitGoods();
                        break;
                    case AppConstant.TYPE_WAIT_RECEIPT:
                        getDataWithAlreadyGoods();
                        break;
                    case AppConstant.TYPE_COMPLETE_GOODS:
                        getDataWithCompleted();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onLoadMore() {
            }
        });
        // 刷新结束
        rvOrderStatus.setPullLoadMoreCompleted();
    }

    private void getDataWithWaitPayment() {
        OkGo.<String>post(Urls.WAIT_GOODS)
                .tag(this)
                .params("member_id", mMemberId)
                .params("token", mToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        LogUtil.d(AppConstant.TAG, "wait_goods request succeeded --->" + body);

                        OrderPagerBean bean = mGson.fromJson(body, OrderPagerBean.class);
                        switch (bean.getCode()) {
                            case 1:
                                mList = bean.getData();
                                // 刷新数据
                                mAdapter.refreshList(mList);
                                // 刷新结束
                                rvOrderStatus.setPullLoadMoreCompleted();
                                break;
                            case 4:
                                llNoOrder.setVisibility(View.VISIBLE);
                                // 刷新结束
                                rvOrderStatus.setPullLoadMoreCompleted();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void getDataWithWaitGoods() {
        OkGo.<String>post(Urls.WAIT_GOODS)
                .tag(this)
                .params("member_id", mMemberId)
                .params("token", mToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        LogUtil.d(AppConstant.TAG, "wait_goods request succeeded --->" + body);

                        OrderPagerBean bean = mGson.fromJson(body, OrderPagerBean.class);
                        switch (bean.getCode()) {
                            case 1:
                                mList = bean.getData();
                                // 刷新数据
                                mAdapter.refreshList(mList);
                                // 刷新结束
                                rvOrderStatus.setPullLoadMoreCompleted();
                                break;
                            case 4:
                                llNoOrder.setVisibility(View.VISIBLE);
                                // 刷新结束
                                rvOrderStatus.setPullLoadMoreCompleted();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void getDataWithAlreadyGoods() {
        OkGo.<String>post(Urls.ALREADY_GOODS)
                .tag(this)
                .params("member_id", mMemberId)
                .params("token", mToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        LogUtil.e(AppConstant.TAG, "already_goods request succeeded --->" + body);

                        OrderPagerBean bean = mGson.fromJson(body, OrderPagerBean.class);
                        switch (bean.getCode()) {
                            case 1:
                                mList = bean.getData();
                                // 刷新数据
                                mAdapter.refreshList(mList);
                                // 刷新结束
                                rvOrderStatus.setPullLoadMoreCompleted();
                                break;
                            case 4:
                                llNoOrder.setVisibility(View.VISIBLE);
                                // 刷新结束
                                rvOrderStatus.setPullLoadMoreCompleted();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void getDataWithCompleted() {
        OkGo.<String>post(Urls.COMPLETED)
                .tag(this)
                .params("member_id", mMemberId)
                .params("token", mToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        LogUtil.e(AppConstant.TAG, "completed request succeeded --->" + body);

                        OrderPagerBean bean = mGson.fromJson(body, OrderPagerBean.class);
                        switch (bean.getCode()) {
                            case 1:
                                mList = bean.getData();
                                // 刷新数据
                                mAdapter.refreshList(mList);
                                // 刷新结束
                                rvOrderStatus.setPullLoadMoreCompleted();
                                break;
                            case 4:
                                llNoOrder.setVisibility(View.VISIBLE);
                                // 刷新结束
                                rvOrderStatus.setPullLoadMoreCompleted();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

}
