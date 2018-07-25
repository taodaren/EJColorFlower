package cn.eejing.ejcolorflower.view.activity;

import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.List;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.model.event.JetStatusEvent;
import cn.eejing.ejcolorflower.model.lite.CtrlStreamEntity;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.BORDER_TO_CENTER;
import static cn.eejing.ejcolorflower.app.AppConstant.CENTER_TO_BORDER;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_STREAM;
import static cn.eejing.ejcolorflower.app.AppConstant.DEFAULT_TOGETHER_HIGH;
import static cn.eejing.ejcolorflower.app.AppConstant.LEFT_TO_RIGHT;
import static cn.eejing.ejcolorflower.app.AppConstant.RIGHT_TO_LEFT;

/**
 * 流水灯
 */

public class CoConfigStreamActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.rbtn_left_to_right)           RadioButton    rbtnLeftToRight;
    @BindView(R.id.rbtn_border_to_center)        RadioButton    rbtnBorderToCenter;
    @BindView(R.id.radio_left)                   RadioGroup     radioLeft;
    @BindView(R.id.rbtn_right_to_left)           RadioButton    rbtnRightToLeft;
    @BindView(R.id.rbtn_center_to_border)        RadioButton    rbtnCenterToBorder;
    @BindView(R.id.radio_right)                  RadioGroup     radioRight;
    @BindView(R.id.btn_config_verify)            Button         btnVerify;
    @BindView(R.id.et_stream_gap)                EditText       etGap;
    @BindView(R.id.et_stream_duration)           EditText       etDuration;
    @BindView(R.id.et_stream_gap_big)            EditText       etGapBig;
    @BindView(R.id.et_stream_loop)               EditText       etLoop;

    // 用于保存当前被选中的按钮
    private String strBtnDirection;
    private BtnSelected mLrBtnListener, mBcBtnListener, mRlBtnListener, mCbBtnListener;
    private boolean mLrChecked, mBcChecked, mRlChecked, mCbChecked;

    private int mGroupId;
    private String mConfigType;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_co_config_strem_ride;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void initView() {
        setToolbar(CONFIG_STREAM, View.VISIBLE);

        mGroupId = getIntent().getIntExtra("group_id", 0);
        mConfigType = getIntent().getStringExtra("config_type");

        mLrBtnListener = new BtnSelected(LEFT_TO_RIGHT);
        mBcBtnListener = new BtnSelected(BORDER_TO_CENTER);
        mRlBtnListener = new BtnSelected(RIGHT_TO_LEFT);
        mCbBtnListener = new BtnSelected(CENTER_TO_BORDER);

        initConfigDB();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initConfigDB() {
        List<CtrlStreamEntity> entities = LitePal.findAll(CtrlStreamEntity.class);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void defaultConfig() {
        etGap.setText(AppConstant.DEFAULT_STREAM_RIDE_GAP);
        etDuration.setText(AppConstant.DEFAULT_STREAM_RIDE_DURATION);
        etGapBig.setText(AppConstant.DEFAULT_STREAM_RIDE_GAP_BIG);
        etLoop.setText(AppConstant.DEFAULT_STREAM_RIDE_LOOP);
        strBtnDirection = LEFT_TO_RIGHT;
        btnChangeState(
                R.drawable.shape_btn_on_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click,
                R.color.colorWhite, R.color.colorNoClick, R.color.colorNoClick, R.color.colorNoClick,
                true, false, false, false
        );
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void updateConfig(List<CtrlStreamEntity> entities, int i) {
        switch (entities.get(i).getDirection()) {
            case LEFT_TO_RIGHT:
                strBtnDirection = LEFT_TO_RIGHT;
                btnChangeState(
                        R.drawable.shape_btn_on_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click,
                        R.color.colorWhite, R.color.colorNoClick, R.color.colorNoClick, R.color.colorNoClick,
                        true, false, false, false
                );
                break;
            case BORDER_TO_CENTER:
                strBtnDirection = BORDER_TO_CENTER;
                btnChangeState(
                        R.drawable.shape_btn_no_click, R.drawable.shape_btn_on_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click,
                        R.color.colorNoClick, R.color.colorWhite, R.color.colorNoClick, R.color.colorNoClick,
                        false, true, false, false
                );
                break;
            case RIGHT_TO_LEFT:
                strBtnDirection = RIGHT_TO_LEFT;
                btnChangeState(
                        R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_on_click, R.drawable.shape_btn_no_click,
                        R.color.colorNoClick, R.color.colorNoClick, R.color.colorWhite, R.color.colorNoClick,
                        false, false, true, false
                );
                break;
            case CENTER_TO_BORDER:
                strBtnDirection = CENTER_TO_BORDER;
                btnChangeState(
                        R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_on_click,
                        R.color.colorNoClick, R.color.colorNoClick, R.color.colorNoClick, R.color.colorWhite,
                        false, false, false, true
                );
                break;
            default:
                break;
        }

        etGap.setText(entities.get(i).getGap());
        etDuration.setText(entities.get(i).getDuration());
        etGapBig.setText(entities.get(i).getGapBig());
        etLoop.setText(entities.get(i).getLoop());
    }

    @Override
    public void initListener() {
        btnVerify.setOnClickListener(this);

        rbtnLeftToRight.setOnClickListener(mLrBtnListener);
        rbtnBorderToCenter.setOnClickListener(mBcBtnListener);
        rbtnRightToLeft.setOnClickListener(mRlBtnListener);
        rbtnCenterToBorder.setOnClickListener(mCbBtnListener);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_config_verify:
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
        List<CtrlStreamEntity> groupIdList = LitePal
                .where("groupId=?", String.valueOf(mGroupId))
                .find(CtrlStreamEntity.class);

        CtrlStreamEntity entity = new CtrlStreamEntity();
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

    private void setEntity(CtrlStreamEntity entity, long millis) {
        entity.setConfigType(CONFIG_STREAM);
        entity.setGroupId(mGroupId);
        entity.setDirection(strBtnDirection);
        entity.setGap(etGap.getText().toString());
        entity.setDuration(etDuration.getText().toString());
        entity.setGapBig(etGapBig.getText().toString());
        entity.setLoop(etLoop.getText().toString());
        entity.setHigh(DEFAULT_TOGETHER_HIGH);
        entity.setMillis(millis);
    }

    private void postEvent(long millis) {
        try {
            int direction = 0, gap, duration, bigGit, loop, high;
            if (strBtnDirection == null) {
                if (mLrChecked) {
                    direction = Integer.parseInt(LEFT_TO_RIGHT);
                }
                if (mRlChecked) {
                    direction = Integer.parseInt(RIGHT_TO_LEFT);
                }
                if (mBcChecked) {
                    direction = Integer.parseInt(BORDER_TO_CENTER);
                }
                if (mCbChecked) {
                    direction = Integer.parseInt(CENTER_TO_BORDER);
                }
            } else {
                direction = Integer.parseInt(strBtnDirection);
            }
            gap = Integer.parseInt(etGap.getText().toString());
            duration = Integer.parseInt(etDuration.getText().toString());
            bigGit = Integer.parseInt(etGapBig.getText().toString());
            loop = Integer.parseInt(etLoop.getText().toString());
            high = Integer.parseInt(DEFAULT_TOGETHER_HIGH);

            JetStatusEvent event = new JetStatusEvent(getString(R.string.config_stream),
                    direction, gap, duration, bigGit, loop, mGroupId, high, millis);
            EventBus.getDefault().post(event);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
    }

    /**
     * 监听类，每个 RadioButton 均对 Click 动作进行监听：
     * 若用户点击的是 GroupLeft 的按钮，则清除 GroupRight 中按钮被选中的状态
     * 若用户点击的是 GroupRight 的按钮，则清除 GroupLeft 中按钮被选中的状态
     */
    public class BtnSelected implements View.OnClickListener {
        final String bntID;

        BtnSelected(String str) {
            bntID = str;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @Override
        public void onClick(View arg0) {
            strBtnDirection = bntID;

            if (bntID.equals(LEFT_TO_RIGHT) || bntID.equals(BORDER_TO_CENTER)) {
                radioRight.clearCheck();
            } else if (bntID.equals(RIGHT_TO_LEFT) || bntID.equals(CENTER_TO_BORDER)) {
                radioLeft.clearCheck();
            }

            switch (arg0.getId()) {
                case R.id.rbtn_left_to_right:
                    btnChangeState(
                            R.drawable.shape_btn_on_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click,
                            R.color.colorWhite, R.color.colorNoClick, R.color.colorNoClick, R.color.colorNoClick,
                            true, false, false, false
                    );
                    break;
                case R.id.rbtn_border_to_center:
                    btnChangeState(
                            R.drawable.shape_btn_no_click, R.drawable.shape_btn_on_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click,
                            R.color.colorNoClick, R.color.colorWhite, R.color.colorNoClick, R.color.colorNoClick,
                            false, true, false, false
                    );
                    break;
                case R.id.rbtn_right_to_left:
                    btnChangeState(
                            R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_on_click, R.drawable.shape_btn_no_click,
                            R.color.colorNoClick, R.color.colorNoClick, R.color.colorWhite, R.color.colorNoClick,
                            false, false, true, false
                    );
                    break;
                case R.id.rbtn_center_to_border:
                    btnChangeState(
                            R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_on_click,
                            R.color.colorNoClick, R.color.colorNoClick, R.color.colorNoClick, R.color.colorWhite,
                            false, false, false, true
                    );
                    break;
                default:
                    break;
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void btnChangeState(@DrawableRes int lr, @DrawableRes int bc, @DrawableRes int rl, @DrawableRes int cb,
                                @ColorRes int lrColor, @ColorRes int bcColor, @ColorRes int rlColor, @ColorRes int cbColor,
                                boolean lrCheck,boolean bcCheck,boolean rlCheck,boolean cbCheck) {
        rbtnLeftToRight.setBackground(getDrawable(lr));
        rbtnBorderToCenter.setBackground(getDrawable(bc));
        rbtnRightToLeft.setBackground(getDrawable(rl));
        rbtnCenterToBorder.setBackground(getDrawable(cb));

        rbtnLeftToRight.setTextColor(getColor(lrColor));
        rbtnBorderToCenter.setTextColor(getColor(bcColor));
        rbtnRightToLeft.setTextColor(getColor(rlColor));
        rbtnCenterToBorder.setTextColor(getColor(cbColor));

        mLrChecked = lrCheck;
        mBcChecked = bcCheck;
        mRlChecked = rlCheck;
        mCbChecked = cbCheck;
    }

}
