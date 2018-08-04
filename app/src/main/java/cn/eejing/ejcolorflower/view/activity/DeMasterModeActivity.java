package cn.eejing.ejcolorflower.view.activity;

import android.annotation.SuppressLint;
import android.os.Handler;
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
import cn.eejing.ejcolorflower.model.manager.MasterOutputManager;
import cn.eejing.ejcolorflower.model.manager.MgrIntervalMaster;
import cn.eejing.ejcolorflower.model.manager.MgrRideMaster;
import cn.eejing.ejcolorflower.model.manager.MgrStreamMaster;
import cn.eejing.ejcolorflower.model.manager.MgrTogetherMaster;
import cn.eejing.ejcolorflower.view.adapter.DeMasterModeAdapter;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_INTERVAL;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_RIDE;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_STREAM;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_TOGETHER;

/**
 * 设置主控模式
 */

public class DeMasterModeActivity extends BaseActivity {
    private static final int PAUSE_STATUS_CANNOT = 0;        // 不可点击-停止状态
    private static final int PAUSE_STATUS_ING = 1;           // 暂停状态
    private static final int PAUSE_STATUS_GOON = 2;          // 喷射状态-点击恢复之后

    @BindView(R.id.rv_master_mode)           RecyclerView rvMasterMode;
    @BindView(R.id.img_start_or_stop)        ImageView imgStartStop;
    @BindView(R.id.img_pause_or_goon)        ImageView imgPauseGoon;
    @BindView(R.id.et_dev_num)               EditText etDevNum;
    @BindView(R.id.et_start_dmx)             EditText etStarDmx;

    // 是否开始喷射，所有喷射是否停止
    private boolean isStart, isStopRunnableMaster;
    private int isPause;
    private String mDeviceId;
    private CustomPopWindow mPopWindow;
    private DeMasterModeAdapter mAdapter;
    private List<MasterCtrlModeEntity> mList;

    private AppActivity.FireworkDevCtrl mDevCtrl;
    // 当前主控喷射效果ID及标志位
    private int mCurrMasterId, mFlagCurrId;

