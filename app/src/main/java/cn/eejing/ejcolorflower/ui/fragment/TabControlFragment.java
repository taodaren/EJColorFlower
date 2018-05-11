package cn.eejing.ejcolorflower.ui.fragment;


import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.base.BaseFragment;


/**
 * 控制模块
 *
 * @author taodaren
 * @date 2018/5/11
 */

public class TabControlFragment extends BaseFragment {

    public static TabControlFragment newInstance() {
        return new TabControlFragment();
    }

    public TabControlFragment() {
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_tab_control;
    }

}
