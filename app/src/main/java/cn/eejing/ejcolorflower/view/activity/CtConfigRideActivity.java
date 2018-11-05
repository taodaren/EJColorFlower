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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.litepal.LitePal;

import java.util.List;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.app.BaseApplication;
import cn.eejing.ejcolorflower.model.lite.JetModeConfigLite;
import cn.eejing.ejcolorflower.model.manager.MgrOutputJet;
import cn.eejing.ejcolorflower.util.DecimalInputTextWatcher;
import cn.eejing.ejcolorflower.util.ToastUtil;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.BORDER_TO_CENTER;
import static cn.eejing.ejcolorflower.app.AppConstant.CENTER_TO_BORDER;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_RIDE;
import static cn.eejing.ejcolorflower.app.AppConstant.DEFAULT_HIGH;
import static cn.eejing.ejcolorflower.app.AppConstant.LEFT_TO_RIGHT;
import static cn.eejing.ejcolorflower.app.AppConstant.RIGHT_TO_LEFT;

/**
 * 跑马灯配置
 */

public class CtConfigRideActivity extends BaseActivity implements View.OnClickListener {

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
    @BindView(R.id.et_stream_frequency)          EditText       etFrequency;
    @BindView(R.id.tv_stream_jet_time)           TextView       tvJetTime;
    @BindView(R.id.ll_jet_time_stream)           LinearLayout   llJetTime;

