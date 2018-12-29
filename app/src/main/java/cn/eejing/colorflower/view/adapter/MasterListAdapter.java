package cn.eejing.colorflower.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.lite.MasterGroupLite;
import cn.eejing.colorflower.util.LogUtil;

/**
 * 主控界面-分組列表适配器
 */

public class MasterListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "MasterListAdapter";

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<MasterGroupLite> mListMasterGroup;

    public MasterListAdapter(Context context, List<MasterGroupLite> list) {
        this.mContext = context;
        this.mListMasterGroup = list;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    private View.OnClickListener mClickSetMaster;
    private View.OnClickListener mClickIsSelectedGroup;
    private View.OnClickListener mClickIsSelectedMaster;
    private View.OnLongClickListener mLongClickListener;

    public void setClickSetMaster(View.OnClickListener clickSetMaster) {
        this.mClickSetMaster = clickSetMaster;
    }

    public void setClickIsSelectedGroup(View.OnClickListener clickIsSelectedGroup) {
        this.mClickIsSelectedGroup = clickIsSelectedGroup;
    }

    public void setClickIsSelectedMaster(View.OnClickListener clickIsSelectedMaster) {
        this.mClickIsSelectedMaster = clickIsSelectedMaster;
    }

    public void setLongClickListener(View.OnLongClickListener longClickListener) {
        this.mLongClickListener = longClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemViewHolder(mLayoutInflater.inflate(R.layout.item_master_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (mListMasterGroup != null) {
            ((ItemViewHolder) holder).setData(mListMasterGroup.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return mListMasterGroup.size();
    }

    @Override
    public int getItemViewType(int position) {
        // 取消复用，解决重复错乱问题
        return position;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ll_group_mst)                 LinearLayout layoutGroupMst;
        @BindView(R.id.img_group_is_selected)        ImageView    imgSelectGroup;
        @BindView(R.id.img_master_is_selected)       ImageView    imgSelectMaster;
        @BindView(R.id.tv_master_group)              TextView     tvGroupName;
        @BindView(R.id.tv_master_num)                TextView     tvDevNum;
        @BindView(R.id.tv_master_dmx)                TextView     tvStartDmx;
        @BindView(R.id.tv_master_type)               TextView     tvJetMode;
        @BindView(R.id.btn_master_set)               Button       btnSetMaster;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("SetTextI18n")
        void setData(MasterGroupLite bean) {
            // 设置是否选中组
            switch (bean.getIsSelectedGroup()) {
                case 1:
                    imgSelectGroup.setImageDrawable(mContext.getDrawable(R.drawable.ic_check_selected));
                    break;
                case 2:
                    imgSelectGroup.setImageDrawable(mContext.getDrawable(R.drawable.ic_check_unselected));
                    break;
            }
            // 设置是否选中包含主控
            switch (bean.getIsSelectedMaster()) {
                case 1:
                    imgSelectMaster.setImageDrawable(mContext.getDrawable(R.drawable.ic_single_selected));
                    break;
                case 2:
                    imgSelectMaster.setImageDrawable(mContext.getDrawable(R.drawable.ic_single_unselected));
                    break;
            }
            tvGroupName.setText(bean.getGroupName());
            tvDevNum.setText("设备数量 " + bean.getDevNum());
            tvStartDmx.setText("起始 DMX " + bean.getStartDmx());
            LogUtil.i(TAG, "setData: " + bean.getJetModes());
            if (bean.getJetModes() == null || bean.getJetModes().size() == 0) {
                tvJetMode.setText("无效果");
            } else {
                tvJetMode.setText("有效果");
            }
            btnSetMaster.setOnClickListener(mClickSetMaster);
            imgSelectGroup.setOnClickListener(mClickIsSelectedGroup);
            imgSelectMaster.setOnClickListener(mClickIsSelectedMaster);
            layoutGroupMst.setOnLongClickListener(mLongClickListener);
            btnSetMaster.setTag(getAdapterPosition());
            imgSelectGroup.setTag(getAdapterPosition());
            imgSelectMaster.setTag(getAdapterPosition());
            layoutGroupMst.setTag(getAdapterPosition());
        }
    }

}