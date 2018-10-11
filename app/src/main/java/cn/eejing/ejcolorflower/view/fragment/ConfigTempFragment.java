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

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.model.event.DevConnEvent;
import cn.eejing.ejcolorflower.util.CircleProgress;
import cn.eejing.ejcolorflower.view.base.BaseFragment;

/**
 * 设备配置温度显示
 */

public class ConfigTempFragment extends BaseFragment {
    private static final String TAG = "ConfigTempFragment";

    @BindView(R.id.circle_progress)        CircleProgress mCircleProgress;
    @BindView(R.id.tv_switch_info)         TextView mTvSwitchInfo;
    @BindView(R.id.tv_dev_temp)            TextView mTvDevTemp;

    private static int mDevTemp;

    public static ConfigTempFragment newInstance(int temp) {
        Log.i(TAG, "newInstance: " + temp);
        ConfigTempFragment fragment = new ConfigTempFragment();
        mDevTemp = temp;
        return fragment;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Log.w(TAG, "TEMP: " + mDevTemp);
                    setDevTemp(mDevTemp);
                    break;
            }
        }
    };

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_page_device_info;
    }

    @Override
    public void initView(View rootView) {
        EventBus.getDefault().register(this);
        setDevTemp(mDevTemp);
    }

    @SuppressLint("SetTextI18n")
    private void setDevTemp(int temp) {
        if (temp == -1) {
            mTvDevTemp.setVisibility(View.GONE);
            setCircleInfo(0);
        } else {
            mTvDevTemp.setText(String.valueOf(temp) + "℃");
            setCircleInfo(mDevTemp);
        }
    }

    /** 设置圆环信息 */
    private void setCircleInfo(float curProgress) {
        mCircleProgress.setMaxProgress(550);
        mCircleProgress.setProgress(curProgress);
        mTvDevTemp.setVisibility(View.VISIBLE);
        mTvSwitchInfo.setText("温度");
        mCircleProgress.post(new Runnable() {
            @Override
            public void run() {
                LinearGradient linearGradient = new LinearGradient(
                        0, 0,
                        mCircleProgress.getWidth(), mCircleProgress.getHeight(),
//                            mCircleProgress.getRingProgressColor(),
                        ContextCompat.getColor(getContext(), R.color.colorTempLow),
                        ContextCompat.getColor(getContext(), R.color.colorTempHigh),
                        Shader.TileMode.MIRROR
                );
                mCircleProgress.setProgressShader(linearGradient);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /** 蓝牙连接状态 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventDevConn(DevConnEvent event) {
        // 接收硬件传过来的已连接设备信息添加到 HashSet
        Log.i(TAG, "temp cfg event: " + event.getMac() + " | " + event.getId() + " | " + event.getStatus());

        switch (event.getStatus()) {
            case "已连接":
                mDevTemp = event.getDeviceStatus().mTemperature;
                mHandler.sendEmptyMessage(1);
                break;
            case "不可连接":
                break;
        }
    }

}
