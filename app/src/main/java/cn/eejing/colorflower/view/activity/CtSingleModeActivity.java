package cn.eejing.colorflower.view.activity;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.device.BleEEJingCtrl;
import cn.eejing.colorflower.util.BleDevProtocol;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.view.base.BaseActivity;

/**
 * 单台控制
 */

public class CtSingleModeActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {

    @BindView(R.id.tv_high_num)         TextView         tvHighNum;
    @BindView(R.id.sb_high_progress)    SeekBar          sbHigh;
    @BindView(R.id.switch_on)           ConstraintLayout switchOn;
    @BindView(R.id.switch_off)          ConstraintLayout switchOff;

    private static final String TAG = "CtSingleModeActivity";
    private static final int MSG_JET_START = 1;

    private boolean mIsSwitch;
    private long mDevId;
    private int mHigh = 20;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == MSG_JET_START) {
                cmdJetStart();
                LogUtil.d(TAG, "handleMessage  MSG_JET_START");
                mHandler.sendEmptyMessageDelayed(MSG_JET_START, 300);
            }
        }
    };

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ct_single_mode;
    }

    @Override
    public void initView() {
        setToolbar("单台控制", View.VISIBLE, null, View.GONE);
        mDevId = MainActivity.getAppCtrl().getDevId();
    }

    @Override
    public void initListener() {
        sbHigh.setOnSeekBarChangeListener(this);
        // 默认关闭状态
        setSeekBarClickable(mIsSwitch);
        switchOn.setVisibility(View.GONE);
        switchOff.setVisibility(View.VISIBLE);
    }

    @OnClick({R.id.switch_on, R.id.switch_off, R.id.layout_high})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.switch_on:
                mIsSwitch = false;
                switchOn.setVisibility(View.GONE);
                switchOff.setVisibility(View.VISIBLE);
                mHandler.removeMessages(MSG_JET_START);
                cmdJetStop();
                break;
            case R.id.switch_off:
                mIsSwitch = true;
                switchOn.setVisibility(View.VISIBLE);
                switchOff.setVisibility(View.GONE);
                mHandler.sendEmptyMessage(MSG_JET_START);
                break;
            case R.id.layout_high:
                if (!mIsSwitch) {
                    Snackbar snackbar = Snackbar.make(switchOff, "开关为关闭状态，打开后方可控制", Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(getResources().getColor(R.color.colorNavBar));
                    snackbar.setAction("确定", v -> snackbar.dismiss()).show();
                }
                break;
        }
        setSeekBarClickable(mIsSwitch);
    }

    /** 进度条发生改变 **/
    @SuppressLint("SetTextI18n")
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        // 当拖动条的滑块位置发生改变时触发该方法,在这里直接使用参数 progress，即当前滑块代表的进度值
        progress += 20;
        mHigh = progress;
        tvHighNum.setText(Integer.toString(progress));
    }

    /** 开始滑动 **/
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    /** 停止滑动 **/
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    private void setSeekBarClickable(boolean isSwitch) {
        if (isSwitch) {
            // 启用状态
            sbHigh.setClickable(true);
            sbHigh.setEnabled(true);
            sbHigh.setSelected(true);
            sbHigh.setFocusable(true);
        } else {
            // 禁用状态
            sbHigh.setClickable(false);
            sbHigh.setEnabled(false);
            sbHigh.setSelected(false);
            sbHigh.setFocusable(false);
        }
    }

    private void cmdJetStart() {
        BleEEJingCtrl.getInstance().sendCommand(BleDevProtocol.pkgJetStart(mDevId, 0, 10, mHigh), null);
    }

    private void cmdJetStop() {
        BleEEJingCtrl.getInstance().sendCommand(BleDevProtocol.pkgJetStop(mDevId), null);
    }
}
