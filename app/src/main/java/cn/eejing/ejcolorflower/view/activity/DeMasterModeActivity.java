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
import cn.eejing.ejcolorflower.model.manager.MasterOutputManager;
import cn.eejing.ejcolorflower.model.manager.MgrIntervalMaster;
import cn.eejing.ejcolorflower.model.manager.MgrRideMaster;
import cn.eejing.ejcolorflower.model.manager.MgrStreamMaster;
import cn.eejing.ejcolorflower.model.manager.MgrTogetherMaster;
import cn.eejing.ejcolorflower.presenter.OnReceivePackage;
import cn.eejing.ejcolorflower.view.adapter.DeMasterModeAdapter;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_INTERVAL;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_RIDE;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_STREAM;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_TOGETHER;
import static cn.eejing.ejcolorflower.app.AppConstant.TAG_DEV;

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

    private boolean isStart;
    private int isPause;
    private String mDeviceId;
    private CustomPopWindow mPopWindow;
    private DeMasterModeAdapter mAdapter;
    private List<MasterCtrlModeEntity> mList;

    private AppActivity.FireworkDevCtrl mDevCtrl;
    private Handler mHandler;
    // 当前主控喷射效果ID及标志位
    private int mCurrMasterId, mFlagCurrId;

    // 主控输入控制
    private Runnable mRunnableMaster = new Runnable() {
        @Override
        public void run() {
            // 定时调用方法（每 0.1 秒给通过蓝牙设备发一次信息）
            timerCallingMethod();
            mHandler.postDelayed(this, 100);
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
        mHandler.removeCallbacks(mRunnableMaster);
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

    private void clickPauseGoon() {
        switch (isPause) {
            case PAUSE_STATUS_ING:
                // 如果是暂停状态，点击变为恢复状态，可以继续喷射
                isPause = PAUSE_STATUS_GOON;
                imgPauseGoon.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_pause));
                break;
            case PAUSE_STATUS_GOON:
                // 如果是恢复状态，点击变为暂停状态，暂停喷射
                isPause = PAUSE_STATUS_ING;
                imgPauseGoon.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_pause_goon));
                break;
            default:
                break;
        }
    }

    private void clickStartStop() {
        if (etDevNum.length() == 0) {
            Toast.makeText(this, "请您设置设备数量", Toast.LENGTH_SHORT).show();
        } else if (etStarDmx.length() == 0) {
            Toast.makeText(this, "请您设置起始 DMX", Toast.LENGTH_SHORT).show();
        } else if (mList.size() == 0) {
            Toast.makeText(this, "请添加喷射效果", Toast.LENGTH_SHORT).show();
        } else {
            if (isStart) {
                // 如果是开始喷射状态，点击变为停止状态，暂停不可点击
                isStart = false;
                imgStartStop.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_start));
                isPause = PAUSE_STATUS_CANNOT;
                imgPauseGoon.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_pause_cannot));
                // 发送退出实时控制模式命令
                Log.e("CMCML", "发送退出实时控制模式命令");
                mDevCtrl.sendCommand(Long.parseLong(mDeviceId), BleDeviceProtocol.pkgExitRealTimeCtrlMode(Long.parseLong(mDeviceId)),
                        new OnReceivePackage() {
                            @Override
                            public void ack(@NonNull byte[] pkg) {
                                Log.e("CMCML", "FLAG");

                                int info = BleDeviceProtocol.parseExitRealTimeCtrlMode(pkg, pkg.length);
                                Log.e("CMCML", "exit ack: " + info);
                            }

                            @Override
                            public void timeout() {
                                Log.e("CMCML", "退出实时控制模式命令超时！");
//                        Log.e(TAG_DEV, "退出实时控制模式命令超时！");
                            }
                        });
            } else {
                // 如果是停止喷射状态，点击变为开始状态，暂停可点击
                isStart = true;
                imgStartStop.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_stop));
                isPause = PAUSE_STATUS_GOON;
                imgPauseGoon.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_pause));
                // 保存或更新设备数量及起始 DMX
                setSQLiteData();
                // 启动计时器，0.1 秒
                mHandler.postDelayed(mRunnableMaster, 100);
            }
        }
    }

    /** 定时调用方法（每 0.1 秒给通过蓝牙设备发一次信息）*/
    private void timerCallingMethod() {
        if (mFlagCurrId != mCurrMasterId) {
            mCurrentManager = getCurrentManager(mFlagCurrId);
            // 如果当前运行的 ID 与 flag 不同，让其相等
            mFlagCurrId = mCurrMasterId;
        }

        // 创建主控管理集合
        if (mMasterCtrlMgrList.size() == 0) {
            // 如果主控管理集合没有数据，创建添加
            createMasterCtrlMgrList();
        }

        // 发送进入在线实时控制模式命令
        byte[] dataOut = new byte[300];
        mDevCtrl.sendCommand(Long.parseLong(mDeviceId), BleDeviceProtocol.pkgEnterRealTimeCtrlMode(
                Long.parseLong(mDeviceId),
                Integer.parseInt(etStarDmx.getText().toString()),
                Integer.parseInt(etDevNum.getText().toString()),
                dataOut)
        );

        try {
            // 调用方法判断当前组是否完成喷射
            boolean isFinish;
            if (mCurrentManager != null) {
                isFinish = mCurrentManager.updateWithDataOut(dataOut);
            } else {
                isFinish = getCurrentManager(0).updateWithDataOut(dataOut);
            }
            if (isFinish) {
                // 当前组喷射完成，进入到下一组，继续执行下一组
                mCurrMasterId++;
                if (mCurrMasterId >= mMasterCtrlMgrList.size()) {
                    // 整个喷射过程完成，停止计时器
                    mHandler.removeCallbacks(mRunnableMaster);
                    // 发送退出实时控制模式命令
                    mDevCtrl.sendCommand(Long.parseLong(mDeviceId), BleDeviceProtocol.pkgExitRealTimeCtrlMode(
                            Long.parseLong(mDeviceId)
                    ));
                    // 按钮状态初始化
                    isStart = false;
                    imgStartStop.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_start));
                    isPause = PAUSE_STATUS_CANNOT;
                    imgPauseGoon.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_pause_cannot));
                }
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

    }

    private void createMasterCtrlMgrList() {
        // 查询数据库保存信息
        mJetModes = LitePal.where("devId = ?", String.valueOf(mDeviceId)).find(MasterCtrlModeEntity.class);

        Log.e("CMCML", "JetMode SIZE: " + mJetModes.size());

        // 给主控管理集合添加数据
        for (int i = 0; i < mJetModes.size(); i++) {
            switch (mJetModes.get(i).getType()) {
                case CONFIG_STREAM:
                    List<CtrlStreamEntity> streamEntities =
                            LitePal.where("groupId = ?", String.valueOf((int) mJetModes.get(i).getMillis())).find(CtrlStreamEntity.class);

                    MgrStreamMaster mgrStream = new MgrStreamMaster();
                    mgrStream.setDevCount(Integer.parseInt(etDevNum.getText().toString()));
                    mgrStream.setCurrentTime(0);
                    mgrStream.setLoopId(0);
                    mgrStream.setDirection(Integer.parseInt(streamEntities.get(0).getDirection()));
                    mgrStream.setGap(Integer.parseInt(streamEntities.get(0).getGap()));
                    mgrStream.setDuration(Integer.parseInt(streamEntities.get(0).getDuration()));
                    mgrStream.setGapBig(Integer.parseInt(streamEntities.get(0).getGapBig()));
                    mgrStream.setLoop(Integer.parseInt(streamEntities.get(0).getLoop()));
                    mgrStream.setHigh((byte) Integer.parseInt(streamEntities.get(0).getHigh()));

                    mMasterCtrlMgrList.add(mgrStream);
                    break;
                case CONFIG_RIDE:
                    List<CtrlRideEntity> rideEntities =
                            LitePal.where("groupId = ?", String.valueOf((int) mJetModes.get(i).getMillis())).find(CtrlRideEntity.class);

                    MgrRideMaster mgrRide = new MgrRideMaster();
                    mgrRide.setDevCount(Integer.parseInt(etDevNum.getText().toString()));
                    mgrRide.setCurrentTime(0);
                    mgrRide.setLoopId(0);
                    mgrRide.setDirection(Integer.parseInt(rideEntities.get(0).getDirection()));
                    mgrRide.setGap(Integer.parseInt(rideEntities.get(0).getGap()));
                    mgrRide.setDuration(Integer.parseInt(rideEntities.get(0).getDuration()));
                    mgrRide.setGapBig(Integer.parseInt(rideEntities.get(0).getGapBig()));
                    mgrRide.setLoop(Integer.parseInt(rideEntities.get(0).getLoop()));
                    mgrRide.setHigh((byte) Integer.parseInt(rideEntities.get(0).getHigh()));

                    mMasterCtrlMgrList.add(mgrRide);
                    break;
                case CONFIG_INTERVAL:
                    List<CtrlIntervalEntity> intervalEntities =
                            LitePal.where("groupId = ?", String.valueOf((int) mJetModes.get(i).getMillis())).find(CtrlIntervalEntity.class);

                    MgrIntervalMaster mgrInterval = new MgrIntervalMaster();
                    mgrInterval.setDevCount(Integer.parseInt(etDevNum.getText().toString()));
                    mgrInterval.setCurrentTime(0);
                    mgrInterval.setLoopId(0);
                    mgrInterval.setGapBig(Integer.parseInt(intervalEntities.get(0).getGap()));
                    mgrInterval.setDuration(Integer.parseInt(intervalEntities.get(0).getDuration()));
                    mgrInterval.setLoop(Integer.parseInt(intervalEntities.get(0).getFrequency()));

                    mMasterCtrlMgrList.add(mgrInterval);
                    break;
                case CONFIG_TOGETHER:
                    List<CtrlTogetherEntity> togetherEntities =
                            LitePal.where("groupId = ?", String.valueOf((int) mJetModes.get(i).getMillis())).find(CtrlTogetherEntity.class);

                    MgrTogetherMaster mgrTogether = new MgrTogetherMaster();
                    mgrTogether.setDevCount(Integer.parseInt(etDevNum.getText().toString()));
                    mgrTogether.setCurrentTime(0);
                    mgrTogether.setDuration(Integer.parseInt(togetherEntities.get(0).getDuration()));
                    mgrTogether.setHigh((byte) Integer.parseInt(togetherEntities.get(0).getHigh()));

                    mMasterCtrlMgrList.add(mgrTogether);
                default:
                    break;
            }
        }
    }

    /** 获取当前的喷射控制类 */
    private MasterOutputManager getCurrentManager(int currMasterId) {
        mJetModes = LitePal.where("devId = ?", String.valueOf(mDeviceId)).find(MasterCtrlModeEntity.class);
        Log.i("CMCML", "MasterCtrlMgrList SIZE: " + mJetModes.size());
        Log.i("CMCML", "MasterCtrlMgrList SIZE: " + mMasterCtrlMgrList.size());
        switch (mJetModes.get(currMasterId).getType()) {
            case CONFIG_STREAM:
                return new MgrStreamMaster();
            case CONFIG_RIDE:
                return new MgrRideMaster();
            case CONFIG_INTERVAL:
                return new MgrIntervalMaster();
            case CONFIG_TOGETHER:
                return new MgrTogetherMaster();
            default:
                break;
        }

        return null;
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
