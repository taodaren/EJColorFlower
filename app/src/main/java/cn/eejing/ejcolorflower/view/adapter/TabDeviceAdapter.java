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
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.device.Device;
import cn.eejing.ejcolorflower.device.DeviceConfig;
import cn.eejing.ejcolorflower.device.DeviceState;
import cn.eejing.ejcolorflower.model.event.DeviceConnectEvent;
import cn.eejing.ejcolorflower.model.event.DeviceEvent;
import cn.eejing.ejcolorflower.model.request.DeviceListBean;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.view.activity.DeDeviceDetailsActivity;
import cn.eejing.ejcolorflower.view.activity.DeQrAddDeviceActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static cn.eejing.ejcolorflower.app.AppConstant.REQUEST_CODE_QRCODE_PERMISSIONS;

/**
 * 设备模块适配器
 */

public class TabDeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements EasyPermissions.PermissionCallbacks {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private Context mContext;
    private List<DeviceListBean.DataBean.ListBean> mList;
    private List<String> mConnDevList;
    private LayoutInflater mLayoutInflater;
    private String mMemberId, mToken;
    private String mDeviceIdByBle;
    private Device mDevice;
    private DeviceState mState;
    private DeviceConfig mConfig;
    private String mConnectDeviceMac;

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
            if (mConnDevList != null) {
                for (int i = 0; i < mConnDevList.size(); i++) {
                    Log.e("LJQ", "onBindViewHolder: " + mConnDevList.get(i));
                    ((ItemViewHolder) holder).setData(mList.get(position), mConnDevList.get(i), position);
                }
            } else {
                ((ItemViewHolder) holder).setData(mList.get(position), null, position);
            }

//            if (mState != null && mConfig != null) {
//                ((ItemViewHolder) holder).setDataHasDevice(mList.get(position), position, mState, mConfig, mDevice);
//            } else {
//                ((ItemViewHolder) holder).setDataOnlyServer(mList.get(position), position);
//            }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getDeviceConnect(DeviceConnectEvent event) {
        // TODO: 2018/7/3 I Love LJQ Forever.
        mConnDevList = new ArrayList<>();
        mConnDevList.add(event.getMac());
        for (int i = 0; i < mConnDevList.size(); i++) {
            Log.i("LJQ", "已连接设备MAC: " + mConnDevList.get(i));
            Log.i("LJQ", "已连接设备数量: " + mConnDevList.size());
        }
    }

    public void refreshList(List<DeviceListBean.DataBean.ListBean> list) {
        if (list != null) {
            mList.clear();
            addList(list);
        }
    }

    private void addList(List<DeviceListBean.DataBean.ListBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void setDeviceState(Device device, DeviceState state) {
        this.mDevice = device;
        this.mState = state;
        notifyDataSetChanged();
    }

    public void setDeviceConfig(DeviceConfig config) {
        this.mConfig = config;
        notifyDataSetChanged();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_connected)
        TextView tvConnected;
        @BindView(R.id.tv_device_id)
        TextView tvDeviceId;
        @BindView(R.id.btn_device_temp)
        Button btnTemp;
        @BindView(R.id.btn_device_dmx)
        Button btnDmx;
        @BindView(R.id.btn_device_time)
        Button btnTime;
        View outView;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            outView = itemView;
        }

        void setData(DeviceListBean.DataBean.ListBean bean, String connectDeviceMac, int position) {
            Log.e("LJQ", "setData设备MAC: " + connectDeviceMac);
            Log.i("LJQ", "服务器MAC: " + bean.getMac());
            Log.i("LJQ", "服务器ID: " + bean.getId());

            if (connectDeviceMac != null) {
                // 如果硬件中通过 MAC 地址已经连接的设备不为空
                if (bean.getMac().equals(connectDeviceMac)) {
                    // 如果服务器设备列表中的 MAC 地址与设备 MAC 一致，设置已连接状态
                    tvDeviceId.setText(bean.getId());
                    tvConnected.setText("已连接");
                    tvConnected.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
                }
            } else {
                // 如果硬件中通过 MAC 地址已经连接的设备为空，则只展示服务器中的设备 ID
                tvDeviceId.setText(bean.getId());
            }

//            if (connectDeviceMac.get(position) != null &&                       // 如果硬件中通过 MAC 地址已经连接的设备不为空，并且
//                    bean.getMac().equals(connectDeviceMac.get(position))) {     // 服务器设备列表中的 MAC 地址与设备 MAC 一致，设置已连接状态
//                tvDeviceId.setText(bean.getId());
//                tvConnected.setText("已连接");
//                tvConnected.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
////                btnTime.setText(nowTimeStr);
//
////                btnDmx.setText(String.valueOf(dmx));
//            } else {                                                            // 否则只展示服务器中的设备 ID
//                tvDeviceId.setText(bean.getId());
//            }

        }

        void setDataHasDevice(DeviceListBean.DataBean.ListBean bean, int position, DeviceState state, DeviceConfig config, Device device) {
            if (bean.getMac().equals(device.getAddress()) && String.valueOf(config.mID).equals(bean.getId())) {
                // 如果设备中的 MAC 地址与设备 ID 跟添加的一致，执行以下操作
                int temp, dmx, time, thresholdHigh;
                double tempLvOne, tempLvTwo, tempLvThree, tempLvFour, tempLvFive;

                temp = state.mTemperature;
                dmx = config.mDMXAddress;
                time = state.mRestTime;
                thresholdHigh = config.mTemperatureThresholdHigh;

                tempLvOne = thresholdHigh * (0.2);
                tempLvTwo = thresholdHigh * (0.4);
                tempLvThree = thresholdHigh * (0.6);
                tempLvFour = thresholdHigh * (0.8);
                tempLvFive = thresholdHigh;

                if (temp <= tempLvOne) {
                    btnTemp.setBackgroundResource(R.drawable.ic_device_one);
                    btnTemp.setText(String.valueOf(temp));
                } else if (tempLvOne < temp && temp <= tempLvTwo) {
                    btnTemp.setBackgroundResource(R.drawable.ic_device_two);
                    btnTemp.setText(String.valueOf(temp));
                } else if (tempLvTwo < temp && temp <= tempLvThree) {
                    btnTemp.setBackgroundResource(R.drawable.ic_device_three);
                    btnTemp.setText(String.valueOf(temp));
                } else if (tempLvThree < temp && temp <= tempLvFour) {
                    btnTemp.setBackgroundResource(R.drawable.ic_device_four);
                    btnTemp.setText(String.valueOf(temp));
                } else if (tempLvFour < temp && temp <= tempLvFive) {
                    btnTemp.setBackgroundResource(R.drawable.ic_device_five);
                    btnTemp.setText(String.valueOf(temp));
                } else {
                    btnTemp.setText(String.valueOf(temp));
                }

                // 将获取到的 int 类型剩余时间转换成 String 类型显示
                long nowTimeLong = (long) time * 1000;
                @SuppressLint("SimpleDateFormat") DateFormat ymdhmsFormat = new SimpleDateFormat("mm:ss");
                String nowTimeStr = ymdhmsFormat.format(nowTimeLong);
                btnTime.setText(nowTimeStr);

                btnDmx.setText(String.valueOf(dmx));

                tvDeviceId.setText(bean.getId());
                tvConnected.setText("已连接");
                tvConnected.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));

