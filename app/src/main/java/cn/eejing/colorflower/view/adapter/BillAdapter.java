package cn.eejing.colorflower.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.request.BillBean;
import cn.eejing.colorflower.util.LogUtil;

import static cn.eejing.colorflower.util.DateUtil.FORMAT_ALL;
import static cn.eejing.colorflower.util.DateUtil.FORMAT_HM;
import static cn.eejing.colorflower.util.DateUtil.FORMAT_MD;
import static cn.eejing.colorflower.util.DateUtil.FORMAT_MDHM;
import static cn.eejing.colorflower.util.DateUtil.date2TimeStamp;
import static cn.eejing.colorflower.util.DateUtil.isToday;
import static cn.eejing.colorflower.util.DateUtil.isWeek;
import static cn.eejing.colorflower.util.DateUtil.isYear;
import static cn.eejing.colorflower.util.DateUtil.isYesterday;
import static cn.eejing.colorflower.util.DateUtil.showWeekString;
import static cn.eejing.colorflower.util.DateUtil.timeStamp2Date;

public class BillAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "BillAdapter";
    private Context mContext;
    private LayoutInflater mInflater;
    private List<BillBean.DataBean> mList;

    public BillAdapter(Context context, List<BillBean.DataBean> list) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mList = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mInflater.inflate(R.layout.item_bill_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ViewHolder) holder).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void refreshList(List<BillBean.DataBean> list) {
        mList.clear();
        addList(list);
    }

    private void addList(List<BillBean.DataBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_bill_title)         TextView tvTitle;
        @BindView(R.id.tv_bill_time)          TextView tvTime;
        @BindView(R.id.tv_bill_money)         TextView tvMoney;
        @BindView(R.id.tv_bill_status)        TextView tvStatus;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("SetTextI18n")
        public void setData(BillBean.DataBean bean) {
            String timeStamp = date2TimeStamp(bean.getCreate_time(), FORMAT_ALL);
            String hmDate = timeStamp2Date(timeStamp, FORMAT_HM);
            String mdDate = timeStamp2Date(timeStamp, FORMAT_MD);
            tvTitle.setText(bean.getTitle());
            tvMoney.setText(bean.getMoney());

            try {
                boolean isToday = isToday(bean.getCreate_time());
                boolean isYesterday = isYesterday(bean.getCreate_time());
                boolean isWeek = isWeek(timeStamp, timeStamp.length());
                boolean isYear = isYear(timeStamp, timeStamp.length());
                if (isToday) {
                    tvTime.setText("今天 " + hmDate);
                } else if (isYesterday) {
                    tvTime.setText("昨天 " + hmDate);
                } else if (isWeek) {
                    String weekString = showWeekString(Long.parseLong(timeStamp));
                    tvTime.setText(weekString + " " + hmDate);
                } else if (isYear) {
                    tvTime.setText(mdDate + " " + hmDate);
                } else {
                    tvTime.setText(timeStamp2Date(timeStamp, FORMAT_MDHM));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            switch (bean.getType()) {
                case "0":// 返点
                case "2":// 驳回退款
                case "3":// VIP购物返现
                    tvMoney.setTextColor(mContext.getResources().getColor(R.color.money_add));
                    break;
                case "1":// 提现
                    tvMoney.setTextColor(mContext.getResources().getColor(R.color.colorBlack));
                    break;
            }

            switch (bean.getStatus()) {
                case "0":// 待处理
                    tvStatus.setText("[审核中]");
                    break;
                case "1":
                    tvStatus.setText("");
                    LogUtil.d(TAG, "正常处理不显示");
                    break;
                case "2":// 驳回
                    tvStatus.setText("[审核失败]");
                    break;
            }
        }
    }
}
