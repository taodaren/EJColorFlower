package cn.eejing.ejcolorflower.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.device.BleDeviceProtocol;
import cn.eejing.ejcolorflower.device.DeviceConfig;
import cn.eejing.ejcolorflower.device.DeviceStatus;
import cn.eejing.ejcolorflower.model.event.DeviceConnectEvent;
import cn.eejing.ejcolorflower.model.event.JetStatusEvent;
import cn.eejing.ejcolorflower.model.lite.CtrlIntervalEntity;
import cn.eejing.ejcolorflower.model.lite.CtrlRideEntity;
import cn.eejing.ejcolorflower.model.lite.CtrlStreamEntity;
import cn.eejing.ejcolorflower.model.lite.CtrlTogetherEntity;
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
import static cn.eejing.ejcolorflower.app.AppConstant.DEVICE_CONNECT_NO;
import static cn.eejing.ejcolorflower.app.AppConstant.DEVICE_CONNECT_YES;
import static cn.eejing.ejcolorflower.app.AppConstant.EMPTY;

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

    // 硬件相关
    private AppActivity.FireworkDevCtrl mDevCtrl;
    private DeviceStatus mState;
    private DeviceConfig mConfig;
    private String mConnStatus, mConnDevMac;
    private long mConnDevID;

    private String mConfigType;
    private long mMillis;
    private int mPostGroupId, mGap, mDirection, mDuration, mGapBig, mLoop, mFrequency, mHigh;

    public TabControlAdapter(Context mContext, List<DeviceGroupListBean.DataBean> mList, String mMemberId) {
        this.mContext = mContext;
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.mList = mList;
        this.mGson = new Gson();
        this.mMemberId = mMemberId;
        this.mToken = Settings.getLoginSessionInfo(mContext).getToken();
        this.mDevCtrl = AppActivity.getFireworksDevCtrl();
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
            ((ItemViewHolder) holder).setData(mList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size() + 1;
        } else {
            return 1;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (mList != null) {
            if (position < mList.size()) {
                return TYPE_ITEM;
            } else {
                return TYPE_FOOTER;
            }
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

    class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_ctrl_group_name)        TextView tvName;
        @BindView(R.id.rv_control_group)          RecyclerView rvGroup;
        @BindView(R.id.tv_ctrl_group_info)        TextView tvInfo;
        @BindView(R.id.sb_type_puff)              SuperButton sbType;
        @BindView(R.id.sb_config_puff)            SuperButton sbConfig;

        // 服务器分组名称、ID
        String groupName;
        int groupId;
        // 时间戳
        long flagMillis = 0;
        // 默认配置
        String strConfigType = EMPTY;
        // 数据库保存信息
        List<CtrlStreamEntity> streamDBList;
        List<CtrlRideEntity> rideDBList;
        List<CtrlIntervalEntity> intervalDBList;
        List<CtrlTogetherEntity> togetherDBList;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("ResourceAsColor")
        public void setData(DeviceGroupListBean.DataBean bean) {
            groupName = bean.getGroup_name();
            groupId = bean.getGroup_id();

            // 设置分组名称
            tvName.setText(bean.getGroup_name());

            Log.i("SLN", "【Config Info】"+
                    " group_id " + groupId +
                    "\nmConfigType: " + mConfigType +
                    "\nmPostGroupId: " + mPostGroupId +
                    "\nmGap: " + mGap +
                    "\nmDirection: " + mDirection +
                    "\nmDuration: " + mDuration +
                    "\nmGapBig: " + mGapBig +
                    "\nmLoop: " + mLoop +
                    "\nmFrequency: " + mFrequency +
                    "\nmHigh: " + mHigh
            );

            // 数据库中查询是否保存信息
            isSavaDBInfo();

            Log.i("LJQ", "setDataConn"
                    + "\n设备 MAC: " + mConnDevMac
                    + "\n设备 ID: " + mConnDevID
                    + "\n设备 STATE: " + mState
                    + "\n设备 CONFIG: " + mConfig
                    + "\n设备 mPostGroupId: " + mPostGroupId
                    + "\n设备 groupId: " + bean.getGroup_id()
            );

            if (bean.getGroup_list() != null && bean.getGroup_list().size() > 0) {
                // 如果有设备，显示设备，隐藏提示文字
                tvInfo.setVisibility(View.GONE);
                rvGroup.setVisibility(View.VISIBLE);
                initGroupDevList(bean.getGroup_list());
            } else {
                tvInfo.setVisibility(View.VISIBLE);
                rvGroup.setVisibility(View.INVISIBLE);
            }
        }

        /** 数据库中查询是否保存信息 */
        private void isSavaDBInfo() {
            streamDBList = LitePal.select("configType", "millis").where("groupId = ?", String.valueOf(groupId)).find(CtrlStreamEntity.class);
            rideDBList = LitePal.select("configType", "millis").where("groupId = ?", String.valueOf(groupId)).find(CtrlRideEntity.class);
            intervalDBList = LitePal.select("configType", "millis").where("groupId = ?", String.valueOf(groupId)).find(CtrlIntervalEntity.class);
            togetherDBList = LitePal.select("configType", "millis").where("groupId = ?", String.valueOf(groupId)).find(CtrlTogetherEntity.class);

            Log.i("CFG_GUB", "isSavaDBInfo mPostGroupId: " + mPostGroupId);
            switch (mPostGroupId) {
                case 0:
                    // 默认进入控制模块
                    defEnterConfig();
                    break;
                default:
                    // 从编辑界面跳转回来
                    jumpBackConfig();
                    break;
            }
        }

        /** 默认进入控制模块 */
        private void defEnterConfig() {
            Log.i("CFG_GUB", "stream size: " + streamDBList.size());
            Log.i("CFG_GUB", "ride size: " + rideDBList.size());
            Log.i("CFG_GUB", "interval size: " + intervalDBList.size());
            Log.i("CFG_GUB", "together size: " + togetherDBList.size());
            if (streamDBList.size() != 0) {
                compareTime(streamDBList.get(0).getMillis(), streamDBList.get(0).getConfigType());
            }

            if (rideDBList.size() != 0) {
                compareTime(rideDBList.get(0).getMillis(), rideDBList.get(0).getConfigType());
            }

            if (intervalDBList.size() != 0) {
                compareTime(intervalDBList.get(0).getMillis(), intervalDBList.get(0).getConfigType());
            }

            if (togetherDBList.size() != 0) {
                compareTime(togetherDBList.get(0).getMillis(), togetherDBList.get(0).getConfigType());
            }

            isEmptyConfigType(strConfigType);
        }

        /** 从编辑界面跳转回来 */
        private void jumpBackConfig() {
            if (groupId == mPostGroupId) {
                isEmptyConfigType(mConfigType);
            }
        }

        private void compareTime(long millis, String configType) {
            if (millis > flagMillis) {
                flagMillis = millis;
                strConfigType = configType;
            }
        }

        private void isEmptyConfigType(String configType) {
            if (TextUtils.isEmpty(configType)) {
                defaultShow();
            } else {
                sbType.setText(configType);
            }
        }

        private void defaultShow() {
            // 显示默认数据（齐喷、10s、高度100）
            sbType.setText(CONFIG_TOGETHER);
            mDuration = 100;
            mHigh = 100;
        }

        @OnClick(R.id.tv_ctrl_group_name)
        public void onClickRenameGroup() {
            // 重命名组
            renameGroup(groupId);
        }

        @OnClick(R.id.img_ctrl_group_jet)
        public void onClickJet() {
            // 控制喷射
            ctrlJet();
        }

        @OnClick(R.id.sb_config_puff)
        public void onClickConfigJetStyle() {
            // 配置喷射样式
            configJetStyle(groupId);
        }

        @OnClick(R.id.img_ctrl_group_add)
        public void onClickCtrlGroup() {
            // 控制分组设备
            ctrlGroup();
        }

        @OnLongClick(R.id.layout_ctrl_group_list)
        public boolean onLongClickDelGroup() {
            // 删除组
            delGroup();
            return true;
        }

        /** 删除组 */
        private void delGroup() {
            Snackbar.make(itemView, "确定要删除组吗？", Snackbar.LENGTH_SHORT)
                    .setAction("确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getDataWithDelGroup(getAdapterPosition());
                        }
                    })
                    .show();
        }

        /** 重命名组 */
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

        /** 控制分组设备 */
        private void ctrlGroup() {
            Intent intent = new Intent(mContext, CoDeviceActivity.class);
            intent.putExtra("member_id", mMemberId);
            intent.putExtra("group_id", groupId);
            intent.putExtra("group_name", groupName);
            intent.putExtra("token", mToken);
            mContext.startActivity(intent);
        }

        /** 配置喷射样式 */
        private void configJetStyle(int groupId) {
            // 显示 PopupWindow 同时背景变暗
            @SuppressLint("InflateParams")
            View contentView = LayoutInflater.from(mContext).inflate(R.layout.layout_pop_menu, null);
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
                    // 设置 pop 位置
                    .showAsDropDown(sbConfig, -100, 20);
        }

        /** 处理弹出显示内容、点击事件等逻辑 */
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

        /** 控制喷射 */
        private void ctrlJet() {
            // TODO: 2018/6/27
//                    Log.i("JET", "onClick: ID--->" + mDevice.getId());
            long id303 = 810303;// has
            long id311 = 810311;// ok
            long id316 = 810316;// has
            final List<Long> list = new ArrayList<>();
            list.add(id303);
            list.add(id316);

            for (int i = 0; i < list.size(); i++) {
                Log.e("SWITCH_CTRL", "第 " + i + " 个设备");
                Log.i("SWITCH_CTRL", "list.get(i): " + list.get(i));
                Log.i("SWITCH_CTRL", "list.size() - i: " + (list.size() - i));
                switch (mConfigType) {
                    case CONFIG_STREAM:
                        final byte[] pkgStream = BleDeviceProtocol.pkgJetStart(list.get(i),// from device
                                mGap * i, (list.size() - i) * mDuration, mHigh);

                        // 循环 loop 次
                        for (int loop = 0; loop < mLoop; loop++) {
                            if (loop == 0) {
                                Log.e("SWITCH_CTRL", list.get(i) + "第" + loop + "次喷射");

                                try {
                                    // 如果是首次喷射，直接发送喷射命令
                                    jetStart(list.get(i), pkgStream);
                                    // 为了多台设备一起喷射，5毫秒人实际感受不到
                                    Thread.sleep(5);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

//                                    if (loop > 0) {
//                                        Log.e("SWITCH_CTRL", list.get(i) + "第" + loop + "次喷射");
//
//
//                                        try {
//                                            Log.e("SWITCH_CTRL", list.get(i) + "第" + loop + "次喷射");
//
//                                            new Thread(new Runnable() {
//                                                @Override
//                                                public void run() {
//                                                    try {
//                                                        // 两次循环间隔时间
//                                                        TimeUnit.SECONDS.sleep((long) mGapBig);
//                                                        jetStart(list.get(i), pkgStream);
//                                                        Thread.sleep(5);
//                                                    } catch (InterruptedException e) {
//                                                        e.printStackTrace();
//                                                    }
//                                                }
//                                            }).start();
//
//                                        } catch (InterruptedException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
                        }
                        break;
                    case CONFIG_RIDE:
                        Log.i("SWITCH_CTRL", "RIDE_I: " + i);
                        Log.i("SWITCH_CTRL", "RIDE_延时: " + (mDirection / 10 + mGap / 1000) * 1000 * i);
                        Log.i("SWITCH_CTRL", "RIDE_喷射时间: " + mDuration);
                        Log.i("SWITCH_CTRL", "RIDE_高度: " + mHigh);
                        Log.i("SWITCH_CTRL", "RIDE_大间隔时间: " + mGapBig);
                        Log.i("SWITCH_CTRL", "RIDE_循环次数: " + mLoop);

                        byte[] pkgRide = BleDeviceProtocol.pkgJetStart(list.get(i),// from device
                                (mDirection / 10 + mGap / 1000) * 1000 * i, mDuration, mHigh);
                        break;
                    case CONFIG_INTERVAL:
                        // 间隔高低
                        jetInterval(list, i);
                        break;
                    case CONFIG_TOGETHER:
                        // 齐喷
                        jetTogether(list, i);
                        break;
                    default:
                        break;
                }
            }
        }

        /** 间隔高低次数判断 */
        private void frequencyInterval(List<Long> list, int i, byte[] pkgInterval) {
            if (mFrequency == 0) {
                try {
                    jetStart(list.get(i), pkgInterval);
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            if (mFrequency > 0) {

            }
        }

        /** 喷射—间隔 */
        private void jetInterval(List<Long> list, int i) {
            byte[] pkgInterval;
            try {
                if (i % 2 == 0) {
                    // 如果设备是第偶数个，高度100
                    pkgInterval = BleDeviceProtocol.pkgJetStart(list.get(i),// from device
                            0, mDuration, mHigh);
                    frequencyInterval(list, i, pkgInterval);
                    Thread.sleep(1);
                }

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (i % 2 == 1) {
                // 如果设备是第奇数个，高度60
                pkgInterval = BleDeviceProtocol.pkgJetStart(list.get(i),// from device
                        0, mDuration, 60);
                frequencyInterval(list, i, pkgInterval);
            }
        }

        /** 喷射—齐喷 */
        private void jetTogether(List<Long> list, int i) {
            Log.i("SWITCH_CTRL", "TOGETHER_喷射时间: " + mDuration);
            Log.i("SWITCH_CTRL", "TOGETHER_高度: " + mHigh);

            byte[] pkgTogether = BleDeviceProtocol.pkgJetStart(list.get(i),// from device
                    mGap, mDuration, mHigh);
            try {
                jetStart(list.get(i), pkgTogether);
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /** 开始喷射 */
        private void jetStart(long deviceId, byte[] pkg) {
            mDevCtrl.sendCommand(deviceId, pkg, new OnReceivePackage() {
                @Override
                public void ack(@NonNull byte[] pkg) {
                    Log.i("JET", "喷射ACK--->" + pkg.length + "===" + pkg);
                    int jet = BleDeviceProtocol.parseStartJet(pkg, pkg.length);
                    Log.i("JET", "喷射解析--->" + jet);
                }

                @Override
                public void timeout() {
                    Log.i("JET", "解析超时");
                }
            });
        }

        /** 初始化分组设备列表 */
        private void initGroupDevList(List<String> list) {
            LinearLayoutManager manager = new LinearLayoutManager(mContext);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            rvGroup.setLayoutManager(manager);
            rvGroup.setAdapter(new GroupListAdapter(list));
        }
    }

    class FootViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_footer_add)        TextView tvAdd;

        FootViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
            tvAdd.setText(R.string.add_group);
        }

        @OnClick(R.id.img_footer_add)
        public void onViewClicked() {
            addGroup();
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

    }

    public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.DeviceHolder> {
        List<String> devList;

        GroupListAdapter(List<String> devList) {
            this.devList = devList;
        }

        @NonNull
        @Override
        public DeviceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new DeviceHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_device, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull DeviceHolder holder, int position) {
            holder.setData(position);
        }

        @Override
        public int getItemCount() {
            return devList.size();
        }

        public class DeviceHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.sb_device_add_list)            SuperButton sbDevice;

            DeviceHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void setData(int position) {
                Log.e(AppConstant.TAG, "devList: " + devList.toString());
                sbDevice.setText(devList.get(getAdapterPosition()));
                // TODO: 2018/6/20  未连接显示不正常
                if (mConnStatus != null) {
                    switch (mConnStatus) {
                        case DEVICE_CONNECT_YES:
                            if (devList.get(position).equals(String.valueOf(mConnDevID))) {
                                sbDevice.setShapeSolidColor(mContext.getResources().getColor(R.color.colorTransparent));
                                sbDevice.setShapeStrokeColor(mContext.getResources().getColor(R.color.colorFrame));
                                sbDevice.setUseShape();
                            }
                            break;
                        case DEVICE_CONNECT_NO:
                            sbDevice.setShapeSolidColor(mContext.getResources().getColor(R.color.colorFrame));
                            sbDevice.setShapeStrokeColor(mContext.getResources().getColor(R.color.colorFrame));
                            sbDevice.setUseShape();
                            break;
                    }
                }
//                    for (String devIdServer : devList) {
//                        if (devIdServer.equals(String.valueOf(mConnDevID))) {
//                            sbDevice.setShapeSolidColor(mContext.getResources().getColor(R.color.colorTransparent));
//                            sbDevice.setShapeStrokeColor(mContext.getResources().getColor(R.color.colorFrame));
//                            sbDevice.setUseShape();
//                        }
//                    }
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
    public void onEventDevConn(DeviceConnectEvent event) {
        // 接收硬件传过来的已连接设备信息
        mConnDevMac = event.getMac();
        mConnStatus = event.getInfo();

        mState = event.getState();
        mConfig = event.getConfig();
        mConnDevID = event.getConfig().mID;

        Log.i("JLTHYC", mConnStatus
                + "\nConn Mac--->" + mConnDevMac
                + "\nConn mConnDevID--->" + mConnDevID
                + "\nTEMP--->" + mState.mTemperature
                + "\nDMX--->" + mConfig.mDMXAddress
                + "\nTIME--->" + mState.mRestTime
        );

        notifyDataSetChanged();
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
