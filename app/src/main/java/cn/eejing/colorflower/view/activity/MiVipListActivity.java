package cn.eejing.colorflower.view.activity;

import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.lzy.okgo.model.HttpParams;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.http.OkGoBuilder;
import cn.eejing.colorflower.model.request.CodeMsgBean;
import cn.eejing.colorflower.model.request.VipListBean;
import cn.eejing.colorflower.presenter.Callback;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.SelfDialog;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.adapter.VipListAdapter;
import cn.eejing.colorflower.view.base.BaseActivity;

/**
 * Vvip 下游 Vip 列表
 */

public class MiVipListActivity extends BaseActivity {
    @BindView(R.id.rv_vip_list)        PullLoadMoreRecyclerView rvVip;
    @BindView(R.id.tv_down_vip)        TextView tvDownVip;

    private static final String TAG = "MiVipListActivity";
    private List<VipListBean.DataBean> mList;
    private VipListAdapter mAdapter;
    private SelfDialog mDialogRemark, mDialogDiscount;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_mi_vip_list;
    }

    @Override
    public void initData() {
        getDataWithVipList();
    }

    @Override
    public void initView() {
        super.initView();
        setToolbar("VIP列表", View.VISIBLE, null, View.GONE);
        mList = new ArrayList<>();
        initRecyclerView();
    }

    private void initRecyclerView() {
        rvVip.setLinearLayout();
        mAdapter = new VipListAdapter(this, mList);
        mAdapter.setClickRemark(v -> {
            int position = (int) v.getTag();
            showDialogByRemark(mList.get(position));
        });
        mAdapter.setClickDiscount(v -> {
            int position = (int) v.getTag();
            showDialogByDiscount(mList.get(position));
        });
        rvVip.setAdapter(mAdapter);
        rvVip.setPushRefreshEnable(false);
        rvVip.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                getDataWithVipList();
            }

            @Override
            public void onLoadMore() {
            }
        });
        rvVip.setPullLoadMoreCompleted();
    }

    /** 设置备注 */
    private void showDialogByRemark(VipListBean.DataBean bean) {
        mDialogRemark = new SelfDialog(this);
        mDialogRemark.setTitle("备注详情");
        mDialogRemark.setMessage("VIP手机号：" + bean.getMobile());
        if (TextUtils.isEmpty(bean.getRemark())) {
            mDialogRemark.setHint("备注未设置");
        } else {
            mDialogRemark.setHint(bean.getRemark());
        }
        mDialogRemark.setYesOnclickListener("确定", () -> {
            if (mDialogRemark.getEditTextStr().length() > 10) {
                ToastUtil.showShort("备注不能超过10个字符");
            }
            getDataWithSetRemark(bean.getMobile(), mDialogRemark.getEditTextStr());
            mDialogRemark.dismiss();
        });
        mDialogRemark.setNoOnclickListener("取消", () -> mDialogRemark.dismiss());
        mDialogRemark.show();
    }

    /** 设置折扣 */
    private void showDialogByDiscount(VipListBean.DataBean bean) {
        mDialogDiscount = new SelfDialog(this);
        mDialogDiscount.setTitle("修改折扣");
        mDialogDiscount.setMessage("折扣输入为0~10(不含0)，如输入8.5即打85折，为原价的85%，但不得低于公司规定最低折扣价。");
        if ("1.00".equals(bean.getDiscount())) {
            mDialogDiscount.setHint("无折扣");
        } else {
            char chShiWei = bean.getDiscount().charAt(2);
            char chGeWei = bean.getDiscount().charAt(3);
            if (chGeWei == '0') {
                mDialogDiscount.setHint("当前折扣：" + chShiWei + "折");
            } else {
                mDialogDiscount.setHint("当前折扣：" + chShiWei + chGeWei + "折");
            }
        }
        mDialogDiscount.setEtInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
        mDialogDiscount.setYesOnclickListener("确定", () -> {
            if (TextUtils.isEmpty(mDialogDiscount.getEditTextStr())) {
                ToastUtil.showShort("对不起，您尚未输入折扣信息");
            }
            getDataWithSetDiscount(bean.getMobile(), Float.parseFloat(mDialogDiscount.getEditTextStr()) / 10f);
            mDialogDiscount.dismiss();
        });
        mDialogDiscount.setNoOnclickListener("取消", () -> mDialogDiscount.dismiss());
        mDialogDiscount.show();
    }

    @SuppressWarnings("unchecked")
    private void getDataWithVipList() {
        OkGoBuilder.getInstance().setToken(MainActivity.getAppCtrl().getToken());

        OkGoBuilder.getInstance().Builder(this)
                .url(Urls.SHOW_UNDERLING_VIP)
                .method(OkGoBuilder.POST)
                .params(new HttpParams())
                .cls(VipListBean.class)
                .callback(new Callback<VipListBean>() {
                    @Override
                    public void onSuccess(VipListBean bean, int id) {
                        LogUtil.d(TAG, "获取下游 vip 列表 请求成功");

                        switch (bean.getCode()) {
                            case 1:
                                rvVip.setVisibility(View.VISIBLE);
                                tvDownVip.setVisibility(View.GONE);
                                mList = bean.getData();
                                // 刷新数据
                                mAdapter.refreshList(mList);
                                // 刷新结束
                                rvVip.setPullLoadMoreCompleted();
                                break;
                            case 0:
                                rvVip.setVisibility(View.GONE);
                                tvDownVip.setVisibility(View.VISIBLE);
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

    @SuppressWarnings("unchecked")
    private void getDataWithSetRemark(String mobile, String remark) {
        OkGoBuilder.getInstance().setToken(MainActivity.getAppCtrl().getToken());
        HttpParams params = new HttpParams();
        params.put("mobile", mobile);
        params.put("remark", remark);

        OkGoBuilder.getInstance().Builder(this)
                .url(Urls.SET_VIP_RERMARK)
                .method(OkGoBuilder.POST)
                .params(params)
                .cls(CodeMsgBean.class)
                .callback(new Callback<CodeMsgBean>() {
                    @Override
                    public void onSuccess(CodeMsgBean bean, int id) {
                        LogUtil.d(TAG, "VVIP 设置 vip 备注 请求成功");

                        switch (bean.getCode()) {
                            case 1:
                                getDataWithVipList();
                                ToastUtil.showShort(bean.getMessage());
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

    @SuppressWarnings("unchecked")
    private void getDataWithSetDiscount(String mobile, float discount) {
        OkGoBuilder.getInstance().setToken(MainActivity.getAppCtrl().getToken());
        HttpParams params = new HttpParams();
        params.put("mobile", mobile);
        params.put("discount", discount);

        OkGoBuilder.getInstance().Builder(this)
                .url(Urls.SET_VIP_DISCOUNT)
                .method(OkGoBuilder.POST)
                .params(params)
                .cls(CodeMsgBean.class)
                .callback(new Callback<CodeMsgBean>() {
                    @Override
                    public void onSuccess(CodeMsgBean bean, int id) {
                        LogUtil.d(TAG, "VVIP 设置 vip 价格折扣 请求成功");

                        switch (bean.getCode()) {
                            case 1:
                                getDataWithVipList();
                                ToastUtil.showShort(bean.getMessage());
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
