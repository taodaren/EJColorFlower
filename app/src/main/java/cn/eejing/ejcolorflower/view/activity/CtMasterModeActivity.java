package cn.eejing.ejcolorflower.view.activity;

import android.os.Handler;
import android.support.annotation.NonNull;
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
import cn.eejing.ejcolorflower.device.BleDeviceProtocol;
import cn.eejing.ejcolorflower.device.Device;
import cn.eejing.ejcolorflower.model.lite.CtrlIntervalEntity;
import cn.eejing.ejcolorflower.model.lite.CtrlRideEntity;
import cn.eejing.ejcolorflower.model.lite.CtrlStreamEntity;
import cn.eejing.ejcolorflower.model.lite.CtrlTogetherEntity;
import cn.eejing.ejcolorflower.model.lite.MasterCtrlModeEntity;
import cn.eejing.ejcolorflower.model.lite.MasterCtrlSetEntity;
import cn.eejing.ejcolorflower.model.manager.MgrIntervalJet;
import cn.eejing.ejcolorflower.model.manager.MgrOutputJet;
import cn.eejing.ejcolorflower.model.manager.MgrRideJet;
import cn.eejing.ejcolorflower.model.manager.MgrStreamJet;
import cn.eejing.ejcolorflower.model.manager.MgrTogetherJet;
import cn.eejing.ejcolorflower.model.request.MasterGroupListBean;
import cn.eejing.ejcolorflower.presenter.IShowListener;
import cn.eejing.ejcolorflower.presenter.OnReceivePackage;
import cn.eejing.ejcolorflower.util.FabScrollListener;
import cn.eejing.ejcolorflower.view.adapter.MasterListAdapter;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.CLEAR_MATERIAL_MASTER;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_INTERVAL;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_RIDE;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_STREAM;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_TOGETHER;
import static cn.eejing.ejcolorflower.app.AppConstant.CTRL_DEV_NUM;
import static cn.eejing.ejcolorflower.app.AppConstant.CURRENT_TIME;
import static cn.eejing.ejcolorflower.app.AppConstant.DEFAULT_INTERVAL_DURATION;
import static cn.eejing.ejcolorflower.app.AppConstant.DEFAULT_INTERVAL_FREQUENCY;
import static cn.eejing.ejcolorflower.app.AppConstant.DEFAULT_INTERVAL_GAP;
import static cn.eejing.ejcolorflower.app.AppConstant.DEFAULT_STREAM_RIDE_DURATION;
import static cn.eejing.ejcolorflower.app.AppConstant.DEFAULT_STREAM_RIDE_GAP;
import static cn.eejing.ejcolorflower.app.AppConstant.DEFAULT_STREAM_RIDE_GAP_BIG;
import static cn.eejing.ejcolorflower.app.AppConstant.DEFAULT_STREAM_RIDE_LOOP;
import static cn.eejing.ejcolorflower.app.AppConstant.DEFAULT_TOGETHER_DURATION;
import static cn.eejing.ejcolorflower.app.AppConstant.DEFAULT_TOGETHER_HIGH;
import static cn.eejing.ejcolorflower.app.AppConstant.INIT_ZERO;
import static cn.eejing.ejcolorflower.app.AppConstant.JET_EFFECT;
import static cn.eejing.ejcolorflower.app.AppConstant.LEFT_TO_RIGHT;
import static cn.eejing.ejcolorflower.app.AppConstant.LOOP_ID;

/**
 * 主控界面
 */

public class CtMasterModeActivity extends BaseActivity implements IShowListener {
    private static final String TAG = "CtMasterModeActivity";
    private static final String JET = "CtMasterModeJet";

    @BindView(R.id.rv_master_list)          PullLoadMoreRecyclerView rvMasterList;
    @BindView(R.id.btn_master_start)        Button btnMasterStart;
    @BindView(R.id.rl_hide_dialog)          RelativeLayout hideDialog;
    @BindView(R.id.rl_show_dialog)          RelativeLayout showDialog;

