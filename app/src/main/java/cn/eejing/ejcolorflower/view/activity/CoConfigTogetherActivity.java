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
import cn.eejing.ejcolorflower.model.lite.CtrlTogetherEntity;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_TOGETHER;

/**
 * 齐喷
 */

public class CoConfigTogetherActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_verify_together)         Button btnVerify;
    @BindView(R.id.et_together_duration)        EditText etDuration;
    @BindView(R.id.et_together_high)            EditText etHigh;

    private int mGroupId;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_co_config_together;
    }

    @Override
    public void initView() {
        setToolbar(CONFIG_TOGETHER, View.VISIBLE);
        mGroupId = getIntent().getIntExtra("group_id", 0);

        initConfigDB();
    }

    private void initConfigDB() {
        List<CtrlTogetherEntity> entities = LitePal.where("groupId = ?", String.valueOf(mGroupId)).find(CtrlTogetherEntity.class);

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
        etDuration.setText(AppConstant.DEFAULT_TOGETHER_DURATION);
        etHigh.setText(AppConstant.DEFAULT_TOGETHER_HIGH);
    }

    private void updateConfig(CtrlTogetherEntity togetherEntity) {
        etDuration.setText(togetherEntity.getDuration());
        etHigh.setText(togetherEntity.getHigh());
    }

    @Override
    public void initListener() {
        btnVerify.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_verify_together:
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
        List<CtrlTogetherEntity> groupIdList = LitePal
                .where("groupId=?", String.valueOf(mGroupId))
                .find(CtrlTogetherEntity.class);

        CtrlTogetherEntity entity = new CtrlTogetherEntity();
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

    private void setEntity(CtrlTogetherEntity entity, long millis) {
        entity.setConfigType(CONFIG_TOGETHER);
        entity.setGroupId(mGroupId);
        entity.setDuration(etDuration.getText().toString());
        entity.setHigh(etHigh.getText().toString());
        entity.setMillis(millis);
    }

    private void postEvent(long millis) {
        try {
            int duration = Integer.parseInt(etDuration.getText().toString());
            int high = Integer.parseInt(etHigh.getText().toString());

            JetStatusEvent event = new JetStatusEvent(getString(R.string.config_together),
                    duration, high, mGroupId, millis);
            EventBus.getDefault().post(event);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

}
