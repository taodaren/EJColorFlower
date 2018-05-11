package cn.eejing.ejcolorflower.ui.fragment;


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

}
