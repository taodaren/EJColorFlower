package cn.eejing.ejcolorflower.view.fragment;

import android.view.View;

import com.allen.library.SuperTextView;

import butterknife.BindView;
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
