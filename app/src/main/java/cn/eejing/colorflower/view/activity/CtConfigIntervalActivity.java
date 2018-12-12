package cn.eejing.colorflower.view.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.litepal.LitePal;

import java.util.List;

import butterknife.BindView;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.app.AppConstant;
import cn.eejing.colorflower.app.BaseApplication;
import cn.eejing.colorflower.model.lite.JetModeConfigLite;
import cn.eejing.colorflower.model.manager.MgrOutputJet;
import cn.eejing.colorflower.util.ClearableEditText;
import cn.eejing.colorflower.util.DecimalInputTextWatcher;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.base.BaseActivity;

import static cn.eejing.colorflower.app.AppConstant.CONFIG_INTERVAL;

/**
 * 间隔高低配置
 */

public class CtConfigIntervalActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_verify_interval)          Button            btnVerify;
    @BindView(R.id.btn_demo_interval)            Button            btnDemo;
    @BindView(R.id.et_interval_gap)              ClearableEditText etGap;
    @BindView(R.id.et_interval_duration)         ClearableEditText etDuration;
    @BindView(R.id.et_interval_frequency)        ClearableEditText etFrequency;
    @BindView(R.id.et_interval_high_max)         ClearableEditText etHighMax;
    @BindView(R.id.et_interval_high_min)         ClearableEditText etHighMin;
    @BindView(R.id.tv_interval_jet_time)         TextView          tvJetTime;
    @BindView(R.id.ll_jet_time_interval)         LinearLayout      llJetTime;

    private BaseApplication mApp;
    private long mJetIdMillis;
    // 喷射效果及配置集合
    private List<JetModeConfigLite> mListJetModeCfg;
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
                    || etFrequency.getText().toString().trim().isEmpty()
                    || etHighMax.getText().toString().trim().isEmpty()
                    || etHighMin.getText().toString().trim().isEmpty()) {
                // EditText 有空情况
                btnVerify.setEnabled(Boolean.FALSE);
                btnVerify.setBackground(getDrawable(R.drawable.shape_btn_clickable_not));
                llJetTime.setVisibility(View.INVISIBLE);
            } else {
                // EditText 同时不为空的情况
                btnVerify.setEnabled(Boolean.TRUE);
                btnVerify.setBackground(getDrawable(R.drawable.shape_btn_clickable));
                llJetTime.setVisibility(View.VISIBLE);
                tvJetTime.setText(countTime());
            }
        }
    };

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ct_config_interval;
    }

    @Override
    public void initView() {
        setToolbar(CONFIG_INTERVAL, View.VISIBLE, null, View.GONE);
        mApp = (BaseApplication) getApplication();
        mJetIdMillis = getIntent().getLongExtra("jet_id_millis", 0);
        initConfig();

        tvJetTime.setText(countTime());
    }

    private void initConfig() {
        mListJetModeCfg = LitePal.where("jetIdMillis = ?", String.valueOf(mJetIdMillis)).find(JetModeConfigLite.class);
        etGap.setText(mListJetModeCfg.get(0).getGap());
        etDuration.setText(mListJetModeCfg.get(0).getDuration());
        etHighMax.setText(mListJetModeCfg.get(0).getHighMax());
        etHighMin.setText(mListJetModeCfg.get(0).getHighMin());
        // 展示给用户看需要 +1
        etFrequency.setText(String.valueOf(Integer.parseInt(mListJetModeCfg.get(0).getJetRound()) + 1));
    }

    @Override
    public void initListener() {
        btnVerify.setOnClickListener(this);
        btnDemo.setOnClickListener(this);

        // 不限制整数位数，限制小数位数为 1 位
        etGap.addTextChangedListener(new DecimalInputTextWatcher(etGap, DecimalInputTextWatcher.Type.decimal, 1));
        etDuration.addTextChangedListener(new DecimalInputTextWatcher(etDuration, DecimalInputTextWatcher.Type.decimal, 1));
        etGap.addTextChangedListener(textWatcher);
        etDuration.addTextChangedListener(textWatcher);
        etFrequency.addTextChangedListener(textWatcher);
        etHighMax.addTextChangedListener(textWatcher);
        etHighMin.addTextChangedListener(textWatcher);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_verify_interval:
                if (Integer.parseInt(etFrequency.getText().toString()) == 0) {
                    ToastUtil.showShort("喷射次数不能为 0，请重新设置！");
                } else {
                    updateLiteData();
                    finish();
                }
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

    private void updateLiteData() {
        int frequency = Integer.parseInt(etFrequency.getText().toString());
        mListJetModeCfg.get(0).setJetType(CONFIG_INTERVAL);
        mListJetModeCfg.get(0).setGap(etGap.getText().toString());
        mListJetModeCfg.get(0).setDuration(etDuration.getText().toString());
        // 用户输入 1 代表喷射一轮不循环
        mListJetModeCfg.get(0).setJetRound(String.valueOf(frequency - 1));
        mListJetModeCfg.get(0).setHighMax(etHighMax.getText().toString());
        mListJetModeCfg.get(0).setHighMin(etHighMin.getText().toString());
        mListJetModeCfg.get(0).updateAll("jetIdMillis=?", String.valueOf(mJetIdMillis));
    }

    private String countTime() {
        float totalTime;
        totalTime = MgrOutputJet.calCountAloneTime(0, CONFIG_INTERVAL, "",
                etGap.getText().toString(),
                etDuration.getText().toString(),
                "0",
                String.valueOf((Integer.parseInt(etFrequency.getText().toString()) - 1)));
        return String.valueOf(totalTime);
    }

}
