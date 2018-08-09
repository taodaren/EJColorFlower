package cn.eejing.ejcolorflower.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allen.library.SuperButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.model.event.DeviceConnectEvent;

import static cn.eejing.ejcolorflower.app.AppConstant.DEVICE_CONNECT_NO;
import static cn.eejing.ejcolorflower.app.AppConstant.DEVICE_CONNECT_YES;

/**
 * 控制模块分组设备列表适配器
 */

public class CoDeviceListAdapter extends RecyclerView.Adapter<CoDeviceListAdapter.DeviceHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<String> mDevList;
    private String mConnStatus;
    private long mConnDevID;

    CoDeviceListAdapter(Context context, List<String> devList) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mDevList = new ArrayList<>();
        this.mDevList.addAll(devList);
    }

    @NonNull
    @Override
    public DeviceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeviceHolder(mInflater.inflate(R.layout.item_device, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceHolder holder, int position) {
        holder.setData(mConnDevID, mDevList.get(position), mConnStatus);
    }

    @Override
    public int getItemCount() {
        return mDevList.size();
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

    class DeviceHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sb_device_add_list)        SuperButton sbDevice;

        DeviceHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setData(long bleDevID, String serDevId, String connStatus) {
            sbDevice.setText(serDevId);
            if (bleDevID != 0) {
                if (serDevId.equals(String.valueOf(bleDevID))) {
                    if (connStatus.equals(DEVICE_CONNECT_YES)) {
                        // 连接成功
                        sbDevice.setShapeSolidColor(mContext.getResources().getColor(R.color.colorTransparent));
                        sbDevice.setShapeStrokeColor(mContext.getResources().getColor(R.color.colorFrame));
                        sbDevice.setUseShape();
                    }
                    if (connStatus.equals(DEVICE_CONNECT_NO)) {
                        // 连接断开
                        sbDevice.setShapeSolidColor(mContext.getResources().getColor(R.color.colorFrame));
                        sbDevice.setShapeStrokeColor(mContext.getResources().getColor(R.color.colorFrame));
                        sbDevice.setUseShape();
                    }
                }
            }
        }
    }

    /** 硬件传递连接设备信息 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventDevConn(DeviceConnectEvent event) {
        mConnStatus = event.getInfo();
        mConnDevID = event.getConfig().mID;

        Log.i("JLTHYC", mConnStatus
                + "\nConn mConnDevID--->" + mConnDevID
        );

        notifyDataSetChanged();
    }
}

