package cn.eejing.ejcolorflower.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.allen.library.SuperTextView;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;
import cn.eejing.ejcolorflower.util.Settings;

import static cn.eejing.ejcolorflower.app.AppConstant.EXIT_LOGIN;

/**
 * @创建者 Taodaren
 * @描述 我的 → 设置
 */
public class MiSetActivity extends BaseActivity {

    @BindView(R.id.stv_set_user_info)
    SuperTextView stvSetUserInfo;
    @BindView(R.id.stv_set_modify_pwd)
    SuperTextView stvSetModifyPwd;
    @BindView(R.id.btn_exit_login)
    Button btnExitLogin;
    @BindView(R.id.img_title_back)
    ImageView imgTitleBack;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_mi_set;
    }

    @Override
    public void initView() {
        setToolbar("设置", View.VISIBLE);
        stvOnClickListener();
    }

    private void stvOnClickListener() {
        stvSetUserInfo.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                jumpToActivity(MiUserInfoActivity.class);
            }
        });
        stvSetModifyPwd.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                jumpToActivity(MiModifyPwdActivity.class);
            }
        });
        btnExitLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 退出登陆
                logout();
            }
        });
        imgTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void logout() {
        // 清空缓存
        Settings.clearInfo(getBaseContext());
        // 退出登陆回到登陆界面
        startActivity(new Intent(MiSetActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
        // 结束 MainActivity
        delActivity(EXIT_LOGIN);
    }

}
