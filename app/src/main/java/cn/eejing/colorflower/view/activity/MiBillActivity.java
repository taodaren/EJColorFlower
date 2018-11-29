package cn.eejing.colorflower.view.activity;

import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.request.BillBean;
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

    private void getDataWithBillLog() {
        OkGo.<String>post(Urls.WALLET_LOG)
                .tag(this)
                .params("token", MainActivity.getAppCtrl().getToken())
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 String body = response.body();
                                 LogUtil.d(TAG, "钱包变更记录 请求成功: " + body);

                                 Gson gson = new Gson();
                                 BillBean bean = gson.fromJson(body, BillBean.class);
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
                         }
                );

    }

}
