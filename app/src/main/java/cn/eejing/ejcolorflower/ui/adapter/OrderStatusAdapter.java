package cn.eejing.ejcolorflower.ui.adapter;

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
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.model.request.OrderPagerBean;

public class OrderStatusAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<OrderPagerBean.DataBean> mList;
    private String mType;

    public OrderStatusAdapter(Context context, List<OrderPagerBean.DataBean> list, String type) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mList = new ArrayList<>();
        this.mList.addAll(list);
        this.mType = type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = mInflater.inflate(R.layout.item_order_status, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void refreshList(List<OrderPagerBean.DataBean> list) {
        // 先把之前的数据清空
        mList.clear();
        addList(list);
    }

    private void addList(List<OrderPagerBean.DataBean> list) {
        // 把新集合添加进来
        mList.addAll(list);
        // 通知列表刷新
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.img_order_status)
        ImageView imgGoods;
        @BindView(R.id.tv_order_status_name)
        TextView tvName;
        @BindView(R.id.tv_order_status_money_rmb)
        TextView tvMoneyRmb;
        @BindView(R.id.tv_order_status_num)
        TextView tvNum;
        @BindView(R.id.tv_order_status_quantity)
        TextView tvQuantity;
        @BindView(R.id.tv_order_status_postage)
        TextView tvPostage;
        @BindView(R.id.tv_order_status_money)
        TextView tvMoney;
        @BindView(R.id.btn_order_status)
        Button btnEdit;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            btnEdit.setOnClickListener(this);
        }

        @SuppressLint("SetTextI18n")
        public void setData(OrderPagerBean.DataBean bean) {
            Glide.with(mContext).load(bean.getImage()).into(imgGoods);
            tvName.setText(bean.getName());
            tvMoneyRmb.setText(mContext.getString(R.string.rmb) + bean.getMoney());
            tvNum.setText("×" + bean.getQuantity());
            tvQuantity.setText("共" + bean.getQuantity() + "件商品");
            tvPostage.setText("邮费:" + bean.getPostage() + "元");
            tvMoney.setText("合计:" + bean.getMoney() + "元");

            switch (mType) {
                case AppConstant.TYPE_WAIT_SHIP:
                    btnEdit.setVisibility(View.GONE);
                    break;
                case AppConstant.TYPE_WAIT_RECEIPT:
                    btnEdit.setText("确认收货");
                    break;
                case AppConstant.TYPE_COMPLETE_GOODS:
                    btnEdit.setText("删除");
                    break;
                default:
                    break;
            }

        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.btn_order_status:
                    if (mType.equals(AppConstant.TYPE_WAIT_RECEIPT)) {
                        Toast.makeText(mContext, "确认收货", Toast.LENGTH_SHORT).show();
                    } else if (mType.equals(AppConstant.TYPE_COMPLETE_GOODS)) {
                        Toast.makeText(mContext, "删除", Toast.LENGTH_SHORT).show();
                    }
                    break;
                default:
                    break;
            }
        }
    }

}
