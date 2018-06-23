package cn.eejing.ejcolorflower.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

import com.allen.library.SuperTextView;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;
import cn.eejing.ejcolorflower.util.SelfDialogBase;
import cn.eejing.ejcolorflower.util.Settings;

import static cn.eejing.ejcolorflower.app.AppConstant.EXIT_LOGIN;

/**
 * 设置
 */
public class MiSetActivity extends BaseActivity {

    @BindView(R.id.stv_set_user_info)
    SuperTextView stvSetUserInfo;
    @BindView(R.id.stv_set_modify_pwd)
    SuperTextView stvSetModifyPwd;
    @BindView(R.id.btn_exit_login)
    Button btnExitLogin;

    private SelfDialogBase mDialog;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_mi_set;
    }

    @Override
    public void initView() {
        setToolbar("设置", View.VISIBLE);
    }

    @Override
    public void initListener() {
        stvSetUserInfo.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                jumpToActivity(MiUserInfoActivity.class);
            }
        });
        stvSetModifyPwd.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                jumpToActivity(MiPwdModifyActivity.class);
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
                logout();
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

    private void logout() {
        // 清空缓存
        Settings.clearInfo(getBaseContext());
        // 退出登陆回到登陆界面
        startActivity(new Intent(MiSetActivity.this, SignInActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
        // 结束 AppActivity
        delActivity(EXIT_LOGIN);
    }

}
