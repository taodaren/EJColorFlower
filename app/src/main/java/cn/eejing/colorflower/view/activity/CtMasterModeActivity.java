package cn.eejing.colorflower.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.device.Device;
import cn.eejing.colorflower.model.event.DevConnEvent;
import cn.eejing.colorflower.model.lite.JetModeConfigLite;
import cn.eejing.colorflower.model.lite.MasterGroupLite;
import cn.eejing.colorflower.model.manager.MgrTogetherJet;
import cn.eejing.colorflower.presenter.IShowListener;
import cn.eejing.colorflower.util.BleDevProtocol;
import cn.eejing.colorflower.util.FabScrollListener;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.adapter.MasterListAdapter;
import cn.eejing.colorflower.view.base.BaseActivity;
import cn.eejing.colorflower.view.customize.SelfDialog;
import cn.eejing.colorflower.view.customize.SelfDialogBase;

import static cn.eejing.colorflower.app.AppConstant.CLEAR_MATERIAL_MASTER;
import static cn.eejing.colorflower.app.AppConstant.CONFIG_TOGETHER;
import static cn.eejing.colorflower.app.AppConstant.CTRL_DEV_NUM;
import static cn.eejing.colorflower.app.AppConstant.DEVICE_CONNECT_NO;
import static cn.eejing.colorflower.app.AppConstant.DEVICE_CONNECT_YES;
import static cn.eejing.colorflower.app.AppConstant.HANDLE_BLE_CONN;
import static cn.eejing.colorflower.app.AppConstant.HANDLE_BLE_DISCONN;
import static cn.eejing.colorflower.app.AppConstant.INIT_ZERO;

/**
 * 多台控制
 */

public class CtMasterModeActivity extends BaseActivity implements IShowListener {

    @BindView(R.id.img_ble_toolbar)     ImageView                imgBleToolbar;
    @BindView(R.id.img_add_toolbar)     ImageView                imgAddGroup;
    @BindView(R.id.rv_master_list)      PullLoadMoreRecyclerView rvMasterList;
    @BindView(R.id.btn_master_start)    Button                   btnMasterStart;
    @BindView(R.id.rl_hide_dialog)      RelativeLayout           hideDialog;
    @BindView(R.id.rl_show_dialog)      RelativeLayout           showDialog;

    private static final String   TAG           =         "CtMasterModeActivity";
    private static final String   JET           =         "主控0.1秒";
    private static final int      MSG_MST_JET   = 1;      // 主控 0.1s 一次
    private static final int      MSG_ZERO_FIVE = 2;      // 齐喷 5 次

    private SelfDialog            mDialogCrt;
    private SelfDialogBase        mDialogDel;
    private MasterListAdapter     mAdapter;
    private List<MasterGroupLite> mListMstGroup;          // 分组信息列表集合【全部】
    private List<MasterGroupLite> mListJetting;           // 分组信息列表集合【正在喷射过程中】

