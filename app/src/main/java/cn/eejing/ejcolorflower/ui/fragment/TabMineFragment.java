package cn.eejing.ejcolorflower.ui.fragment;


import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.base.BaseFragment;


/**
 * 我的模块
 *
 * @author taodaren
 * @date 2018/5/11
 */

public class TabMineFragment extends BaseFragment {

    public static TabMineFragment newInstance() {
        return new TabMineFragment();
    }

    public TabMineFragment() {
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_tab_mine;
    }

}
