package cn.eejing.ejcolorflower.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.activity.MainActivity;
import cn.eejing.ejcolorflower.ui.activity.MiAboutActivity;
import cn.eejing.ejcolorflower.ui.activity.QRCodeActivity;
import cn.eejing.ejcolorflower.ui.base.BaseFragment;


/**
 * @创建者 Taodaren
 * @描述 设备模块
 */

public class TabDeviceFragment extends BaseFragment {

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
        imgAddDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).jumpToActivity(QRCodeActivity.class);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 在 onActivityCreated 方法中初始化 Toolbar
        setToolbar(R.id.main_toolbar, R.string.device_name, View.VISIBLE);
    }

}
