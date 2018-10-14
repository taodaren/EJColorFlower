package cn.eejing.ejcolorflower.view.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.List;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.app.GApp;
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
    @BindView(R.id.btn_demo_interval)            Button btnDemo;
    @BindView(R.id.et_interval_gap)              EditText etGap;
    @BindView(R.id.et_interval_duration)         EditText etDuration;
    @BindView(R.id.et_interval_frequency)        EditText etFrequency;

    private GApp mApp;
    private int mGroupId;

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if (etGap.getText().toString().trim().isEmpty()
                    || etDuration.getText().toString().trim().isEmpty()
                    || etFrequency.getText().toString().trim().isEmpty()) {
                // EditText 有空情况
                btnVerify.setEnabled(Boolean.FALSE);
                btnVerify.setBackground(getDrawable(R.drawable.ic_btn_no_click));
            } else {
                // EditText 同时不为空的情况
                btnVerify.setEnabled(Boolean.TRUE);
                btnVerify.setBackground(getDrawable(R.drawable.ic_btn_full));
            }
        }
    };


    @Override
    protected int layoutViewId() {
        return R.layout.activity_co_config_interval;
    }

    @Override
    public void initView() {
        setToolbar(CONFIG_INTERVAL, View.VISIBLE, null, View.GONE);
        mApp = (GApp) getApplication();
        mGroupId = getIntent().getIntExtra("group_id", 0);

        initConfigDB();

//        // 不限制整数位数，限制小数位数为 1 位
//        etGap.addTextChangedListener(new DecimalInputTextWatcher(etGap, DecimalInputTextWatcher.Type.decimal, 1));
//        etDuration.addTextChangedListener(new DecimalInputTextWatcher(etDuration, DecimalInputTextWatcher.Type.decimal, 1));
        etGap.addTextChangedListener(textWatcher);
        etDuration.addTextChangedListener(textWatcher);
        etFrequency.addTextChangedListener(textWatcher);
    }

    private void initConfigDB() {
        List<CtrlIntervalEntity> entities = LitePal.where("groupId = ?", String.valueOf(mGroupId)).find(CtrlIntervalEntity.class);

        switch (entities.size()) {
            case 0:
                // 默认配置
                defaultConfig();
                break;
            default:
                // 更新配置
                updateConfig(entities.get(0));
                break;
        }
    }

    private void defaultConfig() {
        etGap.setText(AppConstant.DEFAULT_INTERVAL_GAP);
        etDuration.setText(AppConstant.DEFAULT_INTERVAL_DURATION);
        etFrequency.setText(AppConstant.DEFAULT_INTERVAL_FREQUENCY);
    }

    private void updateConfig(CtrlIntervalEntity intervalEntity) {
        etGap.setText(intervalEntity.getGap());
        etDuration.setText(intervalEntity.getDuration());
        etFrequency.setText(intervalEntity.getFrequency());
    }

    @Override
    public void initListener() {
        btnVerify.setOnClickListener(this);
        btnDemo.setOnClickListener(this);
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
            case R.id.btn_demo_interval:
                mApp.setFlagGifDemo(AppConstant.CONFIG_INTERVAL);
                jumpToActivity(CtDemoDescriptionActivity.class);
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mApp.setFlagGifDemo(null);
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
