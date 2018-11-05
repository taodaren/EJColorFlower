package cn.eejing.ejcolorflower.view.activity;

import android.view.View;
import android.widget.Button;

import com.allen.library.SuperTextView;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.BaseApplication;
import cn.eejing.ejcolorflower.util.SelfDialogBase;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.FROM_SET_TO_ADDR;

/**
 * 设置
 */

public class MiSetActivity extends BaseActivity {

    @BindView(R.id.stv_set_user_info)             SuperTextView stvUserInfo;
    @BindView(R.id.stv_set_modify_pwd)            SuperTextView stvModifyPwd;
    @BindView(R.id.stv_set_manage_address)        SuperTextView stvManageAddress;
    @BindView(R.id.btn_exit_login)                Button        btnExitLogin;

    private SelfDialogBase mDialog;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_mi_set;
    }

    @Override
    public void initView() {
        setToolbar("设置", View.VISIBLE, null, View.GONE);
    }

    @Override
    public void initListener() {
        stvUserInfo.setOnSuperTextViewClickListener(superTextView -> jumpToActivity(MiUserInfoActivity.class));
        stvModifyPwd.setOnSuperTextViewClickListener(superTextView -> jumpToActivity(MiPwdModifyActivity.class));
        stvManageAddress.setOnSuperTextViewClickListener(superTextView -> {
            BaseApplication baseApplication = (BaseApplication) getApplication();
            baseApplication.setFlagAddrMgr(FROM_SET_TO_ADDR);
            jumpToActivity(MaAddrMgrActivity.class);
        });
        btnExitLogin.setOnClickListener(v -> signOut());
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
