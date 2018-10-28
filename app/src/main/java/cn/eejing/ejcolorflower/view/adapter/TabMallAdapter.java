package cn.eejing.ejcolorflower.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.model.request.GoodsListBean;
import cn.eejing.ejcolorflower.view.activity.MaGoodsDetailsActivity;
import cn.eejing.ejcolorflower.view.activity.MainActivity;

/**
 * 商城模块适配器
 */

public class TabMallAdapter extends RecyclerView.Adapter<TabMallAdapter.MallViewHolder> {
    private Context mContext;
    private List<GoodsListBean.DataBean> mList;
    private LayoutInflater mLayoutInflater;

    public TabMallAdapter(Context mContext, List<GoodsListBean.DataBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
        this.mLayoutInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public MallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = mLayoutInflater.inflate(R.layout.item_goods_list, parent, false);
        MallViewHolder mallViewHolder = new MallViewHolder(inflate);
        mallViewHolder.setClickListener();
        return mallViewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull MallViewHolder holder, int position) {
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
    public void addList(List<GoodsListBean.DataBean> list) {
        // 把新集合添加进来
        mList.addAll(list);
        // 通知列表刷新
        notifyDataSetChanged();
    }

    class MallViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_goods_list)             ImageView imgGoods;
        @BindView(R.id.tv_goods_list_title)        TextView tvTitle;
        @BindView(R.id.tv_goods_list_rmb)          TextView tvRmb;
        @BindView(R.id.tv_goods_list_num)          TextView tvNum;
        @BindView(R.id.tv_goods_list_has)          TextView tvHas;

        View outView;

        MallViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            outView = itemView;
        }

        void setClickListener() {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int goodsId = mList.get(getAdapterPosition()).getGoods_id();
                    String name = mList.get(getAdapterPosition()).getName();

                    Intent intent = new Intent(mContext, MaGoodsDetailsActivity.class);
                    intent.putExtra("goods_id", goodsId);
                    intent.putExtra("name", name);
                    ((MainActivity) mContext).jumpToActivity(intent);
                }
            });
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @SuppressLint("SetTextI18n")
        public void setData(GoodsListBean.DataBean bean) {
            Glide.with(mContext).load(bean.getImage()).into(imgGoods);
            tvTitle.setText(bean.getName());
            tvRmb.setText(mContext.getResources().getString(R.string.rmb) + bean.getMoney());
            tvNum.setText(mContext.getResources().getString(R.string.sold) + bean.getSold());
            int stock = bean.getStock();
            if (stock == 0) {
                // 无货
                tvHas.setVisibility(View.VISIBLE);
                tvHas.setText(mContext.getResources().getString(R.string.stock_no));
                tvHas.setTextColor(mContext.getColor(R.color.colorRmb));
                tvTitle.setTextColor(mContext.getColor(R.color.colorNoClick));
                tvRmb.setTextColor(mContext.getColor(R.color.colorNoClick));
                tvNum.setTextColor(mContext.getColor(R.color.colorNoClick));
            }
        }
    }

}
