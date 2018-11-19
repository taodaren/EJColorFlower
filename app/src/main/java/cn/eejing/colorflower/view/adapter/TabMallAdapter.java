package cn.eejing.colorflower.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.request.GoodsListBean;

/**
 * 商城模块适配器
 */

public class TabMallAdapter extends RecyclerView.Adapter<TabMallAdapter.ViewHolder> {
    private Context mContext;
    private List<GoodsListBean.DataBean> mList;
    private LayoutInflater mLayoutInflater;
    private View.OnClickListener mOnClickListener;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public TabMallAdapter(Context mContext, List<GoodsListBean.DataBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = mLayoutInflater.inflate(R.layout.item_goods_list, parent, false);
        return new ViewHolder(inflate);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /** 刷新列表 */
    public void refreshList(List<GoodsListBean.DataBean> list) {
        // 先把之前的数据清空
        mList.clear();
        addList(list);
    }

    /** 加载更多列表 */
    private void addList(List<GoodsListBean.DataBean> list) {
        // 把新集合添加进来
        mList.addAll(list);
        // 通知列表刷新
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        View outView;

        @BindView(R.id.img_goods_list)               ImageView imgGoods;
        @BindView(R.id.tv_goods_list_title)          TextView tvTitle;
        @BindView(R.id.tv_goods_list_rmb)            TextView tvRmb;
        @BindView(R.id.tv_goods_list_rmb_old)        TextView tvRmbOld;
        @BindView(R.id.tv_sold_out)                  TextView tvSoldOut;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            outView = itemView;
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @SuppressLint("SetTextI18n")
        public void setData(GoodsListBean.DataBean bean) {
            Glide.with(mContext).load(bean.getOriginal_img()).into(imgGoods);
            tvTitle.setText(bean.getGoods_name());
            tvRmb.setText(mContext.getResources().getString(R.string.rmb) + bean.getSale_price());
            tvRmbOld.setText(mContext.getResources().getString(R.string.rmb) + bean.getPrice());
            // 添加删除线
            tvRmbOld.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            if (Integer.parseInt(bean.getStore_count()) == 0) {
                // 无货
                tvSoldOut.setVisibility(View.VISIBLE);
                tvTitle.setTextColor(mContext.getColor(R.color.colorNoClick));
                tvRmb.setTextColor(mContext.getColor(R.color.colorNoClick));
                tvRmbOld.setTextColor(mContext.getColor(R.color.colorNoClick));
            } else {
                // 有货
                tvSoldOut.setVisibility(View.GONE);
                tvTitle.setTextColor(mContext.getColor(R.color.colorConfig));
                tvRmb.setTextColor(mContext.getColor(R.color.colorRmb));
                tvRmbOld.setTextColor(mContext.getColor(R.color.colorNoClick));
            }
            // 点击商品
            outView.setTag(getAdapterPosition());
            outView.setOnClickListener(mOnClickListener);
        }
    }

}
