package cn.eejing.ejcolorflower.view.activity;

import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.RequiresApi;
import android.text.Editable;
import android.text.TextWatcher;
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
import cn.eejing.ejcolorflower.app.GApp;
import cn.eejing.ejcolorflower.model.event.JetStatusEvent;
import cn.eejing.ejcolorflower.model.lite.CtrlRideEntity;
import cn.eejing.ejcolorflower.util.DecimalInputTextWatcher;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.BORDER_TO_CENTER;
import static cn.eejing.ejcolorflower.app.AppConstant.CENTER_TO_BORDER;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_RIDE;
import static cn.eejing.ejcolorflower.app.AppConstant.DEFAULT_TOGETHER_HIGH;
import static cn.eejing.ejcolorflower.app.AppConstant.LEFT_TO_RIGHT;
import static cn.eejing.ejcolorflower.app.AppConstant.RIGHT_TO_LEFT;

/**
 * 跑马灯配置
 */

public class CoConfigRideActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.rbtn_left_to_right)           RadioButton    rbtnLeftToRight;
    @BindView(R.id.rbtn_border_to_center)        RadioButton    rbtnBorderToCenter;
    @BindView(R.id.radio_left)                   RadioGroup     radioLeft;
    @BindView(R.id.rbtn_right_to_left)           RadioButton    rbtnRightToLeft;
    @BindView(R.id.rbtn_center_to_border)        RadioButton    rbtnCenterToBorder;
    @BindView(R.id.radio_right)                  RadioGroup     radioRight;
    @BindView(R.id.btn_config_verify)            Button         btnVerify;
    @BindView(R.id.btn_demo_stream_ride)         Button         btnDemo;
    @BindView(R.id.et_stream_gap)                EditText       etGap;
    @BindView(R.id.et_stream_duration)           EditText       etDuration;
    @BindView(R.id.et_stream_gap_big)            EditText       etGapBig;
    @BindView(R.id.et_stream_loop)               EditText       etLoop;

    // 用于保存当前被选中的按钮
    private String strBtnDirection;
    private BtnSelected mLrBtnListener, mBcBtnListener, mRlBtnListener, mCbBtnListener;
    private boolean mLrChecked, mBcChecked, mRlChecked, mCbChecked;

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
                    || etGapBig.getText().toString().trim().isEmpty()
                    || etLoop.getText().toString().trim().isEmpty()) {
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
        return R.layout.activity_co_config_strem_ride;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void initView() {
        setToolbar(CONFIG_RIDE, View.VISIBLE, null, View.GONE);
        mApp = (GApp) getApplication();
        mGroupId = getIntent().getIntExtra("group_id", 0);

        mLrBtnListener = new BtnSelected(LEFT_TO_RIGHT);
        mBcBtnListener = new BtnSelected(BORDER_TO_CENTER);
        mRlBtnListener = new BtnSelected(RIGHT_TO_LEFT);
        mCbBtnListener = new BtnSelected(CENTER_TO_BORDER);

        initConfigDB();

        // 不限制整数位数，限制小数位数为 1 位
        etGap.addTextChangedListener(new DecimalInputTextWatcher(etGap, DecimalInputTextWatcher.Type.decimal, 1));
        etDuration.addTextChangedListener(new DecimalInputTextWatcher(etDuration, DecimalInputTextWatcher.Type.decimal, 1));
        etGapBig.addTextChangedListener(new DecimalInputTextWatcher(etGapBig, DecimalInputTextWatcher.Type.decimal, 1));

        etGap.addTextChangedListener(textWatcher);
        etDuration.addTextChangedListener(textWatcher);
        etGapBig.addTextChangedListener(textWatcher);
        etLoop.addTextChangedListener(textWatcher);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initConfigDB() {
        List<CtrlRideEntity> entities = LitePal.where("groupId = ?", String.valueOf(mGroupId)).find(CtrlRideEntity.class);

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
    private void updateConfig(CtrlRideEntity rideEntity) {
        switch (rideEntity.getDirection()) {
            case LEFT_TO_RIGHT:
                btnChangeState(
                        R.drawable.shape_btn_on_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click,
                        R.color.colorWhite, R.color.colorNoClick, R.color.colorNoClick, R.color.colorNoClick,
                        true, false, false, false
                );
                break;
            case BORDER_TO_CENTER:
                btnChangeState(
                        R.drawable.shape_btn_no_click, R.drawable.shape_btn_on_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click,
                        R.color.colorNoClick, R.color.colorWhite, R.color.colorNoClick, R.color.colorNoClick,
                        false, true, false, false
                );
                break;
            case RIGHT_TO_LEFT:
                btnChangeState(
                        R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_on_click, R.drawable.shape_btn_no_click,
                        R.color.colorNoClick, R.color.colorNoClick, R.color.colorWhite, R.color.colorNoClick,
                        false, false, true, false
                );
                break;
            case CENTER_TO_BORDER:
                btnChangeState(
                        R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_on_click,
                        R.color.colorNoClick, R.color.colorNoClick, R.color.colorNoClick, R.color.colorWhite,
                        false, false, false, true
                );
                break;
            default:
                strBtnDirection = LEFT_TO_RIGHT;
                btnChangeState(
                        R.drawable.shape_btn_on_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click,
                        R.color.colorWhite, R.color.colorNoClick, R.color.colorNoClick, R.color.colorNoClick,
                        true, false, false, false
                );
                break;
        }

        etGap.setText(rideEntity.getGap());
        etDuration.setText(rideEntity.getDuration());
        etGapBig.setText(rideEntity.getGapBig());
        etLoop.setText(rideEntity.getLoop());
    }

    @Override
    public void initListener() {
        btnVerify.setOnClickListener(this);
        btnDemo.setOnClickListener(this);

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
            case R.id.btn_demo_stream_ride:
                mApp.setFlagGifDemo(AppConstant.CONFIG_RIDE);
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
        List<CtrlRideEntity> groupIdList = LitePal
                .where("groupId=?", String.valueOf(mGroupId))
                .find(CtrlRideEntity.class);

        CtrlRideEntity entity = new CtrlRideEntity();
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

    private void setEntity(CtrlRideEntity entity, long millis) {
        entity.setConfigType(CONFIG_RIDE);
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

            JetStatusEvent event = new JetStatusEvent(getString(R.string.config_ride),
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
                                boolean lrCheck, boolean bcCheck, boolean rlCheck, boolean cbCheck) {
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
