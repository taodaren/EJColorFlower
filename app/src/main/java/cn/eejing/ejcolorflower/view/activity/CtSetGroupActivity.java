package cn.eejing.ejcolorflower.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
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
import cn.eejing.ejcolorflower.model.lite.MasterCtrlModeEntity;
import cn.eejing.ejcolorflower.model.lite.MasterCtrlSetEntity;
import cn.eejing.ejcolorflower.model.request.MasterGroupListBean;
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

    @BindView(R.id.ll_no_jet_mode)        LinearLayout llNoJetModes;
    @BindView(R.id.rv_master_set)         RecyclerView rvMasterSet;
    @BindView(R.id.tv_dev_num)            TextView     tvDevNum;
    @BindView(R.id.tv_start_dmx)          TextView     tvStartDmx;
    @BindView(R.id.sb_dev_num)            SeekBar      sbDevNum;
    @BindView(R.id.sb_start_dmx)          SeekBar      sbStartDmx;
    @BindView(R.id.tv_jet_time)           TextView     tvJetTime;

    private long mDevId;
    private String mDevMac;
    private int mPosition;
    private String mGroupName;
    private int mDevNum, mStartDmx;
    private int mJetTime;
    private CtMasterSetAdapter mAdapter;
    private List<MasterCtrlModeEntity> mList;
    private SelfDialogBase mDialog;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_set_group;
    }

    @Override
    public void initView() {
        mPosition = getIntent().getIntExtra("group_position", 0);
        mGroupName = getIntent().getStringExtra("group_name");
        setToolbar(mGroupName, View.VISIBLE, null, View.GONE);
        mDevNum = getIntent().getIntExtra("group_dev_num", 0);
        mStartDmx = getIntent().getIntExtra("group_start_dmx", 0);

//        mList = LitePal.where("groupName = ?", String.valueOf(mGroupName)).find(MasterCtrlModeEntity.class);
//        if (mList == null) {
//            mList = new ArrayList<>();
//        }

        mDevId = MainActivity.getAppCtrl().getDevId();
        mDevMac = MainActivity.getAppCtrl().getDevMac();

        showData();
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
            /** 开始滑动 **/
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            /** 停止滑动 **/
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        sbStartDmx.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvStartDmx.setText(Integer.toString(progress * 2));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void showData() {
        // 设备数量
        sbDevNum.setProgress(mDevNum);
        tvDevNum.setText(String.valueOf(sbDevNum.getProgress()));
        // 起始 DMX
        sbStartDmx.setProgress(mStartDmx / 2);
        tvStartDmx.setText(String.valueOf(mStartDmx));

        // 喷射效果列表
        refreshData();
    }

    /** 刷新数据 */
    private void refreshData() {
        if (mList != null) {
            mList.clear();
        }
        initDatabase();
    }

    private void initDatabase() {
        mList = LitePal.where("groupName = ?", mGroupName).find(MasterCtrlModeEntity.class);
        if (mList.size() != 0) {
            rvMasterSet.setVisibility(View.VISIBLE);
            llNoJetModes.setVisibility(View.GONE);

            initRecyclerView();
        } else {
            rvMasterSet.setVisibility(View.GONE);
            llNoJetModes.setVisibility(View.VISIBLE);
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
                refreshData();
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

    @OnClick({R.id.rl_add_effect, R.id.btn_clear_material, R.id.btn_save_master, R.id.tv_dev_num_subtract, R.id.tv_dev_num_add, R.id.tv_start_dmx_subtract, R.id.tv_start_dmx_add})
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
            case R.id.tv_dev_num_subtract:
                sbDevNum.setProgress(sbDevNum.getProgress() - 1);
                break;
            case R.id.tv_dev_num_add:
                sbDevNum.setProgress(sbDevNum.getProgress() + 1);
                break;
            case R.id.tv_start_dmx_subtract:
                sbStartDmx.setProgress(sbStartDmx.getProgress() - 1);
                break;
            case R.id.tv_start_dmx_add:
                sbStartDmx.setProgress(sbStartDmx.getProgress() + 1);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    // 选择喷射效果后保存喷射效果，并刷新喷射效果列表
                    setMasterCtrl(data.getStringExtra("jet_mode"), System.currentTimeMillis());
                }
                break;
            default:
        }
    }

    private void setMasterCtrl(String type, long millis) {
        Log.i(TAG, "setMasterCtrl");
        // 保存主控喷射效果
        MasterCtrlModeEntity entity = new MasterCtrlModeEntity();
        entity.setDevId(String.valueOf(mDevId));
        entity.setGroupName(mGroupName);
        entity.setType(type);
        entity.setMillis(millis);
        entity.save();
        // 添加一条数据到集合，并刷新
        refreshData();
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
        MainActivity.getAppCtrl().sendCommand(
                MainActivity.getAppCtrl().getDevice(mDevMac),
                BleDeviceProtocol.pkgClearMaterial(mDevId, CLEAR_MATERIAL_MASTER, sbStartDmx.getProgress(), sbDevNum.getProgress(), byHighs),
                new OnReceivePackage() {
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
        List<MasterGroupListBean> groupList = LitePal
                .where("groupName = ?", mGroupName)
                .find(MasterGroupListBean.class);
        groupList.get(0).setDevNum(sbDevNum.getProgress());
        groupList.get(0).setStartDmx(Integer.parseInt(tvStartDmx.getText().toString()));
        groupList.get(0).setJetModes(mList);
        groupList.get(0).updateAll("groupName = ?", mGroupName);
        finish();
    }

}
