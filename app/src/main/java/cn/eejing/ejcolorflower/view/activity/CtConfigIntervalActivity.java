package cn.eejing.ejcolorflower.view.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.util.List;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.app.GApp;
import cn.eejing.ejcolorflower.model.lite.JetModeConfigLite;
import cn.eejing.ejcolorflower.model.manager.MgrOutputJet;
import cn.eejing.ejcolorflower.util.DecimalInputTextWatcher;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_INTERVAL;
import static cn.eejing.ejcolorflower.app.AppConstant.DEFAULT_HIGH;

/**
 * 间隔高低配置
 */

public class CtConfigIntervalActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_verify_interval)          Button btnVerify;
    @BindView(R.id.btn_demo_interval)            Button btnDemo;
    @BindView(R.id.et_interval_gap)              EditText etGap;
    @BindView(R.id.et_interval_duration)         EditText etDuration;
    @BindView(R.id.et_interval_frequency)        EditText etFrequency;
    @BindView(R.id.tv_interval_jet_time)         TextView tvJetTime;
    @BindView(R.id.ll_jet_time_interval)         LinearLayout llJetTime;

    private GApp mApp;
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
        return R.layout.activity_ct_config_interval;
    }

    @Override
    public void initView() {
        setToolbar(CONFIG_INTERVAL, View.VISIBLE, null, View.GONE);
        mApp = (GApp) getApplication();
        mJetIdMillis = getIntent().getLongExtra("jet_id_millis", 0);
        initConfig();

        tvJetTime.setText(countTime());
    }

    private void initConfig() {
        mListJetModeCfg = LitePal.where("jetIdMillis = ?", String.valueOf(mJetIdMillis)).find(JetModeConfigLite.class);
        etGap.setText(mListJetModeCfg.get(0).getGap());
        etDuration.setText(mListJetModeCfg.get(0).getDuration());
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
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_verify_interval:
                if (Integer.parseInt(etFrequency.getText().toString()) == 0) {
                    Toast.makeText(this, "喷射次数不能为 0，请重新设置！", Toast.LENGTH_SHORT).show();
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
        mListJetModeCfg.get(0).setHigh(DEFAULT_HIGH);
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
