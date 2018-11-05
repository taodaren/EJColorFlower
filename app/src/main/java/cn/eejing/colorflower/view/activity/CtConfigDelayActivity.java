package cn.eejing.colorflower.view.activity;

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
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.app.BaseApplication;
import cn.eejing.colorflower.model.lite.JetModeConfigLite;
import cn.eejing.colorflower.util.DecimalInputTextWatcher;
import cn.eejing.colorflower.view.base.BaseActivity;

import static cn.eejing.colorflower.app.AppConstant.CONFIG_DELAY;
import static cn.eejing.colorflower.app.AppConstant.DEFAULT_HIGH_DELAY;

public class CtConfigDelayActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.ll_delay_time)           LinearLayout llJetTime;
    @BindView(R.id.et_delay_time)           EditText     etDelay;
    @BindView(R.id.tv_delay_time)           TextView     tvJetTime;
    @BindView(R.id.btn_verify_delay)        Button       btnVerify;

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
            if (etDelay.getText().toString().trim().isEmpty()) {
                // EditText 有空情况
                btnVerify.setEnabled(Boolean.FALSE);
                btnVerify.setBackground(getDrawable(R.drawable.ic_btn_no_click));
                llJetTime.setVisibility(View.INVISIBLE);
            } else {
                // EditText 不为空的情况
                btnVerify.setEnabled(Boolean.TRUE);
                btnVerify.setBackground(getDrawable(R.drawable.ic_btn_full));
                llJetTime.setVisibility(View.VISIBLE);
                tvJetTime.setText(countTime());
            }
        }
    };

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ct_config_delay;
    }

    @Override
    public void initView() {
        setToolbar(CONFIG_DELAY, View.VISIBLE, null, View.GONE);
        mApp = (BaseApplication) getApplication();
        mJetIdMillis = getIntent().getLongExtra("jet_id_millis", 0);
        initConfig();

        tvJetTime.setText(countTime());
    }

    private void initConfig() {
        mListJetModeCfg = LitePal.where("jetIdMillis = ?", String.valueOf(mJetIdMillis)).find(JetModeConfigLite.class);
        etDelay.setText(mListJetModeCfg.get(0).getDuration());
    }

    @Override
    public void initListener() {
        btnVerify.setOnClickListener(this);

        // 不限制整数位数，限制小数位数为 1 位
        etDelay.addTextChangedListener(new DecimalInputTextWatcher(etDelay, DecimalInputTextWatcher.Type.decimal, 1));
        etDelay.addTextChangedListener(textWatcher);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_verify_delay:
                updateLiteData();
                finish();
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        mApp.setFlagGifDemo(null);
    }

    private void updateLiteData() {
        mListJetModeCfg.get(0).setJetType(CONFIG_DELAY);
        mListJetModeCfg.get(0).setDuration(etDelay.getText().toString());
        mListJetModeCfg.get(0).setHigh(DEFAULT_HIGH_DELAY);
        mListJetModeCfg.get(0).updateAll("jetIdMillis=?", String.valueOf(mJetIdMillis));
    }

    private String countTime() {
        return etDelay.getText().toString();
    }

}
