package cn.eejing.ejcolorflower.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;

/**
 * 齐喷
 */

public class ConfigTogetherActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_verify_together)
    Button btnVerify;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_config_together;
    }

    @Override
    public void initView() {
        setToolbar(getString(R.string.config_together), View.VISIBLE);
    }

    @Override
    public void initListener() {
        btnVerify.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_verify_together:
                finish();
                break;
            default:
                break;
        }
    }

}
