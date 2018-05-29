package cn.eejing.ejcolorflower.ui.fragment;

import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sdsmdg.harjot.crollerTest.Croller;
import com.sdsmdg.harjot.crollerTest.OnCrollerChangeListener;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
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

    private int mDeviceInfo;
    private boolean mRunning;

    public static DevicePageFragment newInstance(int info) {
        Log.i("TAG", "newInstance: "+info);
        DevicePageFragment fragment = new DevicePageFragment();
        fragment.mDeviceInfo = info;
        return fragment;
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_device_page;
    }

    @Override
    public void initView(View rootView) {
        tvDmxAddress.setText(String.valueOf(mDeviceInfo));
        // new 倒计时对象,总共的时间,每隔多少秒更新一次时间
        final MyCountDownTimer myCountDownTimer = new MyCountDownTimer(mDeviceInfo * 1000, 1000);
        myCountDownTimer.start();


        // TODO: 18/5/26 写死 假数据
        if (mDeviceInfo == 500) {
            imgTempThreshold.setVisibility(View.VISIBLE);
        }
        if (mDeviceInfo == 18) {
            tvDmxAddress.setVisibility(View.VISIBLE);
        }
        if (mDeviceInfo == 300) {
            layoutDeviceTime.setVisibility(View.VISIBLE);
        }
    }


    // 倒计时
    private class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        // 计时过程
        @Override
        public void onTick(long l) {
            chTimeLeft.setText(l / 1000 + "s");
        }

        // 计时完毕的方法
        @Override
        public void onFinish() {
        }
    }


}