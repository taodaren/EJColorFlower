package cn.eejing.colorflower.view.activity;

import android.view.View;
import android.widget.LinearLayout;

import com.lzy.okgo.model.HttpParams;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.app.BaseApplication;
import cn.eejing.colorflower.model.event.AddrAddEvent;
import cn.eejing.colorflower.model.http.OkGoBuilder;
import cn.eejing.colorflower.model.request.AddrListBean;
import cn.eejing.colorflower.presenter.Callback;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.view.adapter.AddrSelectAdapter;
import cn.eejing.colorflower.view.base.BaseActivity;

import static cn.eejing.colorflower.app.AppConstant.FROM_SELECT_TO_ADDR;

/**
 * 选择收货地址
 */

public class MaAddrSelectActivity extends BaseActivity {
    private static final String TAG = "MaAddrSelectActivity";

    @BindView(R.id.rv_shipping_address)    PullLoadMoreRecyclerView rvAddress;
    @BindView(R.id.ll_shipping_address)    LinearLayout nullAddress;

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
        initRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getDataWithAddressList();
    }

    @OnClick(R.id.tv_menu_toolbar)
    public void onClickedMgr() {
        BaseApplication baseApplication = (BaseApplication) getApplication();
        baseApplication.setFlagAddrMgr(FROM_SELECT_TO_ADDR);
        jumpToActivity(MaAddrMgrActivity.class);
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

    @SuppressWarnings("unchecked")
    private void getDataWithAddressList() {
        OkGoBuilder<AddrListBean> builder = new OkGoBuilder<>();
        builder.setToken(MainActivity.getAppCtrl().getToken());

        builder.Builder(this)
                .url(Urls.ADDRESS_LIST)
                .method(OkGoBuilder.POST)
                .params(new HttpParams())
                .cls(AddrListBean.class)
                .callback(new Callback<AddrListBean>() {
                    @Override
                    public void onSuccess(AddrListBean bean, int id) {
                        LogUtil.d(TAG, "收货地址列表 请求成功");

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
                    public void onError(Throwable e, int id) {
                    }
                }).build();
    }

}
