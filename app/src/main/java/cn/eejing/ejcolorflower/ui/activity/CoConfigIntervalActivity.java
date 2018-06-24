package cn.eejing.ejcolorflower.ui.activity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.model.event.JetStatusEvent;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;

/**
 * 间隔高低
 */

public class CoConfigIntervalActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_verify_interval)
    Button btnVerify;
    @BindView(R.id.et_interval_gap)
    EditText etGap;
    @BindView(R.id.et_interval_duration)
    EditText etDuration;
    @BindView(R.id.et_interval_frequency)
    EditText etFrequency;

    private int mGroupId;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_co_config_interval;
    }

    @Override
    public void initView() {
        setToolbar(getString(R.string.config_interval), View.VISIBLE);
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
            case R.id.btn_verify_interval:
                postEvent();
                finish();
                break;
            default:
                break;
        }
    }

    private void postEvent() {
        JetStatusEvent event = new JetStatusEvent(getString(R.string.config_interval),
                etGap.getText().toString(),
                etDuration.getText().toString(),
                etFrequency.getText().toString(),
                mGroupId
        );
        EventBus.getDefault().post(event);
    }

    private void defaultConfig() {
        etGap.setText(AppConstant.DEFAULT_INTERVAL_GAP);
        etDuration.setText(AppConstant.DEFAULT_INTERVAL_DURATION);
        etFrequency.setText(AppConstant.DEFAULT_INTERVAL_FREQUENCY);
    }

}
