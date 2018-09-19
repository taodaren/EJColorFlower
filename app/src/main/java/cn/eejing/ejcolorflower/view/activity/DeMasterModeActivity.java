package cn.eejing.ejcolorflower.view.activity;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.example.zhouwei.library.CustomPopWindow;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.device.BleDeviceProtocol;
import cn.eejing.ejcolorflower.model.lite.CtrlIntervalEntity;
import cn.eejing.ejcolorflower.model.lite.CtrlRideEntity;
import cn.eejing.ejcolorflower.model.lite.CtrlStreamEntity;
import cn.eejing.ejcolorflower.model.lite.CtrlTogetherEntity;
import cn.eejing.ejcolorflower.model.lite.MasterCtrlModeEntity;
import cn.eejing.ejcolorflower.model.lite.MasterCtrlNumEntity;
import cn.eejing.ejcolorflower.model.manager.MgrIntervalJet;
import cn.eejing.ejcolorflower.model.manager.MgrOutputJet;
import cn.eejing.ejcolorflower.model.manager.MgrRideJet;
import cn.eejing.ejcolorflower.model.manager.MgrStreamJet;
import cn.eejing.ejcolorflower.model.manager.MgrTogetherJet;
import cn.eejing.ejcolorflower.util.JetCommandTools;
import cn.eejing.ejcolorflower.util.SelfDialogBase;
import cn.eejing.ejcolorflower.view.adapter.DeMasterModeAdapter;
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
import static cn.eejing.ejcolorflower.app.AppConstant.LEFT_TO_RIGHT;
import static cn.eejing.ejcolorflower.app.AppConstant.LOOP_ID;

/**
 * 设置主控模式
 */

public class DeMasterModeActivity extends BaseActivity {
    private static final int PAUSE_STATUS_CANNOT = 0;          // 不可点击-停止状态
    private static final int PAUSE_STATUS_ING = 1;             // 暂停状态
    private static final int PAUSE_STATUS_GOON = 2;            // 喷射状态-点击恢复之后

    @BindView(R.id.rv_master_mode)             RecyclerView    rvMasterMode;
    @BindView(R.id.img_start_or_stop)          ImageView       imgStartStop;
    @BindView(R.id.img_pause_or_goon)          ImageView       imgPauseGoon;
    @BindView(R.id.img_add_master_mode)        ImageView       imgMasterMode;
    @BindView(R.id.et_dev_num)                 EditText        etDevNum;
    @BindView(R.id.et_start_dmx)               EditText        etStarDmx;

    // 是否停止喷射
    private boolean isStar;
    private int isPause;
    private String mDeviceId;
    private CustomPopWindow mPopWindow;
    private DeMasterModeAdapter mAdapter;
    private List<MasterCtrlModeEntity> mList;
    private SelfDialogBase mDialog;

    private MainActivity.FireworkDevCtrl mDevCtrl;
    // 当前主控喷射效果ID及标志位
    private int mCurrMasterId, mFlagCurrId;

