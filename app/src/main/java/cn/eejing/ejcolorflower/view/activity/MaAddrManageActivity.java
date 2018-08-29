package cn.eejing.ejcolorflower.view.activity;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.model.event.AddrAddEvent;
import cn.eejing.ejcolorflower.model.request.AddrListBean;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.util.Settings;
import cn.eejing.ejcolorflower.view.adapter.AddrManageAdapter;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

/**
 * 管理收货地址
 */

public class MaAddrManageActivity extends BaseActivity {

    @BindView(R.id.btn_shipping_address)        Button btnAddAddress;
    @BindView(R.id.rv_shipping_address)         PullLoadMoreRecyclerView rvAddress;

    private Gson mGson;
    private String mMemberId, mToken;
    private List<AddrListBean.DataBean> mList;
    private AddrManageAdapter mAdapter;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ma_addr_manage;
    }

    @Override
    public void initView() {
        setToolbar("管理收货地址", View.VISIBLE, null, View.GONE);
        mList = new ArrayList<>();
        mGson = new Gson();
        mMemberId = String.valueOf(Settings.getLoginSessionInfo(this).getMember_id());
        mToken = Settings.getLoginSessionInfo(this).getToken();
        initRecyclerView();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void initData() {
        getDataWithAddressList();
    }

    @OnClick(R.id.btn_shipping_address)
    public void clickAddAddress() {
        jumpToActivity(MaAddrAddActivity.class);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AddrAddEvent event) {
        // 地址添加成功返回刷新列表
        getDataWithAddressList();
    }

    private void initRecyclerView() {
        // 设置布局
        rvAddress.setLinearLayout();
        // 绑定适配器
        mAdapter = new AddrManageAdapter(this, mList, mMemberId, mToken);
        rvAddress.setAdapter(mAdapter);

        // 不需要上拉刷新
        rvAddress.setPushRefreshEnable(false);
        rvAddress.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                getDataWithAddressList();
            }

            @Override
            public void onLoadMore() {

            }
        });
        // 刷新结束
        rvAddress.setPullLoadMoreCompleted();
    }

    private void getDataWithAddressList() {
        OkGo.<String>post(Urls.ADDRESS_LIST)
                .tag(this)
                .params("member_id", mMemberId)
                .params("token", mToken)
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 String body = response.body();
                                 Log.e(AppConstant.TAG, "address_list request succeeded--->" + body);

                                 AddrListBean bean = mGson.fromJson(body, AddrListBean.class);
                                 switch (bean.getCode()) {
                                     case 1:
                                         mList = bean.getData();
                                         // 刷新数据
                                         mAdapter.refreshList(mList);
                                         // 刷新结束
                                         rvAddress.setPullLoadMoreCompleted();
                                         break;
                                     default:
                                         break;
                                 }
                             }

                             @Override
                             public void onError(Response<String> response) {
                                 super.onError(response);
                             }
                         }
                );
    }

}
