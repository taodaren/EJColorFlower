package cn.eejing.ejcolorflower.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;

/**
 * 间隔高低
 */

public class ConfigIntervalActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.img_title_back)
    ImageView imgTitleBack;
    @BindView(R.id.btn_verify_interval)
    Button btnVerify;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_config_interval;
    }

    @Override
    public void initView() {
        setToolbar(getString(R.string.config_interval), View.VISIBLE);
    }

    @Override
    public void initListener() {
        imgTitleBack.setOnClickListener(this);
        btnVerify.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_title_back:
                finish();
                break;
            case R.id.btn_verify_interval:
                finish();
                break;
            default:
                break;
        }
    }

}
