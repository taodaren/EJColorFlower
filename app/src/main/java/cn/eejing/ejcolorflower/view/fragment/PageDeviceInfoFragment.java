package cn.eejing.ejcolorflower.view.fragment;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sdsmdg.harjot.crollerTest.Croller;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.device.BleDeviceProtocol;
import cn.eejing.ejcolorflower.model.event.DeviceConnectEvent;
import cn.eejing.ejcolorflower.model.event.DmxZeroEvent;
import cn.eejing.ejcolorflower.util.SelfDialog;
import cn.eejing.ejcolorflower.view.activity.AppActivity;
import cn.eejing.ejcolorflower.view.base.BaseFragment;

import static cn.eejing.ejcolorflower.app.AppConstant.DEVICE_CONNECT_YES;

/**
 * 设备信息
 */

public class PageDeviceInfoFragment extends BaseFragment {

    @BindView(R.id.tv_dmx_address)            TextView tvDmxAddress;
    @BindView(R.id.img_temp_threshold)        ImageView imgTempThreshold;
    @BindView(R.id.croller)                   Croller croller;
    @BindView(R.id.layout_device_time)        RelativeLayout layoutDeviceTime;
    @BindView(R.id.ch_time_left)              Chronometer chTimeLeft;

    private int mDevInfo, mThresholdHigh;
    private long mDevId;
    private static int mDevTemp, mDevDmx, mDevTime;
    private SelfDialog mDialog;
    private Set<Integer> mDmxSet;
    private AppActivity.FireworkDevCtrl mDevCtrl;

    public static PageDeviceInfoFragment newInstance(int info, int thresholdHigh, int type, long deviceId) {
        Log.i("TAG", "newInstance: " + info);
        PageDeviceInfoFragment fragment = new PageDeviceInfoFragment();
        fragment.mDevInfo = info;
        fragment.mThresholdHigh = thresholdHigh;
        fragment.mDevId = deviceId;

        switch (type) {
            case AppConstant.TYPE_TEMP:
                mDevTemp = fragment.mDevInfo;
                break;
            case AppConstant.TYPE_DMX:
                mDevDmx = fragment.mDevInfo;
                break;
            case AppConstant.TYPE_TIME:
                mDevTime = fragment.mDevInfo;
                break;
            default:
        }
        return fragment;
    }

    @Override
    protected int layoutViewId() {
        return R.layout.fragment_page_device_info;
    }

