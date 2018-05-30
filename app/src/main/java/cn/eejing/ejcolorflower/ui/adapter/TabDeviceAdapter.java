package cn.eejing.ejcolorflower.ui.adapter;

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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.library.SuperButton;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.app.Urls;
import cn.eejing.ejcolorflower.device.Device;
import cn.eejing.ejcolorflower.device.DeviceConfig;
import cn.eejing.ejcolorflower.device.DeviceState;
import cn.eejing.ejcolorflower.model.request.DeviceListBean;
import cn.eejing.ejcolorflower.ui.activity.DeviceDetailsActivity;
import cn.eejing.ejcolorflower.ui.activity.QRCodeActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static cn.eejing.ejcolorflower.app.AppConstant.REQUEST_CODE_QRCODE_PERMISSIONS;

/**
 * @创建者 Taodaren
 * @描述 设备模块适配器
 */

public class TabDeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements EasyPermissions.PermissionCallbacks {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private Context mContext;
    private List<DeviceListBean.DataBean.ListBean> mList;
    private LayoutInflater mLayoutInflater;
    private String mMemberId, mToken;
    private Device mDevice;
    private DeviceState mState;
    private DeviceConfig mConfig;

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
        RecyclerView.ViewHolder holder = null;
        View inflate;
        switch (viewType) {
            case TYPE_ITEM:
                inflate = mLayoutInflater.inflate(R.layout.item_unit_device, parent, false);
                holder = new ItemViewHolder(inflate);
                break;
            case TYPE_FOOTER:
                inflate = mLayoutInflater.inflate(R.layout.item_footer_device, parent, false);
                holder = new FootViewHolder(inflate);
                ((FootViewHolder) holder).setClickListener();
                break;
            default:
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_ITEM) {
            if (mState != null && mConfig != null) {
                ((ItemViewHolder) holder).setDataHasDevice(mList.get(position), position, mState, mConfig, mDevice);
            } else {
                ((ItemViewHolder) holder).setDataOnlyServer(mList.get(position), position);
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

    public void refreshList(List<DeviceListBean.DataBean.ListBean> list) {
        if (list != null) {
            mList.clear();
            addList(list);
        }
    }

    public void addList(List<DeviceListBean.DataBean.ListBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void setDeviceState(Device device, DeviceState state) {
        this.mDevice = device;
        this.mState = state;
        notifyDataSetChanged();
        Log.i(AppConstant.TAG, "setDevice State TIME--->" + mState.mRestTime);
        Log.i(AppConstant.TAG, "setDevice State TEMP--->" + mState.mTemperature);
        Log.i(AppConstant.TAG, "setDevice device ADDRESS--->" + mDevice.getAddress());
    }

    public void setDeviceConfig(DeviceConfig config) {
        this.mConfig = config;
        notifyDataSetChanged();
        Log.i(AppConstant.TAG, "setDevice Config DMX--->" + mConfig.mDMXAddress);
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_connected)
        TextView tvConnected;
        @BindView(R.id.tv_device_id)
        TextView tvDeviceId;
        @BindView(R.id.img_device_id)
        ImageView imgDeviceId;
        @BindView(R.id.sb_device_temp)
        SuperButton sbTemp;
        @BindView(R.id.sb_device_dmx)
        SuperButton sbDmx;
        @BindView(R.id.sb_device_time)
        SuperButton sbTime;
        View outView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            outView = itemView;
        }

        public void setDataOnlyServer(DeviceListBean.DataBean.ListBean bean, int position) {
            tvDeviceId.setText(bean.getId());
            setClickListener(position, bean.getId(), null, null);
        }

        public void setDataHasDevice(DeviceListBean.DataBean.ListBean bean, int position, DeviceState state, DeviceConfig config, Device device) {
            if (bean.getMac().equals(device.getAddress())) {
                Log.i(AppConstant.TAG, "setDataHasDevice: " + state.mTemperature);
                // 如果设备中的 MAC 地址与添加的一致
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
                    sbTemp.setShapeSolidColor(mContext.getResources().getColor(R.color.colorTempLvOne));
                    sbTemp.setText(String.valueOf(temp));
                    sbTemp.setUseShape();
                } else if (tempLvOne < temp && temp <= tempLvTwo) {
                    sbTemp.setShapeSolidColor(mContext.getResources().getColor(R.color.colorTempLvTwo));
                    sbTemp.setText(String.valueOf(temp));
                    sbTemp.setUseShape();
                } else if (tempLvTwo < temp && temp <= tempLvThree) {
                    sbTemp.setShapeSolidColor(mContext.getResources().getColor(R.color.colorTempLvThree));
                    sbTemp.setText(String.valueOf(temp));
                    sbTemp.setUseShape();
                } else if (tempLvThree < temp && temp <= tempLvFour) {
                    sbTemp.setShapeSolidColor(mContext.getResources().getColor(R.color.colorTempLvFour));
                    sbTemp.setText(String.valueOf(temp));
                    sbTemp.setUseShape();
                } else if (tempLvFour < temp && temp <= tempLvFive) {
                    sbTemp.setShapeSolidColor(mContext.getResources().getColor(R.color.colorTempLvFive));
                    sbTemp.setText(String.valueOf(temp));
                    sbTemp.setUseShape();
                } else {
                    sbTemp.setShapeSolidColor(mContext.getResources().getColor(R.color.colorTempLvFive));
                    sbTemp.setText(String.valueOf(temp));
                    sbTemp.setUseShape();
                }

                // 将获取到的 int 类型剩余时间转换成 String 类型显示
                long nowTimeLong = (long) time * 1000;
                @SuppressLint("SimpleDateFormat") DateFormat ymdhmsFormat = new SimpleDateFormat("mm:ss");
                String nowTimeStr = ymdhmsFormat.format(nowTimeLong);
                sbTime.setText(nowTimeStr);

                sbDmx.setText(String.valueOf(dmx));

                tvDeviceId.setText(bean.getId());
                tvConnected.setText("已连接");
                tvConnected.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));

//                Toast.makeText(mContext, bean.getId() + "连接成功", Toast.LENGTH_SHORT).show();
                setClickListener(position, bean.getId(), state, config);
            }
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

            imgDeviceId.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (state != null && config != null) {
                        Intent intent = new Intent(mContext, DeviceDetailsActivity.class);
                        intent.putExtra("device_id", deviceId);
                        intent.putExtra("device_temp", state.mTemperature);
                        intent.putExtra("device_dmx", config.mDMXAddress);
                        intent.putExtra("device_time", state.mRestTime);
                        intent.putExtra("device_threshold", config.mTemperatureThresholdHigh);
                        mContext.startActivity(intent);
                    } else {
                        Toast.makeText(mContext, "此设备尚未连接", Toast.LENGTH_SHORT).show();
                    }
                }
            });


            sbTemp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (state != null && config != null) {
                        Intent intent = new Intent(mContext, DeviceDetailsActivity.class);
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

            sbDmx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (state != null && config != null) {
                        Intent intent = new Intent(mContext, DeviceDetailsActivity.class);
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

            sbTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (state != null && config != null) {
                        Intent intent = new Intent(mContext, DeviceDetailsActivity.class);
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

    }

    class FootViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_add_device)
        ImageView imgAddDevice;

        public FootViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setClickListener() {
            imgAddDevice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, QRCodeActivity.class);
                    intent.putExtra("member_id", mMemberId);
                    mContext.startActivity(intent);
                }
            });
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
