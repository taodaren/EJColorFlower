package cn.eejing.colorflower.view.activity;

import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import cn.eejing.colorflower.model.request.CodeMsgBean;
import cn.eejing.colorflower.presenter.Callback;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.SelfDialogBase;
import cn.eejing.colorflower.view.adapter.AddrManageAdapter;
import cn.eejing.colorflower.view.base.BaseActivity;

import static cn.eejing.colorflower.app.AppConstant.FROM_ORDER_TO_ADDR;
import static cn.eejing.colorflower.app.AppConstant.FROM_SELECT_TO_ADDR;
import static cn.eejing.colorflower.app.AppConstant.FROM_SET_TO_ADDR;

/**
 * 管理收货地址
 */

public class MaAddrMgrActivity extends BaseActivity {
    private static final String TAG = "MaAddrMgrActivity";

    @BindView(R.id.btn_shipping_address)        Button btnAddAddress;
    @BindView(R.id.rv_shipping_address)         PullLoadMoreRecyclerView rvAddress;
    @BindView(R.id.ll_shipping_address)         LinearLayout nullAddress;

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
        initRecyclerView();
        EventBus.getDefault().register(this);
    }

    @Override
    public void setToolbar(String title, int titleVisibility, String menu, int menuVisibility) {
        super.setToolbar(title, titleVisibility, menu, menuVisibility);
        // 设置返回按钮
        ImageView imgTitleBack = findViewById(R.id.img_back_toolbar);
        imgTitleBack.setVisibility(View.VISIBLE);
        imgTitleBack.setOnClickListener(v -> onExit());
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onExit();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void onExit() {
        BaseApplication baseApplication = (BaseApplication) getApplication();
        switch (baseApplication.getFlagAddrMgr()) {
            case FROM_SET_TO_ADDR:
                jumpToActivity(MiSetActivity.class);
                finish();
                break;
            case FROM_SELECT_TO_ADDR:
                if (mList.size() == 0) {
                    EventBus.getDefault().post(new AddrAddEvent("收货地址为空"));
                    jumpToActivity(MaOrderConfirmActivity.class);
                    finish();
                } else {
                    EventBus.getDefault().post(new AddrAddEvent("收货地址不为空"));
                    jumpToActivity(MaAddrSelectActivity.class);
                    finish();
                }
                break;
            case FROM_ORDER_TO_ADDR:
                jumpToActivity(MaOrderConfirmActivity.class);
                if (mList.size() == 0) {
                    EventBus.getDefault().post(new AddrAddEvent("收货地址为空"));
                    finish();
                } else {
                    EventBus.getDefault().post(new AddrAddEvent("收货地址不为空"));
                    finish();
                }
                break;
        }
        baseApplication.setFlagAddrMgr(null);
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
        mAdapter = new AddrManageAdapter(this, mList);
        // 监听删除点击事件
        mAdapter.setOnClickListener(v -> {
            int position = (int) v.getTag();
            showDialogByDel(position);
        });
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

    @SuppressWarnings("unchecked")
    private void getDataWithAddressList() {
        OkGoBuilder.getInstance().setToken(MainActivity.getAppCtrl().getToken());

        OkGoBuilder.getInstance().Builder(this)
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
                                // 刷新结束
                                rvAddress.setPullLoadMoreCompleted();
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e, int id) {
                    }
                }).build();
    }

    @SuppressWarnings("unchecked")
    private void getDataWithAddressDel(final int position) {
        OkGoBuilder.getInstance().setToken(MainActivity.getAppCtrl().getToken());
        HttpParams params = new HttpParams();
        params.put("address_id", mList.get(position).getId());

        OkGoBuilder.getInstance().Builder(this)
                .url(Urls.DEL_ADDRESS)
                .method(OkGoBuilder.POST)
                .params(params)
                .cls(CodeMsgBean.class)
                .callback(new Callback<CodeMsgBean>() {
                    @Override
                    public void onSuccess(CodeMsgBean bean, int id) {
                        LogUtil.d(TAG, "删除收货地址 请求成功");

                        switch (bean.getCode()) {
                            case 1:
                                mList.remove(position);
                                mAdapter.notifyDataSetChanged();
                                getDataWithAddressList();
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e, int id) {
                    }
                }).build();
    }

    private SelfDialogBase mDialogDel;

    private void showDialogByDel(int position) {
        mDialogDel = new SelfDialogBase(this);
        mDialogDel.setTitle("是否确认删除收货地址");
        mDialogDel.setYesOnclickListener("确定", () -> {
            getDataWithAddressDel(position);
            mDialogDel.dismiss();
        });
        mDialogDel.setNoOnclickListener("取消", () -> mDialogDel.dismiss());
        mDialogDel.show();
    }

}