    @Override
    public void initView(View rootView) {
        if (!EventBus.getDefault().isRegistered(this)) {
            // 注册服务之前检查服务是否已注册 EventBus.getDefault().isRegistered(...)或检查服务未停止的原因，因此仍然注册。
            EventBus.getDefault().register(this);
        }
        mDmxSet = new HashSet<>();
        mDevCtrl = AppActivity.getFireworksDevCtrl();

        setTempStatus();
        setDmxAddress();
        setTimeLeft();

        typeOfJudgment();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.tv_dmx_address)
    public void onClickDmxAddr() {
        showDialog();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getDeviceConnect(DeviceConnectEvent event) {
        // 接收硬件传过来的已连接设备信息添加到 HashSet
        mDmxSet.add(event.getConfig().mDMXAddress);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTimeLeft() {
        // 展示剩余时间
        long nowTimeLong = (long) mDevInfo * 1000;
        @SuppressLint("SimpleDateFormat") DateFormat ymdhmsFormat = new SimpleDateFormat("mm:ss");
        String nowTimeStr = ymdhmsFormat.format(nowTimeLong);
        chTimeLeft.setText(nowTimeStr);

        // SeekBar 禁止拖动和点击
        croller.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        // 最大值 2 小时
        croller.setMax(7200);
        // 当前剩余时间进度
        croller.setProgress(mDevInfo);
        // 设置剩余时间占比
        double conversion = (double) mDevInfo / 7200;
        NumberFormat instance = NumberFormat.getPercentInstance();
        instance.setMaximumFractionDigits(1);
        croller.setLabel(instance.format(conversion));
    }

    private void setDmxAddress() {
        tvDmxAddress.setText(String.valueOf(mDevInfo));
    }

    private void setTempStatus() {
        double tempLvOne, tempLvTwo, tempLvThree, tempLvFour, tempLvFive;

        tempLvOne = mThresholdHigh * (0.2);
        tempLvTwo = mThresholdHigh * (0.4);
        tempLvThree = mThresholdHigh * (0.6);
        tempLvFour = mThresholdHigh * (0.8);
        tempLvFive = mThresholdHigh;

        if (mDevInfo <= tempLvOne) {
            imgTempThreshold.setImageDrawable(getContext().getDrawable(R.drawable.lv_temp_one));
        } else if (tempLvOne < mDevInfo && mDevInfo <= tempLvTwo) {
            imgTempThreshold.setImageDrawable(getContext().getDrawable(R.drawable.lv_temp_two));
        } else if (tempLvTwo < mDevInfo && mDevInfo <= tempLvThree) {
            imgTempThreshold.setImageDrawable(getContext().getDrawable(R.drawable.lv_temp_three));
        } else if (tempLvThree < mDevInfo && mDevInfo <= tempLvFour) {
            imgTempThreshold.setImageDrawable(getContext().getDrawable(R.drawable.lv_temp_four));
        } else if (tempLvFour < mDevInfo && mDevInfo <= tempLvFive) {
            imgTempThreshold.setImageDrawable(getContext().getDrawable(R.drawable.lv_temp_five));
        } else {
            imgTempThreshold.setImageDrawable(getContext().getDrawable(R.drawable.lv_temp_five));
        }
    }

    private void typeOfJudgment() {
        if (mDevInfo == mDevTemp) {
            imgTempThreshold.setVisibility(View.VISIBLE);
        }
        if (mDevInfo == mDevDmx) {
            tvDmxAddress.setVisibility(View.VISIBLE);
        }
        if (mDevInfo == mDevTime) {
            layoutDeviceTime.setVisibility(View.VISIBLE);
        }
    }

    private void showDialog() {
        mDialog = new SelfDialog(getContext());
        mDialog.setTitle("修改设备 DMX 地址");
        mDialog.setMessage("设置 DMX 地址和取值范围0~511");
        mDialog.setYesOnclickListener("确定", new SelfDialog.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                if (!(mDialog.getEditTextStr().equals(""))) {
                    try {
                        final int niDmx = Integer.parseInt(mDialog.getEditTextStr());
                        if (!(niDmx >= 0 && niDmx < 512)) {
                            // 如果输入的 DMX 不在 1~511 之间，提示用户
                            Toast.makeText(getContext(), "您设置的 DMX 地址超出范围\n请重新设置", Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        } else {
                            int flag = 0;
                            for (Integer dmx : mDmxSet) {
                                if (niDmx == dmx) {
                                    // 如果输入的 DMX 跟其它连接设备相同，提示用户重新设置
                                    Toast.makeText(getContext(), "您设置的 DMX 地址已被使用\n请重新设置", Toast.LENGTH_LONG).show();
                                    mDialog.dismiss();
                                    break;
                                }
                                flag++;
                            }
                            if (flag == mDmxSet.size()) {
                                // 更新 DMX 地址
                                updateDmx(niDmx);
                            }
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                        // 还有不按规矩出牌的？有！
                        Toast.makeText(getContext(), "请设置正确的 DMX 地址", Toast.LENGTH_SHORT).show();
                        mDialog.dismiss();
                    }
                } else {
                    Toast.makeText(getContext(), "未更新 DMX 地址", Toast.LENGTH_SHORT).show();
                    mDialog.dismiss();
                }
            }
        });
        mDialog.setNoOnclickListener("取消", new SelfDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void updateDmx(int niDmx) {
        // 清空设备配置
        AppActivity.getAppCtrl().clearDeviceConfig(mDevId);
        // 发送更新 DMX 命令
        byte[] pkg = BleDeviceProtocol.pkgSetDmxAddress(mDevId, niDmx);
        mDevCtrl.sendCommand(mDevId, pkg);
        // 更新显示
        tvDmxAddress.setText(String.valueOf(niDmx));
        // 发送 DMX 地址 0
        EventBus.getDefault().post(new DmxZeroEvent(niDmx));
        mDialog.dismiss();
    }

}