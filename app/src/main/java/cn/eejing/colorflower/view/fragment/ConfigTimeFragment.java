package cn.eejing.colorflower.view.fragment;

import android.annotation.SuppressLint;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Objects;

import butterknife.BindView;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.event.DevConnEvent;
import cn.eejing.colorflower.util.CircleProgress;
import cn.eejing.colorflower.view.base.BaseFragmentEvent;

import static cn.eejing.colorflower.app.AppConstant.DEVICE_CONNECT_NO;
import static cn.eejing.colorflower.app.AppConstant.DEVICE_CONNECT_YES;
import static cn.eejing.colorflower.app.AppConstant.HANDLE_BLE_CONN;
import static cn.eejing.colorflower.app.AppConstant.HANDLE_BLE_DISCONN;

/**
 * 设备配置剩余时间显示
 */

public class ConfigTimeFragment extends BaseFragmentEvent {

    @BindView(R.id.circle_progress)        CircleProgress mCircleProgress;
    @BindView(R.id.ch_time_left)           Chronometer    chTimeLeft;
    @BindView(R.id.tv_switch_info)         TextView       mTvSwitchInfo;

    private static int mDevTime;

    public static ConfigTimeFragment newInstance(int time) {
        ConfigTimeFragment fragment = new ConfigTimeFragment();
        mDevTime = time;
        return fragment;
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_page_device_info;
    }

    @Override
    public void initView(View rootView) {
        setTimeLeft(mDevTime);
    }

    private void setTimeLeft(int time) {
        if (time == -1) {
            chTimeLeft.setVisibility(View.GONE);
            chTimeLeft.setText("--:--");
            setCircleInfo(0);
        } else {
            // 展示剩余时间
            long nowTimeLong = (long) time * 1000;
            @SuppressLint("SimpleDateFormat")
            DateFormat format = new SimpleDateFormat("mm:ss");
            String nowTimeStr = format.format(nowTimeLong);
            chTimeLeft.setText(nowTimeStr);
            setCircleInfo(time);
        }
    }

    /** 设置圆环信息 */
    private void setCircleInfo(float curProgress) {
        mCircleProgress.setMaxProgress(7200);
        mCircleProgress.setProgress(curProgress);
        chTimeLeft.setVisibility(View.VISIBLE);
        mTvSwitchInfo.setText("时间");
        mCircleProgress.post(() -> {
            LinearGradient linearGradient = new LinearGradient(
                    0, 0,
                    mCircleProgress.getWidth(), mCircleProgress.getHeight(),
                    ContextCompat.getColor(Objects.requireNonNull(getContext()), R.color.colorTimeSmall),
                    ContextCompat.getColor(getContext(), R.color.colorTimeMore),
                    Shader.TileMode.MIRROR
            );
            mCircleProgress.setProgressShader(linearGradient);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mCircleProgress.setProgress(mDevTime);
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLE_BLE_CONN:
                    setTimeLeft(mDevTime);
                    break;
                case HANDLE_BLE_DISCONN:
                    setTimeLeft(-1);
                    break;
                default:
            }
        }
    };

    @Override
    public void onEventBleConn(DevConnEvent event) {
        super.onEventBleConn(event);
        // 接收硬件传过来的已连接设备信息添加到 HashSet
        switch (event.getStatus()) {
            case DEVICE_CONNECT_YES:
                mDevTime = event.getDeviceStatus().getRestTime();
                mHandler.sendEmptyMessage(HANDLE_BLE_CONN);
                break;
            case DEVICE_CONNECT_NO:
                mHandler.sendEmptyMessage(HANDLE_BLE_DISCONN);
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mCircleProgress.setProgress(0);
    }

}
