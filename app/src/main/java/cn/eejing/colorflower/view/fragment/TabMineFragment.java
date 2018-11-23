package cn.eejing.colorflower.view.fragment;

import android.view.View;

import com.allen.library.SuperTextView;

import java.util.Objects;

import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.activity.MainActivity;
import cn.eejing.colorflower.view.activity.MiAboutActivity;
import cn.eejing.colorflower.view.activity.MiOpinionActivity;
import cn.eejing.colorflower.view.activity.MiOrderActivity;
import cn.eejing.colorflower.view.activity.MiSetActivity;
import cn.eejing.colorflower.view.activity.MiUpgradeVipActivity;
import cn.eejing.colorflower.view.base.BaseFragment;

import static cn.eejing.colorflower.app.BaseApplication.getVersionName;

/**
 * 我的模块
 */

public class TabMineFragment extends BaseFragment {
    public static TabMineFragment newInstance() {
        return new TabMineFragment();
    }

    @Override
    public void initToolbar() {
        setToolbar(R.id.main_toolbar, R.string.mine_name, View.VISIBLE);
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_tab_mine;
    }

    @Override
    public void initView(View rootView) {
        String versionName = getVersionName(Objects.requireNonNull(getContext()));
        ((SuperTextView) rootView.findViewById(R.id.stv_mine_version)).setRightString("V " + versionName + " 版本");
    }

    @OnClick({R.id.btn_mine_upgrade, R.id.stv_mine_order, R.id.stv_mine_opinion, R.id.stv_mine_about, R.id.stv_mine_set})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_mine_upgrade:
                ((MainActivity) Objects.requireNonNull(getActivity())).jumpToActivity(MiUpgradeVipActivity.class);
                break;
            case R.id.stv_mine_order:
                ((MainActivity) Objects.requireNonNull(getActivity())).jumpToActivity(MiOrderActivity.class);
                break;
            case R.id.stv_mine_opinion:
                ((MainActivity) Objects.requireNonNull(getActivity())).jumpToActivity(MiOpinionActivity.class);
                break;
            case R.id.stv_mine_about:
                ((MainActivity) Objects.requireNonNull(getActivity())).jumpToActivity(MiAboutActivity.class);
                break;
            case R.id.stv_mine_set:
                ((MainActivity) Objects.requireNonNull(getActivity())).jumpToActivity(MiSetActivity.class);
                break;
        }
    }
}
