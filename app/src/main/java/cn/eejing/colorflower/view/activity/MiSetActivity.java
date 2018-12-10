package cn.eejing.colorflower.view.activity;

import android.view.View;

import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.app.BaseApplication;
import cn.eejing.colorflower.view.customize.SelfDialogBase;
import cn.eejing.colorflower.view.base.BaseActivity;

import static cn.eejing.colorflower.app.AppConstant.FROM_SET_TO_ADDR;

/**
 * 设置
 */

public class MiSetActivity extends BaseActivity {
    private SelfDialogBase mDialog;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_mi_set;
    }

    @Override
    public void initView() {
        setToolbar("设置", View.VISIBLE, null, View.GONE);
    }

    @OnClick({R.id.stv_set_user_info, R.id.stv_set_pay_pwd, R.id.stv_set_manage_address, R.id.btn_exit_login})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.stv_set_user_info:
                jumpToActivity(MiUserInfoActivity.class);
                break;
            case R.id.stv_set_pay_pwd:
                jumpToActivity(MiSetPayPwdActivity.class);
                break;
            case R.id.stv_set_manage_address:
                BaseApplication baseApplication = (BaseApplication) getApplication();
                baseApplication.setFlagAddrMgr(FROM_SET_TO_ADDR);
                jumpToActivity(MaAddrMgrActivity.class);
                break;
            case R.id.btn_exit_login:
                signOut();
                break;
        }
    }

    private void signOut() {
        mDialog = new SelfDialogBase(this);
        mDialog.setTitle("您确认要退出登陆？");
        mDialog.setYesOnclickListener("确认", () -> {
            logout(MiSetActivity.this);
            mDialog.dismiss();
        });
        mDialog.setNoOnclickListener("取消", () -> mDialog.dismiss());
        mDialog.show();
    }
}
