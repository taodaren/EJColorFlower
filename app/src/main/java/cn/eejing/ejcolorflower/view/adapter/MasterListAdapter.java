package cn.eejing.ejcolorflower.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    private View.OnClickListener mClickListener;

    public void setClickListener(View.OnClickListener clickListener) {
        this.mClickListener = clickListener;
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
        @BindView(R.id.tv_master_group)        TextView tvGroup;
        @BindView(R.id.tv_master_num)          TextView tvMasterNum;
        @BindView(R.id.tv_master_dmx)          TextView tvMasterDmx;
        @BindView(R.id.tv_master_type)         TextView tvMasterType;
        @BindView(R.id.btn_master_set)         Button   btnMasterSet;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("SetTextI18n")
        void setData(MasterGroupListBean bean) {
            if (bean.getGroupName().equals("分组功能敬请期待...")) {
                btnMasterSet.setBackground(mContext.getDrawable(R.drawable.ic_btn_master_set_null));
            } else {
                btnMasterSet.setBackground(mContext.getDrawable(R.drawable.ic_btn_master_set));
//                tvMasterNum.setText("设备数量 " + String.valueOf(bean.getCfgInfo().get(0).getDevNum()));
//                tvMasterDmx.setText("起始DMX " + String.valueOf(bean.getCfgInfo().get(0).getStartDmx()));
//                tvMasterType.setText(String.valueOf(bean.getCfgInfo().get(0).getJetMode()));
            }
            tvGroup.setText(bean.getGroupName());
            btnMasterSet.setOnClickListener(mClickListener);
        }

    }

}