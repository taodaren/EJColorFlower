package cn.eejing.ejcolorflower.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.allen.library.SuperButton;
import com.example.zhouwei.library.CustomPopWindow;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.device.Device;
import cn.eejing.ejcolorflower.device.DeviceConfig;
import cn.eejing.ejcolorflower.device.Protocol;
import cn.eejing.ejcolorflower.model.event.JetStatusEvent;
import cn.eejing.ejcolorflower.model.request.DeviceGroupListBean;
import cn.eejing.ejcolorflower.presenter.OnReceivePackage;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.util.SelfDialog;
import cn.eejing.ejcolorflower.util.Settings;
import cn.eejing.ejcolorflower.view.activity.AppActivity;
import cn.eejing.ejcolorflower.view.activity.CoConfigIntervalActivity;
import cn.eejing.ejcolorflower.view.activity.CoConfigRideActivity;
import cn.eejing.ejcolorflower.view.activity.CoConfigStreamActivity;
import cn.eejing.ejcolorflower.view.activity.CoConfigTogetherActivity;
import cn.eejing.ejcolorflower.view.activity.CoDeviceActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_INTERVAL;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_RIDE;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_STREAM;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_TOGETHER;

/**
 * 控制模块适配器
 */

public class TabControlAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private Gson mGson;
    private List<DeviceGroupListBean.DataBean> mList;
    private String mMemberId, mToken;
    private CustomPopWindow mCustomPopWindow;
    private SelfDialog mDialog;

    private Device mDevice;
    private DeviceConfig mConfig;
    private AppActivity.FireworksDeviceControl mDeviceControl;
    private int mConfigType, mDirection, mGap, mDuration, mGapBig, mLoop, mFrequency, mHigh;
    private int mPostGroupId;


    public TabControlAdapter(Context mContext, List<DeviceGroupListBean.DataBean> mList, String mMemberId) {
        this.mContext = mContext;
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.mList = mList;
        this.mGson = new Gson();
        this.mMemberId = mMemberId;
        this.mToken = Settings.getLoginSessionInfo(mContext).getToken();
        this.mDeviceControl = AppActivity.getFireworksDeviceControl();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_ITEM:
                return new ItemViewHolder(mLayoutInflater.inflate(R.layout.item_ctrl_card, parent, false));
            case TYPE_FOOTER:
                return new FootViewHolder(mLayoutInflater.inflate(R.layout.item_footer_control, parent, false));
            default:
                Log.e(AppConstant.TAG, "onCreateViewHolder: is null");
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == TYPE_ITEM) {
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

    public void refreshList(List<DeviceGroupListBean.DataBean> list) {
        if (list != null) {
            mList.clear();
            addList(list);
        }
    }

    private void addList(List<DeviceGroupListBean.DataBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void setDeviceConfig(Device device, DeviceConfig config) {
        this.mDevice = device;
        this.mConfig = config;
        notifyDataSetChanged();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_ctrl_group_name)
        TextView tvName;
        @BindView(R.id.img_ctrl_group_switch)
        ImageView imgSwitch;
        @BindView(R.id.rv_control_group)
        RecyclerView rvGroup;
        @BindView(R.id.tv_ctrl_group_info)
        TextView tvInfo;
        @BindView(R.id.img_ctrl_group_add)
        ImageView imgAdd;
        @BindView(R.id.sb_type_puff)
        SuperButton sbType;
        @BindView(R.id.sb_config_puff)
        SuperButton sbConfig;

        View outItem;
        int groupId;
        String groupName;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            outItem = itemView;

            tvName.setOnClickListener(this);
            imgSwitch.setOnClickListener(this);
            imgAdd.setOnClickListener(this);
            sbConfig.setOnClickListener(this);
        }

        @SuppressLint("ResourceAsColor")
        public void setData(DeviceGroupListBean.DataBean bean, final int position) {
            if (bean.getGroup_list() != null && bean.getGroup_list().size() > 0) {
                // 如果有设备，显示设备，隐藏提示文字
                tvInfo.setVisibility(View.GONE);
                rvGroup.setVisibility(View.VISIBLE);
                initGroupList(bean.getGroup_list());
            } else {
                tvInfo.setVisibility(View.VISIBLE);
                rvGroup.setVisibility(View.INVISIBLE);
            }

            tvName.setText(bean.getGroup_name());

            groupId = bean.getGroup_id();
            groupName = bean.getGroup_name();

            if (mPostGroupId == groupId) {
                // 如果返回的 groupId 与服务器 groupId 一致，改变配置
                sbType.setText(mConfigType);
            }

            outItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Snackbar.make(v, "确定要删除组吗？", Snackbar.LENGTH_SHORT)
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
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_ctrl_group_name:
                    renameGroup(groupId);
                    break;
                case R.id.img_ctrl_group_switch:
                    // TODO: 2018/6/27
//                    Log.i("JET", "onClick: ID--->" + mDevice.getId());
                    long id303 = 810303;
                    long id311 = 810311;
                    long id316 = 810316;
                    List<Long> list = new ArrayList<>();
                    list.add(id303);
                    list.add(id316);
                    for (int i = 0; i < list.size(); i++) {
                        Log.i("SWITCH_CTRL", "list.get(i): " + list.get(i));
                    }
                    Log.i("SWITCH_CTRL", "间隔时间: " + mGap / 1000 + "秒");
                    Log.i("SWITCH_CTRL", "持续时间: " + mDuration / 10 + "秒");
                    Log.i("SWITCH_CTRL", "大间隔时间: " + mGapBig / 1000 + "秒");
                    Log.i("SWITCH_CTRL", "循环次数: " + mLoop);
                    Log.i("SWITCH_CTRL", "次数: " + mFrequency);
                    Log.i("SWITCH_CTRL", "高度: " + mHigh);
                    final byte[] pkg = Protocol.jet_start_package(id303, mGap, mDuration, mHigh);


//                    mDeviceControl.sendCommand(id303, pkg, new OnReceivePackage() {
//                        @Override
//                        public void ack(@NonNull byte[] pkg) {
//                            Log.i("JET", "喷射ACK--->" + pkg.length + "===" + pkg);
//                            int jet = Protocol.parseStartJet(pkg, pkg.length);
//                            Log.i("JET", "喷射解析--->" + jet);
//                        }
//
//                        @Override
//                        public void timeout() {
//                            Log.i("JET", "解析超时");
//                        }
//                    });
                    break;
                case R.id.img_ctrl_group_add:
                    Intent intent = new Intent(mContext, CoDeviceActivity.class);
                    intent.putExtra("member_id", mMemberId);
                    intent.putExtra("group_id", groupId);
                    intent.putExtra("group_name", groupName);
                    intent.putExtra("token", mToken);
                    mContext.startActivity(intent);
                    break;
                case R.id.sb_config_puff:
                    showPopTopWithDarkBg(groupId);
                    break;
                default:
                    break;
            }
        }

        // 显示 PopupWindow 同时背景变暗
        private void showPopTopWithDarkBg(int groupId) {
            View contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_pop_menu, null);
            // 处理 popWindow 显示内容
            handleLogic(contentView, groupId);
            // 创建并显示 popWindow
            mCustomPopWindow = new CustomPopWindow.PopupWindowBuilder(mContext)
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
                    .showAsDropDown(sbConfig, 0, 20);
        }

        // 处理弹出显示内容、点击事件等逻辑
        private void handleLogic(View contentView, final int groupId) {
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCustomPopWindow != null) {
                        mCustomPopWindow.dissmiss();
                    }
                    switch (v.getId()) {
                        case R.id.pop_config_stream:
                            mContext.startActivity(new Intent(mContext, CoConfigStreamActivity.class).putExtra("group_id", groupId));
                            break;
                        case R.id.pop_config_ride:
                            mContext.startActivity(new Intent(mContext, CoConfigRideActivity.class).putExtra("group_id", groupId));
                            break;
                        case R.id.pop_config_interval:
                            mContext.startActivity(new Intent(mContext, CoConfigIntervalActivity.class).putExtra("group_id", groupId));
                            break;
                        case R.id.pop_config_together:
                            mContext.startActivity(new Intent(mContext, CoConfigTogetherActivity.class).putExtra("group_id", groupId));
                            break;
                        default:
                            break;
                    }
                }
            };
            contentView.findViewById(R.id.pop_config_stream).setOnClickListener(listener);
            contentView.findViewById(R.id.pop_config_ride).setOnClickListener(listener);
            contentView.findViewById(R.id.pop_config_interval).setOnClickListener(listener);
            contentView.findViewById(R.id.pop_config_together).setOnClickListener(listener);
        }

        private void renameGroup(final int group_id) {
            // 重命名组 Dialog
            mDialog = new SelfDialog(mContext);
            mDialog.setTitle("请重新输入组名称");
            mDialog.setMessage("名字长度不能超过6个字符");
            mDialog.setYesOnclickListener("确定", new SelfDialog.onYesOnclickListener() {
                @Override
                public void onYesClick() {
                    getDataWithRenameGroup(mDialog.getEditTextStr(), group_id);
                    mDialog.dismiss();
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

        private void initGroupList(List<String> list) {
            LinearLayoutManager manager = new LinearLayoutManager(mContext);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            rvGroup.setLayoutManager(manager);
            rvGroup.setAdapter(new GroupListAdapter(list));
        }
    }

    class FootViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.img_add_group)
        ImageView imgAddGroup;

        FootViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
            imgAddGroup.setOnClickListener(this);
        }

        private void addGroup() {
            mDialog = new SelfDialog(mContext);
            mDialog.setTitle("请输入新建组名称");
            mDialog.setMessage("名字长度不能超过6个字符");
            mDialog.setYesOnclickListener("确定", new SelfDialog.onYesOnclickListener() {
                @Override
                public void onYesClick() {
                    // 新建组
                    getDataWithAddGroup(mDialog.getEditTextStr());
                    mDialog.dismiss();
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

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.img_add_group:
                    addGroup();
                    break;
                default:
                    break;
            }
        }
    }

    public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.DeviceHolder> {
        List<String> devices;

        GroupListAdapter(List<String> devices) {
            this.devices = devices;
        }

        @NonNull
        @Override
        public DeviceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new DeviceHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull DeviceHolder holder, int position) {
            holder.setData(mConfig, mDevice);
        }

        @Override
        public int getItemCount() {
            return devices.size();
        }

        public class DeviceHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.sb_device_add_list)
            SuperButton sbDevice;

            DeviceHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void setData(DeviceConfig config, Device device) {
                Log.e(AppConstant.TAG, "devices: " + devices.toString());
                sbDevice.setText(devices.get(getAdapterPosition()));
                // TODO: 2018/6/20  未连接显示不正常
//                if (device != null && config != null) {
//                    if (!device.isConnected()) {
//                        Log.e("TCF", "未连接!");
//                        sbDevice.setShapeSolidColor(mContext.getResources().getColor(R.color.colorNoClick));
//                        sbDevice.setShapeStrokeColor(mContext.getResources().getColor(R.color.colorNoClick));
//                        sbDevice.setUseShape();
//                    }
//                }
            }
        }
    }

    private void getDataWithDelGroup(int position) {
        int groupId = mList.get(position).getGroup_id();
        OkGo.<String>post(Urls.RM_GROUP)
                .tag(this)
                .params("member_id", mMemberId)
                .params("group_id", groupId)
                .params("token", mToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        getDataWithDeviceGroupList();
                    }
                });
    }

    private void getDataWithDeviceGroupList() {
        OkGo.<String>post(Urls.GET_DEVICE_GROUP_LIST)
                .tag(this)
                .params("member_id", mMemberId)
                .params("token", mToken)
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 String body = response.body();
                                 Log.e("GET_DEVICE_GROUP_LIST", "Network request succeeded！！！" + body);

                                 DeviceGroupListBean bean = mGson.fromJson(body, DeviceGroupListBean.class);
                                 mList = bean.getData();
                                 notifyDataSetChanged();
                             }
                         }
                );
    }

    private void getDataWithAddGroup(String groupName) {
        if (groupName.length() > 6) {
            groupName = groupName.substring(0, 6);
        }
        OkGo.<String>post(Urls.ADD_GROUP)
                .tag(this)
                .params("member_id", mMemberId)
                .params("group_name", groupName)
                .params("token", mToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        getDataWithDeviceGroupList();
                    }
                });
    }

    private void getDataWithRenameGroup(String groupName, int group_id) {
        if (groupName.length() > 6) {
            groupName = groupName.substring(0, 6);
        }
        OkGo.<String>post(Urls.RENAME_GROUP)
                .tag(this)
                .params("group_name", groupName)
                .params("group_id", group_id)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        getDataWithDeviceGroupList();
                    }
                });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(JetStatusEvent event) {
        switch (event.getType()) {
            case CONFIG_STREAM:
            case CONFIG_RIDE:
                initStatus();
                mGap = Integer.parseInt(event.getGap()) * 1000;
                mDuration = Integer.parseInt(event.getDuration()) * 10;
                mGapBig = Integer.parseInt(event.getGapBig());
                mLoop = Integer.parseInt(event.getLoop());
                break;
            case CONFIG_INTERVAL:
                initStatus();
                mGap = Integer.parseInt(event.getGap()) * 1000;
                mDuration = Integer.parseInt(event.getDuration()) * 10;
                mFrequency = Integer.parseInt(event.getFrequency());
                break;
            case CONFIG_TOGETHER:
                initStatus();
                mDuration = Integer.parseInt(event.getDuration()) * 10;
                mHigh = Integer.parseInt(event.getHigh());
                break;
            default:
                break;
        }

        Log.i("JET_STATUS", "mPostGroupId: " + event.getGroupId());
        Log.i("JET_STATUS", "configType: " + mConfigType);
        Log.i("JET_STATUS", "direction: 方向--->" + mDirection);
        Log.i("JET_STATUS", "gap: 间隔时间--->" + mGap);
        Log.i("JET_STATUS", "duration: 持续时间--->" + mDuration);
        Log.i("JET_STATUS", "gapBig: 大间隔时间--->" + mGapBig);
        Log.i("JET_STATUS", "loop: 循环次数--->" + mLoop);
        Log.i("JET_STATUS", "frequency: 次数（换向）--->" + mFrequency);
        Log.i("JET_STATUS", "high: 高度--->" + mHigh);
    }

    private void initStatus() {
        mConfigType = 0;
        mDirection = 0;
        mGap = 0;
        mDuration = 0;
        mGapBig = 0;
        mLoop = 0;
        mFrequency = 0;
        mHigh = 0;
    }

}
