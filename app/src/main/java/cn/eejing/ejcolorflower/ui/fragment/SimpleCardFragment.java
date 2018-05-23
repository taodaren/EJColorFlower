package cn.eejing.ejcolorflower.ui.fragment;

import android.annotation.SuppressLint;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.base.BaseFragment;

@SuppressLint("ValidFragment")
public class SimpleCardFragment extends BaseFragment {
    private String mTitle;

    @BindView(R.id.card_title_tv)
    TextView cardTitleTv;

    public static SimpleCardFragment getInstance(String title) {
        SimpleCardFragment sf = new SimpleCardFragment();
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