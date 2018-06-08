package cn.eejing.ejcolorflower.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.allen.library.SuperButton;
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
import cn.eejing.ejcolorflower.model.request.DeviceGroupListBean;
import cn.eejing.ejcolorflower.ui.activity.CoDeviceActivity;
import cn.eejing.ejcolorflower.util.Settings;

/**
 * @创建者 Taodaren
 * @描述 控制模块适配器
 */

public class TabControlAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private Context mContext;
    private List<DeviceGroupListBean.DataBean> mList;
    private LayoutInflater mLayoutInflater;
    private Gson mGson;
    private String mMemberId, mToken;

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

    public void refreshList(List<DeviceGroupListBean.DataBean> list) {
        if (list != null) {
            mList.clear();
            addList(list);
        }
    }

    public void addList(List<DeviceGroupListBean.DataBean> list) {
        mList.addAll(list);
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

        View outItem;


        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            outItem = itemView;
        }

        public void setData(DeviceGroupListBean.DataBean bean, int position) {
            if (bean.getGroup_list() != null && bean.getGroup_list().size() > 0) {
                tvInfo.setVisibility(View.GONE);
                rvGroup.setVisibility(View.VISIBLE);
                initGroupList(bean.getGroup_list());
            } else {
                tvInfo.setVisibility(View.VISIBLE);
                rvGroup.setVisibility(View.INVISIBLE);
            }

            tvName.setText(bean.getGroup_name());

//            setClickListener(bean.getGroup_name(), bean.getGroup_id(), position);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.img_ctrl_group_switch:
                    Toast.makeText(mContext, "img_ctrl_group_switch", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }

        public void setClickListener(final String group_name, final int group_id, final int position) {
            imgAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mContext, CoDeviceActivity.class);
                    intent.putExtra("member_id", mMemberId);
                    intent.putExtra("group_id", group_id);
                    intent.putExtra("group_name", group_name);
                    intent.putExtra("token", mToken);
                    mContext.startActivity(intent);
                }
            });


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


            imgSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });

        }

        private void renameGroup(final int group_id) {
            final EditText editText = new EditText(mContext);
            // 重命名组 Dialog
            new AlertDialog.Builder(mContext, R.style.BtnDialogColor)
                    .setTitle("请重新输入组名称")
                    .setMessage("名字长度不能超过6个字符")
                    .setView(editText)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            getDataWithRenameGroup(editText, group_id);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
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

        public FootViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, itemView);
        }

        private void addGroup() {
            final EditText editText = new EditText(mContext);
            // 弹出新建组 Dialog
            new AlertDialog.Builder(mContext, R.style.BtnDialogColor)
                    .setTitle("请输入新建组名称")
                    .setMessage("名字长度不能超过6个字符")
                    .setView(editText)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // 新建组
                            getDataWithAddGroup(editText);
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .show();
        }

        @Override
        public void onClick(View view) {
            imgAddGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addGroup();
                }
            });
        }
    }

    public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.DeviceHolder> {

        List<String> devices;

        public GroupListAdapter(List<String> devices) {
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

            public DeviceHolder(View itemView) {
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

    private void getDataWithAddGroup(EditText editText) {
        String groupName = editText.getText().toString();

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

    private void getDataWithRenameGroup(EditText editText, int group_id) {
        String groupName = editText.getText().toString();
        Log.e(AppConstant.TAG, "getDataWithRenameGroup: " + groupName + "===" + group_id);
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

    private void getDataWithEditHigh(float high, int group_id) {
        OkGo.<String>post(Urls.EDIT_HIGH)
                .tag(this)
                .params("member_id", mMemberId)
                .params("high", high)
                .params("group_id", group_id)
                .params("token", mToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "edit high request succeeded！！！" + body);
                    }
                });
    }

}
