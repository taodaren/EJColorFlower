package cn.eejing.ejcolorflower.view.activity;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.List;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.model.event.JetStatusEvent;
import cn.eejing.ejcolorflower.model.lite.CtrlTypeEntity;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_INTERVAL;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_STREAM;
import static cn.eejing.ejcolorflower.app.AppConstant.DEFAULT_TOGETHER_HIGH;

/**
 * 间隔高低
 */

public class CoConfigIntervalActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_verify_interval)          Button btnVerify;
    @BindView(R.id.et_interval_gap)              EditText etGap;
    @BindView(R.id.et_interval_duration)         EditText etDuration;
    @BindView(R.id.et_interval_frequency)        EditText etFrequency;

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
                setSQLiteData();
                postEvent();
                finish();
                break;
            default:
                break;
        }
    }

    private void setSQLiteData() {
        List<CtrlTypeEntity> groupIdList = LitePal
                .where("groupId=?", String.valueOf(mGroupId))
                .find(CtrlTypeEntity.class);

        CtrlTypeEntity entity = new CtrlTypeEntity();
        if (groupIdList.size() == 0) {
            // 增
            setEntity(entity);
            entity.save();
        } else {
            // 改
            setEntity(entity);
            entity.updateAll("groupId=?", String.valueOf(mGroupId));
        }
    }

    private void setEntity(CtrlTypeEntity entity) {
        entity.setConfigType(CONFIG_INTERVAL);
        entity.setGroupId(mGroupId);
        entity.setGap(etGap.getText().toString());
        entity.setDuration(etDuration.getText().toString());
        entity.setFrequency(etFrequency.getText().toString());
        entity.setHigh(DEFAULT_TOGETHER_HIGH);
    }

    private void postEvent() {
        JetStatusEvent event = new JetStatusEvent(getString(R.string.config_interval),
                etGap.getText().toString(),
                etDuration.getText().toString(),
                etFrequency.getText().toString(),
                mGroupId,
                DEFAULT_TOGETHER_HIGH
        );
        EventBus.getDefault().post(event);
    }

    private void defaultConfig() {
        etGap.setText(AppConstant.DEFAULT_INTERVAL_GAP);
        etDuration.setText(AppConstant.DEFAULT_INTERVAL_DURATION);
        etFrequency.setText(AppConstant.DEFAULT_INTERVAL_FREQUENCY);
    }

}
