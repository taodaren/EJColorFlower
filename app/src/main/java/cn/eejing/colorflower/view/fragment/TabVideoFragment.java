package cn.eejing.colorflower.view.fragment;

import android.os.Build;
import android.view.View;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.request.VideoListBean;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.LocalJsonResolutionUtils;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.view.activity.MainActivity;
import cn.eejing.colorflower.view.adapter.TabVideoAdapter;
import cn.eejing.colorflower.view.base.BaseFragment;

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

    private void initRecyclerView() {
        // 设置布局
        rvTabVideo.setLinearLayout();
        // 绑定适配器
        mAdapter = new TabVideoAdapter(getContext(), mList);
        rvTabVideo.setAdapter(mAdapter);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rvTabVideo.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {

            });
        }

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

    private void getDataWithVideoList() {
        OkGo.<String>get(Urls.VIDEO_LIST)
                .tag(this)
                .params("token", MainActivity.getAppCtrl().getToken())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        LogUtil.d(TAG, "视频列表 请求成功: " + body);

                        String json = LocalJsonResolutionUtils.getJson(Objects.requireNonNull(getActivity()), "video.json");
                        // 转换为对象
                        VideoListBean bean = LocalJsonResolutionUtils.JsonToObject(json, VideoListBean.class);

//                        VideoListBean bean = new Gson().fromJson(json, VideoListBean.class);
                        mList = bean.getData();
                        mAdapter.refreshList(mList);
                        rvTabVideo.setPullLoadMoreCompleted();
                    }
                });
    }

}
