package cn.eejing.ejcolorflower.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.model.lite.MasterCtrlSetEntity;
import cn.eejing.ejcolorflower.model.request.MasterGroupListBean;
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
    private long mDeviceId;
    private List<MasterGroupListBean> mList;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ct_master_mode;
    }

    @Override
    public void initView() {
        setToolbar("主控", View.VISIBLE, null, View.GONE);
        mDeviceId = getIntent().getLongExtra("device_id", 0);
        mList = new ArrayList<>();

        initDatabase();
        initRecyclerView();
    }

    private void initDatabase() {
        List<MasterCtrlSetEntity> setGroupList = LitePal.where("devId = ?", String.valueOf(mDeviceId)).find(MasterCtrlSetEntity.class);
        Log.i(TAG, "list size: " + setGroupList.size());
        if (setGroupList.size() == 0) {
            mList.add(new MasterGroupListBean("分组名称", 0, 0, "喷射效果"));
            for (int i = 0; i < 9; i++) {
                mList.add(new MasterGroupListBean("分组功能敬请期待...", 0, 0, "喷射效果"));
            }
        } else {
            Log.d(TAG, "info: " + setGroupList.get(0).getDevNum() + " " + setGroupList.get(0).getStartDmx() + " " + setGroupList.get(0).getJetMode());

            mList.add(new MasterGroupListBean("分组名称", setGroupList.get(0).getDevNum(), setGroupList.get(0).getStartDmx(), setGroupList.get(0).getJetMode()));
            for (int i = 0; i < 9; i++) {
                mList.add(new MasterGroupListBean("分组功能敬请期待...", 0, 0, "喷射效果"));
            }
        }
    }

    private void initRecyclerView() {
        // 设置布局
        rvMasterList.setLinearLayout();
        // 绑定适配器
        mAdapter = new MasterListAdapter(this, mList, mDeviceId);
//        mAdapter.setHasStableIds(true);
        // 监听设置主控按钮
        mAdapter.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(CtMasterModeActivity.this, CtSetGroupActivity.class), 1);
            }
        });
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Log.i(TAG, "此时可以刷新列表了");
                    mList.clear();
                    initDatabase();
                }
                break;
            default:
                break;
        }
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

    /** 点击开始或停止 */
    private void clickStartStop() {
//        if (etDevNum.length() == 0) {
//            Toast.makeText(this, "请您设置设备数量", Toast.LENGTH_SHORT).show();
//        } else if (etStarDmx.length() == 0) {
//            Toast.makeText(this, "请您设置起始 DMX", Toast.LENGTH_SHORT).show();
//        } else if (mList.size() == 0) {
//            Toast.makeText(this, "请添加喷射效果", Toast.LENGTH_SHORT).show();
//        } else {
//            if (isStar) {
//                stopJet();
//            } else {
//                starJet();
//            }
//        }
    }

}
