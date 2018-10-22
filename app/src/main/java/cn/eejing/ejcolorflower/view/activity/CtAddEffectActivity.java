package cn.eejing.ejcolorflower.view.activity;

import android.content.Intent;
import android.view.View;

import butterknife.OnClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

/**
 * 添加喷射效果界面
 */

public class CtAddEffectActivity extends BaseActivity {

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ct_add_effect;
    }

    @Override
    public void initView() {
        setToolbar("添加效果", View.VISIBLE, null, View.GONE);
    }

    @OnClick({R.id.add_ride_effect, R.id.add_stream_effect, R.id.add_together_effect, R.id.add_interval_effect, R.id.add_stop_jet})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_ride_effect:
                setResult(RESULT_OK, new Intent().putExtra("jet_mode", AppConstant.CONFIG_RIDE));
                finish();
                break;
            case R.id.add_stream_effect:
                setResult(RESULT_OK, new Intent().putExtra("jet_mode", AppConstant.CONFIG_STREAM));
                finish();
                break;
            case R.id.add_together_effect:
                setResult(RESULT_OK, new Intent().putExtra("jet_mode", AppConstant.CONFIG_TOGETHER));
                finish();
                break;
            case R.id.add_interval_effect:
                setResult(RESULT_OK, new Intent().putExtra("jet_mode", AppConstant.CONFIG_INTERVAL));
                finish();
                break;
            case R.id.add_stop_jet:
                setResult(RESULT_OK, new Intent().putExtra("jet_mode", AppConstant.CONFIG_STOP));
                finish();
                break;
        }
    }

}
