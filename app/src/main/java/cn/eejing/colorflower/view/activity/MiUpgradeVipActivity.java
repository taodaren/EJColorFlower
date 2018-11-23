package cn.eejing.colorflower.view.activity;

import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.request.CodeMsgBean;
import cn.eejing.colorflower.model.request.UpgradeVipBean;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.ClearableEditText;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.SelfDialogBase;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.base.BaseActivity;

import static cn.eejing.colorflower.app.AppConstant.LEVEL_GENERAL_USER;
import static cn.eejing.colorflower.app.AppConstant.LEVEL_VIP_USER;
import static cn.eejing.colorflower.app.BaseApplication.saveUserLv;

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

    private void getDataWithUpgradeVIP() {
        OkGo.<String>post(Urls.TO_UPGRADE_VIP)
                .tag(this)
                .params("token", MainActivity.getAppCtrl().getToken())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        LogUtil.d(TAG, "进入升级 VIP 页面 请求成功: " + body);

                        Gson gson = new Gson();
                        UpgradeVipBean bean = gson.fromJson(body, UpgradeVipBean.class);
                        switch (bean.getCode()) {
                            case 1:
                                // TODO: 展示升级条件和VIP权益，目前写死了
                                break;
                            default:
                                ToastUtil.showShort(bean.getMessage());
                                break;
                        }
                    }
                });
    }

    private void getDataWithToBeVip() {
        OkGo.<String>post(Urls.TO_BE_VIP)
                .tag(this)
                .params("mobile", mPhone)
                .params("token", MainActivity.getAppCtrl().getToken())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        LogUtil.d(TAG, "升级为 VIP 请求成功: " + body);

                        Gson gson = new Gson();
                        CodeMsgBean bean = gson.fromJson(body, CodeMsgBean.class);
                        switch (bean.getCode()) {
                            case 1:
                                finish();
                                saveUserLv(mPhone, LEVEL_VIP_USER);
                                ToastUtil.showLong(bean.getMessage());
                                break;
                            default:
                                ToastUtil.showShort(bean.getMessage());
                                break;
                        }
                    }
                });
    }
}