    private Handler mHandler;
    // 主控输入控制
    private final Runnable mRunnableMaster = new Runnable() {
        @Override
        public void run() {
            // 定时调用方法（每 0.1 秒给通过蓝牙设备发一次信息）
            timerCallingMethod();
            if (!isStopRunnableMaster) {
                // 如果整体循环没有结束，继续发送
                mHandler.postDelayed(this, 100);
            }
        }
    };
    private MasterOutputManager mCurrentManager;
    private List<MasterCtrlModeEntity> mJetModes;
    private List<MasterOutputManager> mMasterCtrlMgrList;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_de_master_mode;
    }

    @Override
    public void initView() {
        setToolbar("设置主控模式", View.VISIBLE);
        mDeviceId = getIntent().getStringExtra("device_id");

        mList = new ArrayList<>();
        mMasterCtrlMgrList = new ArrayList<>();
        mHandler = new Handler();
        mDevCtrl = AppActivity.getFireworksDevCtrl();

        initConfigDB();
        initRecyclerView();
    }

    @Override
    protected void onDestroy() {
        isStopRunnableMaster = true;
        super.onDestroy();
    }

    @OnClick({R.id.img_start_or_stop, R.id.img_pause_or_goon, R.id.img_add_master_mode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
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

    /** 点击暂停或继续 */
    private void clickPauseGoon() {
        switch (isPause) {
            case PAUSE_STATUS_ING:
                // 如果是暂停状态，点击变为恢复状态，可以继续喷射
                isPause = PAUSE_STATUS_GOON;
                imgPauseGoon.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_pause));
                // 启动计时器，0.1 秒
                isStopRunnableMaster = false;
                mHandler.post(mRunnableMaster);
                break;
            case PAUSE_STATUS_GOON:
                // 齐喷五次
                togetherFiveStopPause();
                // 如果是恢复状态，点击变为暂停状态，暂停喷射
                isPause = PAUSE_STATUS_ING;
                imgPauseGoon.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_pause_goon));
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
            if (isStart) {
                // 齐喷五次
                togetherFiveStopPause();

                // 如果是开始喷射状态，点击变为停止状态，暂停不可点击
                isStart = false;
                imgStartStop.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_start));
                isPause = PAUSE_STATUS_CANNOT;
                imgPauseGoon.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_pause_cannot));
            } else {
                // 如果是停止喷射状态，点击变为开始状态，暂停可点击
                mCurrMasterId = 0;
                mFlagCurrId = 0;

                mCurrentManager = null;
                mMasterCtrlMgrList.clear();
                isStopRunnableMaster = false;
                isStart = true;
                imgStartStop.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_stop));
                isPause = PAUSE_STATUS_GOON;
                imgPauseGoon.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_pause));
                // 保存或更新设备数量及起始 DMX
                setSQLiteData();
                // 启动计时器，0.1 秒
                isStopRunnableMaster = false;
                mHandler.post(mRunnableMaster);
            }
        }
    }

    /** 发送喷射五次命令 */
    private void togetherFiveStopPause() {
        byte[] dataOut = new byte[300];
        // 喷射 5 次高度为 0，持续时间 0.1s 齐喷效果
        MgrTogetherMaster mgrStop = new MgrTogetherMaster();
        mgrStop.setType(CONFIG_TOGETHER);
        mgrStop.setDevCount(Integer.parseInt(etDevNum.getText().toString()));
        mgrStop.setCurrentTime(0);
        mgrStop.setDuration(1);
        mgrStop.setHigh((byte) 0);
        mgrStop.updateWithDataOut(dataOut);
        for (int i = 0; i < 5; i++) {
            Log.i("CMCML", "第" + (i + 1) + "次停止喷射命令 star！");

            mDevCtrl.sendCommand(Long.parseLong(mDeviceId), BleDeviceProtocol.pkgEnterRealTimeCtrlMode(
                    Long.parseLong(mDeviceId),
                    Integer.parseInt(etStarDmx.getText().toString()),
                    Integer.parseInt(etDevNum.getText().toString()),
                    dataOut)
            );

            Log.i("CMCML", "第" + (i + 1) + "次停止喷射命令 over！");
        }

        // 发送退出实时控制模式命令
        isStopRunnableMaster = true;
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
            isStopRunnableMaster = true;
            return;
        }

        if (mFlagCurrId != mCurrMasterId) {
            // 如果当前运行的 ID 与 flag 不同，让其相等
            mFlagCurrId = mCurrMasterId;
            mCurrentManager = getCurrentManager(mCurrMasterId);
        }

        // 发送进入在线实时控制模式命令
        byte[] dataOut = new byte[300];
        // 调用方法判断当前组是否完成喷射
        boolean isFinish = false;
        if (!isStopRunnableMaster) {
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

                // 齐喷五次
                togetherFiveStopPause();

                // 按钮状态初始化
                isStart = false;
                imgStartStop.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_start));
                isPause = PAUSE_STATUS_CANNOT;
                imgPauseGoon.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_pause_cannot));
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
                    MgrStreamMaster mgrStream = new MgrStreamMaster();
                    mgrStream.setType(CONFIG_STREAM);
                    mMasterCtrlMgrList.add(mgrStream);
                    break;
                case CONFIG_RIDE:
                    MgrRideMaster mgrRide = new MgrRideMaster();
                    mgrRide.setType(CONFIG_RIDE);
                    mMasterCtrlMgrList.add(mgrRide);
                    break;
                case CONFIG_INTERVAL:
                    MgrIntervalMaster mgrInterval = new MgrIntervalMaster();
                    mgrInterval.setType(CONFIG_INTERVAL);
                    mMasterCtrlMgrList.add(mgrInterval);
                    break;
                case CONFIG_TOGETHER:
                    MgrTogetherMaster mgrTogether = new MgrTogetherMaster();
                    mgrTogether.setType(CONFIG_TOGETHER);
                    mMasterCtrlMgrList.add(mgrTogether);
                default:
                    break;
            }
        }
    }

    /** 获取当前的喷射控制类 */
    private MasterOutputManager getCurrentManager(int currMasterId) {
        Log.i("CMCML", "MasterCtrlMgrList SIZE: " + mMasterCtrlMgrList.size());

        switch (mMasterCtrlMgrList.get(currMasterId).getType()) {
            case CONFIG_STREAM:
                List<CtrlStreamEntity> streamEntities =
                        LitePal.where("groupId = ?", String.valueOf((int) mJetModes.get(currMasterId).getMillis())).find(CtrlStreamEntity.class);

                MgrStreamMaster mgrStream = new MgrStreamMaster();
                mgrStream.setType(CONFIG_STREAM);
                mgrStream.setDevCount(Integer.parseInt(etDevNum.getText().toString()));
                mgrStream.setCurrentTime(0);
                mgrStream.setLoopId(0);
                mgrStream.setDirection(Integer.parseInt(streamEntities.get(0).getDirection()));
                mgrStream.setGap(Integer.parseInt(streamEntities.get(0).getGap())*10);
                mgrStream.setDuration(Integer.parseInt(streamEntities.get(0).getDuration())*10);
                mgrStream.setGapBig(Integer.parseInt(streamEntities.get(0).getGapBig())*10);
                mgrStream.setLoop(Integer.parseInt(streamEntities.get(0).getLoop()));
                mgrStream.setHigh((byte) Integer.parseInt(streamEntities.get(0).getHigh()));

                return mgrStream;
            case CONFIG_RIDE:
                List<CtrlRideEntity> rideEntities =
                        LitePal.where("groupId = ?", String.valueOf((int) mJetModes.get(currMasterId).getMillis())).find(CtrlRideEntity.class);

                MgrRideMaster mgrRide = new MgrRideMaster();
                mgrRide.setType(CONFIG_RIDE);
                mgrRide.setDevCount(Integer.parseInt(etDevNum.getText().toString()));
                mgrRide.setCurrentTime(0);
                mgrRide.setLoopId(0);
                mgrRide.setDirection(Integer.parseInt(rideEntities.get(0).getDirection()));
                mgrRide.setGap(Integer.parseInt(rideEntities.get(0).getGap()) * 10);
                mgrRide.setDuration(Integer.parseInt(rideEntities.get(0).getDuration()) * 10);
                mgrRide.setGapBig(Integer.parseInt(rideEntities.get(0).getGapBig()) * 10);
                mgrRide.setLoop(Integer.parseInt(rideEntities.get(0).getLoop()));
                mgrRide.setHigh((byte) Integer.parseInt(rideEntities.get(0).getHigh()));

                return mgrRide;
            case CONFIG_INTERVAL:
                List<CtrlIntervalEntity> intervalEntities =
                        LitePal.where("groupId = ?", String.valueOf((int) mJetModes.get(currMasterId).getMillis())).find(CtrlIntervalEntity.class);

                MgrIntervalMaster mgrInterval = new MgrIntervalMaster();
                mgrInterval.setType(CONFIG_INTERVAL);
                mgrInterval.setDevCount(Integer.parseInt(etDevNum.getText().toString()));
                mgrInterval.setCurrentTime(0);
                mgrInterval.setLoopId(0);
                mgrInterval.setGapBig(Integer.parseInt(intervalEntities.get(0).getGap()) * 10);
                mgrInterval.setDuration(Integer.parseInt(intervalEntities.get(0).getDuration()) * 10);
                mgrInterval.setLoop(Integer.parseInt(intervalEntities.get(0).getFrequency()));

                return mgrInterval;
            case CONFIG_TOGETHER:
                List<CtrlTogetherEntity> togetherEntities =
                        LitePal.where("groupId = ?", String.valueOf((int) mJetModes.get(currMasterId).getMillis())).find(CtrlTogetherEntity.class);

                MgrTogetherMaster mgrTogether = new MgrTogetherMaster();
                mgrTogether.setType(CONFIG_TOGETHER);
                mgrTogether.setDevCount(Integer.parseInt(etDevNum.getText().toString()));
                mgrTogether.setCurrentTime(0);
                mgrTogether.setDuration(Integer.parseInt(togetherEntities.get(0).getDuration()) * 10);
                mgrTogether.setHigh((byte) Integer.parseInt(togetherEntities.get(0).getHigh()));

                return mgrTogether;
            default:
                return null;
        }
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
}
