package cn.eejing.ejcolorflower.ui.fragment;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.base.BaseFragment;

public class DevicePageFragment extends BaseFragment {

    @BindView(R.id.tv_dmx_address)
    TextView tvDmxAddress;
    @BindView(R.id.img_temp_threshold)
    ImageView imgTempThreshold;
    @BindView(R.id.tv_time_left)
    TextView tvTimeLeft;

    private int mDeviceInfo;

    public static DevicePageFragment newInstance(int info) {
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
        tvTimeLeft.setText(String.valueOf(mDeviceInfo));

        // TODO: 18/5/26 写死 假数据
        if (mDeviceInfo == 500) {
            imgTempThreshold.setVisibility(View.VISIBLE);
        }
        if (mDeviceInfo == 18) {
            tvDmxAddress.setVisibility(View.VISIBLE);
        }
        if (mDeviceInfo == 300) {
            tvTimeLeft.setVisibility(View.VISIBLE);
        }
    }

}