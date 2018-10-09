package cn.eejing.ejcolorflower.view.activity;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.presenter.IShowListener;
import cn.eejing.ejcolorflower.util.FabScrollListener;
import cn.eejing.ejcolorflower.view.adapter.MasterListAdapter;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

public class CtMasterModeActivity extends BaseActivity implements IShowListener {
    private static final String TAG = "CtMasterModeActivity";

    @BindView(R.id.rv_master_list)          PullLoadMoreRecyclerView rvMasterList;
    @BindView(R.id.btn_master_start)        Button btnMasterStart;
    @BindView(R.id.rl_hide_dialog)          RelativeLayout hideDialog;
    @BindView(R.id.rl_show_dialog)          RelativeLayout showDialog;

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

        mList = new ArrayList<>();
        mList.add("分组名称");
        for (int i = 0; i < 9; i++) {
            mList.add("分组功能敬请期待...");
        }

        initRecyclerView();
    }

    private void initRecyclerView() {
        // 设置布局
        rvMasterList.setLinearLayout();
        // 绑定适配器
        mAdapter = new MasterListAdapter(this, mList, mDeviceId);
//        mAdapter.setHasStableIds(true);
        rvMasterList.setAdapter(mAdapter);

        rvMasterList.getRecyclerView().addOnScrollListener(new FabScrollListener(this));

        // 不需要上拉刷新
        rvMasterList.setPushRefreshEnable(false);
        rvMasterList.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                // 刷新结束
                rvMasterList.setPullLoadMoreCompleted();
            }

            @Override
            public void onLoadMore() {
            }
        });
    }

    @Override
    public void setHideListener() {
        hideStartDialog();
    }

    @Override
    public void setShowListener() {
        showStartDialog();
    }

    private void showStartDialog() {
        showDialog.setVisibility(View.GONE);
        hideDialog.animate().translationY(0)
                .setInterpolator(new DecelerateInterpolator(3));
    }

    private void hideStartDialog() {
        showDialog.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) hideDialog.getLayoutParams();
        hideDialog.animate().translationY(hideDialog.getHeight() + layoutParams.bottomMargin)
                .setInterpolator(new AccelerateInterpolator(3));
    }

    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Toast.makeText(CtMasterModeActivity.this, "任务执行完毕", Toast.LENGTH_SHORT).show();
            showStartDialog();
        }
    };

    @OnClick({R.id.btn_master_start, R.id.img_start_hide, R.id.rl_show_dialog})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_master_start:
                Toast.makeText(this, "开始执行任务", Toast.LENGTH_SHORT).show();
                hideStartDialog();
                mHandler.sendEmptyMessageDelayed(1, 3000);

                break;
            case R.id.img_start_hide:
                hideStartDialog();
                break;
            case R.id.rl_show_dialog:
                showStartDialog();
                break;
        }
    }

}
