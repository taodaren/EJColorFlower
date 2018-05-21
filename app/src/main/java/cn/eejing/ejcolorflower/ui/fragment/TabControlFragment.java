package cn.eejing.ejcolorflower.ui.fragment;

import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.eejing.ejcolorflower.LoginSession;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.app.Urls;
import cn.eejing.ejcolorflower.model.request.DeviceGroupListBean;
import cn.eejing.ejcolorflower.ui.adapter.TabControlAdapter;
import cn.eejing.ejcolorflower.ui.base.BaseFragment;
import cn.eejing.ejcolorflower.util.Settings;
import cn.eejing.ejcolorflower.util.SimpleItemTouchHelperCallback;

/**
 * @创建者 Taodaren
 * @描述 控制模块
 */

public class TabControlFragment extends BaseFragment {

    @BindView(R.id.rv_tab_control)
    PullLoadMoreRecyclerView rvTabControl;

    private Gson mGson;
    private List<DeviceGroupListBean.DataBean> mList;
    private TabControlAdapter mAdapter;
    private String mMemberId;

    public static TabControlFragment newInstance() {
        return new TabControlFragment();
    }

    public TabControlFragment() {
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_tab_control;
    }

    @Override
    public void initView(View rootView) {
        mGson = new Gson();
        mList = new ArrayList<>();

        LoginSession session = Settings.getLoginSessionInfo(getActivity());
        mMemberId = String.valueOf(session.getMember_id());

        initRecyclerView();
    }

    @Override
    public void initToolbar() {
        setToolbar(R.id.main_toolbar, R.string.control_name, View.VISIBLE);
    }

    @Override
    public void initData() {
        getDataWithDeviceGroupList();
    }

    private void getDataWithDeviceGroupList() {
        OkGo.<String>post(Urls.GET_DEVICE_GROUP_LIST)
                .tag(this)
                .params("member_id", mMemberId)
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 String body = response.body();
                                 Log.e(AppConstant.TAG, "get device group list request succeeded--->" + body);

                                 /* 请求成功后，开始 json 解析 */
                                 // Gson 直接解析成对象
                                 DeviceGroupListBean bean = mGson.fromJson(body, DeviceGroupListBean.class);
                                 // 对象中拿到集合
                                 mList = bean.getData();
                                 Log.e(AppConstant.TAG, "onSuccess: get device group list--->" + mList);
                                 // 刷新数据
                                 mAdapter.refreshList(mList);
                                 // 刷新结束
                                 rvTabControl.setPullLoadMoreCompleted();

                             }

                             @Override
                             public void onError(Response<String> response) {
                                 super.onError(response);
                             }
                         }
                );

    }

    private void initRecyclerView() {
        // 设置布局
        rvTabControl.setLinearLayout();
        // 绑定适配器
        mAdapter = new TabControlAdapter(getContext(), mList);
        rvTabControl.setAdapter(mAdapter);

        // 先实例化 Callback
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mAdapter);
        // 用 Callback 构造 ItemTouchHelper
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        // 调用 ItemTouchHelper 的 attachToRecyclerView 方法建立联系
        touchHelper.attachToRecyclerView(rvTabControl.getRecyclerView());

        // 不需要上拉刷新
        rvTabControl.setPushRefreshEnable(false);
        rvTabControl.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                getDataWithDeviceGroupList();
            }

            @Override
            public void onLoadMore() {

            }
        });
        // 刷新结束
        rvTabControl.setPullLoadMoreCompleted();
    }

}
