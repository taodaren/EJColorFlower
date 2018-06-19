package cn.eejing.ejcolorflower.ui.adapter;

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
import android.widget.Toast;

import com.allen.library.SuperButton;
import com.example.zhouwei.library.CustomPopWindow;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.app.Urls;
import cn.eejing.ejcolorflower.device.Device;
import cn.eejing.ejcolorflower.device.DeviceConfig;
import cn.eejing.ejcolorflower.model.request.DeviceGroupListBean;
import cn.eejing.ejcolorflower.ui.activity.CoDeviceActivity;
import cn.eejing.ejcolorflower.ui.activity.ConfigIntervalActivity;
import cn.eejing.ejcolorflower.ui.activity.ConfigRideActivity;
import cn.eejing.ejcolorflower.ui.activity.ConfigStreamActivity;
import cn.eejing.ejcolorflower.ui.activity.ConfigTogetherActivity;
import cn.eejing.ejcolorflower.util.SelfDialog;
import cn.eejing.ejcolorflower.util.Settings;

/**
 * @创建者 Taodaren
 * @描述 控制模块适配器
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


    public TabControlAdapter(Context mContext, List<DeviceGroupListBean.DataBean> mList, String mMemberId) {
        this.mContext = mContext;
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.mList = mList;
        this.mGson = new Gson();
        this.mMemberId = mMemberId;
        this.mToken = Settings.getLoginSessionInfo(mContext).getToken();
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
            ((ItemViewHolder) holder).setData(mList.get(position), position, mConfig, mDevice);
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
        Log.i("TCF", "setDeviceConfig: isConnected--->" + device.isConnected());
        Log.i("TCF", "setDeviceConfig: mID--->" + config.mID);
        Log.i("TCF", "setDeviceConfig: mDMXAddress--->" + config.mDMXAddress);

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
        public void setData(DeviceGroupListBean.DataBean bean, final int position, DeviceConfig config, Device device) {
            if (bean.getGroup_list() != null && bean.getGroup_list().size() > 0) {
                Log.i("TCF", "setData: isConnected--->" + device.isConnected());
                Log.i("TCF", "setData: mDMXAddress--->" + config.mDMXAddress);
                Log.i("TCF", "setData: mID--->" + config.mID);
                // 如果有设备，显示设备，隐藏提示文字
                tvInfo.setVisibility(View.GONE);
                rvGroup.setVisibility(View.VISIBLE);
                initGroupList(bean.getGroup_list());

                if (device.isConnected()) {
                    // TODO: 2018/6/19 连接成功
                } else {
                }
            } else {
                tvInfo.setVisibility(View.VISIBLE);
                rvGroup.setVisibility(View.INVISIBLE);
            }

            tvName.setText(bean.getGroup_name());

            groupId = bean.getGroup_id();
            groupName = bean.getGroup_name();

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
                    Toast.makeText(mContext, "img_ctrl_group_switch", Toast.LENGTH_SHORT).show();
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
                    showPopTopWithDarkBg();
                    break;
                default:
                    break;
            }
        }

        // 显示 PopupWindow 同时背景变暗
        private void showPopTopWithDarkBg() {
            View contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_pop_menu, null);
            // 处理 popWindow 显示内容
            handleLogic(contentView);
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
        private void handleLogic(View contentView) {
            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mCustomPopWindow != null) {
                        mCustomPopWindow.dissmiss();
                    }
                    switch (v.getId()) {
                        case R.id.pop_config_stream:
                            mContext.startActivity(new Intent(mContext, ConfigStreamActivity.class));
                            break;
                        case R.id.pop_config_ride:
                            mContext.startActivity(new Intent(mContext, ConfigRideActivity.class));
                            break;
                        case R.id.pop_config_interval:
                            mContext.startActivity(new Intent(mContext, ConfigIntervalActivity.class));
                            break;
                        case R.id.pop_config_together:
                            mContext.startActivity(new Intent(mContext, ConfigTogetherActivity.class));
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
            holder.setData();
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

            public void setData() {
                Log.e(AppConstant.TAG, "devices: " + devices.toString());
                for (int i = 0; i < devices.size(); i++) {
                    sbDevice.setText(devices.get(getAdapterPosition()));
                }
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
        OkGo.<String>post(Urls.ADD_GROUP)
                .tag(this)
                .params("member_id", mMemberId)
                .params("group_name", groupName.substring(0, 6))
                .params("token", mToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        getDataWithDeviceGroupList();
                    }
                });
    }

    private void getDataWithRenameGroup(String groupName, int group_id) {
        OkGo.<String>post(Urls.RENAME_GROUP)
                .tag(this)
                .params("group_name", groupName.substring(0, 6))
                .params("group_id", group_id)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        getDataWithDeviceGroupList();
                    }
                });
    }

}
