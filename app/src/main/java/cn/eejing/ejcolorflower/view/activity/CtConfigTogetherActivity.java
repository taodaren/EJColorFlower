package cn.eejing.ejcolorflower.view.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.litepal.LitePal;

import java.util.List;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.app.GApp;
import cn.eejing.ejcolorflower.model.lite.JetModeConfigLite;
import cn.eejing.ejcolorflower.util.DecimalInputTextWatcher;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_TOGETHER;
import static cn.eejing.ejcolorflower.app.AppConstant.DEFAULT_HIGH;

/**
 * 齐喷配置
 */

public class CtConfigTogetherActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.btn_verify_together)         Button btnVerify;
    @BindView(R.id.btn_demo_together)           Button btnDemo;
    @BindView(R.id.et_together_duration)        EditText etDuration;
    @BindView(R.id.et_together_high)            EditText etHigh;
    @BindView(R.id.tv_together_jet_time)        TextView tvJetTime;
    @BindView(R.id.ll_jet_time_together)        LinearLayout llJetTime;

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
            if (etDuration.getText().toString().trim().isEmpty()
                    || etHigh.getText().toString().trim().isEmpty()) {
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
        return R.layout.activity_ct_config_together;
    }

    @Override
    public void initView() {
        setToolbar(CONFIG_TOGETHER, View.VISIBLE, null, View.GONE);
        mApp = (GApp) getApplication();
        mJetIdMillis = getIntent().getLongExtra("jet_id_millis", 0);
        initConfig();

        tvJetTime.setText(countTime());
    }

    private void initConfig() {
        mListJetModeCfg = LitePal.where("jetIdMillis = ?", String.valueOf(mJetIdMillis)).find(JetModeConfigLite.class);
        etDuration.setText(mListJetModeCfg.get(0).getDuration());
        etHigh.setText(mListJetModeCfg.get(0).getHigh());
    }

    @Override
    public void initListener() {
        btnVerify.setOnClickListener(this);
        btnDemo.setOnClickListener(this);

        // 不限制整数位数，限制小数位数为 1 位
        etDuration.addTextChangedListener(new DecimalInputTextWatcher(etDuration, DecimalInputTextWatcher.Type.decimal, 1));
        etDuration.addTextChangedListener(textWatcher);
        etHigh.addTextChangedListener(textWatcher);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_verify_together:
                updateLiteData();
                finish();
                break;
            case R.id.btn_demo_together:
                mApp.setFlagGifDemo(AppConstant.CONFIG_TOGETHER);
                jumpToActivity(CtDemoDescriptionActivity.class);
                break;
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
        mListJetModeCfg.get(0).setJetType(CONFIG_TOGETHER);
        mListJetModeCfg.get(0).setDuration(etDuration.getText().toString());
        mListJetModeCfg.get(0).setHigh(etHigh.getText().toString());
        mListJetModeCfg.get(0).updateAll("jetIdMillis=?", String.valueOf(mJetIdMillis));
    }

    private String countTime() {
        return etDuration.getText().toString();
    }

}
