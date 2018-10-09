package cn.eejing.ejcolorflower.view.activity;

import android.view.View;

import butterknife.OnClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

public class CtAddEffectActivity extends BaseActivity {

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ct_add_effect;
    }

    @Override
    public void initView() {
        setToolbar("添加效果", View.VISIBLE, null, View.GONE);
    }

    @OnClick({R.id.add_ride_effect, R.id.add_stream_effect, R.id.add_together_effect, R.id.add_interval_effect})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.add_ride_effect:
                break;
            case R.id.add_stream_effect:
                break;
            case R.id.add_together_effect:
                break;
            case R.id.add_interval_effect:
                break;
        }
    }

}