    private BaseApplication mApp;
    private long mJetIdMillis;
    private int mDevNum;
    // 喷射效果及配置集合
    private List<JetModeConfigLite> mListJetModeCfg;
    // 用于保存当前被选中的按钮
    private String strBtnDirection;
    // 监听 EditText 文本
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
                    || etFrequency.getText().toString().trim().isEmpty()) {
                // EditText 有空情况
                btnVerify.setEnabled(Boolean.FALSE);
                btnVerify.setBackground(getDrawable(R.drawable.ic_btn_no_click));
                llJetTime.setVisibility(View.INVISIBLE);
            } else {
                // EditText 同时不为空的情况
                btnVerify.setEnabled(Boolean.TRUE);
                btnVerify.setBackground(getDrawable(R.drawable.ic_btn_full));
                llJetTime.setVisibility(View.VISIBLE);
                tvJetTime.setText(countTime());
            }
        }
    };

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ct_config_strem_ride;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void initView() {
        setToolbar(CONFIG_RIDE, View.VISIBLE, null, View.GONE);
        mApp = (BaseApplication) getApplication();
        mDevNum = getIntent().getIntExtra("device_num", 0);
        mJetIdMillis = getIntent().getLongExtra("jet_id_millis", 0);
        initConfig();

        tvJetTime.setText(countTime());
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void initConfig() {
        mListJetModeCfg = LitePal.where("jetIdMillis = ?", String.valueOf(mJetIdMillis)).find(JetModeConfigLite.class);
        etGap.setText(mListJetModeCfg.get(0).getGap());
        etDuration.setText(mListJetModeCfg.get(0).getDuration());
        etGapBig.setText(mListJetModeCfg.get(0).getBigGap());
        // 展示给用户看需要 +1
        etFrequency.setText(String.valueOf(Integer.parseInt(mListJetModeCfg.get(0).getJetRound()) + 1));

        switch (mListJetModeCfg.get(0).getDirection()) {
            case LEFT_TO_RIGHT:
                strBtnDirection = LEFT_TO_RIGHT;
                btnChangeState(
                        R.drawable.shape_btn_on_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click,
                        R.color.colorWhite, R.color.colorNoClick, R.color.colorNoClick, R.color.colorNoClick
                );
                break;
            case BORDER_TO_CENTER:
                strBtnDirection = BORDER_TO_CENTER;
                btnChangeState(
                        R.drawable.shape_btn_no_click, R.drawable.shape_btn_on_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click,
                        R.color.colorNoClick, R.color.colorWhite, R.color.colorNoClick, R.color.colorNoClick
                );
                break;
            case RIGHT_TO_LEFT:
                strBtnDirection = RIGHT_TO_LEFT;
                btnChangeState(
                        R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_on_click, R.drawable.shape_btn_no_click,
                        R.color.colorNoClick, R.color.colorNoClick, R.color.colorWhite, R.color.colorNoClick
                );
                break;
            case CENTER_TO_BORDER:
                strBtnDirection = CENTER_TO_BORDER;
                btnChangeState(
                        R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_on_click,
                        R.color.colorNoClick, R.color.colorNoClick, R.color.colorNoClick, R.color.colorWhite
                );
                break;
            default:
                strBtnDirection = LEFT_TO_RIGHT;
                btnChangeState(
                        R.drawable.shape_btn_on_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click,
                        R.color.colorWhite, R.color.colorNoClick, R.color.colorNoClick, R.color.colorNoClick
                );
                break;
        }
    }

    @Override
    public void initListener() {
        btnVerify.setOnClickListener(this);
        btnDemo.setOnClickListener(this);

        BtnSelected lrBtnListener, bcBtnListener, rlBtnListener, cbBtnListener;

        lrBtnListener = new BtnSelected(LEFT_TO_RIGHT);
        bcBtnListener = new BtnSelected(BORDER_TO_CENTER);
        rlBtnListener = new BtnSelected(RIGHT_TO_LEFT);
        cbBtnListener = new BtnSelected(CENTER_TO_BORDER);

        rbtnLeftToRight.setOnClickListener(lrBtnListener);
        rbtnBorderToCenter.setOnClickListener(bcBtnListener);
        rbtnRightToLeft.setOnClickListener(rlBtnListener);
        rbtnCenterToBorder.setOnClickListener(cbBtnListener);

        // 不限制整数位数，限制小数位数为 1 位
        etGap.addTextChangedListener(new DecimalInputTextWatcher(etGap, DecimalInputTextWatcher.Type.decimal, 1));
        etDuration.addTextChangedListener(new DecimalInputTextWatcher(etDuration, DecimalInputTextWatcher.Type.decimal, 1));
        etGapBig.addTextChangedListener(new DecimalInputTextWatcher(etGapBig, DecimalInputTextWatcher.Type.decimal, 1));

        etGap.addTextChangedListener(textWatcher);
        etDuration.addTextChangedListener(textWatcher);
        etGapBig.addTextChangedListener(textWatcher);
        etFrequency.addTextChangedListener(textWatcher);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_config_verify:
                if (Integer.parseInt(etFrequency.getText().toString()) == 0) {
                    ToastUtil.showShort("喷射次数不能为 0，请重新设置！");
                } else {
                    updateLiteData();
                    finish();
                }
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

    private void updateLiteData() {
        int loop = Integer.parseInt(etFrequency.getText().toString());
        mListJetModeCfg.get(0).setJetType(CONFIG_RIDE);
        mListJetModeCfg.get(0).setDirection(strBtnDirection);
        mListJetModeCfg.get(0).setGap(etGap.getText().toString());
        mListJetModeCfg.get(0).setDuration(etDuration.getText().toString());
        mListJetModeCfg.get(0).setBigGap(etGapBig.getText().toString());
        // 用户输入 1 代表喷射一轮不循环
        mListJetModeCfg.get(0).setJetRound(String.valueOf(loop - 1));
        mListJetModeCfg.get(0).setHigh(DEFAULT_HIGH);
        mListJetModeCfg.get(0).updateAll("jetIdMillis=?", String.valueOf(mJetIdMillis));
    }

    private String countTime() {
        float totalTime;
        totalTime = MgrOutputJet.calCountAloneTime(mDevNum, CONFIG_RIDE, strBtnDirection,
                etGap.getText().toString(),
                etDuration.getText().toString(),
                etGapBig.getText().toString(),
                String.valueOf(Integer.parseInt(etFrequency.getText().toString()) - 1
                ));
        return String.valueOf(totalTime);
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
                            R.color.colorWhite, R.color.colorNoClick, R.color.colorNoClick, R.color.colorNoClick
                    );
                    break;
                case R.id.rbtn_border_to_center:
                    btnChangeState(
                            R.drawable.shape_btn_no_click, R.drawable.shape_btn_on_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click,
                            R.color.colorNoClick, R.color.colorWhite, R.color.colorNoClick, R.color.colorNoClick
                    );
                    break;
                case R.id.rbtn_right_to_left:
                    btnChangeState(
                            R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_on_click, R.drawable.shape_btn_no_click,
                            R.color.colorNoClick, R.color.colorNoClick, R.color.colorWhite, R.color.colorNoClick
                    );
                    break;
                case R.id.rbtn_center_to_border:
                    btnChangeState(
                            R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_no_click, R.drawable.shape_btn_on_click,
                            R.color.colorNoClick, R.color.colorNoClick, R.color.colorNoClick, R.color.colorWhite
                    );
                    break;
                default:
                    break;
            }
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void btnChangeState(@DrawableRes int lr, @DrawableRes int bc, @DrawableRes int rl, @DrawableRes int cb,
                                @ColorRes int lrColor, @ColorRes int bcColor, @ColorRes int rlColor, @ColorRes int cbColor) {
        rbtnLeftToRight.setBackground(getDrawable(lr));
        rbtnBorderToCenter.setBackground(getDrawable(bc));
        rbtnRightToLeft.setBackground(getDrawable(rl));
        rbtnCenterToBorder.setBackground(getDrawable(cb));

        rbtnLeftToRight.setTextColor(getResources().getColor(lrColor));
        rbtnBorderToCenter.setTextColor(getResources().getColor(bcColor));
        rbtnRightToLeft.setTextColor(getResources().getColor(rlColor));
        rbtnCenterToBorder.setTextColor(getResources().getColor(cbColor));

        tvJetTime.setText(countTime());
    }

}