    private Handler mHandler;
    // 主控输入控制
    private final Runnable mRunnableMaster = new Runnable() {
        @Override
        public void run() {
            // 定时调用方法（每 0.1 秒给通过蓝牙设备发一次信息）
            timerCallingMethod();
            if (isStar) {
                // 如果喷射中，继续发送
                mHandler.postDelayed(this, 100);
            }
        }
    };
    private MgrOutputJet mCurrentManager;
    private List<MasterCtrlModeEntity> mJetModes;
    private List<MgrOutputJet> mMasterCtrlMgrList;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_de_master_mode;
    }

    @Override
    public void initView() {
        setToolbar("设置主控模式", View.VISIBLE, getString(R.string.clear_material), View.VISIBLE);
        mDeviceId = getIntent().getStringExtra("device_id");

        mList = new ArrayList<>();
        mMasterCtrlMgrList = new ArrayList<>();
        mHandler = new Handler();
        mDevCtrl = MainActivity.getFireworksDevCtrl();

        initConfigDB();
        initRecyclerView();
    }

    @Override
    protected void onDestroy() {
        isStar = false;
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (isStar) {
            showExitDialog();
        } else {
            finish();
        }
    }

    @OnClick({R.id.img_back_toolbar, R.id.tv_menu_toolbar, R.id.img_start_or_stop, R.id.img_pause_or_goon, R.id.img_add_master_mode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back_toolbar:
                if (isStar) {
                    showExitDialog();
                } else {
                    finish();
                }
                break;
            case R.id.tv_menu_toolbar:
                clearMaterialMaster();
                break;
            case R.id.img_start_or_stop:
                clickStartStop();
                break;
            case R.id.img_pause_or_goon:
                clickPauseGoon();
                break;
            case R.id.img_add_master_mode:
                clickJetStyle();
                break;
            default:
                break;
        }
    }

    /** 点击开始或停止 */
    private void clickStartStop() {
        if (etDevNum.length() == 0) {
            Toast.makeText(this, "请您设置设备数量", Toast.LENGTH_SHORT).show();
        } else if (etStarDmx.length() == 0) {
            Toast.makeText(this, "请您设置起始 DMX", Toast.LENGTH_SHORT).show();
        } else if (mList.size() == 0) {
            Toast.makeText(this, "请添加喷射效果", Toast.LENGTH_SHORT).show();
        } else {
            if (isStar) {
                stopJet();
            } else {
                starJet();
            }
        }
    }

    /** 点击暂停或继续 */
    private void clickPauseGoon() {
        switch (isPause) {
            case PAUSE_STATUS_ING:
                goonJet();
                break;
            case PAUSE_STATUS_GOON:
                pauseJet();
                break;
            default:
                break;
        }
    }

    private void starJet() {
        // 如果是停止喷射状态，点击变为开始状态，暂停可点击
        isStar = true;
        isPause = PAUSE_STATUS_GOON;
        mCurrMasterId = 0;
        mFlagCurrId = 0;
        mCurrentManager = null;
        mMasterCtrlMgrList.clear();
        imgStartStop.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_stop));
        imgPauseGoon.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_pause));
        // 设置不可编辑状态
        mAdapter.isClickItem(false);
        imgMasterMode.setClickable(false);
        etStarDmx.setFocusable(false);
        etDevNum.setFocusable(false);
        etStarDmx.setFocusableInTouchMode(false);
        etDevNum.setFocusableInTouchMode(false);
        // 保存或更新设备数量及起始 DMX
        setSQLiteData();
        // 启动计时器，0.1 秒
        mHandler.post(mRunnableMaster);
    }

    private void stopJet() {
        // 如果是开始喷射状态，点击变为停止状态，暂停不可点击
        isStar = false;
        isPause = PAUSE_STATUS_CANNOT;
        imgStartStop.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_start));
        imgPauseGoon.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_pause_cannot));
        // 设置可编辑状态
        mAdapter.isClickItem(true);
        imgMasterMode.setClickable(true);
        etStarDmx.setFocusableInTouchMode(true);
        etDevNum.setFocusableInTouchMode(true);
        etStarDmx.setFocusable(true);
        etDevNum.setFocusable(true);
        etStarDmx.requestFocus();
        etDevNum.requestFocus();
        // 齐喷五次
        togetherFiveStopPause();
    }

    private void pauseJet() {
        // 如果是恢复状态，点击变为暂停状态，暂停喷射
        isPause = PAUSE_STATUS_ING;
        imgPauseGoon.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_pause_goon));

        // 喷射停止状态
        isStar = false;
        // 齐喷五次
        togetherFiveStopPause();
    }

    private void goonJet() {
        // 如果是暂停状态，点击变为恢复状态，可以继续喷射
        isStar = true;
        isPause = PAUSE_STATUS_GOON;
        imgPauseGoon.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_pause));
        // 启动计时器，0.1 秒
        mHandler.post(mRunnableMaster);
    }

    /** 发送喷射五次命令 */
    private void togetherFiveStopPause() {
        byte[] dataOut = new byte[CTRL_DEV_NUM];
        // 喷射 5 次高度为 0，持续时间 0.1s 齐喷效果
        MgrTogetherJet mgrStop = new MgrTogetherJet();
        mgrStop.setType(CONFIG_TOGETHER);
        mgrStop.setDevCount(Integer.parseInt(etDevNum.getText().toString()));
        mgrStop.setCurrentTime(0);
        mgrStop.setDuration(1);
        mgrStop.setHigh((byte) 0);
        mgrStop.updateWithDataOut(dataOut);
        for (int i = 0; i < 5; i++) {
            mDevCtrl.sendCommand(Long.parseLong(mDeviceId), BleDeviceProtocol.pkgEnterRealTimeCtrlMode(
                    Long.parseLong(mDeviceId),
                    Integer.parseInt(etStarDmx.getText().toString()),
                    Integer.parseInt(etDevNum.getText().toString()),
                    dataOut)
            );
        }
    }

    /** 清料 */
    private void clearMaterialMaster() {
        if (etDevNum.length() == 0) {
            Toast.makeText(this, "请您设置设备数量", Toast.LENGTH_SHORT).show();
        } else if (etStarDmx.length() == 0) {
            Toast.makeText(this, "请您设置起始 DMX", Toast.LENGTH_SHORT).show();
        } else {
            JetCommandTools.clearMaterial(this, null,
                    Long.parseLong(mDeviceId),
                    CLEAR_MATERIAL_MASTER,
                    Integer.parseInt(etStarDmx.getText().toString()),
                    Integer.parseInt(etDevNum.getText().toString()), 20);
        }
    }

    /** 定时调用方法（每 0.1 秒给通过蓝牙设备发一次信息）*/
    private void timerCallingMethod() {
        Log.i("CMCML", "Flag ID: " + mFlagCurrId);
        Log.i("CMCML", "Current ID: " + mCurrMasterId);

        // 创建主控管理集合
        if (mMasterCtrlMgrList.size() == 0) {
            // 如果主控管理集合没有数据，创建添加
            createMasterCtrlMgrList();
        }

        // 如果如果当前运行的 ID 大于或等于主控管理集合的最大数量，停止
        if (mCurrMasterId >= mMasterCtrlMgrList.size()) {
            isStar = false;
            return;
        }

        if (mFlagCurrId != mCurrMasterId) {
            // 如果当前运行的 ID 与 flag 不同，让其相等
            mFlagCurrId = mCurrMasterId;
            mCurrentManager = getCurrentManager(mCurrMasterId);
        }

        // 发送进入在线实时控制模式命令
        byte[] dataOut = new byte[CTRL_DEV_NUM];
        // 调用方法判断当前组是否完成喷射
        boolean isFinish = false;
        if (isStar) {
            if (mCurrentManager != null) {
                Log.e("CMCML", "Current Manager have: " + mCurrentManager.getType());
                isFinish = mCurrentManager.updateWithDataOut(dataOut);
                Log.i("CMCML", "isFinish: " + isFinish);
            } else {
                Log.e("CMCML", "Current Manager null: " + mCurrentManager);
                mCurrentManager = getCurrentManager(0);
                Log.e("CMCML", "Current Manager NO.1: " + mCurrentManager.getType());
                isFinish = mCurrentManager.updateWithDataOut(dataOut);
                Log.i("CMCML", "isFinish: " + isFinish);
            }
        }

        mDevCtrl.sendCommand(Long.parseLong(mDeviceId), BleDeviceProtocol.pkgEnterRealTimeCtrlMode(
                Long.parseLong(mDeviceId),
                Integer.parseInt(etStarDmx.getText().toString()),
                Integer.parseInt(etDevNum.getText().toString()),
                dataOut)
        );

        if (isFinish) {
            // 当前组喷射完成，进入到下一组，继续执行下一组
            mCurrMasterId++;
            if (mCurrMasterId >= mMasterCtrlMgrList.size()) {
                Log.i("CMCML", "老子终于喷完了！！！");

                // 设置不可编辑状态
                mAdapter.isClickItem(false);
                imgMasterMode.setClickable(false);
                etStarDmx.setFocusable(false);
                etDevNum.setFocusable(false);
                etStarDmx.setFocusableInTouchMode(false);
                etDevNum.setFocusableInTouchMode(false);

                // 按钮状态初始化
                imgStartStop.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_start));
                isPause = PAUSE_STATUS_CANNOT;
                imgPauseGoon.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_pause_cannot));

                // 喷射停止状态
                isStar = false;
                // 齐喷五次
                togetherFiveStopPause();
                // 清料
                clearMaterialMaster();
            }
        }

    }

    private void createMasterCtrlMgrList() {
        // 查询数据库保存信息
        mJetModes = LitePal.where("devId = ?", String.valueOf(mDeviceId)).find(MasterCtrlModeEntity.class);

        // 给主控管理集合添加数据
        for (int i = 0; i < mJetModes.size(); i++) {
            switch (mJetModes.get(i).getType()) {
                case CONFIG_STREAM:
                    MgrStreamJet mgrStream = new MgrStreamJet();
                    mgrStream.setType(CONFIG_STREAM);
                    mMasterCtrlMgrList.add(mgrStream);
                    break;
                case CONFIG_RIDE:
                    MgrRideJet mgrRide = new MgrRideJet();
                    mgrRide.setType(CONFIG_RIDE);
                    mMasterCtrlMgrList.add(mgrRide);
                    break;
                case CONFIG_INTERVAL:
                    MgrIntervalJet mgrInterval = new MgrIntervalJet();
                    mgrInterval.setType(CONFIG_INTERVAL);
                    mMasterCtrlMgrList.add(mgrInterval);
                    break;
                case CONFIG_TOGETHER:
                    MgrTogetherJet mgrTogether = new MgrTogetherJet();
                    mgrTogether.setType(CONFIG_TOGETHER);
                    mMasterCtrlMgrList.add(mgrTogether);
                default:
                    break;
            }
        }
    }

    /** 获取当前的喷射控制类 */
    private MgrOutputJet getCurrentManager(int currMasterId) {
        Log.i("CMCML", "MasterCtrlMgrList SIZE: " + mMasterCtrlMgrList.size());

        switch (mMasterCtrlMgrList.get(currMasterId).getType()) {
            case CONFIG_STREAM:
                return setDataWithStream(currMasterId);
            case CONFIG_RIDE:
                return setDataWithRide(currMasterId);
            case CONFIG_INTERVAL:
                return setDataWithInterval(currMasterId);
            case CONFIG_TOGETHER:
                return setDataWithTogether(currMasterId);
            default:
                return null;
        }
    }

    @NonNull
    private MgrOutputJet setDataWithStream(int currMasterId) {
        List<CtrlStreamEntity> streamEntities =
                LitePal.where("groupId = ?", String.valueOf((int) mJetModes.get(currMasterId).getMillis())).find(CtrlStreamEntity.class);

        MgrStreamJet mgrStream = new MgrStreamJet();
        mgrStream.setType(CONFIG_STREAM);
        mgrStream.setDevCount(Integer.parseInt(etDevNum.getText().toString()));
        mgrStream.setCurrentTime(CURRENT_TIME);
        mgrStream.setLoopId(LOOP_ID);
        mgrStream.setHigh((byte) Integer.parseInt(DEFAULT_TOGETHER_HIGH));
        switch (streamEntities.size()) {
            case 1:
                mgrStream.setDirection(Integer.parseInt(streamEntities.get(0).getDirection()));
                mgrStream.setGap(Integer.parseInt(streamEntities.get(0).getGap()) * 10);
                mgrStream.setDuration(Integer.parseInt(streamEntities.get(0).getDuration()) * 10);
                mgrStream.setGapBig(Integer.parseInt(streamEntities.get(0).getGapBig()) * 10);
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
                LitePal.where("groupId = ?", String.valueOf((int) mJetModes.get(currMasterId).getMillis())).find(CtrlRideEntity.class);

        MgrRideJet mgrRide = new MgrRideJet();
        mgrRide.setType(CONFIG_RIDE);
        mgrRide.setDevCount(Integer.parseInt(etDevNum.getText().toString()));
        mgrRide.setCurrentTime(CURRENT_TIME);
        mgrRide.setLoopId(LOOP_ID);
        mgrRide.setHigh((byte) Integer.parseInt(DEFAULT_TOGETHER_HIGH));

        switch (rideEntities.size()) {
            case 1:
                mgrRide.setDirection(Integer.parseInt(rideEntities.get(0).getDirection()));
                mgrRide.setGap(Integer.parseInt(rideEntities.get(0).getGap()) * 10);
                mgrRide.setDuration(Integer.parseInt(rideEntities.get(0).getDuration()) * 10);
                mgrRide.setGapBig(Integer.parseInt(rideEntities.get(0).getGapBig()) * 10);
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
                LitePal.where("groupId = ?", String.valueOf((int) mJetModes.get(currMasterId).getMillis())).find(CtrlIntervalEntity.class);

        MgrIntervalJet mgrInterval = new MgrIntervalJet();
        mgrInterval.setType(CONFIG_INTERVAL);
        mgrInterval.setDevCount(Integer.parseInt(etDevNum.getText().toString()));
        mgrInterval.setCurrentTime(CURRENT_TIME);
        mgrInterval.setLoopId(LOOP_ID);
        switch (intervalEntities.size()) {
            case 1:
                mgrInterval.setGapBig(Integer.parseInt(intervalEntities.get(0).getGap()) * 10);
                mgrInterval.setDuration(Integer.parseInt(intervalEntities.get(0).getDuration()) * 10);
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
                LitePal.where("groupId = ?", String.valueOf((int) mJetModes.get(currMasterId).getMillis())).find(CtrlTogetherEntity.class);

        MgrTogetherJet mgrTogether = new MgrTogetherJet();
        mgrTogether.setType(CONFIG_TOGETHER);
        mgrTogether.setDevCount(Integer.parseInt(etDevNum.getText().toString()));
        mgrTogether.setCurrentTime(CURRENT_TIME);
        switch (togetherEntities.size()) {
            case 1:
                mgrTogether.setDuration(Integer.parseInt(togetherEntities.get(0).getDuration()) * 10);
                mgrTogether.setHigh((byte) Integer.parseInt(togetherEntities.get(0).getHigh()));
                break;
            default:
                mgrTogether.setDuration(Integer.parseInt(DEFAULT_TOGETHER_DURATION) * 10);
                mgrTogether.setHigh((byte) Integer.parseInt(DEFAULT_TOGETHER_HIGH));
                break;
        }

        return mgrTogether;
    }

    /** 配置喷射样式 */
    private void clickJetStyle() {
        // 显示 PopupWindow 同时背景变暗
        @SuppressLint("InflateParams")
        View inflate = LayoutInflater.from(this).inflate(R.layout.layout_selec_jet_mode_master, null, false);
        logicHandle(inflate);
        // 创建并显示 popWindow
        mPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(inflate)
                // 弹出 popWindow 时，背景是否变暗
                .enableBackgroundDark(true)
                // 控制亮度
                .setBgDarkAlpha(0.7f)
                .setOnDissmissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                    }
                })
                .create()
                // 设置 pop 位置为中央显示
                .showAtLocation(this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    private void setSQLiteData() {
        List<MasterCtrlNumEntity> groupIdList = LitePal
                .where("devId=?", String.valueOf(mDeviceId))
                .find(MasterCtrlNumEntity.class);

        MasterCtrlNumEntity entity = new MasterCtrlNumEntity();
        if (groupIdList.size() == 0) {
            // 增
            setEntity(entity);
            entity.save();
        } else {
            // 改
            setEntity(entity);
            entity.updateAll("devId=?", String.valueOf(mDeviceId));
        }
    }

    private void setEntity(MasterCtrlNumEntity entity) {
        entity.setDevId(mDeviceId);
        entity.setDevNum(etDevNum.getText().toString());
        entity.setStarDmx(etStarDmx.getText().toString());
    }

    /** 初始化数据库信息 */
    private void initConfigDB() {
        List<MasterCtrlNumEntity> jetNumes = LitePal.where("devId = ?", String.valueOf(mDeviceId)).find(MasterCtrlNumEntity.class);
        List<MasterCtrlModeEntity> jetModes = LitePal.where("devId = ?", String.valueOf(mDeviceId)).find(MasterCtrlModeEntity.class);

        // 设备数量及起始 DMX
        if (jetNumes.size() != 0) {
            etDevNum.setText(jetNumes.get(0).getDevNum());
            etStarDmx.setText(jetNumes.get(0).getStarDmx());
        }
        // 喷射效果列表
        if (jetModes != null && jetModes.size() > 0) {
            for (int i = 0; i < jetModes.size(); i++) {
                mList.add(new MasterCtrlModeEntity(jetModes.get(i).getType(), jetModes.get(i).getMillis()));
            }
        }
    }

    private void initRecyclerView() {
        // 解决滑动冲突
        rvMasterMode.setNestedScrollingEnabled(false);

        LinearLayoutManager manager = new LinearLayoutManager(this);
        rvMasterMode.setLayoutManager(manager);
        // 绑定适配器
        mAdapter = new DeMasterModeAdapter(this, mList);
        // 监听长按点击事件
        mAdapter.setLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!isStar) {
                    int position = (int) v.getTag();
                    showDelDialog(position);
                }
                return true;
            }
        });

        rvMasterMode.setAdapter(mAdapter);
    }

    /** 处理 PopWindow 逻辑 */
    private void logicHandle(View view) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long millis = System.currentTimeMillis();
                if (mPopWindow != null) {
                    mPopWindow.dissmiss();
                }
                switch (v.getId()) {
                    case R.id.master_mode_stream:
                        setMasterCtrl(CONFIG_STREAM, millis);
                        break;
                    case R.id.master_mode_ride:
                        setMasterCtrl(CONFIG_RIDE, millis);
                        break;
                    case R.id.master_mode_interval:
                        setMasterCtrl(CONFIG_INTERVAL, millis);
                        break;
                    case R.id.master_mode_together:
                        setMasterCtrl(CONFIG_TOGETHER, millis);
                        break;
                    default:
                        break;
                }
            }

            private void setMasterCtrl(String type, long millis) {
                // 保存主控喷射效果
                MasterCtrlModeEntity entity = new MasterCtrlModeEntity(type);
                entity.setDevId(mDeviceId);
                entity.setType(type);
                entity.setMillis(millis);
                entity.save();
                // 添加一条数据到集合，并刷新
                mList.add(new MasterCtrlModeEntity(type, millis));
                mAdapter.refreshList(mList);
            }
        };
        view.findViewById(R.id.master_mode_stream).setOnClickListener(listener);
        view.findViewById(R.id.master_mode_ride).setOnClickListener(listener);
        view.findViewById(R.id.master_mode_interval).setOnClickListener(listener);
        view.findViewById(R.id.master_mode_together).setOnClickListener(listener);
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

    private void showExitDialog() {
        mDialog = new SelfDialogBase(this);
        mDialog.setTitle("设备喷射中...若退出将停止喷射");
        mDialog.setYesOnclickListener("退出", new SelfDialogBase.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                // 停止喷射
                stopJet();
                finish();
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
}
