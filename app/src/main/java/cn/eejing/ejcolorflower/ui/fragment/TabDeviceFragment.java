package cn.eejing.ejcolorflower.ui.fragment;

import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.MainActivity;
import cn.eejing.ejcolorflower.ui.activity.QRCodeActivity;
import cn.eejing.ejcolorflower.ui.base.BaseFragment;

/**
 * @创建者 Taodaren
 * @描述 设备模块
 */

public class TabDeviceFragment extends BaseFragment implements View.OnClickListener {

    @BindView(R.id.img_add_device)
    ImageView imgAddDevice;

    public static TabDeviceFragment newInstance() {
        return new TabDeviceFragment();
    }

    public TabDeviceFragment() {
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_tab_device;
    }

    @Override
    public void initView(View rootView) {
    }

    @Override
    public void initToolbar() {
        setToolbar(R.id.main_toolbar, R.string.device_name, View.VISIBLE);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initListener() {
        imgAddDevice.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_add_device:
                ((MainActivity) getActivity()).jumpToActivity(QRCodeActivity.class);
                break;
            default:
                break;
        }
    }
}
