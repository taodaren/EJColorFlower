package cn.eejing.ejcolorflower.view.activity;

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
import cn.eejing.ejcolorflower.model.lite.CtrlIntervalEntity;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_INTERVAL;
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
    private String mConfigType;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_co_config_interval;
    }

    @Override
    public void initView() {
        setToolbar(CONFIG_INTERVAL, View.VISIBLE);
        mGroupId = getIntent().getIntExtra("group_id", 0);
        mConfigType = getIntent().getStringExtra("config_type");

        initConfigDB();
    }

    private void initConfigDB() {
        List<CtrlIntervalEntity> entities = LitePal.findAll(CtrlIntervalEntity.class);
        if (entities.size() == 0) {
            // 默认配置
            defaultConfig();
        } else {
            // 更新配置
            for (int i = 0; i < entities.size(); i++) {
                if (mGroupId == entities.get(i).getGroupId() && mConfigType.equals(entities.get(i).getConfigType())) {
                    updateConfig(entities, i);
                }
            }
        }
    }

    private void defaultConfig() {
        etGap.setText(AppConstant.DEFAULT_INTERVAL_GAP);
        etDuration.setText(AppConstant.DEFAULT_INTERVAL_DURATION);
        etFrequency.setText(AppConstant.DEFAULT_INTERVAL_FREQUENCY);
    }

    private void updateConfig(List<CtrlIntervalEntity> entities, int i) {
        etGap.setText(entities.get(i).getGap());
        etDuration.setText(entities.get(i).getDuration());
        etFrequency.setText(entities.get(i).getFrequency());
    }

    @Override
    public void initListener() {
        btnVerify.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_verify_interval:
                long millis = System.currentTimeMillis();
                setSQLiteData(millis);
                postEvent(millis);
                finish();
                break;
            default:
                break;
        }
    }

    private void setSQLiteData(long millis) {
        List<CtrlIntervalEntity> groupIdList = LitePal
                .where("groupId=?", String.valueOf(mGroupId))
                .find(CtrlIntervalEntity.class);

        CtrlIntervalEntity entity = new CtrlIntervalEntity();
        if (groupIdList.size() == 0) {
            // 增
            setEntity(entity, millis);
            entity.save();
        } else {
            // 改
            setEntity(entity, millis);
            entity.updateAll("groupId=?", String.valueOf(mGroupId));
        }
    }

    private void setEntity(CtrlIntervalEntity entity, long millis) {
        entity.setConfigType(CONFIG_INTERVAL);
        entity.setGroupId(mGroupId);
        entity.setGap(etGap.getText().toString());
        entity.setDuration(etDuration.getText().toString());
        entity.setFrequency(etFrequency.getText().toString());
        entity.setHigh(DEFAULT_TOGETHER_HIGH);
        entity.setMillis(millis);
    }

    private void postEvent(long millis) {
        try {
            int gap = Integer.parseInt(etGap.getText().toString());
            int duration = Integer.parseInt(etDuration.getText().toString());
            int frequency = Integer.parseInt(etFrequency.getText().toString());
            int high = Integer.parseInt(DEFAULT_TOGETHER_HIGH);

            JetStatusEvent event = new JetStatusEvent(getString(R.string.config_interval),
                    gap, duration, frequency, mGroupId, high, millis);
            EventBus.getDefault().post(event);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

}
