package cn.eejing.ejcolorflower.view.fragment;

import android.content.Intent;
import android.view.View;

import butterknife.OnClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.view.activity.CtQrScanActivity;
import cn.eejing.ejcolorflower.view.base.BaseFragment;

/**
 * 控制模块
 */

public class TabCtrlFragment extends BaseFragment {


    public static TabCtrlFragment newInstance() {
        return new TabCtrlFragment();
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_tab_ctrl;
    }

    @Override
    public void initToolbar() {
        setToolbar(R.id.main_toolbar, R.string.control_name, View.VISIBLE);
    }

    @OnClick(R.id.btn_ctrl_ble_conn)
    public void onClickedConnDev() {
        startActivity(new Intent(getContext(), CtQrScanActivity.class));
    }
}
