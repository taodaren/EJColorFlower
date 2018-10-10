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

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.util.CircleProgress;
import cn.eejing.ejcolorflower.view.base.BaseFragment;

/**
 * 设备配置温度显示
 */

public class ConfigTempFragment extends BaseFragment {
    private static final String TAG = "ConfigTempFragment";

    @BindView(R.id.circle_progress)
    CircleProgress mCircleProgress;
    @BindView(R.id.tv_switch_info)
    TextView mTvSwitchInfo;

    private static TextView mTvDevTemp;

    private int mDevInfo;
    private static int mDevTemp;

    public static ConfigTempFragment newInstance(int temp) {
        Log.i(TAG, "newInstance: " + temp);
        ConfigTempFragment fragment = new ConfigTempFragment();
        fragment.mDevInfo = temp;

        mDevTemp = fragment.mDevInfo;
        return fragment;
    }

    @SuppressLint("HandlerLeak")
    private static Handler handler = new Handler(){
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

    public static void updateBleData(int temp) {
        mDevTemp = temp;
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
        mTvDevTemp = rootView.findViewById(R.id.tv_dev_temp);
        Log.w(TAG, "wwwwww: " + mTvDevTemp);

        setDevTemp(mDevInfo);
        typeOfJudgment();
    }

    @SuppressLint("SetTextI18n")
    private static void setDevTemp(int temp) {
        mTvDevTemp.setText(String.valueOf(temp) + "℃");
    }

    private void typeOfJudgment() {
        if (mDevInfo == mDevTemp) {
            setCircleInfo(550, mDevInfo, View.VISIBLE, View.GONE, "温度", R.color.colorTempLow, R.color.colorTempHigh);
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
