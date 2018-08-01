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

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.device.BleDeviceProtocol;
import cn.eejing.ejcolorflower.model.event.JetStatusEvent;
import cn.eejing.ejcolorflower.model.lite.CtrlIntervalEntity;
import cn.eejing.ejcolorflower.model.lite.CtrlRideEntity;
import cn.eejing.ejcolorflower.model.lite.CtrlStreamEntity;
import cn.eejing.ejcolorflower.model.lite.CtrlTogetherEntity;
import cn.eejing.ejcolorflower.model.lite.MasterCtrlModeEntity;
import cn.eejing.ejcolorflower.model.lite.MasterCtrlNumEntity;
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

    private boolean isStart;
    private int isPause;
    private String mDeviceId;
    private CustomPopWindow mPopWindow;
    private DeMasterModeAdapter mAdapter;
    private List<MasterCtrlModeEntity> mList;

    private AppActivity.FireworkDevCtrl mDevCtrl;
    private Handler mHandler;
    private int mCurrMasterId, mFlagId;

    private String mConfigType;
    private long mMillis;
    private int mPostGroupId, mGap, mDirection, mDuration, mGapBig, mLoop, mFrequency, mHigh;

    // 主控输入控制
    private Runnable mRunnableMaster = new Runnable() {
        @Override
        public void run() {
            // 定时调用方法（每 0.1 秒给通过蓝牙设备发一次信息）
            timerCallingMethod();
            mHandler.postDelayed(this, 100);
        }
    };
    private List<MasterCtrlModeEntity> mJetModes;
    private MasterCtrlModeEntity mCurrentManager;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_de_master_mode;
    }

    @Override
    public void initView() {
        setToolbar("设置主控模式", View.VISIBLE);
        mDeviceId = getIntent().getStringExtra("device_id");

        mList = new ArrayList<>();
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
        mJetModes = LitePal.where("devId = ?", String.valueOf(mDeviceId)).find(MasterCtrlModeEntity.class);

        // 事件处理 传 mCurrMasterId
        if (mFlagId != mCurrMasterId) {
            // 获取当前主控管理
            mCurrentManager = getCurrentManager(mFlagId);
            // 如果当前运行的 ID 与 flag 不同，让其相等
            mFlagId = mCurrMasterId;
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
                isFinish = mJetModes.get(mCurrMasterId).updateWithDataOut(dataOut);
            }
            if (isFinish) {
                // 当前组喷射完成，进入到下一组，继续执行下一组
                mCurrMasterId++;
                if (mCurrMasterId >= mJetModes.size()) {
                    // 整个喷射过程完成，停止计时器
                    mHandler.removeCallbacks(mRunnableMaster);
                    // 发送退出实时控制模式命令
                    mDevCtrl.sendCommand(Long.parseLong(mDeviceId),BleDeviceProtocol.pkgExitRealTimeCtrlMode(
                            Long.parseLong(mDeviceId),0x55
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

    /** 获取当前的喷射控制类 */
    private MasterCtrlModeEntity getCurrentManager(int currMasterId) {
        mJetModes = LitePal.where("devId = ?", String.valueOf(mDeviceId)).find(MasterCtrlModeEntity.class);

        try {
            String type = mJetModes.get(currMasterId).getType();
            switch (type) {
                case CONFIG_STREAM:
                    CtrlStreamEntity mgrStream = new CtrlStreamEntity(type);
                    mgrStream.setDevCount(Integer.parseInt(etDevNum.getText().toString()));
                    mgrStream.currentTime = 0;
                    mgrStream.loopId = 0;
                    mgrStream.setLoop("2");
                    mgrStream.setDirection("1");
                    mgrStream.setGap("2");
                    mgrStream.setDuration("2");
                    mgrStream.setGapBig("2");
                    mgrStream.setHigh("100");
                    return mgrStream;
                case CONFIG_RIDE:
                    CtrlRideEntity mgrRide = new CtrlRideEntity(type);
                    mgrRide.setDevCount(Integer.parseInt(etDevNum.getText().toString()));
                    mgrRide.currentTime = 0;
                    mgrRide.loopId = 0;
                    mgrRide.setLoop("2");
                    mgrRide.setDirection("1");
                    mgrRide.setGap("2");
                    mgrRide.setDuration("2");
                    mgrRide.setGapBig("2");
                    mgrRide.setHigh("100");
                    return mgrRide;
                case CONFIG_INTERVAL:
                    CtrlIntervalEntity mgrInterval = new CtrlIntervalEntity(type);
                    mgrInterval.setDevCount(Integer.parseInt(etDevNum.getText().toString()));
                    mgrInterval.currentTime = 0;
                    mgrInterval.loopId = 0;
                    mgrInterval.setDuration("2");
                    mgrInterval.setGap("2");
                    mgrInterval.setFrequency("2");
                    return mgrInterval;
                case CONFIG_TOGETHER:
                    CtrlTogetherEntity mgrTogether = new CtrlTogetherEntity(type);
                    mgrTogether.setDevCount(Integer.parseInt(etDevNum.getText().toString()));
                    mgrTogether.currentTime = 0;
                    mgrTogether.setDuration("2");
                    mgrTogether.setHigh("30");
                    return mgrTogether;
                default:
                    break;
            }
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
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

    /** 配置喷射信息界面返回数据 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventJetStatus(JetStatusEvent event) {
        mConfigType = event.getType();
        Log.i("JET_STATUS", "onEventJetStatus: " + mConfigType);
        switch (mConfigType) {
            case CONFIG_STREAM:
            case CONFIG_RIDE:
                initStatus();
                mPostGroupId = event.getGroupId();
                mGap = event.getGap() * 1000;
                mDuration = event.getDuration() * 10;
                mGapBig = event.getGapBig();
                mLoop = event.getLoop();
                mHigh = event.getHigh();
                mDirection = event.getmDirection();
                mMillis = event.getMillis();
                break;
            case CONFIG_INTERVAL:
                initStatus();
                mPostGroupId = event.getGroupId();
                mGap = event.getGap() * 1000;
                mDuration = event.getDuration() * 10;
                mFrequency = event.getFrequency();
                mHigh = event.getHigh();
                mMillis = event.getMillis();
                break;
            case CONFIG_TOGETHER:
                initStatus();
                mPostGroupId = event.getGroupId();
                mDuration = event.getDuration() * 10;
                mHigh = event.getHigh();
                mMillis = event.getMillis();
                break;
            default:
                break;
        }

        Log.i("JET_STATUS", "mPostGroupId: " + mPostGroupId);
        Log.i("JET_STATUS", "configType: " + mConfigType);
        Log.i("JET_STATUS", "direction: 方向--->" + mDirection);
        Log.i("JET_STATUS", "gap: 间隔时间--->" + mGap);
        Log.i("JET_STATUS", "duration: 持续时间--->" + mDuration);
        Log.i("JET_STATUS", "gapBig: 大间隔时间--->" + mGapBig);
        Log.i("JET_STATUS", "loop: 循环次数--->" + mLoop);
        Log.i("JET_STATUS", "frequency: 次数（换向）--->" + mFrequency);
        Log.i("JET_STATUS", "high: 高度--->" + mHigh);
        Log.i("JET_STATUS", "millis: 时间戳--->" + mMillis);
    }

    private void initStatus() {
        mDirection = 0;
        mGap = 0;
        mDuration = 0;
        mGapBig = 0;
        mLoop = 0;
        mFrequency = 0;
        mHigh = 0;
    }

}
