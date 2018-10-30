package cn.eejing.ejcolorflower.view.fragment;

import android.annotation.SuppressLint;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Objects;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.model.event.DevConnEvent;
import cn.eejing.ejcolorflower.util.CircleProgress;
import cn.eejing.ejcolorflower.view.base.BaseFragment;

import static cn.eejing.ejcolorflower.app.AppConstant.DEVICE_CONNECT_NO;
import static cn.eejing.ejcolorflower.app.AppConstant.DEVICE_CONNECT_YES;
import static cn.eejing.ejcolorflower.app.AppConstant.HANDLE_BLE_CONN;
import static cn.eejing.ejcolorflower.app.AppConstant.HANDLE_BLE_DISCONN;

/**
 * 设备配置温度显示
 */

public class ConfigTempFragment extends BaseFragment {
    private static final String TAG = "ConfigTempFragment";

    @BindView(R.id.circle_progress)        CircleProgress mCircleProgress;
    @BindView(R.id.tv_switch_info)         TextView mTvSwitchInfo;
    @BindView(R.id.tv_dev_temp)            TextView mTvDevTemp;

    private static int mDevTemp;
    private int mHeating;

    public static ConfigTempFragment newInstance(int temp) {
        ConfigTempFragment fragment = new ConfigTempFragment();
        mDevTemp = temp;
        return fragment;
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_page_device_info;
    }

    @Override
    public void initView(View rootView) {
        setDevTemp(mDevTemp);
    }

    @SuppressLint("SetTextI18n")
    private void setDevTemp(int temp) {
        if (temp == -1) {
            mTvDevTemp.setText("设备断连");
            mTvDevTemp.setVisibility(View.GONE);
            setCircleInfo(0);
        } else {
            if (temp > mHeating) {
                mTvDevTemp.setText("预热完成");
            } else {
                mTvDevTemp.setText("预热中...");
            }
            setCircleInfo(temp);
        }
    }

    /** 设置圆环信息 */
    private void setCircleInfo(float curProgress) {
        mCircleProgress.setMaxProgress(550);
        mCircleProgress.setProgress(curProgress);
        mTvDevTemp.setVisibility(View.VISIBLE);
        mTvSwitchInfo.setText("温度");
        mCircleProgress.post(() -> {
            LinearGradient linearGradient = new LinearGradient(
                    0, 0,
                    mCircleProgress.getWidth(), mCircleProgress.getHeight(),
                    ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorTempLow),
                    ContextCompat.getColor(getContext(), R.color.colorTempHigh),
                    Shader.TileMode.MIRROR
            );
            mCircleProgress.setProgressShader(linearGradient);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        mCircleProgress.setProgress(mDevTemp);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
        mCircleProgress.setProgress(0);
    }

    /** 蓝牙连接状态 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventDevConn(DevConnEvent event) {
        // 接收硬件传过来的已连接设备信息添加到 HashSet
        Log.d(TAG, "temp cfg event: " + event.getMac() + " | " + event.getId() + " | " + event.getStatus());

        switch (event.getStatus()) {
            case DEVICE_CONNECT_YES:
                mHeating = event.getDeviceConfig().mTemperatureThresholdLow;
                mDevTemp = event.getDeviceStatus().mTemperature;
                mHandler.sendEmptyMessage(HANDLE_BLE_CONN);
                break;
            case DEVICE_CONNECT_NO:
                mHandler.sendEmptyMessage(HANDLE_BLE_DISCONN);
                break;
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLE_BLE_CONN:
                    setDevTemp(mDevTemp);
                    break;
                case HANDLE_BLE_DISCONN:
                    setDevTemp(-1);
                    break;
                default:
            }
        }
    };

}
