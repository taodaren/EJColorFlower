package cn.eejing.ejcolorflower.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.model.lite.MasterCtrlModeEntity;
import cn.eejing.ejcolorflower.view.adapter.CtMasterSetAdapter;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

/**
 * 设置主控分组
 */

public class CtSetGroupActivity extends BaseActivity {
    private static final String TAG = "CtSetGroupActivity";

    @BindView(R.id.rv_master_set)        RecyclerView rvMasterSet;

    private Long mDeviceId;
    private CtMasterSetAdapter mAdapter;
    private List<MasterCtrlModeEntity> mList;
    private boolean isStar;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_set_group;
    }

    @Override
    public void initView() {
        setToolbar("设置主控", View.VISIBLE, null, View.GONE);
        mDeviceId = getIntent().getLongExtra("device_id", 0);
        Log.i(TAG, "initView: " + mDeviceId);
        mList = new ArrayList<>();

        initRecyclerView();
    }

    private void initRecyclerView() {
        // 解决滑动冲突
        rvMasterSet.setNestedScrollingEnabled(false);

        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvMasterSet.setLayoutManager(manager);
        // 绑定适配器
        mAdapter = new CtMasterSetAdapter(this, mList);
        // 监听长按点击事件
        mAdapter.setLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                if (!isStar) {
//                    int position = (int) v.getTag();
//                    showDelDialog(position);
//                }
                return true;
            }
        });

        rvMasterSet.setAdapter(mAdapter);
    }

    @OnClick(R.id.rl_add_effect)
    public void onViewClicked() {
        jumpToActivity(new Intent(this, CtAddEffectActivity.class));
    }
}
