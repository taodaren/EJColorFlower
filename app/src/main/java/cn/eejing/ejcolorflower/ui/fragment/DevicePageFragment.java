package cn.eejing.ejcolorflower.ui.fragment;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.base.BaseFragment;

@SuppressLint("ValidFragment")
public class DevicePageFragment extends BaseFragment {
    private String mTitle;

    @BindView(R.id.tv_device_info)
    TextView cardTitleTv;

    public static DevicePageFragment getInstance(String title) {
        DevicePageFragment sf = new DevicePageFragment();
        sf.mTitle = title;
        return sf;
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_info_device;
    }

    @Override
    public void initView(View rootView) {
        cardTitleTv.setText(mTitle);
    }

}