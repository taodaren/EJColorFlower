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

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.util.CircleProgress;
import cn.eejing.ejcolorflower.view.base.BaseFragment;

/**
 * 设备配置剩余时间显示
 */

public class ConfigTimeFragment extends BaseFragment {
    private static final String TAG = "PageDeviceInfoFragment";

    @BindView(R.id.circle_progress)        CircleProgress mCircleProgress;
    @BindView(R.id.tv_switch_info)         TextView       mTvSwitchInfo;

    private static Chronometer chTimeLeft;

    private int mDevInfo;
    private static int  mDevTime;

    public static ConfigTimeFragment newInstance(int info) {
        Log.i(TAG, "newInstance: " + info);
        ConfigTimeFragment fragment = new ConfigTimeFragment();
        fragment.mDevInfo = info;

        mDevTime = fragment.mDevInfo;
        return fragment;
    }

    @SuppressLint("HandlerLeak")
    private static Handler handler = new Handler(){
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

    public static void updateBleData(int time) {
        mDevTime = time;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }).start();
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_page_device_info;
    }

    @Override
    public void initView(View rootView) {
        chTimeLeft = rootView.findViewById(R.id.ch_time_left);

        setTimeLeft(mDevInfo);
        typeOfJudgment();
    }

    private static void setTimeLeft(int time) {
        // 展示剩余时间
        long nowTimeLong = (long) time * 1000;
        @SuppressLint("SimpleDateFormat") DateFormat ymdhmsFormat = new SimpleDateFormat("mm:ss");
        String nowTimeStr = ymdhmsFormat.format(nowTimeLong);
        chTimeLeft.setText(nowTimeStr);
    }

    private void typeOfJudgment() {
        if (mDevInfo == mDevTime) {
            setCircleInfo(7200, mDevInfo, View.GONE, View.VISIBLE, "时间", R.color.colorTimeSmall, R.color.colorTimeMore);
        }
    }

    /**
     * 设置圆环信息
     *
     * @param maxProgress    总进度
     * @param curProgress    当前进度
     * @param tempVisibility 是否显示温度
     * @param timeVisibility 是否显示剩余时间
     * @param type           显示类型
     * @param colorStart     渐变起始颜色
     * @param colorEnd       渐变结束颜色
     */
    private void setCircleInfo(float maxProgress, float curProgress,
                               int tempVisibility, int timeVisibility, CharSequence type,
                               final int colorStart, final int colorEnd) {
        mCircleProgress.setMaxProgress(maxProgress);
        mCircleProgress.setProgress(curProgress);
        chTimeLeft.setVisibility(timeVisibility);
        mTvSwitchInfo.setText(type);
        mCircleProgress.post(new Runnable() {
            @Override
            public void run() {
                LinearGradient linearGradient = new LinearGradient(
                        0, 0,
                        mCircleProgress.getWidth(), mCircleProgress.getHeight(),
//                            mCircleProgress.getRingProgressColor(),
                        ContextCompat.getColor(getContext(), colorStart),
                        ContextCompat.getColor(getContext(), colorEnd),
                        Shader.TileMode.MIRROR
                );
                mCircleProgress.setProgressShader(linearGradient);
            }
        });
    }

}