//                Toast.makeText(mContext, bean.getId() + "连接成功", Toast.LENGTH_SHORT).show();
                setClickListener(position, bean.getId(), state, config);
            }
        }

        void setDataOnlyServer(DeviceListBean.DataBean.ListBean bean, int position) {
            tvDeviceId.setText(bean.getId());
            setClickListener(position, bean.getId(), null, null);
        }

        private void setClickListener(final int position, final String deviceId, final DeviceState state, final DeviceConfig config) {
            outView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Snackbar.make(view, "确定要删除设备吗？", Snackbar.LENGTH_SHORT)
                            .setAction("确定", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    getDataWithDelGroup(position);
                                }
                            })
                            .show();
                    return true;
                }
            });

            btnTemp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (state != null && config != null) {
                        postDeviceId(position);

                        Intent intent = new Intent(mContext, DeDeviceDetailsActivity.class);
                        intent.putExtra("device_id", deviceId);
                        intent.putExtra("device_temp", state.mTemperature);
                        intent.putExtra("device_dmx", config.mDMXAddress);
                        intent.putExtra("device_time", state.mRestTime);
                        intent.putExtra("device_threshold", config.mTemperatureThresholdHigh);
                        intent.putExtra("page", 0);
                        mContext.startActivity(intent);
                    } else {
                        Toast.makeText(mContext, "此设备尚未连接", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnDmx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (state != null && config != null) {
                        postDeviceId(position);

                        Intent intent = new Intent(mContext, DeDeviceDetailsActivity.class);
                        intent.putExtra("device_id", deviceId);
                        intent.putExtra("device_temp", state.mTemperature);
                        intent.putExtra("device_dmx", config.mDMXAddress);
                        intent.putExtra("device_time", state.mRestTime);
                        intent.putExtra("device_threshold", config.mTemperatureThresholdHigh);
                        intent.putExtra("page", 1);
                        mContext.startActivity(intent);
                    } else {
                        Toast.makeText(mContext, "此设备尚未连接", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btnTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (state != null && config != null) {
                        postDeviceId(position);

                        Intent intent = new Intent(mContext, DeDeviceDetailsActivity.class);
                        intent.putExtra("device_id", deviceId);
                        intent.putExtra("device_temp", state.mTemperature);
                        intent.putExtra("device_dmx", config.mDMXAddress);
                        intent.putExtra("device_time", state.mRestTime);
                        intent.putExtra("device_threshold", config.mTemperatureThresholdHigh);
                        intent.putExtra("page", 2);
                        mContext.startActivity(intent);
                    } else {
                        Toast.makeText(mContext, "此设备尚未连接", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }

        // 通过 EventBus 将 deviceId 传到首页
        private void postDeviceId(int position) {
            DeviceEvent event = new DeviceEvent(mList.get(position).getId());
            EventBus.getDefault().post(event);
        }

    }

    class FootViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_footer_add)
        TextView tvAdd;

        FootViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            tvAdd.setText(R.string.add_device);
        }

        @OnClick(R.id.img_footer_add)
        public void onViewClicked() {
            Intent intent = new Intent(mContext, DeQrAddDeviceActivity.class);
            intent.putExtra("member_id", mMemberId);
            mContext.startActivity(intent);
        }

    }

    private void getDataWithDelGroup(int position) {
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
                        refreshList(mList);
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
