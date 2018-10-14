package cn.eejing.ejcolorflower.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.device.BleDeviceProtocol;
import cn.eejing.ejcolorflower.device.Device;
import cn.eejing.ejcolorflower.model.lite.MasterCtrlModeEntity;
import cn.eejing.ejcolorflower.model.lite.MasterCtrlNumEntity;
import cn.eejing.ejcolorflower.model.lite.MasterCtrlSetEntity;
import cn.eejing.ejcolorflower.presenter.OnReceivePackage;
import cn.eejing.ejcolorflower.util.SelfDialogBase;
import cn.eejing.ejcolorflower.view.adapter.CtMasterSetAdapter;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.CLEAR_MATERIAL_MASTER;
import static cn.eejing.ejcolorflower.app.AppConstant.CTRL_DEV_NUM;

/**
 * 设置主控分组
 */

public class CtSetGroupActivity extends BaseActivity {
    private static final String TAG = "CtSetGroupActivity";

    @BindView(R.id.rv_master_set)        RecyclerView rvMasterSet;
    @BindView(R.id.tv_dev_num)           TextView     tvDevNum;
    @BindView(R.id.tv_start_dmx)         TextView     tvStartDmx;
    @BindView(R.id.sb_dev_num)           SeekBar      sbDevNum;
    @BindView(R.id.sb_start_dmx)         SeekBar      sbStartDmx;

    private long mDevId;
    private String mDevMac;
    private CtMasterSetAdapter mAdapter;
    private List<MasterCtrlModeEntity> mList;
    private SelfDialogBase mDialog;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_set_group;
    }

    @Override
    public void initView() {
        setToolbar("设置主控", View.VISIBLE, null, View.GONE);
        mDevId = MainActivity.getAppCtrl().getDevId();
        mDevMac = MainActivity.getAppCtrl().getDevMac();
        Log.i(TAG, "initView: " + mDevId + " " + mDevMac);
        mList = new ArrayList<>();

        initConfigDB();
        initRecyclerView();
    }

    @Override
    public void initListener() {
        sbDevNum.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 当拖动条的滑块位置发生改变时触发该方法,在这里直接使用参数 progress，即当前滑块代表的进度值
                tvDevNum.setText(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e(TAG, "开始滑动！");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e(TAG, "停止滑动！");
            }
        });

        sbStartDmx.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvStartDmx.setText(Integer.toString(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                Log.e(TAG, "开始滑动！");
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Log.e(TAG, "停止滑动！");
            }
        });
    }

    /** 初始化数据库信息 */
    private void initConfigDB() {
        List<MasterCtrlSetEntity> jetNumes = LitePal.where("devId = ?", String.valueOf(mDevId)).find(MasterCtrlSetEntity.class);
        List<MasterCtrlModeEntity> jetModes = LitePal.where("devId = ?", String.valueOf(mDevId)).find(MasterCtrlModeEntity.class);
        Log.i(TAG, "initConfigDB: " + jetModes.size());

        // 设备数量及起始 DMX
        if (jetNumes.size() != 0) {
            sbDevNum.setProgress(jetNumes.get(0).getDevNum());
            sbStartDmx.setProgress(jetNumes.get(0).getStartDmx());
            tvDevNum.setText(String.valueOf(sbDevNum.getProgress()));
            tvStartDmx.setText(String.valueOf(sbStartDmx.getProgress()));
        }

        // 喷射效果列表
        if (jetModes.size() > 0) {
            for (int i = 0; i < jetModes.size(); i++) {
                Log.i(TAG, "initConfigDB: " + jetModes.get(i).getType());
                mList.add(new MasterCtrlModeEntity(jetModes.get(i).getType(), jetModes.get(i).getMillis()));
            }
        }
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
                int position = (int) v.getTag();
                showDelDialog(position);
                return true;
            }
        });

        rvMasterSet.setAdapter(mAdapter);
    }

    private void showDelDialog(final int position) {
        mDialog = new SelfDialogBase(this);
        mDialog.setTitle("确定要删除");
        mDialog.setYesOnclickListener("确定", new SelfDialogBase.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                // 删除喷射效果
                LitePal.deleteAll(MasterCtrlModeEntity.class, "millis = ?", String.valueOf(mList.get(position).getMillis()));
                mList.remove(position);
                mAdapter.refreshList(mList);
                mDialog.dismiss();
            }
        });
        mDialog.setNoOnclickListener("取消", new SelfDialogBase.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    @OnClick({R.id.rl_add_effect, R.id.btn_clear_material, R.id.btn_save_master})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_add_effect:
                startActivityForResult(new Intent(this, CtAddEffectActivity.class), 1);
                break;
            case R.id.btn_clear_material:
                clearMaterialMaster();
                break;
            case R.id.btn_save_master:
                saveMasterConfig();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    setMasterCtrl(data.getStringExtra("jet_mode"), System.currentTimeMillis());
                }
                break;
            default:
        }
    }

    private void setMasterCtrl(String type, long millis) {
        // 保存主控喷射效果
        MasterCtrlModeEntity entity = new MasterCtrlModeEntity(type);
        entity.setDevId(String.valueOf(mDevId));
        entity.setType(type);
        entity.setMillis(millis);
        entity.save();
        // 添加一条数据到集合，并刷新
        mList.add(new MasterCtrlModeEntity(type, millis));
        mAdapter.refreshList(mList);
    }

    /** 点击清料 */
    private void clearMaterialMaster() {
        if (sbDevNum.getProgress() == 0) {
            Toast.makeText(this, "设备数量不能为 0", Toast.LENGTH_SHORT).show();
        } else if (sbStartDmx.getProgress() == 0) {
            Toast.makeText(this, "起始DMX不能为 0", Toast.LENGTH_SHORT).show();
        } else {
            cmdClearMaterial();
        }
    }

    /** 发送清料命令 */
    private void cmdClearMaterial() {
        byte[] byHighs = new byte[CTRL_DEV_NUM];
        for (int i = 0; i < sbDevNum.getProgress(); i++) {
            int high = 20;
            byHighs[i] = (byte) high;
        }
        Device device = MainActivity.getAppCtrl().getDevice(mDevMac);
        byte[] pkgClearMaterial = BleDeviceProtocol.pkgClearMaterial(mDevId, CLEAR_MATERIAL_MASTER, sbStartDmx.getProgress(), sbDevNum.getProgress(), byHighs);
        MainActivity.getAppCtrl().sendCommand(device, pkgClearMaterial, new OnReceivePackage() {
            @Override
            public void ack(@NonNull byte[] pkg) {

            }

            @Override
            public void timeout() {
            }
        });
    }

    /** 点击保存 */
    private void saveMasterConfig() {
        List<MasterCtrlSetEntity> groupIdList = LitePal
                .where("devId=?", String.valueOf(mDevId))
                .find(MasterCtrlSetEntity.class);
        MasterCtrlSetEntity entity = new MasterCtrlSetEntity();
        if (groupIdList.size() == 0) {
            // 增
            setEntity(entity);
            entity.save();
        } else {
            // 改
            setEntity(entity);
            entity.updateAll("devId=?", String.valueOf(mDevId));
        }
        setResult(RESULT_OK, new Intent());
        finish();
    }

    private void setEntity(MasterCtrlSetEntity entity) {
        Log.i(TAG, "setEntity: " + sbDevNum.getProgress() + " " + sbStartDmx.getProgress());
        entity.setDevId(String.valueOf(mDevId));
        entity.setDevNum(sbDevNum.getProgress());
        entity.setStartDmx(sbStartDmx.getProgress());
        entity.setJetMode(mList.get(0).getType());
    }

}
