package cn.eejing.ejcolorflower.view.fragment;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.harjot.crollerTest.Croller;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.device.BleDeviceProtocol;
import cn.eejing.ejcolorflower.model.event.DeviceConnectEvent;
import cn.eejing.ejcolorflower.model.event.DmxZeroEvent;
import cn.eejing.ejcolorflower.util.SelfDialog;
import cn.eejing.ejcolorflower.view.activity.MainActivity;
import cn.eejing.ejcolorflower.view.base.BaseFragment;

/**
 * 设备信息
 */

public class PageDeviceInfoFragment extends BaseFragment {

    @BindView(R.id.croller)                   Croller croller;
    @BindView(R.id.layout_device_time)        RelativeLayout layoutDeviceTime;
    @BindView(R.id.ch_time_left)              Chronometer chTimeLeft;

    private int mDevInfo;
    private long mDevId;
    private static int mDevTemp, mDevTime;

    public static PageDeviceInfoFragment newInstance(int info, int type, long deviceId) {
        Log.i("TAG", "newInstance: " + info);
        PageDeviceInfoFragment fragment = new PageDeviceInfoFragment();
        fragment.mDevInfo = info;
        fragment.mDevId = deviceId;

        switch (type) {
            case AppConstant.TYPE_TEMP:
                mDevTemp = fragment.mDevInfo;
                break;
            case AppConstant.TYPE_TIME:
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
//        setTimeLeft();

//        typeOfJudgment();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTimeLeft() {
        // 展示剩余时间
        long nowTimeLong = (long) mDevInfo * 1000;
        @SuppressLint("SimpleDateFormat") DateFormat ymdhmsFormat = new SimpleDateFormat("mm:ss");
        String nowTimeStr = ymdhmsFormat.format(nowTimeLong);
        chTimeLeft.setText(nowTimeStr);

        // SeekBar 禁止拖动和点击
        croller.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(false);
                return true;
            }
        });
        // 最大值 2 小时
        croller.setMax(7200);
        // 当前剩余时间进度
        croller.setProgress(mDevInfo);
        // 设置剩余时间占比
        double conversion = (double) mDevInfo / 7200;
        NumberFormat instance = NumberFormat.getPercentInstance();
        instance.setMaximumFractionDigits(1);
        croller.setLabel(instance.format(conversion));
    }

    private void typeOfJudgment() {
        if (mDevInfo == mDevTime) {
            layoutDeviceTime.setVisibility(View.VISIBLE);
        }
    }

}