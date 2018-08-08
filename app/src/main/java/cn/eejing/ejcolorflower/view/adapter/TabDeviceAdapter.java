package cn.eejing.ejcolorflower.view.adapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.device.DeviceConfig;
import cn.eejing.ejcolorflower.device.DeviceStatus;
import cn.eejing.ejcolorflower.model.event.DelDeviceEvent;
import cn.eejing.ejcolorflower.model.event.DeviceConnectEvent;
import cn.eejing.ejcolorflower.model.event.DeviceEvent;
import cn.eejing.ejcolorflower.model.request.DeviceListBean;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.view.activity.DeDeviceDetailsActivity;
import cn.eejing.ejcolorflower.view.activity.DeQrAddDeviceActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static cn.eejing.ejcolorflower.app.AppConstant.DEVICE_CONNECT_NO;
import static cn.eejing.ejcolorflower.app.AppConstant.DEVICE_CONNECT_YES;
import static cn.eejing.ejcolorflower.app.AppConstant.REQUEST_CODE_QRCODE_PERMISSIONS;

/**
 * 设备模块适配器
 */

public class TabDeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements EasyPermissions.PermissionCallbacks {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<DeviceListBean.DataBean.ListBean> mList;
    private String mMemberId, mToken;

    // 硬件相关
    private DeviceStatus mState;
    private DeviceConfig mConfig;
    private String mConnDevMac, mConnStatus;
    private int mTemp, mDMX, mTime, mThresholdHigh;

    public TabDeviceAdapter(Context context, List<DeviceListBean.DataBean.ListBean> list, String memberId, String token) {
        this.mContext = context;
        this.mList = list;
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.mMemberId = memberId;
        this.mToken = token;
        requestCodeQRCodePermissions();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ITEM:
                return new ItemViewHolder(mLayoutInflater.inflate(R.layout.item_unit_device, parent, false));
            case TYPE_FOOTER:
                return new FootViewHolder(mLayoutInflater.inflate(R.layout.item_footer_control, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_ITEM) {
            if (mList != null) {
                ((ItemViewHolder) holder).setData(position, mConnDevMac, mList.get(position).getMac(), mConnStatus, mState, mConfig);
            }
        }

        if (getItemViewType(position) == TYPE_FOOTER) {
            FootViewHolder footHolder = (FootViewHolder) holder;
            if (mList.size() == 0) {
                footHolder.nullDevice.setVisibility(View.VISIBLE);
                footHolder.haveDevice.setVisibility(View.GONE);
            } else {
                footHolder.nullDevice.setVisibility(View.GONE);
                footHolder.haveDevice.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mList.size()) {
            return TYPE_ITEM;
        } else {
            return TYPE_FOOTER;
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        EventBus.getDefault().unregister(this);
    }

    public void refreshList(List<DeviceListBean.DataBean.ListBean> list) {
        if (list != null) {
            mList.clear();
            addList(list);
        }
        if (list == null) {
            mList.clear();
            notifyDataSetChanged();
        }
    }

    private void addList(List<DeviceListBean.DataBean.ListBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    /** 硬件传来连接状态及设备信息 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventDevConn(DeviceConnectEvent event) {
        mConnStatus = event.getInfo();
        mConnDevMac = event.getMac();
        mState = event.getState();
        mConfig = event.getConfig();

        mTemp = event.getState().mTemperature;
        mDMX = event.getConfig().mDMXAddress;
        mTime = event.getState().mRestTime;
        mThresholdHigh = event.getConfig().mTemperatureThresholdHigh;

        Log.i("JLTHTYC", mConnStatus
                + "\nConn Mac--->" + mConnDevMac
                + "\nConn Temp--->" + mTemp
                + "\nConn DMX--->" + mDMX
                + "\nConn Time--->" + mTime
                + "\nConn ThresholdHigh--->" + mThresholdHigh
        );

        notifyDataSetChanged();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.layout_device_list)        LinearLayout outView;
        @BindView(R.id.tv_connected)              TextView tvConnected;
        @BindView(R.id.tv_device_id)              TextView tvDeviceId;
        @BindView(R.id.btn_device_temp)           Button btnTemp;
        @BindView(R.id.btn_device_dmx)            Button btnDmx;
        @BindView(R.id.btn_device_time)           Button btnTime;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setData(int position, String devMac, String serverMac, String connStatus, DeviceStatus state, DeviceConfig config) {
            if (devMac != null) {
                if (devMac.equals(serverMac)) {
                    // 连接成功
                    if (connStatus.equals(DEVICE_CONNECT_YES)) {
                        devConn(position, state, config);
                    }
                    // 连接断开
                    if (connStatus.equals(DEVICE_CONNECT_NO)) {
                        devDisconn(position);
                    }
                }
            } else {
                // 设备初始状态
                devNotConn(position);
            }
        }

        /** 未连接 */
        private void devNotConn(int position) {
            tvDeviceId.setText(mList.get(position).getId());
            tvConnected.setText("未连接");
            tvConnected.setTextColor(mContext.getResources().getColor(R.color.colorNoClick));
            // 设置点击事件
            setClickListener(null, null, null, position);
        }

        /** 连接成功 */
        private void devConn(int position, DeviceStatus state, DeviceConfig config) {
            tvDeviceId.setText(mList.get(position).getId());
            tvConnected.setText("已连接");
            tvConnected.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            // 显示硬件数据
            displayHardwareData();
            // 设置点击事件
            setClickListener(mList.get(position).getId(), state, config, position);
        }

        /** 连接断开 */
        private void devDisconn(int position) {
            tvDeviceId.setText(mList.get(position).getId());
            tvConnected.setText("未连接");
            tvConnected.setTextColor(mContext.getResources().getColor(R.color.colorNoClick));
            // 隐藏硬件数据
            hideHardwareData();
            // 设置点击事件
            setClickListener(null, null, null, position);
        }

        /** 显示硬件数据 */
        private void displayHardwareData() {
            // 根据温度阈值划分温度等级（5个等级）
            double tempLvOne = mThresholdHigh * (0.2);
            double tempLvTwo = mThresholdHigh * (0.4);
            double tempLvThree = mThresholdHigh * (0.6);
            double tempLvFour = mThresholdHigh * (0.8);

            // 根据温度等级改变背景颜色
            if (mTemp <= tempLvOne) {
                btnTemp.setBackgroundResource(R.drawable.ic_device_one);
            } else if (tempLvOne < mTemp && mTemp <= tempLvTwo) {
                btnTemp.setBackgroundResource(R.drawable.ic_device_two);
            } else if (tempLvTwo < mTemp && mTemp <= tempLvThree) {
                btnTemp.setBackgroundResource(R.drawable.ic_device_three);
            } else if (tempLvThree < mTemp && mTemp <= tempLvFour) {
                btnTemp.setBackgroundResource(R.drawable.ic_device_four);
            } else if (tempLvFour < mTemp && mTemp <= (double) mThresholdHigh) {
                btnTemp.setBackgroundResource(R.drawable.ic_device_five);
            } else {
                btnTemp.setBackgroundResource(R.drawable.ic_device_five);
            }

            // 展示硬件数据到 View
            btnDmx.setText(String.valueOf(mDMX));
            // 将获取到的 int 类型剩余时间转换成 String 类型显示
            @SuppressLint("SimpleDateFormat")
            String nowTimeStr = new SimpleDateFormat("mm:ss").format((long) mTime * 1000);
            btnTime.setText(nowTimeStr);
        }

        /** 隐藏硬件数据 */
        private void hideHardwareData() {
            btnTemp.setBackgroundResource(R.drawable.ic_device_temp);
            btnDmx.setText(null);
            btnTime.setText(null);
        }

        @OnLongClick(R.id.layout_device_list)
        public boolean onLongClickOut() {
            Snackbar.make(outView, "确定要删除设备吗？", Snackbar.LENGTH_SHORT)
                    .setAction("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getDataWithDelDevice(getAdapterPosition());
                        }
                    })
                    .show();
            return true;
        }

        private void setClickListener(final String deviceId, final DeviceStatus state, final DeviceConfig config, final int position) {
            btnTemp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickDeviceInfo(0, position, deviceId, state, config);
                }
            });