    private Device mDevice;
    private long mDevId;
    private int mFlagFive;
    private boolean isStarJet;                            // 是否开始喷射
    private int[] mBeginId;                               // 喷射效果数据 每组需要放置在总喷射数据的开始位置
    private int mDevNum;                                  // 真实设备数量
    private int mStartDmx;                                // 真实起始DMX
    private int mEndDmx;                                  // 真实结束DMX
    private int mIsIncludeMst;                            // 是否包含主控 1-包含 0-不包含

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ct_master_mode;
    }

    @Override
    public void initView() {
        EventBus.getDefault().register(this);
        setToolbar("多台控制", View.VISIBLE, null, View.GONE);
        mDevId = MainActivity.getAppCtrl().getDevId();
        mDevice = MainActivity.getAppCtrl().getDevice(MainActivity.getAppCtrl().getDevMac());
    }

    @Override
    public void setToolbar(String title, int titleVisibility, String menu, int menuVisibility) {
        super.setToolbar(title, titleVisibility, menu, menuVisibility);
        imgBleToolbar.setVisibility(View.VISIBLE);
        imgBleToolbar.setImageDrawable(getResources().getDrawable(R.drawable.ic_ble_conn));
        imgAddGroup.setVisibility(View.VISIBLE);
        imgAddGroup.setImageDrawable(getResources().getDrawable(R.drawable.ic_toolbar_add));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 刷新数据
        refreshData();
    }

    private void refreshData() {
        if (mListMstGroup != null) {
            mListMstGroup.clear();
        }
        initDatabase();
    }

    private void initDatabase() {
        mListMstGroup = LitePal.where("devId = ?", String.valueOf(mDevId)).find(MasterGroupLite.class);

        for (int i = 0; i < mListMstGroup.size(); i++) {
            long mGroupIdMillis = mListMstGroup.get(i).getGroupIdMillis();
            List<JetModeConfigLite> listJetModes =
                    LitePal.where("groupIdMillis = ?", String.valueOf(mGroupIdMillis)).find(JetModeConfigLite.class);
            mListMstGroup.get(i).setJetModes(listJetModes);
        }

        if (mListMstGroup.size() > 0) {
            initRecyclerView();
        }
    }

    private void initRecyclerView() {
        rvMasterList.setLinearLayout();
        mAdapter = new MasterListAdapter(this, mListMstGroup);
        mAdapter.setHasStableIds(true);
        // 监听长按点击分组
        mAdapter.setLongClickListener(v -> {
            int position = (int) v.getTag();
            showDialogDel(position);
            return true;
        });
        // 监听设置主控按钮
        mAdapter.setClickSetMaster(v -> {
            int position = (int) v.getTag();
            for (int i = 0; i < mListMstGroup.size(); i++) {
                if (i == position) {
                    Intent intent = new Intent(CtMasterModeActivity.this, CtSetGroupActivity.class);
                    intent.putExtra("group_position", position);
                    jumpToActivity(intent);
                }
            }
        });
        // 监听是否选中分组按钮
        mAdapter.setClickIsSelectedGroup(v -> {
            int position = (int) v.getTag();
            switch (mListMstGroup.get(position).getIsSelectedGroup()) {
                case 1:
                    // 若是选中，点击变为未选中
                    refreshGroupBySelectedGroup(mListMstGroup, position, 2);
                    break;
                case 2:
                    // 若是未选中，点击变为选中
                    refreshGroupBySelectedGroup(mListMstGroup, position, 1);
                    break;
            }
        });
        // 监听是否选中包含主控按钮
        mAdapter.setClickIsSelectedMaster(v -> {
            int position = (int) v.getTag();
            switch (mListMstGroup.get(position).getIsSelectedMaster()) {
                case 1:
                    // 若选中，点击变为未选中
                    refreshGroupBySelectedMaster(mListMstGroup, position, 2);
                    break;
                case 2:
                    // 如果其它组有选中包含主控，点击后变为未选中
                    for (int i = 0; i < mListMstGroup.size(); i++) {
                        if (mListMstGroup.get(i).getIsSelectedMaster() == 1) {
                            refreshGroupBySelectedMaster(mListMstGroup, i, 2);
                        }
                    }
                    // 若未选中，点击变为选中
                    refreshGroupBySelectedMaster(mListMstGroup, position, 1);
                    break;
            }
        });
        rvMasterList.setAdapter(mAdapter);
        rvMasterList.getRecyclerView().addOnScrollListener(new FabScrollListener(this));

        // 不需要上拉刷新
        rvMasterList.setPushRefreshEnable(false);
        rvMasterList.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                refreshData();
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

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        isStarJet = false;
        super.onDestroy();
    }

    @Override
    public void initListener() {
        // 添加分组监听
        imgAddGroup.setOnClickListener(v -> showDialogCrt());
        // 蓝牙连接监听
        imgBleToolbar.setOnClickListener(v -> ToastUtil.showShort("ble"));
    }

    /** 蓝牙连接状态 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventDevConn(DevConnEvent event) {
        // 接收硬件传过来的已连接设备信息添加到 HashSet
        if (event.getStatus() != null) {
            LogUtil.i(TAG, "dev cfg event: " + event.getMac() + " | " + event.getId() + " | " + event.getStatus());

            switch (event.getStatus()) {
                case DEVICE_CONNECT_YES:
                    mHandler.sendEmptyMessage(HANDLE_BLE_CONN);
                    break;
                case DEVICE_CONNECT_NO:
                    mHandler.sendEmptyMessage(HANDLE_BLE_DISCONN);
                    break;
            }
        }
    }

    /** 保存分组数据 */
    private void saveGroupLite(String groupName) {
        MasterGroupLite bean = new MasterGroupLite();
        bean.setDevId(String.valueOf(mDevId));
        bean.setMemberId(MainActivity.getAppCtrl().getUserId());
        bean.setGroupName(groupName);
        bean.setGroupIdMillis(System.currentTimeMillis());
        bean.setIsSelectedGroup(2);
        bean.setIsSelectedMaster(2);
        bean.setJetTime(0);
        bean.setDevNum(0);
        bean.setStartDmx(0);
        bean.save();
        refreshData();
    }

    /** 判断分组名是否有重复 */
    private int checkGroupName(String newName) {
        int flag = 0;
        for (int i = 0; i < mListMstGroup.size(); i++) {
            if (mListMstGroup.get(i).getGroupName().equals(newName)) {
                flag++;
            }
        }
        return flag;
    }

    /** 创建分组 Dialog */
    private void showDialogCrt() {
        mDialogCrt = new SelfDialog(this);
        mDialogCrt.setTitle("添加分组");
        mDialogCrt.setMessage("请输入主控分组名称");
        mDialogCrt.setYesOnclickListener("确定", () -> {
            if (mDialogCrt.getEditTextStr().length() > 6) {
                // 如果输入的 DMX 不在 1~511 之间，提示用户
                ToastUtil.showShort("分组名称不能大于 6 个字\\n请重新设置");
                mDialogCrt.dismiss();
            } else if (checkGroupName(mDialogCrt.getEditTextStr()) > 0) {
                ToastUtil.showShort("分组名已使用\n请重新设置");
            } else {
                // 创建主控分组
                saveGroupLite(mDialogCrt.getEditTextStr());
                refreshData();
                mDialogCrt.dismiss();
            }
        });
        mDialogCrt.setNoOnclickListener("取消", () -> mDialogCrt.dismiss());
        mDialogCrt.show();
    }

    /** 删除分组 Dialog */
    private void showDialogDel(final int position) {
        mDialogDel = new SelfDialogBase(this);
        mDialogDel.setTitle("确定要删除");
        mDialogDel.setYesOnclickListener("确定", () -> {
            // 删除喷射效果
            LitePal.deleteAll(MasterGroupLite.class, "groupIdMillis = ?", String.valueOf(mListMstGroup.get(position).getGroupIdMillis()));
            refreshData();
            mDialogDel.dismiss();
        });
        mDialogDel.setNoOnclickListener("取消", () -> mDialogDel.dismiss());
        mDialogDel.show();
    }

    private void refreshGroupBySelectedGroup(List<MasterGroupLite> list, int position, int isSelectedGroup) {
        list.get(position).setIsSelectedGroup(isSelectedGroup);
        MasterGroupLite listBean = list.get(position);
        listBean.setIsSelectedGroup(isSelectedGroup);
        listBean.updateAll("groupName = ?", listBean.getGroupName());
        mAdapter.notifyDataSetChanged();
    }

    private void refreshGroupBySelectedMaster(List<MasterGroupLite> list, int position, int isSelectedMaster) {
        list.get(position).setIsSelectedMaster(isSelectedMaster);
        MasterGroupLite listBean = list.get(position);
        listBean.setIsSelectedMaster(isSelectedMaster);
        listBean.updateAll("groupName = ?", listBean.getGroupName());
        mAdapter.notifyDataSetChanged();
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLE_BLE_CONN:
                    imgBleToolbar.setImageDrawable(getResources().getDrawable(R.drawable.ic_ble_conn));
                    break;
                case HANDLE_BLE_DISCONN:
                    imgBleToolbar.setImageDrawable(getResources().getDrawable(R.drawable.ic_ble_desconn));
                    showDialogByDisconnect(CtMasterModeActivity.this);
                    break;
                case MSG_MST_JET:
                    // 主控输入控制
                    timerCallingMethod();
                    if (isStarJet && isOverPreFeed) {
                        // 如果喷射中，继续发送。定时调用方法（每 0.1 秒给通过蓝牙设备发一次信息）
                        mHandler.sendEmptyMessageDelayed(MSG_MST_JET, 100);
                    }
                    break;
                case MSG_ZERO_FIVE:
                    // 齐喷5次
                    if (mFlagFive < 5) {
                        mFlagFive++;
                        togetherZeroStop();
                        mHandler.sendEmptyMessageDelayed(MSG_ZERO_FIVE, 100);
                    } else {
                        mFlagFive = 0;
                    }
                    break;
            }
        }
    };

    @OnClick({R.id.btn_master_start, R.id.img_start_hide, R.id.rl_show_dialog})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_master_start:
                clickStartStop();
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
        if (isStarJet) {
            stopJet();
        } else {
            boolean bNeedStart = true;
            boolean bSelect = false;
            for (int i = 0; i < mListMstGroup.size(); i++) {
                // 选中的分组进行喷射
                if (mListMstGroup.get(i).getIsSelectedGroup() == 1) {
                    bSelect = true;
                    if (mListMstGroup.get(i).getDevNum() == 0) {
                        ToastUtil.showShort("设备数量不能为0，请您重新设置");
                        bNeedStart = false;
                    } else if (mListMstGroup.get(i).getStartDmx() == 0) {
                        ToastUtil.showShort("起始DMX不能为0，请您重新设置");
                        bNeedStart = false;
                    } else if (mListMstGroup.get(i).getJetModes().size() == 0) {
                        ToastUtil.showShort("有选中组未设置效果，请重新设置");
                        bNeedStart = false;
                    }
                }
            }
            if (bNeedStart && bSelect) {
                starJet();
            } else if (!bSelect) {
                ToastUtil.showShort("起始DMX不能为0，请您重新设置");
            }
        }
    }

    private void starJet() {
        mListJetting = new ArrayList<>();
        for (int i = 0; i < mListMstGroup.size(); i++) {
            if (mListMstGroup.get(i).getIsSelectedGroup() == 1) {
                // 添加选中的组到喷射集合
                mListJetting.add(mListMstGroup.get(i));
            }
        }
        if (mListJetting.size() == 0) {
            // 没有选中喷射
            return;
        }

        mBeginId = new int[mListJetting.size()];
        mStartDmx = mListJetting.get(0).getStartDmx();
        mEndDmx = mListJetting.get(0).getEndDmx();
        mIsIncludeMst = 0;
        // 对 mListJetting 进行初始化操作
        for (int i = 0; i < mListJetting.size(); i++) {
            mListJetting.get(i).makeJetMgrs();
            int newStartDmx = mListJetting.get(i).getStartDmx();
            int newEndDmx = mListJetting.get(i).getEndDmx();
            if (newStartDmx < mStartDmx) {
                mStartDmx = newStartDmx;
            }
            if (newEndDmx > mEndDmx) {
                mEndDmx = newEndDmx;
            }
            if (mListJetting.get(i).getIsSelectedMaster() == 1) {
                mIsIncludeMst = 1;
            }
        }
        mDevNum = (mEndDmx - mStartDmx) / 2 + 1 + 1;
        for (int i = 0; i < mListJetting.size(); i++) {
            // +1 表示第一个字节肯定是主控数据
            mBeginId[i] = 1 + (mListJetting.get(i).getStartDmx() - mStartDmx) / 2;
        }

        // 如果是停止喷射状态，点击变为开始状态，暂停可点击
        isStarJet = true;
        btnMasterStart.setText("停止");
        LogUtil.i(JET, "jet=true: " + " jet= " + isStarJet);

        // 启动计时器 0.1s
        mHandler.sendEmptyMessage(MSG_MST_JET);
    }

    private void stopJet() {
        // 如果是开始喷射状态，点击变为停止状态，暂停不可点击
        isStarJet = false;
        isOverPreFeed = false;
        btnMasterStart.setText("开始");
        LogUtil.i(JET, "jet=false: " + " jet= " + isStarJet );
        // 齐喷五次
        mHandler.sendEmptyMessage(MSG_ZERO_FIVE);
        mHandler.removeMessages(MSG_MST_JET);
    }

    private boolean isOverPreFeed;// 是否完成预进料操作

    /** 定时调用方法（每 0.1 秒给通过蓝牙设备发一次信息） */
    private void timerCallingMethod() {
        LogUtil.i(JET, "timerCallingMethod isStarJet = " + isStarJet);
        byte[] dataOut = new byte[CTRL_DEV_NUM];

        // 预进料操作
        if (!isOverPreFeed) {
            // 开始预进料
            setPreFeed(dataOut);
        } else {
            // 调用方法判断全部组是否完成喷射
            boolean isAllFinish = true;
            for (int i = 0; i < CTRL_DEV_NUM; i++) {
                dataOut[i] = 0;
            }

            for (int i = 0; i < mListJetting.size(); i++) {
                byte[] dataOutOne = mListJetting.get(i).updateWithDataOut();
                LogUtil.i(JET, "timerCallingMethod: " + i + " devcnt = " + mListJetting.get(i).getCurJettingDevCnt() + " " + mBeginId[i]);
                if (dataOutOne != null) {
                    LogUtil.i(JET, "timerCallingMethod: " + dataOutOne[0] + " " + dataOutOne[1] + " " + dataOutOne[2]);
                    isAllFinish = false;
                    if (mListJetting.get(i).getIsSelectedMaster() == 1) {
                        // 包含主控
                        dataOut[0] = dataOutOne[0];
                        System.arraycopy(dataOutOne, 1, dataOut, mBeginId[i], mListJetting.get(i).getDevNum());
                    } else {
                        System.arraycopy(dataOutOne, 0, dataOut, mBeginId[i], mListJetting.get(i).getDevNum());
                    }
                }
            }

            MainActivity.getAppCtrl().sendCommand(MainActivity.getAppCtrl().getDevice(MainActivity.getAppCtrl().getDevMac()),
                    BleDevProtocol.pkgEnterRealTimeCtrlMode(mDevId, mStartDmx, mDevNum, dataOut));
            LogUtil.i(JET, "timerCallingMethod2: " + dataOut[0] + " " + dataOut[1] + " " + dataOut[2]);
            LogUtil.i(JET, "timerCallingMethod: " + mDevId + " " + mStartDmx + " " + mDevNum
                    + " " + isAllFinish + " jet = " + isStarJet);
            if (isAllFinish) {
                LogUtil.i(JET, "终于喷完了！！！");

                // 按钮状态初始化
                btnMasterStart.setText("开始");
                // 喷射停止状态
                isStarJet = false;
                // 预进料初始化
                isOverPreFeed = false;
                // 齐喷五次
                mHandler.sendEmptyMessage(MSG_ZERO_FIVE);
                // 清料
                cmdClearMaterial();
            }
        }
    }

    /** 预进料操作 */
    private void setPreFeed(byte[] dataOut) {
        for (int i = 0; i < CTRL_DEV_NUM; i++) {
            dataOut[i] = (byte) 133;
        }
        MainActivity.getAppCtrl().sendCommand(MainActivity.getAppCtrl().getDevice(MainActivity.getAppCtrl().getDevMac()),
                BleDevProtocol.pkgEnterRealTimeCtrlMode(mDevId, mStartDmx, mDevNum, dataOut));
        // 暂定2s倒计时，每隔1s更新UI
        new CountDownTimer(1999, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                btnMasterStart.setTextSize(40);
                btnMasterStart.setText(String.valueOf(millisUntilFinished / 1000 + 1));
            }

            @Override
            public void onFinish() {
                // 停止预进料
                for (int i = 0; i < CTRL_DEV_NUM; i++) {
                    dataOut[i] = (byte) 134;
                }
                MainActivity.getAppCtrl().sendCommand(MainActivity.getAppCtrl().getDevice(MainActivity.getAppCtrl().getDevMac()),
                        BleDevProtocol.pkgEnterRealTimeCtrlMode(mDevId, mStartDmx, mDevNum, dataOut));
                btnMasterStart.setTextSize(20);
                btnMasterStart.setText("停止");
                isOverPreFeed = true;
                mHandler.sendEmptyMessage(MSG_MST_JET);
            }
        }.start();
    }

    /** 发送 5 次高度为 0，持续时间 0.1s 齐喷效果 */
    private void togetherZeroStop() {
        byte[] dataOut = new byte[CTRL_DEV_NUM];
        // 喷射 5 次高度为 0，持续时间 0.1s 齐喷效果
        MgrTogetherJet mgrStop = new MgrTogetherJet();
        mgrStop.setType(CONFIG_TOGETHER);
        mgrStop.setDevCount(mDevNum);
        mgrStop.setCurrentTime(INIT_ZERO);
        mgrStop.setDuration(1);
        mgrStop.setHigh((byte) INIT_ZERO);
        mgrStop.updateWithDataOut(dataOut);

        MainActivity.getAppCtrl().sendCommand(mDevice,
                BleDevProtocol.pkgEnterRealTimeCtrlMode(mDevId, mStartDmx, mDevNum, dataOut));
    }

    /** 发送清料命令 */
    private void cmdClearMaterial() {
        byte[] byHighs = new byte[CTRL_DEV_NUM];
        for (int i = 0; i < mDevNum; i++) {
            int high = 20;
            byHighs[i] = (byte) high;
        }
        MainActivity.getAppCtrl().sendCommand(mDevice,
                 BleDevProtocol.pkgClearMaterial(mDevId, CLEAR_MATERIAL_MASTER, mStartDmx, mDevNum, byHighs));
    }

}
