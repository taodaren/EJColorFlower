package cn.eejing.ejcolorflower.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.base.BaseFragment;


/**
 * 设备模块
 *
 * @author taodaren
 * @date 2018/5/11
 */

public class TabDeviceFragment extends BaseFragment {

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // 在 onActivityCreated 方法中初始化 Toolbar
        setToolbar(R.id.main_toolbar, R.string.device_name, View.VISIBLE);
    }

}
