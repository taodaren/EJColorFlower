package cn.eejing.ejcolorflower.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.model.event.ConnDevInfo;
import cn.eejing.ejcolorflower.model.event.DeviceConnectEvent;
import cn.eejing.ejcolorflower.model.event.JetStatusEvent;
import cn.eejing.ejcolorflower.model.lite.CtrlIntervalEntity;
import cn.eejing.ejcolorflower.model.lite.CtrlRideEntity;
import cn.eejing.ejcolorflower.model.lite.CtrlStreamEntity;
import cn.eejing.ejcolorflower.model.lite.CtrlTogetherEntity;
import cn.eejing.ejcolorflower.model.manager.MgrIntervalJet;
import cn.eejing.ejcolorflower.model.manager.MgrOutputJet;
import cn.eejing.ejcolorflower.model.manager.MgrRideJet;
import cn.eejing.ejcolorflower.model.manager.MgrStreamJet;
import cn.eejing.ejcolorflower.model.manager.MgrTogetherJet;
import cn.eejing.ejcolorflower.model.request.DeviceGroupListBean;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.util.JetCommandTools;
import cn.eejing.ejcolorflower.util.SelfDialog;
import cn.eejing.ejcolorflower.util.Settings;
import cn.eejing.ejcolorflower.view.activity.CtConfigIntervalActivity;
import cn.eejing.ejcolorflower.view.activity.CtConfigRideActivity;
import cn.eejing.ejcolorflower.view.activity.CtConfigStreamActivity;
import cn.eejing.ejcolorflower.view.activity.CtConfigTogetherActivity;
import cn.eejing.ejcolorflower.view.activity.CoDeviceActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.CLEAR_MATERIAL_GROUP;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_DEF;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_INTERVAL;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_RIDE;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_STREAM;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_TOGETHER;
import static cn.eejing.ejcolorflower.app.AppConstant.CTRL_DEV_NUM;
import static cn.eejing.ejcolorflower.app.AppConstant.CURRENT_TIME;
import static cn.eejing.ejcolorflower.app.AppConstant.DEFAULT_TOGETHER_DURATION;
import static cn.eejing.ejcolorflower.app.AppConstant.DEFAULT_TOGETHER_HIGH;
import static cn.eejing.ejcolorflower.app.AppConstant.DEVICE_CONNECT_NO;
import static cn.eejing.ejcolorflower.app.AppConstant.DEVICE_CONNECT_YES;
import static cn.eejing.ejcolorflower.app.AppConstant.EMPTY;
import static cn.eejing.ejcolorflower.app.AppConstant.LOOP_ID;

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
    private CustomPopWindow mPopWindow;
    private SelfDialog mDialog;

    // 硬件相关
    private String mConfigType;
    private long mMillis;
    private int mPostGroupId, mGap, mDirection, mDuration, mGapBig, mLoop, mFrequency, mHigh;
    private String mConnStatus;
    private long mConnDevID;
    private int mConnDmx;
    private Map<Long, ConnDevInfo> mConnDevMap;
    private List<ConnDevInfo> mConnDevList;

    public TabControlAdapter(Context mContext, List<DeviceGroupListBean.DataBean> mList, String mMemberId) {
        this.mContext = mContext;
        this.mLayoutInflater = LayoutInflater.from(mContext);
        this.mList = mList;
        this.mGson = new Gson();
        this.mMemberId = mMemberId;
        this.mToken = Settings.getLoginSessionInfo(mContext).getToken();
        this.mConnDevMap = new HashMap();
        this.mConnDevList = new ArrayList<>();
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
        @BindView(R.id.img_ctrl_group_jet)        ImageView imgJet;

        // 数据库保存信息
        List<CtrlStreamEntity> streamDBList;
        List<CtrlRideEntity> rideDBList;
        List<CtrlIntervalEntity> intervalDBList;
        List<CtrlTogetherEntity> togetherDBList;
        // 默认配置
        String strConfigType = EMPTY;
        // 服务器分组名称
        int groupId;
        // 服务器分组 ID
        String groupName;
        // 时间戳
        long flagMillis = 0;

        Handler handler;
        boolean isStar;
        MgrOutputJet mgrFather;
        MgrStreamJet mgrStream;
        MgrRideJet mgrRide;
        MgrIntervalJet mgrInterval;
        MgrTogetherJet mgrTogether;

        @SuppressLint("HandlerLeak")
        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            handler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    switch (msg.what) {
                        case 4:
                            long millis = System.currentTimeMillis();
                            timerCallingMethod(mgrFather);
                            if (isStar) {
                                // 每 0.1s 发一次消息，但需要减去时间戳之差
                                handler.sendEmptyMessageDelayed(4, 100 - (System.currentTimeMillis() - millis));
                            }
                            break;
                    }
                }
            };
        }

        /** 定时调用方法（每 0.1 秒给通过蓝牙设备发一次信息）*/
        private void timerCallingMethod(MgrOutputJet mgr) {
            byte[] highs = new byte[CTRL_DEV_NUM];
            boolean isFinish = false;
            Log.e("CMCML", "isStar: " + isStar);
            if (isStar) isFinish = mgr.updateWithDataOut(highs);

            Log.i("CMCML", "isFinish: " + isFinish);
            if (!isFinish) {
                // 开始喷射
                JetCommandTools.jetStart(mConnDevList, highs);
            } else {
                Log.e("CMCML", "喷射停止!!!!!!!!!!");
                // 停止喷射五次，喷射完成，喷射停止状态
                JetCommandTools.jetStopFive(mConnDevList);
                // 清料
                JetCommandTools.clearMaterial(mContext, mConnDevList, -1, CLEAR_MATERIAL_GROUP, 0, 0, 0);
                handler.removeMessages(4);
                isStar = false;
                imgJet.setImageDrawable(mContext.getDrawable(R.drawable.ic_jet_dev_star));
            }
        }

        @SuppressLint("ResourceAsColor")
        public void setData(DeviceGroupListBean.DataBean bean) {
            groupName = bean.getGroup_name();
            groupId = bean.getGroup_id();

            // 设置分组名称
            tvName.setText(bean.getGroup_name());

            Log.i("SLN", "【setData 喷射信息】"+
                    " group_id " + groupId +
                    "\nConfigType: " + mConfigType +
                    "\nPostGroupId: " + mPostGroupId +
                    "\nGap: " + mGap +
                    "\nDirection: " + mDirection +
                    "\nDuration: " + mDuration +
                    "\nGapBig: " + mGapBig +
                    "\nLoop: " + mLoop +
                    "\nFrequency: " + mFrequency +
                    "\nHigh: " + mHigh
            );

            // 数据库中查询是否保存信息
            isSaveDBInfo();

            if (bean.getGroup_list() != null && bean.getGroup_list().size() > 0) {
                // 如果有设备，显示设备，隐藏提示文字
                tvInfo.setVisibility(View.GONE);
                rvGroup.setVisibility(View.VISIBLE);
                initDevList(bean.getGroup_list());
            } else {
                tvInfo.setVisibility(View.VISIBLE);
                rvGroup.setVisibility(View.INVISIBLE);
            }
        }

        /** 数据库中查询是否保存信息 */
        private void isSaveDBInfo() {
            streamDBList = LitePal.where("groupId = ?", String.valueOf(groupId)).find(CtrlStreamEntity.class);
            rideDBList = LitePal.where("groupId = ?", String.valueOf(groupId)).find(CtrlRideEntity.class);
            intervalDBList = LitePal.where("groupId = ?", String.valueOf(groupId)).find(CtrlIntervalEntity.class);
            togetherDBList = LitePal.where("groupId = ?", String.valueOf(groupId)).find(CtrlTogetherEntity.class);

            Log.i("CFG_GUB", "isSaveDBInfo mPostGroupId: " + mPostGroupId);
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

        private void isEmptyConfigType(String configType) {
            if (TextUtils.isEmpty(configType)) {
                defaultShow();
            } else {
                sbType.setText(configType);
            }
        }

        /** 比较时间 */
        private void compareTime(long millis, String configType) {
            if (millis > flagMillis) {
                flagMillis = millis;
                strConfigType = configType;
            }
        }

        private void defaultShow() {
            // 显示默认数据（齐喷、10s、高度100）
            sbType.setText(CONFIG_TOGETHER);
            mConfigType = CONFIG_TOGETHER;
            mDuration = Integer.parseInt(DEFAULT_TOGETHER_DURATION);
            mHigh = Integer.parseInt(DEFAULT_TOGETHER_HIGH);
        }

        @OnClick({R.id.tv_ctrl_group_name, R.id.tv_ctrl_group_clear, R.id.img_ctrl_group_jet, R.id.sb_config_puff, R.id.img_ctrl_group_add})
        public void onClickView(View view) {
            switch (view.getId()) {
                case R.id.tv_ctrl_group_name:
                    // 重命名组
                    renameGroup(groupId);
                    break;
                case R.id.tv_ctrl_group_clear:
                    // 清料
                    JetCommandTools.clearMaterial(mContext, mConnDevList, -1, CLEAR_MATERIAL_GROUP, 0, 0, 0);
                    break;
                case R.id.img_ctrl_group_jet:
                    // 控制喷射
                    ctrlJet();
                    break;
                case R.id.sb_config_puff:
                    // 配置喷射样式
                    configJetStyle(groupId);
                    break;
                case R.id.img_ctrl_group_add:
                    // 控制分组设备
                    ctrlGroup();
                    break;
                default:
                    break;
            }
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
            mPopWindow = new CustomPopWindow.PopupWindowBuilder(mContext)
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
                    if (mPopWindow != null) {
                        mPopWindow.dissmiss();
                    }
                    switch (v.getId()) {
                        case R.id.pop_config_stream:
                            mContext.startActivity(new Intent(mContext, CtConfigStreamActivity.class).putExtra("group_id", groupId));
                            break;
                        case R.id.pop_config_ride:
                            mContext.startActivity(new Intent(mContext, CtConfigRideActivity.class).putExtra("group_id", groupId));
                            break;
                        case R.id.pop_config_interval:
                            mContext.startActivity(new Intent(mContext, CtConfigIntervalActivity.class).putExtra("group_id", groupId));
                            break;
                        case R.id.pop_config_together:
                            mContext.startActivity(new Intent(mContext, CtConfigTogetherActivity.class).putExtra("group_id", groupId));
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
            getConnDevList();

            if (streamDBList.size() > 0 && sbType.getText().equals(CONFIG_STREAM)) {
                // 流水灯
                ctrlJetType(CONFIG_STREAM);
            } else if (rideDBList.size() > 0 && sbType.getText().equals(CONFIG_RIDE)) {
                // 跑马灯
                ctrlJetType(CONFIG_RIDE);
            } else if (intervalDBList.size() > 0 && sbType.getText().equals(CONFIG_INTERVAL)) {
                // 间隔高低
                ctrlJetType(CONFIG_INTERVAL);
            } else if (togetherDBList.size() > 0 && sbType.getText().equals(CONFIG_TOGETHER)) {
                // 齐喷
                ctrlJetType(CONFIG_TOGETHER);
            } else {
                // 默认齐喷
                ctrlJetType(CONFIG_DEF);
            }
        }

        private void ctrlJetType(String type) {
            if (isStar) {
                // 如果是喷射状态，点击变为停止状态
                handler.removeMessages(4);
                isStar = false;
                imgJet.setImageDrawable(mContext.getDrawable(R.drawable.ic_jet_dev_star));
                JetCommandTools.jetStop(mConnDevList);
            } else {
                // 如果是停止状态，点击变为喷射状态
                Log.e("CMCML", type + "喷开始!!!!!!!!!!");
                isStar = true;
                imgJet.setImageDrawable(mContext.getDrawable(R.drawable.ic_jet_dev_stop));
                switch (type) {
                    case CONFIG_STREAM:
                        setStreamData();
                        break;
                    case CONFIG_RIDE:
                        setRideData();
                        break;
                    case CONFIG_INTERVAL:
                        setIntervalData();
                        break;
                    case CONFIG_TOGETHER:
                        setTogetherData(type);
                        break;
                    default:
                        setTogetherData(type);
                        break;
                }
                handler.sendEmptyMessage(4);
            }
        }

        private void setStreamData() {
            mgrStream = new MgrStreamJet();
            mgrStream.setType(CONFIG_STREAM);
            mgrStream.setDevCount(mConnDevList.size());
            mgrStream.setCurrentTime(CURRENT_TIME);
            mgrStream.setLoopId(LOOP_ID);
            mgrStream.setHigh((byte) Integer.parseInt(DEFAULT_TOGETHER_HIGH));
            mgrStream.setDirection(Integer.parseInt(streamDBList.get(0).getDirection()));
            mgrStream.setGap(Integer.parseInt(streamDBList.get(0).getGap()) * 10);
            mgrStream.setDuration(Integer.parseInt(streamDBList.get(0).getDuration()) * 10);
            mgrStream.setGapBig(Integer.parseInt(streamDBList.get(0).getGapBig()) * 10);
            mgrStream.setLoop(Integer.parseInt(streamDBList.get(0).getLoop()));
            mgrFather = mgrStream;
        }

        private void setRideData() {
            mgrRide = new MgrRideJet();
            mgrRide.setType(CONFIG_RIDE);
            mgrRide.setDevCount(mConnDevList.size());
            mgrRide.setCurrentTime(CURRENT_TIME);
            mgrRide.setLoopId(LOOP_ID);
            mgrRide.setHigh((byte) Integer.parseInt(DEFAULT_TOGETHER_HIGH));
            mgrRide.setDirection(Integer.parseInt(rideDBList.get(0).getDirection()));
            mgrRide.setGap(Integer.parseInt(rideDBList.get(0).getGap()) * 10);
            mgrRide.setDuration(Integer.parseInt(rideDBList.get(0).getDuration()) * 10);
            mgrRide.setGapBig(Integer.parseInt(rideDBList.get(0).getGapBig()) * 10);
            mgrRide.setLoop(Integer.parseInt(rideDBList.get(0).getLoop()));
            mgrFather = mgrRide;
        }

        private void setIntervalData() {
            mgrInterval = new MgrIntervalJet();
            mgrInterval.setType(CONFIG_INTERVAL);
            mgrInterval.setDevCount(mConnDevList.size());
            mgrInterval.setCurrentTime(CURRENT_TIME);
            mgrInterval.setLoopId(LOOP_ID);
            mgrInterval.setGapBig(Integer.parseInt(intervalDBList.get(0).getGap()) * 10);
            mgrInterval.setDuration(Integer.parseInt(intervalDBList.get(0).getDuration()) * 10);
            mgrInterval.setLoop(Integer.parseInt(intervalDBList.get(0).getFrequency()));
            mgrFather = mgrInterval;
        }

        private void setTogetherData(String isDef) {
            mgrTogether = new MgrTogetherJet();
            mgrTogether.setType(CONFIG_TOGETHER);
            mgrTogether.setDevCount(mConnDevList.size());
            mgrTogether.setCurrentTime(CURRENT_TIME);
            switch (isDef) {
                case CONFIG_TOGETHER:
                    mgrTogether.setDuration(Integer.parseInt(togetherDBList.get(0).getDuration()) * 10);
                    mgrTogether.setHigh((byte) Integer.parseInt(togetherDBList.get(0).getHigh()));
                    break;
                case CONFIG_DEF:
                    mgrTogether.setDuration(Integer.parseInt(DEFAULT_TOGETHER_DURATION) * 10);
                    mgrTogether.setHigh((byte) Integer.parseInt(DEFAULT_TOGETHER_HIGH));
                    break;
            }
            mgrFather = mgrTogether;
        }

        /** 获取已连接设备集合，并按 DMX 顺序进行排序 */
        private void getConnDevList() {
            mConnDevList.clear();
            for (int i = 0; i < mList.get(getAdapterPosition()).getGroup_list().size(); i++) {
                String dev = mList.get(getAdapterPosition()).getGroup_list().get(i);
                for (Map.Entry<Long, ConnDevInfo> entry : mConnDevMap.entrySet()) {
                    if (entry.getKey() == Long.parseLong(dev)) {
                        // 如果服务器的设备与蓝牙连接成功的设备一致，添加到集合
                        mConnDevList.add(new ConnDevInfo(entry.getValue().getDevID(), entry.getValue().getDmx()));
                    }
                }
            }

            // 对连接的设备进行排序
            Collections.sort(mConnDevList, new Comparator<ConnDevInfo>() {
                @Override
                public int compare(ConnDevInfo o1, ConnDevInfo o2) {
                    // 按照 DMX 进行升序排列
                    return Integer.compare(o1.getDmx(), o2.getDmx());
                }
            });
        }

        /** 初始化分组设备列表 */
        private void initDevList(List<String> list) {
            LinearLayoutManager manager = new LinearLayoutManager(mContext);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            rvGroup.setLayoutManager(manager);
            rvGroup.setAdapter(new CoDeviceListAdapter(mContext, list));
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

        Log.i("JET_STATUS", "【配置界面返回喷射信息】" +
                "\npostGroupId: " + mPostGroupId +
                "\nconfigType: " + mConfigType +
                "\ndirection: 方向: " + mDirection +
                "\ngap: 间隔时间: " + mGap +
                "\nduration: 持续时间: " + mDuration +
                "\ngapBig: 大间隔时间: " + mGapBig +
                "\nloop: 循环次数: " + mLoop +
                "\nfrequency: 次数（换向）: " + mFrequency +
                "\nhigh: 高度: " + mHigh +
                "\nmillis: 时间戳: " + mMillis
        );
    }

    /** 硬件传递连接设备信息 */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventDevConn(DeviceConnectEvent event) {
        mConnStatus = event.getInfo();
        mConnDmx = event.getConfig().mDMXAddress;
        mConnDevID = event.getConfig().mID;

        switch (mConnStatus) {
            case DEVICE_CONNECT_YES:
                mConnDevMap.put(mConnDevID, new ConnDevInfo(mConnDevID, mConnDmx));
                break;
            case DEVICE_CONNECT_NO:
                mConnDevMap.remove(mConnDevID);
                break;
            default:
                break;
        }
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
