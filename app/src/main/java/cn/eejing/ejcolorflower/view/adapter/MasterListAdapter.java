package cn.eejing.ejcolorflower.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.model.request.MasterGroupListBean;

/**
 * 主控分組列表适配器
 */

public class MasterListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "MasterListAdapter";

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<MasterGroupListBean> mList;

    public MasterListAdapter(Context context, List<MasterGroupListBean> list) {
        this.mContext = context;
        this.mList = list;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    private View.OnClickListener mClickSetMaster;
    private View.OnClickListener mClickIsSelectedGroup;
    private View.OnClickListener mClickIsSelectedMaster;

    public void setClickSetMaster(View.OnClickListener clickSetMaster) {
        this.mClickSetMaster = clickSetMaster;
    }

    public void setClickIsSelectedGroup(View.OnClickListener clickIsSelectedGroup) {
        this.mClickIsSelectedGroup = clickIsSelectedGroup;
    }

    public void setClickIsSelectedMaster(View.OnClickListener clickIsSelectedMaster) {
        this.mClickIsSelectedMaster = clickIsSelectedMaster;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(mLayoutInflater.inflate(R.layout.item_master_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (mList != null) {
            ((ItemViewHolder) holder).setData(mList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void refreshList(List<MasterGroupListBean> list) {
        if (list != null) {
            mList.clear();
            addList(list);
        }
        if (list == null) {
            mList.clear();
            notifyDataSetChanged();
        }
    }

    private void addList(List<MasterGroupListBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_group_is_selected)        ImageView imgSelectGroup;
        @BindView(R.id.img_master_is_selected)       ImageView imgSelectMaster;
        @BindView(R.id.tv_master_group)              TextView  tvGroupName;
        @BindView(R.id.tv_master_num)                TextView  tvDevNum;
        @BindView(R.id.tv_master_dmx)                TextView  tvStartDmx;
        @BindView(R.id.tv_master_type)               TextView  tvJetMode;
        @BindView(R.id.btn_master_set)               Button    btnSetMaster;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("SetTextI18n")
        void setData(MasterGroupListBean bean) {
            // 设置是否选中组
            switch (bean.getIsSelectedGroup()) {
                case 1:
                    imgSelectGroup.setImageDrawable(mContext.getDrawable(R.drawable.ic_group_selected));
                    break;
                case 2:
                    imgSelectGroup.setImageDrawable(mContext.getDrawable(R.drawable.ic_group_unselected));
                    break;
            }
            // 设置是否选中包含主控
            switch (bean.getIsSelectedMaster()) {
                case 1:
                    imgSelectMaster.setImageDrawable(mContext.getDrawable(R.drawable.ic_group_master_selected));
                    break;
                case 2:
                    imgSelectMaster.setImageDrawable(mContext.getDrawable(R.drawable.ic_group_master_unselected));
                    break;
            }
            tvGroupName.setText(bean.getGroupName());
            tvDevNum.setText("设备数量 " + bean.getDevNum());
            tvStartDmx.setText("起始 DMX " + bean.getStartDmx());
            if (bean.getJetModes() == null) {
                tvJetMode.setText("无效果");
            } else {
                tvJetMode.setText("有效果");
            }
            btnSetMaster.setOnClickListener(mClickSetMaster);
            imgSelectGroup.setOnClickListener(mClickIsSelectedGroup);
            imgSelectMaster.setOnClickListener(mClickIsSelectedMaster);
            btnSetMaster.setTag(getAdapterPosition());
            imgSelectGroup.setTag(getAdapterPosition());
            imgSelectMaster.setTag(getAdapterPosition());
        }
    }

}