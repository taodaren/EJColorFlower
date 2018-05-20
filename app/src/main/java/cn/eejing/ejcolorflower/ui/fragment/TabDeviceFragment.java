package cn.eejing.ejcolorflower.ui.fragment;

import android.view.View;
import android.widget.ImageView;

import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.MainActivity;
import cn.eejing.ejcolorflower.ui.activity.QRCodeActivity;
import cn.eejing.ejcolorflower.ui.base.BaseFragment;

/**
 * @创建者 Taodaren
 * @描述 设备模块
 */

public class TabDeviceFragment extends BaseFragment  {


    @BindView(R.id.rv_tab_control)
    PullLoadMoreRecyclerView rvTabControl;

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
    }

}
