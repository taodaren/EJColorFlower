package cn.eejing.ejcolorflower.view.activity;

import android.view.View;
import android.widget.Button;

import com.allen.library.SuperTextView;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.util.SelfDialogBase;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

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
        stvUserInfo.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                jumpToActivity(MiUserInfoActivity.class);
            }
        });
        stvModifyPwd.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                jumpToActivity(MiPwdModifyActivity.class);
            }
        });
        stvManageAddress.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                jumpToActivity(MaAddrManageActivity.class);
            }
        });
        btnExitLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    private void signOut() {
        mDialog = new SelfDialogBase(this);
        mDialog.setTitle("您确认要退出登陆？");
        mDialog.setYesOnclickListener("确认", new SelfDialogBase.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                logout(MiSetActivity.this);
                mDialog.dismiss();
            }
        });
        mDialog.setNoOnclickListener("取消", new SelfDialogBase.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

}
