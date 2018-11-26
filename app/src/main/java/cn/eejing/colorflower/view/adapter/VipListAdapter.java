package cn.eejing.colorflower.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.request.VipListBean;

/**
 * 下游 Vip 列表适配器
 */

public class VipListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mInflater;
    private List<VipListBean.DataBean> mList;

    private View.OnClickListener mClickRemark, mClickDiscount;

    public void setClickRemark(View.OnClickListener clickRemark) {
        this.mClickRemark = clickRemark;
    }

    public void setClickDiscount(View.OnClickListener clickDiscount) {
        this.mClickDiscount = clickDiscount;
    }

    public VipListAdapter(Context context, List<VipListBean.DataBean> list) {
        this.mInflater = LayoutInflater.from(context);
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_vip_cfg, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void refreshList(List<VipListBean.DataBean> list) {
        mList.clear();
        addList(list);
    }

    private void addList(List<VipListBean.DataBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_vip_remarks)          TextView tvRemarks;
        @BindView(R.id.btn_vip_remarks)         TextView btnRemarks;
        @BindView(R.id.btn_vip_discount)        TextView btnDiscount;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("SetTextI18n")
        public void setData(VipListBean.DataBean bean) {
            if (bean.getRemark().equals("")) {
                tvRemarks.setText("备注：" + bean.getMobile());
            } else {
                tvRemarks.setText("备注：" + bean.getRemark());
            }
            btnRemarks.setOnClickListener(mClickRemark);
            btnDiscount.setOnClickListener(mClickDiscount);
            btnRemarks.setTag(getAdapterPosition());
            btnDiscount.setTag(getAdapterPosition());
        }
    }
}
