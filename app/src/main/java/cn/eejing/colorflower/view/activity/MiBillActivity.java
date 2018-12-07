package cn.eejing.colorflower.view.activity;

import android.view.View;
import android.widget.TextView;

import com.lzy.okgo.model.HttpParams;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.http.OkGoBuilder;
import cn.eejing.colorflower.model.request.BillBean;
import cn.eejing.colorflower.presenter.Callback;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.adapter.BillAdapter;
import cn.eejing.colorflower.view.base.BaseActivity;

/**
 * 账单
 */

public class MiBillActivity extends BaseActivity {

    @BindView(R.id.rv_bill)       PullLoadMoreRecyclerView rvBill;
    @BindView(R.id.tv_bill)       TextView tvBill;

    private static final String TAG = "MiBuyRecordActivity";
    private List<BillBean.DataBean> mList;
    private BillAdapter mAdapter;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_mi_bill;
    }

    @Override
    public void initView() {
        setToolbar("账单", View.VISIBLE, null, View.GONE);
        mList = new ArrayList<>();
        initRecyclerView();
    }

    @Override
    public void initData() {
        getDataWithBillLog();
    }

    private void initRecyclerView() {
        rvBill.setLinearLayout();
        mAdapter = new BillAdapter(this, mList);
        rvBill.setAdapter(mAdapter);
        rvBill.setPushRefreshEnable(false);
        rvBill.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                getDataWithBillLog();
            }

            @Override
            public void onLoadMore() {
            }
        });
        rvBill.setPullLoadMoreCompleted();
    }

    @SuppressWarnings("unchecked")
    private void getDataWithBillLog() {
        OkGoBuilder.getInstance().setToken(MainActivity.getAppCtrl().getToken());

        OkGoBuilder.getInstance().Builder(this)
                .url(Urls.WALLET_LOG)
                .method(OkGoBuilder.POST)
                .params(new HttpParams())
                .cls(BillBean.class)
                .callback(new Callback<BillBean>() {
                    @Override
                    public void onSuccess(BillBean bean, int id) {
                        LogUtil.d(TAG, "钱包变更记录 请求成功");

                        switch (bean.getCode()) {
                            case 1:
                                rvBill.setVisibility(View.VISIBLE);
                                tvBill.setVisibility(View.GONE);
                                mList = bean.getData();
                                // 刷新数据
                                mAdapter.refreshList(mList);
                                // 刷新结束
                                rvBill.setPullLoadMoreCompleted();
                                break;
                            case 0:
                                rvBill.setVisibility(View.GONE);
                                tvBill.setVisibility(View.VISIBLE);
                                break;
                            default:
                                ToastUtil.showShort(bean.getMessage());
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e, int id) {
                    }
                }).build();
    }

}
