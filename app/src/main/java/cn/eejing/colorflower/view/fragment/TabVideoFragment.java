package cn.eejing.colorflower.view.fragment;

import android.view.View;

import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import butterknife.BindView;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.view.base.BaseFragment;

/**
 * 商城模块
 */

public class TabVideoFragment extends BaseFragment {

    private static final String TAG = "TabVideoFragment";

    @BindView(R.id.rv_tab_mall)    PullLoadMoreRecyclerView rvTabVideo;

//    private Gson mGson;
//    private List<VideoListBean.DataBean> mList;
//    private TabVideoAdapter mAdapter;

    public static TabVideoFragment newInstance() {
        return new TabVideoFragment();
    }

    public TabVideoFragment() {
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_tab_video;
    }

    @Override
    public void initView(View rootView) {
//        mGson = new Gson();
//        mList = new ArrayList<>();
//        initRecyclerView();
    }

    @Override
    public void initToolbar() {
        setToolbar(R.id.main_toolbar, R.string.video_name, View.VISIBLE);
    }


}
