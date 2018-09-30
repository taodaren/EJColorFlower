package cn.eejing.ejcolorflower.view.fragment;

import android.annotation.SuppressLint;
import android.graphics.LinearGradient;
import android.graphics.Shader;
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

import static cn.eejing.ejcolorflower.app.AppConstant.TYPE_TEMP;
import static cn.eejing.ejcolorflower.app.AppConstant.TYPE_TIME;

/**
 * 设备信息
 */

public class PageDeviceInfoFragment extends BaseFragment {
    private static final String TAG = "PageDeviceInfoFragment";

    @BindView(R.id.ch_time_left)           Chronometer    chTimeLeft;
    @BindView(R.id.circle_progress)        CircleProgress mCircleProgress;
    @BindView(R.id.tv_dev_temp)            TextView       mTvDevTemp;
    @BindView(R.id.tv_switch_info)         TextView       mTvSwitchInfo;

    private int mDevInfo;
    private long mDevId;
    private static int mDevTemp, mDevTime;

    public static PageDeviceInfoFragment newInstance(int info, int type, long deviceId) {
        Log.i(TAG, "newInstance: " + info);
        PageDeviceInfoFragment fragment = new PageDeviceInfoFragment();
        fragment.mDevInfo = info;
        fragment.mDevId = deviceId;

        switch (type) {
            case TYPE_TEMP:
                mDevTemp = fragment.mDevInfo;
                break;
            case TYPE_TIME:
                mDevTime = fragment.mDevInfo;
                break;
            default:
        }
        return fragment;
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_page_device_info;
    }

    @Override
    public void initView(View rootView) {
        setTimeLeft();
        setDevTemp();
        typeOfJudgment();
    }

    private void setDevTemp() {
        mTvDevTemp.setText(String.valueOf(mDevInfo) + "℃");
    }

    private void setTimeLeft() {
        // 展示剩余时间
        long nowTimeLong = (long) mDevInfo * 1000;
        @SuppressLint("SimpleDateFormat") DateFormat ymdhmsFormat = new SimpleDateFormat("mm:ss");
        String nowTimeStr = ymdhmsFormat.format(nowTimeLong);
        chTimeLeft.setText(nowTimeStr);
    }

    private void typeOfJudgment() {
        if (mDevInfo == mDevTemp) {
            setCircleInfo(550, mDevInfo, View.VISIBLE, View.GONE, "温度", R.color.colorTempLow, R.color.colorTempHigh);
        }
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
        mTvDevTemp.setVisibility(tempVisibility);
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