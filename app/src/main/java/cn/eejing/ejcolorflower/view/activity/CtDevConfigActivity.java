package cn.eejing.ejcolorflower.view.activity;

import android.util.Log;
import android.view.View;

import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.QR_DEV_ID;

public class CtDevConfigActivity extends BaseActivity {
    private static final String TAG = "CtDevConfigActivity";

    private long mDevId;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ct_dev_config;
    }

    @Override
    public void initView() {
        setToolbar("设备配置", View.VISIBLE, null, View.GONE);

        mDevId = getIntent().getLongExtra(QR_DEV_ID, 0);
        Log.i(TAG, "mDevId: " + mDevId);
    }
}
