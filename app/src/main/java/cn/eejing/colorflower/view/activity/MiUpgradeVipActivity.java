package cn.eejing.colorflower.view.activity;

import android.view.View;
import android.widget.ImageView;

import com.lzy.okgo.model.HttpParams;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.http.OkGoBuilder;
import cn.eejing.colorflower.model.request.CodeMsgBean;
import cn.eejing.colorflower.model.request.UpgradeVipBean;
import cn.eejing.colorflower.presenter.Callback;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.ClearableEditText;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.SelfDialogBase;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.base.BaseActivity;

import static cn.eejing.colorflower.app.AppConstant.LEVEL_VIP_USER;

/**
 * 升级为VIP
 */

public class MiUpgradeVipActivity extends BaseActivity {
    @BindView(R.id.et_vvip_phone)           ClearableEditText etPhone;
    @BindView(R.id.img_back_upgrade_vip)    ImageView imgBack;

    private static final String TAG = "MiUpgradeVipActivity";
    private SelfDialogBase mDialog;
    private String mPhone;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_mi_upgrade_vip;
    }

    @Override
    public void initData() {
        getDataWithUpgradeVIP();
    }

    @OnClick({R.id.img_back_upgrade_vip, R.id.btn_upgrade_vip})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back_upgrade_vip:
                finish();
                break;
            case R.id.btn_upgrade_vip:
                mPhone = etPhone.getText().toString().trim();
                if (mPhone.length() == 0) {
                    showDialog();
                } else {
                    getDataWithToBeVip();
                }
                break;
        }
    }

    public void showDialog() {
        mDialog = new SelfDialogBase(this);
        mDialog.setTitle("您未输入VVIP推荐人手机号\n\t\t\t\t\t\t\t是否直接升级？");
        mDialog.setYesOnclickListener("确定", () -> {
            getDataWithToBeVip();
            mDialog.dismiss();
        });
        mDialog.setNoOnclickListener("取消", () -> mDialog.dismiss());
        mDialog.show();
    }

    @SuppressWarnings("unchecked")
    private void getDataWithUpgradeVIP() {
        OkGoBuilder.getInstance().setToken(MainActivity.getAppCtrl().getToken());

        OkGoBuilder.getInstance().Builder(this)
                .url(Urls.TO_UPGRADE_VIP)
                .method(OkGoBuilder.POST)
                .params(new HttpParams())
                .cls(UpgradeVipBean.class)
                .callback(new Callback<UpgradeVipBean>() {
                    @Override
                    public void onSuccess(UpgradeVipBean bean, int id) {
                        LogUtil.d(TAG, "进入升级 VIP 页面 请求成功");

                        switch (bean.getCode()) {
                            case 1:
                                // TODO: 展示升级条件和VIP权益，目前写死了
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
    private void getDataWithToBeVip() {
        OkGoBuilder.getInstance().setToken(MainActivity.getAppCtrl().getToken());
        HttpParams params = new HttpParams();
        params.put("mobile", mPhone);

        OkGoBuilder.getInstance().Builder(this)
                .url(Urls.TO_BE_VIP)
                .method(OkGoBuilder.POST)
                .params(params)
                .cls(CodeMsgBean.class)
                .callback(new Callback<CodeMsgBean>() {
                    @Override
                    public void onSuccess(CodeMsgBean bean, int id) {
                        LogUtil.d(TAG, "升级为 VIP 请求成功 请求成功");

                        switch (bean.getCode()) {
                            case 1:
                                MainActivity.getAppCtrl().setLevel(LEVEL_VIP_USER);
                                finish();
                                ToastUtil.showLong(bean.getMessage());
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
