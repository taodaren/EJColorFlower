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
import cn.eejing.colorflower.model.request.BuyRecordBean;

/**
 * 购买记录适配器
 */

public class BuyRecordAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mInflater;
    private List<BuyRecordBean.DataBean> mList;

    public BuyRecordAdapter(Context context, List<BuyRecordBean.DataBean> list) {
        this.mInflater = LayoutInflater.from(context);
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_buy_record, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void refreshList(List<BuyRecordBean.DataBean> list) {
        mList.clear();
        addList(list);
    }

    private void addList(List<BuyRecordBean.DataBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_goods_name)         TextView tvGoodsName;
        @BindView(R.id.tv_buy_num)            TextView tvBuyNum;
        @BindView(R.id.tv_create_time)        TextView tvCreateTime;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("SetTextI18n")
        public void setData(BuyRecordBean.DataBean bean) {
            tvGoodsName.setText(bean.getGoods_name());
            tvBuyNum.setText(bean.getQuantity());
            tvCreateTime.setText(bean.getCreate_time());
        }
    }
}
