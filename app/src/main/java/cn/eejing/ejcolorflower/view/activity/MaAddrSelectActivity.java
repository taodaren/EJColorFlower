package cn.eejing.ejcolorflower.view.activity;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.model.event.AddrAddEvent;
import cn.eejing.ejcolorflower.model.request.AddrListBean;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.util.Settings;
import cn.eejing.ejcolorflower.view.adapter.AddrSelectAdapter;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

/**
 * 选择收货地址
 */

public class MaAddrSelectActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_title_shipping_address)
    TextView tvTitle;
    @BindView(R.id.img_back_shipping_address)
    ImageView imgBack;
    @BindView(R.id.tv_manage_shipping_address)
    TextView tvManage;
    @BindView(R.id.rv_shipping_address)
    PullLoadMoreRecyclerView rvAddress;

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
        tvTitle.setText("选择收货地址");
        mList = new ArrayList<>();
        mGson = new Gson();
        mMemberId = String.valueOf(Settings.getLoginSessionInfo(this).getMember_id());
        mToken = Settings.getLoginSessionInfo(this).getToken();
        initRecyclerView();
        EventBus.getDefault().register(this);
    }

    @Override
    public void initData() {
        getDataWithAddressList();
    }

    @Override
    public void initListener() {
        imgBack.setOnClickListener(this);
        tvManage.setOnClickListener(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(AddrAddEvent event) {
        // 地址添加成功返回刷新列表
        getDataWithAddressList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back_shipping_address:
                finish();
                break;
            case R.id.tv_manage_shipping_address:
                jumpToActivity(MaAddrManageActivity.class);
                break;
            default:
                break;
        }
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
