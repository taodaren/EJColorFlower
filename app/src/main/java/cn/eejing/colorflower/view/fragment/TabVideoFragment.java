package cn.eejing.colorflower.view.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import com.lzy.okgo.model.HttpParams;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.http.OkGoBuilder;
import cn.eejing.colorflower.model.request.VideoListBean;
import cn.eejing.colorflower.presenter.Callback;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.view.activity.MainActivity;
import cn.eejing.colorflower.view.adapter.TabVideoAdapter;
import cn.eejing.colorflower.view.base.BaseFragment;
import cn.jzvd.JZMediaManager;
import cn.jzvd.Jzvd;
import cn.jzvd.JzvdMgr;
import cn.jzvd.JzvdStd;

/**
 * 视频模块
 */

public class TabVideoFragment extends BaseFragment {

    @BindView(R.id.rv_tab_mall)    PullLoadMoreRecyclerView rvTabVideo;

    private static final String TAG = "TabVideoFragment";
    private List<VideoListBean.DataBean> mList;
    private TabVideoAdapter mAdapter;

    public static TabVideoFragment newInstance() {
        return new TabVideoFragment();
    }

    @Override
    public void initToolbar() {
        setToolbar(R.id.main_toolbar, R.string.video_name, View.VISIBLE);
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_tab_video;
    }

    @Override
    public void initData() {
        getDataWithVideoList();
    }

    @Override
    public void initView(View rootView) {
        mList = new ArrayList<>();
        initRecyclerView();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        // 这个方法可代替 onResume 和 onPause
        if (hidden) {
            JzvdStd.releaseAllVideos();
        } else {
            JzvdStd.goOnPlayOnResume();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Objects.requireNonNull(getActivity()).finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initRecyclerView() {
        // 设置布局
        rvTabVideo.setLinearLayout();
        // 绑定适配器
        mAdapter = new TabVideoAdapter(getContext(), mList);
        rvTabVideo.setAdapter(mAdapter);

//        // RecyclerView  划出列表开启小窗
//        rvTabVideo.getRecyclerView().addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
//            @Override
//            public void onChildViewAttachedToWindow(View view) {
//                Jzvd.onChildViewAttachedToWindow(view, R.id.detail_player);
//            }
//
//            @Override
//            public void onChildViewDetachedFromWindow(View view) {
//                Jzvd.onChildViewDetachedFromWindow(view);
//            }
//        });

        // RecyclerView 划出屏幕释放 JZ，同时也是不开启列表划出显示小窗
        rvTabVideo.getRecyclerView().addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(View view) {
            }

            @Override
            public void onChildViewDetachedFromWindow(View view) {
                Jzvd jzvd = view.findViewById(R.id.detail_player);
                if (jzvd != null && jzvd.jzDataSource.containsTheUrl(JZMediaManager.getCurrentUrl())) {
                    Jzvd currentJzvd = JzvdMgr.getCurrentJzvd();
                    if (currentJzvd != null && currentJzvd.currentScreen != Jzvd.SCREEN_WINDOW_FULLSCREEN) {
                        Jzvd.releaseAllVideos();
                    }
                }
            }
        });

        // 不需要上拉刷新
        rvTabVideo.setPushRefreshEnable(false);
        // 调用下拉刷新和加载更多
        rvTabVideo.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                getDataWithVideoList();
                // 刷新结束
                rvTabVideo.setPullLoadMoreCompleted();
            }

            @Override
            public void onLoadMore() {
            }
        });
        // 刷新结束
        rvTabVideo.setPullLoadMoreCompleted();
    }

    @SuppressWarnings("unchecked")
    private void getDataWithVideoList() {
        OkGoBuilder.getInstance().setToken(MainActivity.getAppCtrl().getToken());

        OkGoBuilder.getInstance().Builder(getActivity())
                .url(Urls.VIDEO_LIST)
                .method(OkGoBuilder.POST)
                .params(new HttpParams())
                .cls(VideoListBean.class)
                .callback(new Callback<VideoListBean>() {
                    @Override
                    public void onSuccess(VideoListBean response, int id) {
                        LogUtil.d(TAG, "视频列表 请求成功");

//                        // 获取本地 json 文件
//                        String json = LocalJsonResolutionUtils.getJson(Objects.requireNonNull(getActivity()), "video.json");
//                        // 转换为对象
//                        VideoListBean bean = LocalJsonResolutionUtils.JsonToObject(json, VideoListBean.class);
//                        mList = bean.getData();

                        mList = response.getData();
                        // 刷新数据
                        mAdapter.refreshList(mList);
                        rvTabVideo.setPullLoadMoreCompleted();
                    }

                    @Override
                    public void onError(Throwable e, int id) {
                    }
                }).build();
    }

}
