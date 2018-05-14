package cn.eejing.ejcolorflower.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.List;

import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.model.request.GoodsListBean;

/**
 * @创建者 Taodaren
 * 实现 Adapter 步骤：
 * 写 ViewHolder → extends RecyclerView.Adapter<泛型是写的VH> → 全局变量与构造方法（Context,list,LayoutInflater）→ 完善重写方法
 * @描述
 */
public class TabMallAdapter extends RecyclerView.Adapter<TabMallAdapter.MallViewHolder> {
    private Context mContext;
    private List<GoodsListBean.DataBean> mList;
    private LayoutInflater mLayoutInflater;

    public TabMallAdapter(Context mContext, List<GoodsListBean.DataBean> mList) {
        this.mContext = mContext;
        this.mList = mList;
        this.mLayoutInflater = LayoutInflater.from(mContext);
        //        this.mList.addAll(mList);
        //        this.mList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MallViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = mLayoutInflater.inflate(R.layout.item_goods_list, parent, false);
        MallViewHolder mallViewHolder = new MallViewHolder(inflate);
        mallViewHolder.setClickListener();
        return mallViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MallViewHolder holder, int position) {
        holder.setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    /**
     * 刷新列表
     */
    public void refreshList(List<GoodsListBean.DataBean> list) {
        // 先把之前的数据清空
        mList.clear();
        addList(list);
    }

    /**
     * 加载更多列表
     */
    public void addList(List<GoodsListBean.DataBean> list) {
        // 把新集合添加进来
        mList.addAll(list);
        // 通知列表刷新
        notifyDataSetChanged();
    }

    class MallViewHolder extends RecyclerView.ViewHolder {
        View outView;
        ImageView imgGoods;
        TextView tvTitle, tvRmb, tvNum, tvHas;

        public MallViewHolder(View itemView) {
            super(itemView);
            outView = itemView;
            imgGoods = itemView.findViewById(R.id.img_goods_list);
            tvTitle = itemView.findViewById(R.id.tv_goods_list_title);
            tvRmb = itemView.findViewById(R.id.tv_goods_list_rmb);
            tvNum = itemView.findViewById(R.id.tv_goods_list_num);
            tvHas = itemView.findViewById(R.id.tv_goods_list_has);
        }

        public void setClickListener() {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, "" + mList.get(getPosition()).getGoods_id(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @SuppressLint("SetTextI18n")
        public void setData(GoodsListBean.DataBean bean) {
            Glide.with(mContext).load(bean.getImage()).into(imgGoods);
            tvTitle.setText(bean.getName());
            tvRmb.setText(mContext.getResources().getString(R.string.rmb) + bean.getMoney());
            tvNum.setText(R.string.sold + bean.getSold());
            int stock = bean.getStock();
            switch (stock) {
                case 1:
                    tvHas.setText(mContext.getResources().getString(R.string.stock_have));
                    break;
                case 2:
                    tvHas.setText(mContext.getResources().getString(R.string.stock_no));
                    break;
                default:
            }
        }
    }

}