            btnDmx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickDeviceInfo(1, position, deviceId, state, config);
                }
            });

            btnTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickDeviceInfo(2, position, deviceId, state, config);
                }
            });
        }

        private void clickDeviceInfo(int type, int position, String deviceId, DeviceStatus state, DeviceConfig config) {
            if (state != null && config != null) {
                // 将 deviceId 传到首页
                EventBus.getDefault().post(new DeviceEvent(mList.get(position).getId()));
                // 携带设备参数跳转到设备详情界面
                Intent intent = new Intent(mContext, DeDeviceDetailsActivity.class);
                intent.putExtra("device_id", deviceId);
                intent.putExtra("device_temp", state.mTemperature);
                intent.putExtra("device_dmx", config.mDMXAddress);
                intent.putExtra("device_time", state.mRestTime);
                intent.putExtra("device_threshold", config.mTemperatureThresholdHigh);
                intent.putExtra("page", type);
                intent.putExtra("member_id", mMemberId);
                intent.putExtra("token", mToken);
                mContext.startActivity(intent);
            } else {
                Toast.makeText(mContext, "此设备尚未连接", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class FootViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_footer_add)             TextView tvAdd;
        @BindView(R.id.layout_device_have)        RelativeLayout haveDevice;
        @BindView(R.id.layout_device_null)        LinearLayout nullDevice;

        FootViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            tvAdd.setText(R.string.add_device);
        }

        @OnClick({R.id.img_footer_add, R.id.home_null_device})
        public void onClickAddDev(View view) {
            switch (view.getId()) {
                case R.id.img_footer_add:
                case R.id.home_null_device:
                    mContext.startActivity(new Intent(mContext, DeQrAddDeviceActivity.class).putExtra("member_id", mMemberId));
                    break;
                default:
                    break;
            }
        }
    }

    private void getDataWithDelDevice(final int position) {
        OkGo.<String>post(Urls.RM_DEVICE)
                .tag(this)
                .params("member_id", mMemberId)
                .params("device_id", mList.get(position).getId())
                .params("token", mToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "rm device request succeeded--->" + body);
                        EventBus.getDefault().post(new DelDeviceEvent(mList.get(position).getId()));
                    }
                });
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(mContext, perms)) {
            EasyPermissions.requestPermissions((Activity) mContext, "扫描二维码需要打开相机和散光灯的权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        }
    }

}