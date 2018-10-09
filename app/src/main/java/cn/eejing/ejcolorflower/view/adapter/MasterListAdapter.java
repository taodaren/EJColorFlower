package cn.eejing.ejcolorflower.view.adapter;

import android.content.Context;
import android.content.Intent;
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
import butterknife.OnClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.view.activity.CtSetGroupActivity;

/**
 * 主控列表适配器
 */

public class MasterListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "MasterListAdapter";

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<String> mList;
    private Long mDeviceId;

    public MasterListAdapter(Context context, List<String> list, Long devId) {
        this.mContext = context;
        this.mDeviceId = devId;
        this.mList = list;
        this.mLayoutInflater = LayoutInflater.from(mContext);
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

    public void refreshList(List<String> list) {
        if (list != null) {
            mList.clear();
            addList(list);
        }
        if (list == null) {
            mList.clear();
            notifyDataSetChanged();
        }
    }

    private void addList(List<String> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_master_group)        TextView tvGroup;
        @BindView(R.id.btn_master_set)         Button btnMasterSet;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setData(String s) {
            if (s.equals("分组功能敬请期待...")) {
                btnMasterSet.setBackground(mContext.getDrawable(R.drawable.ic_btn_master_set_null));
            } else {
                btnMasterSet.setBackground(mContext.getDrawable(R.drawable.ic_btn_master_set));
            }
            tvGroup.setText(s);
        }

        @OnClick(R.id.btn_master_set)
        public void onViewClicked() {
            if (!mList.get(getAdapterPosition()).equals("分组功能敬请期待...")) {
                mContext.startActivity(new Intent(mContext, CtSetGroupActivity.class).putExtra("device_id", mDeviceId));
            }
        }

    }

}