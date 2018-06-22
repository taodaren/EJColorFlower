package cn.eejing.ejcolorflower.ui.fragment;

import android.content.Context;
import android.os.Bundle;
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
import cn.eejing.ejcolorflower.model.request.OrderPagerBean;
import cn.eejing.ejcolorflower.ui.adapter.OrderStatusAdapter;
import cn.eejing.ejcolorflower.ui.base.BaseFragment;
import cn.eejing.ejcolorflower.util.Settings;

/**
 * 订单状态
 */

public class MiOrderPageFragment extends BaseFragment {

    @BindView(R.id.rv_mi_order_status)
    PullLoadMoreRecyclerView rvOrderStatus;

    private Gson mGson;
    private List<OrderPagerBean.DataBean> mList;
    private OrderStatusAdapter mAdapter;
    private String mMemberId, mToken;
    private String mType;

    public static MiOrderPageFragment newInstance(String mTitle) {
        MiOrderPageFragment fragment = new MiOrderPageFragment();
        Bundle bundle = new Bundle();
        bundle.putString(AppConstant.ARG_TYPE, mTitle);
        fragment.setArguments(bundle);
        return fragment;
    }

    public MiOrderPageFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mType = bundle.getString(AppConstant.ARG_TYPE);
        }
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_mi_order_page;
    }

    @Override
    public void initView(View rootView) {
        mGson = new Gson();
        mList = new ArrayList<>();
        mMemberId = String.valueOf(Settings.getLoginSessionInfo(getActivity()).getMember_id());
        mToken = Settings.getLoginSessionInfo(getActivity()).getToken();
        initRecyclerView();
    }

    @Override
    public void initData() {
        switch (mType) {
            case AppConstant.TYPE_WAIT_SHIP:
                getDataWithWaitGoods();
                break;
            case AppConstant.TYPE_WAIT_RECEIPT:
                getDataWithWaitGoods();
                break;
            case AppConstant.TYPE_COMPLETE_GOODS:
                getDataWithWaitGoods();
                break;
            default:
                break;
        }
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
                        Log.e(AppConstant.TAG, "wait_goods request succeeded --->" + body);

                        OrderPagerBean bean = mGson.fromJson(body, OrderPagerBean.class);
                        mList = bean.getData();
                        // 刷新数据
                        mAdapter.refreshList(mList);
                        // 刷新结束
                        rvOrderStatus.setPullLoadMoreCompleted();
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
                        Log.e(AppConstant.TAG, "already_goods request succeeded --->" + body);

                        OrderPagerBean bean = mGson.fromJson(body, OrderPagerBean.class);
                        mList = bean.getData();
                        // 刷新数据
                        mAdapter.refreshList(mList);
                        // 刷新结束
                        rvOrderStatus.setPullLoadMoreCompleted();
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
                        Log.e(AppConstant.TAG, "completed request succeeded --->" + body);

                        OrderPagerBean bean = mGson.fromJson(body, OrderPagerBean.class);
                        mList = bean.getData();
                        // 刷新数据
                        mAdapter.refreshList(mList);
                        // 刷新结束
                        rvOrderStatus.setPullLoadMoreCompleted();
                    }
                });
    }

    private void initRecyclerView() {
        // 设置布局
        rvOrderStatus.setLinearLayout();
        // 绑定适配器
        mAdapter = new OrderStatusAdapter(getContext(), mList, mType);
        rvOrderStatus.setAdapter(mAdapter);
        // 不需要上拉刷新
        rvOrderStatus.setPushRefreshEnable(false);
        // 调用下拉刷新和加载更多
        rvOrderStatus.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                switch (mType) {
                    case AppConstant.TYPE_WAIT_SHIP:
                        getDataWithWaitGoods();
                        break;
                    case AppConstant.TYPE_WAIT_RECEIPT:
                        getDataWithWaitGoods();
                        break;
                    case AppConstant.TYPE_COMPLETE_GOODS:
                        getDataWithWaitGoods();
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

}
