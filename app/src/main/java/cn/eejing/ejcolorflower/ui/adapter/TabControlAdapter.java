package cn.eejing.ejcolorflower.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.allen.library.SuperButton;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.eejing.ejcolorflower.LoginSession;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.Urls;
import cn.eejing.ejcolorflower.model.request.DeviceGroupListBean;
import cn.eejing.ejcolorflower.util.Settings;

/**
 * @创建者 Taodaren
 * @描述
 */
public class TabControlAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

    private Context mContext;
    private List<DeviceGroupListBean.DataBean> mList;
    private LayoutInflater mLayoutInflater;

    public TabControlAdapter(Context mContext, List<DeviceGroupListBean.DataBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View inflate;
        switch (viewType) {
            case TYPE_ITEM:
                inflate = mLayoutInflater.inflate(R.layout.item_unit_control, parent, false);
                holder = new ItemViewHolder(inflate);
                ((ItemViewHolder) holder).setClickListener();
                break;
            case TYPE_FOOTER:
                inflate = mLayoutInflater.inflate(R.layout.item_footer_control, parent, false);
                holder = new FootViewHolder(inflate);
                ((FootViewHolder) holder).setClickListener();
                break;
            default:
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_ITEM:
                ((ItemViewHolder) holder).setData(mList.get(position));
                break;
            case TYPE_FOOTER:
                ((FootViewHolder) holder).setData();
                break;
            default:
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

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.seek_bar_control)
        SeekBar sbControl;
        @BindView(R.id.rv_control_group)
        RecyclerView rvGroup;
        @BindView(R.id.tv_ctrl_group_info)
        TextView tvInfo;
        @BindView(R.id.tv_ctrl_group_name)
        TextView tvName;
        @BindView(R.id.ch_ctrl_group_time)
        Chronometer itemTime;// 这是一个可以倒计时和计时的控件
        @BindView(R.id.img_ctrl_group_add)
        ImageView imgAdd;
        @BindView(R.id.img_ctrl_group_switch)
        ImageView imgSwitch;
        @BindView(R.id.sb_ctrl_group_time)
        SuperButton sbTime;

        LinearLayoutManager manager;
        DeviceAdapter adapter;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setClickListener() {
            sbControl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            imgAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

        public void setData(DeviceGroupListBean.DataBean bean) {
            // 这里给控件赋值
            if (bean.getGroup_list() != null && bean.getGroup_list().size() > 0) {
                tvInfo.setVisibility(View.GONE);
                rvGroup.setVisibility(View.VISIBLE);
                init(bean.getGroup_list());
            } else {
                tvInfo.setVisibility(View.VISIBLE);
            }
            tvName.setText(bean.getGroup_name());
        }

        private void init(List<String> list) {
            manager = new LinearLayoutManager(mContext);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            rvGroup.setLayoutManager(manager);
            adapter = new DeviceAdapter(list);
            rvGroup.setAdapter(adapter);
        }
    }

    class FootViewHolder extends RecyclerView.ViewHolder {
        Gson gson;
        List<DeviceGroupListBean.DataBean> beanList;
        ImageView imgAddGroup;

        public FootViewHolder(View view) {
            super(view);
            gson = new Gson();
            beanList = new ArrayList<>();
            imgAddGroup = view.findViewById(R.id.img_add_group);
        }

        public void setData() {
            imgAddGroup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final EditText editText = new EditText(mContext);
                    // 弹出新建组 Dialog
                    new AlertDialog.Builder(mContext, R.style.BtnDialogColor)
                            .setTitle("请输入新建组名称")
                            .setMessage("名字长度不能超过6个字符")
                            .setView(editText)
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
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

                private void getDataWithAddGroup(EditText editText) {
                    // 获取用户 id
                    LoginSession session = Settings.getLoginSessionInfo(mContext);
                    final String memberId = String.valueOf(session.getMember_id());
                    // 获取输入框内容
                    String groupName = editText.getText().toString();

                    // 网络请求：用户新建设备组
                    OkGo.<String>post(Urls.ADD_GROUP)
                            .tag(this)
                            .params("member_id", memberId)
                            .params("group_name", groupName)
                            .execute(new StringCallback() {
                                @Override
                                public void onSuccess(Response<String> response) {
                                    String body = response.body();
                                    Log.e("ADD_GROUP", "Network request succeeded！！！" + body);
                                    refreshList(mList);

                                    // 网络请求：获取设备用户组
                                    OkGo.<String>post(Urls.GET_DEVICE_GROUP_LIST)
                                            .tag(this)
                                            .params("member_id", memberId)
                                            .execute(new StringCallback() {
                                                         @Override
                                                         public void onSuccess(Response<String> response) {
                                                             String body = response.body();
                                                             Log.e("GET_DEVICE_GROUP_LIST", "Network request succeeded！！！" + body);

                                                             DeviceGroupListBean bean = gson.fromJson(body, DeviceGroupListBean.class);
                                                             beanList = bean.getData();
                                                             refreshList(beanList);
                                                         }
                                                     }
                                            );

                                }
                            });
                }
            });
        }

        public void setClickListener() {

        }
    }

    public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceHolder> {
        // 这里的类型根据项目决定
        List<String> devices;

        public DeviceAdapter(List<String> devices) {
            this.devices = devices;
        }

        @NonNull
        @Override
        public DeviceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new DeviceHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.device_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull DeviceHolder holder, int position) {
            holder.bind(devices.get(position));
        }

        @Override
        public int getItemCount() {
            return devices.size();
        }

        public class DeviceHolder extends RecyclerView.ViewHolder {
            ImageView deviceimg;

            public DeviceHolder(View itemView) {
                super(itemView);
                deviceimg = itemView.findViewById(R.id.device_img);
            }

            public void bind(String s) {
                // 赋值,这里就是上面那个设备列表，但是我不知道是根据什么显示的,这样写，有几条数据，就会有几个
                deviceimg.setImageResource(R.drawable.ic_add_solid_black);
            }
        }
    }

}
