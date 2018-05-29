package cn.eejing.ejcolorflower.ui.adapter;

import android.annotation.SuppressLint;
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
import cn.eejing.ejcolorflower.device.DeviceConfig;
import cn.eejing.ejcolorflower.device.DeviceState;
import cn.eejing.ejcolorflower.model.request.DeviceListBean;
import cn.eejing.ejcolorflower.ui.activity.DeviceDetailsActivity;
import cn.eejing.ejcolorflower.ui.activity.QRCodeActivity;

/**
 * @创建者 Taodaren
 * @描述 设备模块适配器
 */

public class TabDeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private Context mContext;
    private List<DeviceListBean.DataBean.ListBean> mList;
    private LayoutInflater mLayoutInflater;
    private String mMemberId, mToken;
    private DeviceState mState;
    private DeviceConfig mConfig;

    public TabDeviceAdapter(Context context, List<DeviceListBean.DataBean.ListBean> list, String memberId, String token) {
        this.mContext = context;
        this.mList = list;
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.mMemberId = memberId;
        this.mToken = token;
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
//            Log.i(AppConstant.TAG, "onBind State : " + mState.mRestTime);
//            Log.i(AppConstant.TAG, "onBind Config : " + mConfig.mDMXAddress);
            ((ItemViewHolder) holder).setData(mList.get(position), position);
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

    public void setDeviceState(DeviceState state) {
        this.mState = state;
        notifyDataSetChanged();
        Log.i(AppConstant.TAG, "setDevice State : " + mState.mRestTime);
    }

    public void setDeviceConfig(DeviceConfig config) {
        this.mConfig = config;
        notifyDataSetChanged();
        Log.i(AppConstant.TAG, "setDevice Config : " + mConfig.mDMXAddress);
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

        public void setData(DeviceListBean.DataBean.ListBean bean, int position) {
//            Log.i(AppConstant.TAG, "setData State : " + mState.mRestTime);
//            Log.i(AppConstant.TAG, "setData Config : " + mConfig.mDMXAddress);
            int temp = 20, dmx = 17, time = 48;
            // 将获取到的 int 类型剩余时间转换成 String 类型显示
            long nowTimeLong = (long) time * 1000;
            @SuppressLint("SimpleDateFormat") DateFormat ymdhmsFormat = new SimpleDateFormat("mm:ss");
            String nowTimeStr = ymdhmsFormat.format(nowTimeLong);
            sbTime.setText(nowTimeStr);

            sbTemp.setText(String.valueOf(temp));
            sbDmx.setText(String.valueOf(dmx));


//            sbDmx.setText(String.valueOf(mConfig.mDMXAddress));
//            sbTime.setText(String.valueOf(mState.mRestTime));
            tvDeviceId.setText(bean.getId());
            setClickListener(position, bean.getId(), temp, dmx, time);
        }

        private void setClickListener(final int position, final String deviceId, final int temp, final int dmx, final int time) {
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
                    Intent intent = new Intent(mContext, DeviceDetailsActivity.class);
                    intent.putExtra("device_id", deviceId);
                    intent.putExtra("device_temp", temp);
                    intent.putExtra("device_dmx", dmx);
                    intent.putExtra("device_time", time);
                    mContext.startActivity(intent);
                }
            });


            sbTemp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DeviceDetailsActivity.class);
                    intent.putExtra("device_id", deviceId);
                    intent.putExtra("device_temp", temp);
                    intent.putExtra("device_dmx", dmx);
                    intent.putExtra("device_time", time);
                    intent.putExtra("page", 0);
                    mContext.startActivity(intent);
                }
            });

            sbDmx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DeviceDetailsActivity.class);
                    intent.putExtra("device_id", deviceId);
                    intent.putExtra("device_temp", temp);
                    intent.putExtra("device_dmx", dmx);
                    intent.putExtra("device_time", time);
                    intent.putExtra("page", 1);
                    mContext.startActivity(intent);
                }
            });

            sbTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, DeviceDetailsActivity.class);
                    intent.putExtra("device_id", deviceId);
                    intent.putExtra("device_temp", temp);
                    intent.putExtra("device_dmx", dmx);
                    intent.putExtra("device_time", time);
                    intent.putExtra("page", 2);
                    mContext.startActivity(intent);
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

}
