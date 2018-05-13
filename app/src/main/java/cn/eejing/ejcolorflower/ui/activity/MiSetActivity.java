package cn.eejing.ejcolorflower.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.allen.library.SuperTextView;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;

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
                Toast.makeText(MiSetActivity.this, "btnExitLogin", Toast.LENGTH_SHORT).show();
            }
        });
        imgTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
