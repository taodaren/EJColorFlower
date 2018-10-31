package cn.eejing.ejcolorflower.view.fragment;

import android.view.View;

import com.allen.library.SuperTextView;

import java.util.Objects;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.view.activity.MainActivity;
import cn.eejing.ejcolorflower.view.activity.MiAboutActivity;
import cn.eejing.ejcolorflower.view.activity.MiOpinionActivity;
import cn.eejing.ejcolorflower.view.activity.MiOrderActivity;
import cn.eejing.ejcolorflower.view.activity.MiSetActivity;
import cn.eejing.ejcolorflower.view.base.BaseFragment;

/**
 * 我的模块
 */

public class TabMineFragment extends BaseFragment {

    @BindView(R.id.stv_mine_opinion)        SuperTextView stvMineOpinion;
    @BindView(R.id.stv_mine_about)          SuperTextView stvMineAbout;
    @BindView(R.id.stv_mine_set)            SuperTextView stvMineSet;
    @BindView(R.id.stv_mine_order)          SuperTextView stvMineOrder;

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
    public void initListener() {
        stvMineOrder.setOnSuperTextViewClickListener(superTextView -> ((MainActivity) Objects.requireNonNull(getActivity())).jumpToActivity(MiOrderActivity.class));

        stvMineOpinion.setOnSuperTextViewClickListener(superTextView -> ((MainActivity) Objects.requireNonNull(getActivity())).jumpToActivity(MiOpinionActivity.class));

        stvMineAbout.setOnSuperTextViewClickListener(superTextView -> ((MainActivity) Objects.requireNonNull(getActivity())).jumpToActivity(MiAboutActivity.class));

        stvMineSet.setOnSuperTextViewClickListener(superTextView -> ((MainActivity) Objects.requireNonNull(getActivity())).jumpToActivity(MiSetActivity.class));
    }

    @OnClick(R.id.btn_mine_upgrade)
    public void onViewClicked() {
        // 升级为黄金
    }
}