    private Device mDevice;
    private long mDeviceId;
    private int mDevNum, mStartDmx;

    private boolean isStarJet;                            // 是否开始喷射
    private int mCurrMasterId, mFlagCurrId;               // 当前主控喷射效果 ID 及标志位
    private MgrOutputJet mCurrentManager;                 // 当前喷射效果管理

    private List<MasterGroupListBean> mMasterInfoList;    // 主控分组信息列表集合
    private List<MasterCtrlModeEntity> mGroupJetModes;    // 分组喷射效果集合
    private List<MasterCtrlSetEntity> mGroupInfoList;     // 分组配置信息集合
    private List<MgrOutputJet> mMasterCtrlMgrList;        // 主控喷射管理集合

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ct_master_mode;
    }

    @Override
    public void initView() {
        setToolbar("主控", View.VISIBLE, null, View.GONE);

        mDevice = MainActivity.getAppCtrl().getDevice(MainActivity.getAppCtrl().getDevMac());
        mDeviceId = getIntent().getLongExtra("device_id", 0);
        mMasterInfoList = new ArrayList<>();
        mMasterCtrlMgrList = new ArrayList<>();

        initDatabase();
        initRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 刷新数据
        mMasterInfoList.clear();
        initDatabase();
        initRecyclerView();
    }

    private void initDatabase() {
        mGroupJetModes = LitePal.where("devId = ?", String.valueOf(mDeviceId)).find(MasterCtrlModeEntity.class);
        mGroupInfoList = LitePal.where("devId = ?", String.valueOf(mDeviceId)).find(MasterCtrlSetEntity.class);

        Log.i(TAG, "mGroupJetModes size: " + mGroupJetModes.size());
        Log.i(TAG, "mGroupInfoList size: " + mGroupInfoList.size());
        if (mGroupInfoList.size() == 0) {
            initGroupInfo(INIT_ZERO, INIT_ZERO, JET_EFFECT);
        } else {
            initGroupInfo(mGroupInfoList.get(0).getDevNum(), mGroupInfoList.get(0).getStartDmx(), mGroupJetModes.get(0).getType());
        }
    }

    private void initGroupInfo(int devNum, int startDmx, String jetEffect) {
        String groupName = "分组名称";
        String jetModel = jetEffect;
        mDevNum = devNum;
        mStartDmx = startDmx;
        Log.w(TAG, "initGroupInfo: " + mDevNum + " " + mStartDmx + " " + jetModel);

        mMasterInfoList.add(new MasterGroupListBean(1, groupName, mGroupInfoList));
//        for (int i = 0; i < 9; i++) {
//            mGroupName = "分组功能敬请期待...";
//            mDevNum = INIT_ZERO;
//            mStartDmx = INIT_ZERO;
//            jetModel = JET_EFFECT;
//            mMasterInfoList.add(new MasterGroupListBean(0, groupName, mGroupInfoList));
//        }
    }

    private void initRecyclerView() {
        // 设置布局
        rvMasterList.setLinearLayout();
        // 绑定适配器
        MasterListAdapter adapter = new MasterListAdapter(this, mMasterInfoList);
//        adapter.setHasStableIds(true);
        // 监听设置主控按钮
        adapter.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToActivity(CtSetGroupActivity.class);
            }
        });
        rvMasterList.setAdapter(adapter);

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

    @Override
    protected void onDestroy() {
        isStarJet = false;
        super.onDestroy();
    }

//    @SuppressLint("HandlerLeak")
//    Handler mHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            Toast.makeText(CtMasterModeActivity.this, "任务执行完毕", Toast.LENGTH_SHORT).show();
//            showStartDialog();
//        }
//    };

    private Handler mHandler = new Handler();

    @OnClick({R.id.btn_master_start, R.id.img_start_hide, R.id.rl_show_dialog})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_master_start:
                clickStartStop();
