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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.model.request.AddressListBean;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.util.Settings;
import cn.eejing.ejcolorflower.view.adapter.AddressSelectAdapter;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

/**
 * 选择收货地址
 */

public class MaAddressSelectActivity extends BaseActivity implements View.OnClickListener {

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
    private List<AddressListBean.DataBean> mList;
    private AddressSelectAdapter mAdapter;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ma_address_select;
    }

    @Override
    public void initView() {
        tvTitle.setText("选择收货地址");
        mList = new ArrayList<>();
        mGson = new Gson();
        mMemberId = String.valueOf(Settings.getLoginSessionInfo(this).getMember_id());
        mToken = Settings.getLoginSessionInfo(this).getToken();
        initRecyclerView();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_back_shipping_address:
                finish();
                break;
            case R.id.tv_manage_shipping_address:
                jumpToActivity(MaAddressManageActivity.class);
                break;
            default:
                break;
        }
    }

    private void initRecyclerView() {
        // 设置布局
        rvAddress.setLinearLayout();
        // 绑定适配器
        mAdapter = new AddressSelectAdapter(this, mList);
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

                                 AddressListBean bean = mGson.fromJson(body, AddressListBean.class);
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
