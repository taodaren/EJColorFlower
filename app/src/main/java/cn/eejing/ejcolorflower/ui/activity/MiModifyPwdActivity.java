package cn.eejing.ejcolorflower.ui.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;

/**
 * @创建者 Taodaren
 * @描述 我的 → 设置 → 修改密码
 */
public class MiModifyPwdActivity extends BaseActivity {

    @BindView(R.id.img_title_back)
    ImageView imgTitleBack;
    @BindView(R.id.et_current_psd)
    EditText etCurrentPsd;
    @BindView(R.id.et_reset_psd)
    EditText etResetPsd;
    @BindView(R.id.et_confirm_psd)
    EditText etConfirmPsd;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_mi_modify_pwd;
    }

    @Override
    public void initView() {
        setToolbar("修改密码", View.VISIBLE);
        imgTitleBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