//                Toast.makeText(this, "开始执行任务", Toast.LENGTH_SHORT).show();
//                hideStartDialog();
//                mHandler.sendEmptyMessageDelayed(1, 3000);
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
        for (int i = 0; i < mMasterInfoList.size(); i++) {
            // 选中的分组进行喷射
            if (mMasterInfoList.get(i).getIsSelected() == 1) {
                if (mMasterInfoList.get(i).getCfgInfo().get(0).getDevNum() == 0) {
                    Toast.makeText(this, "设备数量不能为 0，请您重新设置", Toast.LENGTH_SHORT).show();
                } else if (mMasterInfoList.get(i).getCfgInfo().get(0).getStartDmx() == 0) {
                    Toast.makeText(this, "起始DMX不能为 0，请您重新设置", Toast.LENGTH_SHORT).show();
                } else if (mMasterInfoList.get(i).getCfgInfo().get(0).getJetMode().equals("喷射效果")) {
                    Toast.makeText(this, "请添加喷射效果", Toast.LENGTH_SHORT).show();
                } else {
                    if (isStarJet) {
                        stopJet();
                    } else {
                        starJet();
                    }
                }
            }
        }
    }

    private void starJet() {
        Log.i(TAG, "开始喷射 size : " + mGroupJetModes.size());
        Log.i(TAG, "开始喷射 info : " + mDevNum + " " + mStartDmx);
        for (int i = 0; i < mGroupJetModes.size(); i++) {
            Log.i(TAG, "开始喷射 mode: " + mGroupJetModes.get(i).getType());
        }
        // 如果是停止喷射状态，点击变为开始状态，暂停可点击
        isStarJet = true;
        mCurrMasterId = INIT_ZERO;
        mFlagCurrId = INIT_ZERO;
        mCurrentManager = null;
        mMasterCtrlMgrList.clear();
        btnMasterStart.setText("停止");
        // 设置不可编辑状态
//        imgMasterMode.setClickable(false);
//        etStarDmx.setFocusable(false);
//        etDevNum.setFocusable(false);
//        etStarDmx.setFocusableInTouchMode(false);
//        etDevNum.setFocusableInTouchMode(false);

        // 启动计时器，0.1 秒
        mHandler.post(mRunnableMaster);
    }

    private void stopJet() {
        // 如果是开始喷射状态，点击变为停止状态，暂停不可点击
        isStarJet = false;
        btnMasterStart.setText("开始");
        // 设置可编辑状态
//        imgMasterMode.setClickable(true);
//        etStarDmx.setFocusableInTouchMode(true);
//        etDevNum.setFocusableInTouchMode(true);
//        etStarDmx.setFocusable(true);
//        etDevNum.setFocusable(true);
//        etStarDmx.requestFocus();
//        etDevNum.requestFocus();
        // 齐喷五次
        togetherFiveStopPause();
    }

    /** 发送喷射五次命令 */
    private void togetherFiveStopPause() {
        byte[] dataOut = new byte[CTRL_DEV_NUM];
        // 喷射 5 次高度为 0，持续时间 0.1s 齐喷效果
        MgrTogetherJet mgrStop = new MgrTogetherJet();
        mgrStop.setType(CONFIG_TOGETHER);
        mgrStop.setDevCount(mDevNum);
        mgrStop.setCurrentTime(INIT_ZERO);
        mgrStop.setDuration(1);
        mgrStop.setHigh((byte) INIT_ZERO);
        mgrStop.updateWithDataOut(dataOut);
        for (int i = 0; i < 5; i++) {
            MainActivity.getAppCtrl().sendCommand(mDevice, BleDeviceProtocol.pkgEnterRealTimeCtrlMode(mDeviceId, mStartDmx, mDevNum, dataOut));
        }
    }

    // 主控输入控制
    private final Runnable mRunnableMaster = new Runnable() {
        @Override
        public void run() {
            // 定时调用方法（每 0.1 秒给通过蓝牙设备发一次信息）
            timerCallingMethod();
            if (isStarJet) {
                // 如果喷射中，继续发送
                mHandler.postDelayed(this, 100);
            }
        }
    };

    /** 定时调用方法（每 0.1 秒给通过蓝牙设备发一次信息）*/
    private void timerCallingMethod() {
        Log.w(JET, "FlagCur ID: " + mFlagCurrId);
        Log.i(JET, "Current ID: " + mCurrMasterId);
        Log.i(JET, "mMasterCtrlMgrList.size(): " + mMasterCtrlMgrList.size());

        // 创建主控管理集合
        if (mMasterCtrlMgrList.size() == 0) {
            // 如果主控管理集合没有数据，创建添加
            createMasterCtrlMgrList();
        }

        // 如果如果当前运行的 ID 大于或等于主控管理集合的最大数量，喷射停止
        if (mCurrMasterId >= mMasterCtrlMgrList.size()) {
            isStarJet = false;
            return;
        }

        // 如果当前运行的 ID 与 flag 不同，让其相等
        if (mFlagCurrId != mCurrMasterId) {
            mFlagCurrId = mCurrMasterId;
            mCurrentManager = mMasterCtrlMgrList.get(mCurrMasterId);
        }

        // 发送进入在线实时控制模式命令
        byte[] dataOut = new byte[CTRL_DEV_NUM];
        // 调用方法判断当前组是否完成喷射
        boolean isFinish = false;
        if (isStarJet) {
            if (mCurrentManager != null) {
                Log.e(JET, "Current Manager have: " + mCurrentManager.getType());
                isFinish = mCurrentManager.updateWithDataOut(dataOut);
                Log.i(JET, "isFinish: " + isFinish);
            } else {
                Log.e(JET, "Current Manager null: " + mCurrentManager);
                mCurrentManager = mMasterCtrlMgrList.get(0);
                Log.e(JET, "Current Manager NO.1: " + mCurrentManager.getType());
                isFinish = mCurrentManager.updateWithDataOut(dataOut);
                Log.i(JET, "isFinish: " + isFinish);
            }
        }

        Log.d(JET, "timerCallingMethod: " + mDeviceId + " " + mStartDmx + " " + mDevNum + " " + dataOut.length);
        MainActivity.getAppCtrl().sendCommand(mDevice,
                BleDeviceProtocol.pkgEnterRealTimeCtrlMode(mDeviceId, mStartDmx, mDevNum, dataOut));

        if (isFinish) {
            // 当前组喷射完成，进入到下一组，继续执行下一组
            mCurrMasterId++;
            if (mCurrMasterId >= mMasterCtrlMgrList.size()) {
                Log.i(JET, "老子终于喷完了！！！");

                // 设置不可编辑状态
//                imgMasterMode.setClickable(false);
//                etStarDmx.setFocusable(false);
//                etDevNum.setFocusable(false);
//                etStarDmx.setFocusableInTouchMode(false);
//                etDevNum.setFocusableInTouchMode(false);

                // 按钮状态初始化
                btnMasterStart.setText("开始");

                // 喷射停止状态
                isStarJet = false;
                // 齐喷五次
                togetherFiveStopPause();
                // 清料
                cmdClearMaterial();
            }
        }

    }

    /** 发送清料命令 */
    private void cmdClearMaterial() {
        byte[] byHighs = new byte[CTRL_DEV_NUM];
        for (int i = 0; i < mDevNum; i++) {
            int high = 20;
            byHighs[i] = (byte) high;
        }
        Device device = MainActivity.getAppCtrl().getDevice(MainActivity.getAppCtrl().getDevMac());
        byte[] pkgClearMaterial = BleDeviceProtocol.pkgClearMaterial(mDeviceId, CLEAR_MATERIAL_MASTER, mStartDmx, mDevNum, byHighs);
        MainActivity.getAppCtrl().sendCommand(device, pkgClearMaterial, new OnReceivePackage() {
            @Override
            public void ack(@NonNull byte[] pkg) {
            }

            @Override
            public void timeout() {
            }
        });
    }

    /** 创建主控管理列表集合 */
    private void createMasterCtrlMgrList() {
        // 查询数据库保存分组效果信息
        mGroupJetModes = LitePal.where("devId = ?", String.valueOf(mDeviceId)).find(MasterCtrlModeEntity.class);

        // 给主控管理集合添加数据
        for (int i = 0; i < mGroupJetModes.size(); i++) {
            switch (mGroupJetModes.get(i).getType()) {
                case CONFIG_STREAM:
                    mMasterCtrlMgrList.add(setDataWithStream(i));
                    break;
                case CONFIG_RIDE:
                    mMasterCtrlMgrList.add(setDataWithRide(i));
                    break;
                case CONFIG_INTERVAL:
                    mMasterCtrlMgrList.add(setDataWithInterval(i));
                    break;
                case CONFIG_TOGETHER:
                    mMasterCtrlMgrList.add(setDataWithTogether(i));
                    break;
                default:
                    break;
            }
        }
    }

    @NonNull
    private MgrOutputJet setDataWithStream(int currMasterId) {
        List<CtrlStreamEntity> streamEntities =
                LitePal.where("groupId = ?", String.valueOf((int) mGroupJetModes.get(currMasterId).getMillis())).find(CtrlStreamEntity.class);

        MgrStreamJet mgrStream = new MgrStreamJet();
        mgrStream.setType(CONFIG_STREAM);
        mgrStream.setDevCount(mDevNum);
        mgrStream.setCurrentTime(CURRENT_TIME);
        mgrStream.setLoopId(LOOP_ID);
        mgrStream.setHigh((byte) Integer.parseInt(DEFAULT_TOGETHER_HIGH));
        switch (streamEntities.size()) {
            case 1:
                mgrStream.setDirection(Integer.parseInt(streamEntities.get(0).getDirection()));
                mgrStream.setGap((int)(Float.parseFloat(streamEntities.get(0).getGap()) * 10));
                mgrStream.setDuration((int)(Float.parseFloat(streamEntities.get(0).getDuration()) * 10));
                mgrStream.setGapBig((int)(Float.parseFloat(streamEntities.get(0).getGapBig()) * 10));
                mgrStream.setLoop(Integer.parseInt(streamEntities.get(0).getLoop()));
                break;
            default:
                mgrStream.setDirection(Integer.parseInt(LEFT_TO_RIGHT));
                mgrStream.setGap(Integer.parseInt(DEFAULT_STREAM_RIDE_GAP) * 10);
                mgrStream.setDuration(Integer.parseInt(DEFAULT_STREAM_RIDE_DURATION) * 10);
                mgrStream.setGapBig(Integer.parseInt(DEFAULT_STREAM_RIDE_GAP_BIG) * 10);
                mgrStream.setLoop(Integer.parseInt(DEFAULT_STREAM_RIDE_LOOP));
                break;
        }

        return mgrStream;
    }

    @NonNull
    private MgrOutputJet setDataWithRide(int currMasterId) {
        List<CtrlRideEntity> rideEntities =
                LitePal.where("groupId = ?", String.valueOf((int) mGroupJetModes.get(currMasterId).getMillis())).find(CtrlRideEntity.class);

        MgrRideJet mgrRide = new MgrRideJet();
        mgrRide.setType(CONFIG_RIDE);
        mgrRide.setDevCount(mDevNum);
        mgrRide.setCurrentTime(CURRENT_TIME);
        mgrRide.setLoopId(LOOP_ID);
        mgrRide.setHigh((byte) Integer.parseInt(DEFAULT_TOGETHER_HIGH));

        switch (rideEntities.size()) {
            case 1:
                mgrRide.setDirection(Integer.parseInt(rideEntities.get(0).getDirection()));
                mgrRide.setGap((int)(Float.parseFloat(rideEntities.get(0).getGap()) * 10));
                mgrRide.setDuration((int)(Float.parseFloat(rideEntities.get(0).getDuration()) * 10));
                mgrRide.setGapBig((int)(Float.parseFloat(rideEntities.get(0).getGapBig()) * 10));
                mgrRide.setLoop(Integer.parseInt(rideEntities.get(0).getLoop()));
                break;
            default:
                mgrRide.setDirection(Integer.parseInt(LEFT_TO_RIGHT));
                mgrRide.setGap(Integer.parseInt(DEFAULT_STREAM_RIDE_GAP) * 10);
                mgrRide.setDuration(Integer.parseInt(DEFAULT_STREAM_RIDE_DURATION) * 10);
                mgrRide.setGapBig(Integer.parseInt(DEFAULT_STREAM_RIDE_GAP_BIG) * 10);
                mgrRide.setLoop(Integer.parseInt(DEFAULT_STREAM_RIDE_LOOP));
                break;
        }

        return mgrRide;
    }

    @NonNull
    private MgrOutputJet setDataWithInterval(int currMasterId) {
        List<CtrlIntervalEntity> intervalEntities =
                LitePal.where("groupId = ?", String.valueOf((int) mGroupJetModes.get(currMasterId).getMillis())).find(CtrlIntervalEntity.class);

        MgrIntervalJet mgrInterval = new MgrIntervalJet();
        mgrInterval.setType(CONFIG_INTERVAL);
        mgrInterval.setDevCount(mDevNum);
        mgrInterval.setCurrentTime(CURRENT_TIME);
        mgrInterval.setLoopId(LOOP_ID);
        switch (intervalEntities.size()) {
            case 1:
                mgrInterval.setGapBig((int)(Float.parseFloat(intervalEntities.get(0).getGap()) * 10));
                mgrInterval.setDuration((int)(Float.parseFloat(intervalEntities.get(0).getDuration()) * 10));
                mgrInterval.setLoop(Integer.parseInt(intervalEntities.get(0).getFrequency()));
                break;
            default:
                mgrInterval.setGapBig(Integer.parseInt(DEFAULT_INTERVAL_GAP) * 10);
                mgrInterval.setDuration(Integer.parseInt(DEFAULT_INTERVAL_DURATION) * 10);
                mgrInterval.setLoop(Integer.parseInt(DEFAULT_INTERVAL_FREQUENCY));
                break;
        }

        return mgrInterval;
    }

    @NonNull
    private MgrOutputJet setDataWithTogether(int currMasterId) {
        List<CtrlTogetherEntity> togetherEntities =
                LitePal.where("groupId = ?", String.valueOf((int) mGroupJetModes.get(currMasterId).getMillis())).find(CtrlTogetherEntity.class);

        MgrTogetherJet mgrTogether = new MgrTogetherJet();
        mgrTogether.setType(CONFIG_TOGETHER);
        mgrTogether.setDevCount(mDevNum);
        mgrTogether.setCurrentTime(CURRENT_TIME);
        switch (togetherEntities.size()) {
            case 1:
                mgrTogether.setDuration((int)(Float.parseFloat(togetherEntities.get(0).getDuration()) * 10));
                mgrTogether.setHigh((byte) Integer.parseInt(togetherEntities.get(0).getHigh()));
                break;
            default:
                mgrTogether.setDuration(Integer.parseInt(DEFAULT_TOGETHER_DURATION) * 10);
                mgrTogether.setHigh((byte) Integer.parseInt(DEFAULT_TOGETHER_HIGH));
                break;
        }

        return mgrTogether;
    }

}
