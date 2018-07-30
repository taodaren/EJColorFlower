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
import cn.eejing.ejcolorflower.model.event.JetStatusEvent;
import cn.eejing.ejcolorflower.model.lite.MasterModeEntity;
import cn.eejing.ejcolorflower.model.request.AddMasterModeBean;
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

    @BindView(R.id.rv_master_mode)           RecyclerView rvMasterMode;
    @BindView(R.id.img_start_or_stop)        ImageView imgStartStop;
    @BindView(R.id.img_pause_or_goon)        ImageView imgPauseGoon;
    @BindView(R.id.et_dev_num)               EditText etDevNum;
    @BindView(R.id.et_start_dmx)             EditText etStarDmx;

    private boolean isStart, isPause;
    private CustomPopWindow mCustomPopWindow;
    private DeMasterModeAdapter mAdapter;
    private List<AddMasterModeBean> mList;
    private Handler mHandler;

    private String mConfigType;
    private long mMillis;
    private int mPostGroupId, mGap, mDirection, mDuration, mGapBig, mLoop, mFrequency, mHigh;

    // 主控输入控制
    private Runnable mRunnableMaster = new Runnable() {
        @Override
        public void run() {
            // 要做的事情
            // 定时调用方法
            Log.i("JLTHMODE", "MODE");
//            updateWithDataOut();
            // 每 0.1 秒调用一次方法
            mHandler.postDelayed(this, 100);
        }
    };

    @Override
    protected int layoutViewId() {
        return R.layout.activity_de_master_mode;
    }

    @Override
    public void initView() {
        setToolbar("设置主控模式", View.VISIBLE);
        mList = new ArrayList<>();
        mHandler = new Handler();
        // 数据库中查询是否保存信息
        isSaveDBInfo();

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
                if (etDevNum.getText().toString() == null || etDevNum.length() == 0) {
                    Toast.makeText(this, "请您设置设备数量", Toast.LENGTH_SHORT).show();
                } else if (etStarDmx.getText().toString() == null || etStarDmx.length() == 0) {
                    Toast.makeText(this, "请您设置起始 DMX", Toast.LENGTH_SHORT).show();
                } else {
                    isStart = true;
                    // 暂停按钮变为可点击状态
                    isPause = true;
                    imgPauseGoon.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_pause));
                    // 启动计时器，0.1 秒
                    mHandler.postDelayed(mRunnableMaster, 100);
                }
                break;
            case R.id.img_pause_or_goon:
                if (isStart) {
                    // 开始喷射状态可以暂停，再点击变为继续
                    imgPauseGoon.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_pause_goon));
                } else {
                    // 未启动/停止状态禁用暂停
                    isPause = false;
                    imgPauseGoon.setImageDrawable(getResources().getDrawable(R.drawable.ic_jet_pause_cannot));
                }
                break;
            case R.id.img_add_master_mode:
                // 配置喷射样式
                configJetStyle();
                break;
        }
    }

    private void isSaveDBInfo() {
        List<MasterModeEntity> entities = LitePal.findAll(MasterModeEntity.class);
        if (entities != null && entities.size() > 0) {
            for (int i = 0; i < entities.size(); i++) {
                mList.add(new AddMasterModeBean(entities.get(i).getMode(), entities.get(i).getMillis()));
            }
        }
    }

    private void configJetStyle() {
        // 显示 PopupWindow 同时背景变暗
        @SuppressLint("InflateParams")
        View contentView = LayoutInflater.from(this).inflate(R.layout.layout_selec_jet_mode_master, null, false);
        handleLogic(contentView);
        // 创建并显示 popWindow
        mCustomPopWindow = new CustomPopWindow.PopupWindowBuilder(this)
                .setView(contentView)
                // 弹出 popWindow 时，背景是否变暗
                .enableBackgroundDark(true)
                // 控制亮度
                .setBgDarkAlpha(0.7f)
                .setOnDissmissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        Log.e("TAG", "onDismiss");
                    }
                })
                .create()
                // 设置 pop 位置
                .showAtLocation(this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
    }

    private void handleLogic(View contentView) {
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long millis = System.currentTimeMillis();
                if (mCustomPopWindow != null) {
                    mCustomPopWindow.dissmiss();
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
                MasterModeEntity entity = new MasterModeEntity(type);
                entity.setMode(type);
                entity.setMillis(millis);
                entity.save();
                mList.add(new AddMasterModeBean(type, millis));
                mAdapter.refreshList(mList);
            }
        };
        contentView.findViewById(R.id.master_mode_stream).setOnClickListener(listener);
        contentView.findViewById(R.id.master_mode_ride).setOnClickListener(listener);
        contentView.findViewById(R.id.master_mode_interval).setOnClickListener(listener);
        contentView.findViewById(R.id.master_mode_together).setOnClickListener(listener);
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
