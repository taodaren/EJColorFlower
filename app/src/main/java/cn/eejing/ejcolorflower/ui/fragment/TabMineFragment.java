package cn.eejing.ejcolorflower.ui.fragment;


import android.view.View;

import com.allen.library.SuperTextView;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.activity.MainActivity;
import cn.eejing.ejcolorflower.ui.activity.MiAboutActivity;
import cn.eejing.ejcolorflower.ui.activity.MiOpinionActivity;
import cn.eejing.ejcolorflower.ui.activity.MiOrderActivity;
import cn.eejing.ejcolorflower.ui.activity.MiSetActivity;
import cn.eejing.ejcolorflower.ui.base.BaseFragment;


/**
 * @创建者 Taodaren
 * @描述 我的模块
 */

public class TabMineFragment extends BaseFragment {

    @BindView(R.id.stv_mine_opinion)
    SuperTextView stvMineOpinion;
    @BindView(R.id.stv_mine_about)
    SuperTextView stvMineAbout;
    @BindView(R.id.stv_mine_set)
    SuperTextView stvMineSet;
    @BindView(R.id.stv_mine_order)
    SuperTextView stvMineOrder;

    public static TabMineFragment newInstance() {
        return new TabMineFragment();
    }

    public TabMineFragment() {
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_tab_mine;
    }

    @Override
    public void initView(View rootView) {
        stvOnClickListener();
    }

    private void stvOnClickListener() {
        stvMineOrder.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                ((MainActivity) getActivity()).jumpToActivity(MiOrderActivity.class);
            }
        });
        stvMineOpinion.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                ((MainActivity) getActivity()).jumpToActivity(MiOpinionActivity.class);
            }
        });
        stvMineAbout.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                ((MainActivity) getActivity()).jumpToActivity(MiAboutActivity.class);
            }
        });
        stvMineSet.setOnSuperTextViewClickListener(new SuperTextView.OnSuperTextViewClickListener() {
            @Override
            public void onClickListener(SuperTextView superTextView) {
                ((MainActivity) getActivity()).jumpToActivity(MiSetActivity.class);
            }
        });
    }

}
