package cn.eejing.ejcolorflower.view.activity;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

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
import cn.eejing.ejcolorflower.util.MySettings;
import cn.eejing.ejcolorflower.view.adapter.AddrSelectAdapter;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

/**
 * 选择收货地址
 */

public class MaAddrSelectActivity extends BaseActivity {

    @BindView(R.id.rv_shipping_address)    PullLoadMoreRecyclerView rvAddress;
    @BindView(R.id.ll_shipping_address)    LinearLayout nullAddress;

    private Gson mGson;
    private String mMemberId, mToken;
    private List<AddrListBean.DataBean> mList;
    private AddrSelectAdapter mAdapter;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ma_addr_select;
    }

    @Override
    public void initView() {
        EventBus.getDefault().register(this);
        setToolbar("选择收货地址", View.VISIBLE, "管理", View.VISIBLE);

        mList = new ArrayList<>();
        mGson = new Gson();
        mMemberId = String.valueOf(MySettings.getLoginSessionInfo(this).getMember_id());
        mToken = MySettings.getLoginSessionInfo(this).getToken();

        initRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getDataWithAddressList();
    }

    @OnClick(R.id.tv_menu_toolbar)
    public void onClickedMgr() {
        jumpToActivity(MaAddrManageActivity.class);
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
        mAdapter = new AddrSelectAdapter(this, mList);
        rvAddress.setAdapter(mAdapter);

        // 不需要上拉刷新
        rvAddress.setPushRefreshEnable(false);
        rvAddress.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                getDataWithAddressList();
                // 刷新结束
                rvAddress.setPullLoadMoreCompleted();
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
                                         nullAddress.setVisibility(View.GONE);
                                         rvAddress.setVisibility(View.VISIBLE);
                                         mList = bean.getData();
                                         // 刷新数据
                                         mAdapter.refreshList(mList);
                                         // 刷新结束
                                         rvAddress.setPullLoadMoreCompleted();
                                         break;
                                     case 0:
                                         // 该会员暂无地址
                                         nullAddress.setVisibility(View.VISIBLE);
                                         rvAddress.setVisibility(View.GONE);
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
