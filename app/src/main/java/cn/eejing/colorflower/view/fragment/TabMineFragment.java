package cn.eejing.colorflower.view.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allen.library.SuperTextView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.util.MySettings;
import cn.eejing.colorflower.view.activity.MainActivity;
import cn.eejing.colorflower.view.activity.MiAboutActivity;
import cn.eejing.colorflower.view.activity.MiOpinionActivity;
import cn.eejing.colorflower.view.activity.MiOrderActivity;
import cn.eejing.colorflower.view.activity.MiSetActivity;
import cn.eejing.colorflower.view.activity.MiUpgradeVipActivity;
import cn.eejing.colorflower.view.base.BaseFragment;

import static cn.eejing.colorflower.app.AppConstant.LEVEL_GENERAL_USER;
import static cn.eejing.colorflower.app.AppConstant.LEVEL_VIP_USER;
import static cn.eejing.colorflower.app.AppConstant.LEVEL_VVIP_USER;
import static cn.eejing.colorflower.app.BaseApplication.getUserLv;
import static cn.eejing.colorflower.app.BaseApplication.getVersionName;

/**
 * 我的模块
 */

public class TabMineFragment extends BaseFragment {
    @BindView(R.id.layout_user_info)        RelativeLayout layoutUserInfo;
    @BindView(R.id.btn_mine_upgrade)        Button         btnUpgrade;

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

    @SuppressLint("SetTextI18n")
    @Override
    public void initView(View rootView) {
        String userLv = getUserLv(MySettings.getLoginSessionInfo(Objects.requireNonNull(getContext())).getUsername());
        switch (userLv) {
            case LEVEL_GENERAL_USER:
                layoutUserInfo.setBackground(getContext().getResources().getDrawable(R.drawable.ic_user_general));
                btnUpgrade.setVisibility(View.VISIBLE);
                break;
            case LEVEL_VIP_USER:
                layoutUserInfo.setBackground(getContext().getResources().getDrawable(R.drawable.ic_user_vip));
                btnUpgrade.setVisibility(View.GONE);
                break;
            case LEVEL_VVIP_USER:
                layoutUserInfo.setBackground(getContext().getResources().getDrawable(R.drawable.ic_user_vvip));
                btnUpgrade.setVisibility(View.GONE);
                break;
        }
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
