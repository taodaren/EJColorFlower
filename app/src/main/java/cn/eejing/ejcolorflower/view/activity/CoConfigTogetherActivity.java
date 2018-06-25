package cn.eejing.ejcolorflower.view.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.model.event.JetStatusEvent;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

/**
 * 齐喷
 */

public class CoConfigTogetherActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_verify_together)
    Button btnVerify;
    @BindView(R.id.et_together_duration)
    EditText etDuration;
    @BindView(R.id.et_together_high)
    EditText etHigh;

    private int mGroupId;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_co_config_together;
    }

    @Override
    public void initView() {
        setToolbar(getString(R.string.config_together), View.VISIBLE);
        mGroupId = getIntent().getIntExtra("group_id", 0);
        defaultConfig();
    }

    @Override
    public void initListener() {
        btnVerify.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_verify_together:
                postEvent();
                finish();
                break;
            default:
                break;
        }
    }

    private void postEvent() {
        JetStatusEvent event = new JetStatusEvent(getString(R.string.config_together),
                etDuration.getText().toString(),
                etHigh.getText().toString(),
                mGroupId);
        EventBus.getDefault().post(event);
    }

    private void defaultConfig() {
        etDuration.setText(AppConstant.DEFAULT_TOGETHER_DURATION);
        etHigh.setText(AppConstant.DEFAULT_TOGETHER_HIGH);
    }

}
