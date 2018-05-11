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

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import cn.eejing.ejcolorflower.LoginSession;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.Urls;
import cn.eejing.ejcolorflower.model.request.ControlBean;
import cn.eejing.ejcolorflower.util.Settings;

/**
 * @author taodaren
 * @date 2018/5/8
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;
    // 先声明context和集合
    Context mContext;
    List<ControlBean.DataBean> mList;

    public RecyclerViewAdapter(Context mContext, List<ControlBean.DataBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    // 布局先这样写OK
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder holder = null;
        View view = null;
        switch (viewType) {
            case TYPE_ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
                holder = new ViewHolder(view);
                break;
            case TYPE_FOOTER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_footer, parent, false);
                holder = new FootViewHolder(view);
                break;
            default:
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_ITEM:
                ((ViewHolder) viewHolder).bind(mList.get(position));
                break;
            case TYPE_FOOTER:
                ((FootViewHolder) viewHolder).bind();

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

    // 刷新数据的时候调用
    public void refreshList(List<ControlBean.DataBean> list) {
        if (list != null) {
            mList.clear();
            addList(list);
        }
    }

    // 加载更多的时候调用
    public void addList(List<ControlBean.DataBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public class FootViewHolder extends RecyclerView.ViewHolder {
        Gson gson;
        List<ControlBean.DataBean> beanList;
        ImageView imgAddGroup;

        public FootViewHolder(View view) {
            super(view);
            gson = new Gson();
            beanList = new ArrayList<>();
            imgAddGroup = view.findViewById(R.id.img_add_group);
        }

        public void bind() {
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

                                                             ControlBean bean = gson.fromJson(body, ControlBean.class);
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
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        RecyclerView itemRv;
        TextView itemAddDevice, itemName;
        ImageView itemAdd, itemClose;
        SeekBar itemSb;
        // 这是一个可以倒计时和计时的控件
        Chronometer itemTime;
        LinearLayoutManager manager;
        DeviceAdapter adapter;


        public ViewHolder(View itemView) {
            super(itemView);
            itemRv = itemView.findViewById(R.id.item_rv);
            itemAddDevice = itemView.findViewById(R.id.item_add_device);
            itemAdd = itemView.findViewById(R.id.item_add);
            itemName = itemView.findViewById(R.id.item_name);
            itemClose = itemView.findViewById(R.id.item_close);
            itemSb = itemView.findViewById(R.id.item_sb);
            itemTime = itemView.findViewById(R.id.item_time);
        }

        public void bind(ControlBean.DataBean controllBean) {
            // 这里给控件赋值
            //            init(controllBean.getGroup_list());
            if (controllBean.getGroup_list() != null && controllBean.getGroup_list().size() > 0) {
                itemAddDevice.setVisibility(View.GONE);
                itemRv.setVisibility(View.VISIBLE);
                init(controllBean.getGroup_list());
            } else {
                itemAddDevice.setVisibility(View.VISIBLE);
            }

            itemName.setText(controllBean.getGroup_name());

        }

        private void init(List<String> controllBean) {
            manager = new LinearLayoutManager(mContext);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            itemRv.setLayoutManager(manager);

            adapter = new DeviceAdapter(controllBean);
            itemRv.setAdapter(adapter);
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
