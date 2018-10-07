package cn.eejing.ejcolorflower.view.activity;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.view.adapter.MasterListAdapter;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

public class CtMasterModeActivity extends BaseActivity {
    private static final String TAG = "CtMasterModeActivity";

    @BindView(R.id.rv_master_list)          PullLoadMoreRecyclerView rvMasterList;
    @BindView(R.id.btn_master_start)        Button btnMasterStart;

    private MasterListAdapter mAdapter;
    private Long mDeviceId;
    private List<String> mList;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ct_master_mode;
    }

    @Override
    public void initView() {
        setToolbar("主控", View.VISIBLE, null, View.GONE);
        mDeviceId = getIntent().getLongExtra("device_id", 0);
        Log.i(TAG, "initView: " + mDeviceId);

        mList = new ArrayList<>();
        mList.add("分组1");

        initRecyclerView();
    }

    private void initRecyclerView() {
        // 设置布局
        rvMasterList.setLinearLayout();
        // 绑定适配器
        mAdapter = new MasterListAdapter(this, mList);
//        mAdapter.setHasStableIds(true);
        rvMasterList.setAdapter(mAdapter);

        // 不需要上拉刷新
        rvMasterList.setPushRefreshEnable(false);
        rvMasterList.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
            }
        });
        // 刷新结束
        rvMasterList.setPullLoadMoreCompleted();
    }
}
