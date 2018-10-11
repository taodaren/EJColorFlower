package cn.eejing.ejcolorflower.view.fragment;

import android.annotation.SuppressLint;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.model.event.DevConnEvent;
import cn.eejing.ejcolorflower.util.CircleProgress;
import cn.eejing.ejcolorflower.view.base.BaseFragment;

/**
 * 设备配置剩余时间显示
 */

public class ConfigTimeFragment extends BaseFragment {
    private static final String TAG = "PageDeviceInfoFragment";

    @BindView(R.id.circle_progress)        CircleProgress mCircleProgress;
    @BindView(R.id.ch_time_left)           Chronometer    chTimeLeft;
    @BindView(R.id.tv_switch_info)         TextView       mTvSwitchInfo;

    private static int  mDevTime;

    public static ConfigTimeFragment newInstance(int time) {
        Log.i(TAG, "newInstance: " + time);
        ConfigTimeFragment fragment = new ConfigTimeFragment();
        mDevTime = time;
        return fragment;
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Log.w(TAG, "TIME: " + mDevTime );
                    setTimeLeft(mDevTime);
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
        setTimeLeft(mDevTime);
    }

    private void setTimeLeft(int time) {
        if (time == -1) {
            chTimeLeft.setVisibility(View.GONE);
            setCircleInfo(0);
        } else {
            // 展示剩余时间
            long nowTimeLong = (long) time * 1000;
            @SuppressLint("SimpleDateFormat") DateFormat ymdhmsFormat = new SimpleDateFormat("mm:ss");
            String nowTimeStr = ymdhmsFormat.format(nowTimeLong);
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
        mCircleProgress.post(new Runnable() {
            @Override
            public void run() {
                LinearGradient linearGradient = new LinearGradient(
                        0, 0,
                        mCircleProgress.getWidth(), mCircleProgress.getHeight(),
//                            mCircleProgress.getRingProgressColor(),
                        ContextCompat.getColor(getContext(), R.color.colorTimeSmall),
                        ContextCompat.getColor(getContext(), R.color.colorTimeMore),
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
        Log.i(TAG, "time cfg event: " + event.getMac() + " | " + event.getId() + " | " + event.getStatus());

        switch (event.getStatus()) {
            case "已连接":
                mDevTime = event.getDeviceStatus().mRestTime;
                mHandler.sendEmptyMessage(1);
                break;
            case "不可连接":
                break;
        }
    }

}
