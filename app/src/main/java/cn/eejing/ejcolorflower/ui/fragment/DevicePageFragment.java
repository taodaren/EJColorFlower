package cn.eejing.ejcolorflower.ui.fragment;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sdsmdg.harjot.crollerTest.Croller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.ui.base.BaseFragment;

public class DevicePageFragment extends BaseFragment {

    @BindView(R.id.tv_dmx_address)
    TextView tvDmxAddress;
    @BindView(R.id.img_temp_threshold)
    ImageView imgTempThreshold;
    @BindView(R.id.croller)
    Croller croller;
    @BindView(R.id.layout_device_time)
    RelativeLayout layoutDeviceTime;
    @BindView(R.id.ch_time_left)
    Chronometer chTimeLeft;

    private int mDeviceInfo, mThresholdHigh;
    private static int mDeviceTemp, mDeviceDmx, mDevicetime;

    public static DevicePageFragment newInstance(int info, int thresholdHigh, int type) {
        Log.i("TAG", "newInstance: " + info);
        DevicePageFragment fragment = new DevicePageFragment();
        fragment.mDeviceInfo = info;
        fragment.mThresholdHigh = thresholdHigh;

        switch (type) {
            case AppConstant.TYPE_TEMP:
                mDeviceTemp = fragment.mDeviceInfo;
                break;
            case AppConstant.TYPE_DMX:
                mDeviceDmx = fragment.mDeviceInfo;
                break;
            case AppConstant.TYPE_TIME:
                mDevicetime = fragment.mDeviceInfo;
                break;
            default:
        }
        return fragment;
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_device_page;
    }

    @Override
    public void initView(View rootView) {
        setTempStatus();
        setDmxAddress();
        setTimeLeft();

        typeOfJudgment();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTimeLeft() {
        // 展示剩余时间
        long nowTimeLong = (long) mDeviceInfo * 1000;
        @SuppressLint("SimpleDateFormat") DateFormat ymdhmsFormat = new SimpleDateFormat("mm:ss");
        String nowTimeStr = ymdhmsFormat.format(nowTimeLong);
        chTimeLeft.setText(nowTimeStr);

        // SeekBar 禁止拖动和点击
        croller.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        // 最大值 2 小时
        croller.setMax(7200);
        // 当前剩余时间进度
        croller.setProgress(mDeviceInfo);
        // 设置当前物料占比
        double percent = mDeviceInfo / 7200 * 100;
//        @SuppressLint("DefaultLocale") String strPercent = String.format("%.2f", percent);
        croller.setLabel(percent + "%");
    }

    private void setDmxAddress() {
        tvDmxAddress.setText(String.valueOf(mDeviceInfo));
    }

    private void setTempStatus() {
        double tempLvOne, tempLvTwo, tempLvThree, tempLvFour, tempLvFive;

        tempLvOne = mThresholdHigh * (0.2);
        tempLvTwo = mThresholdHigh * (0.4);
        tempLvThree = mThresholdHigh * (0.6);
        tempLvFour = mThresholdHigh * (0.8);
        tempLvFive = mThresholdHigh;

        if (mDeviceInfo <= tempLvOne) {
            imgTempThreshold.setImageDrawable(getContext().getDrawable(R.drawable.lv_temp_one));
        } else if (tempLvOne < mDeviceInfo && mDeviceInfo <= tempLvTwo) {
            imgTempThreshold.setImageDrawable(getContext().getDrawable(R.drawable.lv_temp_two));
        } else if (tempLvTwo < mDeviceInfo && mDeviceInfo <= tempLvThree) {
            imgTempThreshold.setImageDrawable(getContext().getDrawable(R.drawable.lv_temp_three));
        } else if (tempLvThree < mDeviceInfo && mDeviceInfo <= tempLvFour) {
            imgTempThreshold.setImageDrawable(getContext().getDrawable(R.drawable.lv_temp_four));
        } else if (tempLvFour < mDeviceInfo && mDeviceInfo <= tempLvFive) {
            imgTempThreshold.setImageDrawable(getContext().getDrawable(R.drawable.lv_temp_five));
        }
    }

    private void typeOfJudgment() {
        if (mDeviceInfo == mDeviceTemp) {
            imgTempThreshold.setVisibility(View.VISIBLE);
        }
        if (mDeviceInfo == mDeviceDmx) {
            tvDmxAddress.setVisibility(View.VISIBLE);
        }
        if (mDeviceInfo == mDevicetime) {
            layoutDeviceTime.setVisibility(View.VISIBLE);
        }
    }

}