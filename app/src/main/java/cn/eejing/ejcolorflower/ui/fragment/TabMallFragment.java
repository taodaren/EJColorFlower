package cn.eejing.ejcolorflower.ui.fragment;


import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.ui.base.BaseFragment;


/**
 * 商城模块
 *
 * @author taodaren
 * @date 2018/5/11
 */

public class TabMallFragment extends BaseFragment {

    public static TabMallFragment newInstance() {
        return new TabMallFragment();
    }

    public TabMallFragment() {
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_tab_mall;
    }

}
