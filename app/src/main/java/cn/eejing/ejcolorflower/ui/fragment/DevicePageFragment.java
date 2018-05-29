package cn.eejing.ejcolorflower.ui.fragment;

import android.annotation.SuppressLint;
import android.util.Log;
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

    private int mDeviceInfo;
    private boolean mRunning;

    public static DevicePageFragment newInstance(int info, int type) {
        Log.i("TAG", "newInstance: " + info);

        switch (type) {
            case AppConstant.TYPE_TEMP:
                break;
            case AppConstant.TYPE_DMX:
                break;
            case AppConstant.TYPE_TIME:
                break;
            default:
        }
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

        // 将获取到的 int 类型剩余时间转换成 String 类型显示
        long nowTimeLong = (long) mDeviceInfo * 1000;
        @SuppressLint("SimpleDateFormat") DateFormat ymdhmsFormat = new SimpleDateFormat("mm:ss");
        String nowTimeStr = ymdhmsFormat.format(nowTimeLong);
        chTimeLeft.setText(nowTimeStr);

        // TODO: 18/5/26 写死 假数据
        if (mDeviceInfo == 20) {
            imgTempThreshold.setVisibility(View.VISIBLE);
        }
        if (mDeviceInfo == 17) {
            tvDmxAddress.setVisibility(View.VISIBLE);
        }
        if (mDeviceInfo == 48) {
            layoutDeviceTime.setVisibility(View.VISIBLE);
        }
    }

}