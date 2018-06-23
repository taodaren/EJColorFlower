package cn.eejing.ejcolorflower.ui.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.model.request.OrderPagerBean;
import cn.eejing.ejcolorflower.model.request.OrderStatusBean;
import cn.eejing.ejcolorflower.ui.activity.MiOrderDetailsActivity;
import cn.eejing.ejcolorflower.util.SelfDialogBase;

public class OrderStatusAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<OrderPagerBean.DataBean> mList;
    private String mType, mMemberId, mToken;
    private SelfDialogBase mDialog;
    private Gson mGson;

    public OrderStatusAdapter(Context context, List<OrderPagerBean.DataBean> list, String type, String memberId, String token) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mList = new ArrayList<>();
        this.mList.addAll(list);
        this.mType = type;
        this.mMemberId = memberId;
        this.mToken = token;
        this.mGson = new Gson();
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

    class ViewHolder extends RecyclerView.ViewHolder {
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
        View outItem;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            outItem = itemView;
        }

        @SuppressLint("SetTextI18n")
        public void setData(final OrderPagerBean.DataBean bean) {
            double money = bean.getMoney() * bean.getQuantity();
            Glide.with(mContext).load(bean.getImage()).into(imgGoods);
            tvName.setText(bean.getName());
            tvMoneyRmb.setText(mContext.getString(R.string.rmb) + money);
            tvNum.setText(mContext.getString(R.string.text_multiply) + bean.getQuantity());
            tvQuantity.setText(mContext.getString(R.string.text_common) + bean.getQuantity() + mContext.getString(R.string.text_items));
            tvPostage.setText(mContext.getString(R.string.postage) + bean.getPostage() + mContext.getString(R.string.yuan));
            tvMoney.setText(mContext.getString(R.string.text_total) + money + mContext.getString(R.string.yuan));

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

            outItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext, MiOrderDetailsActivity.class)
                            .putExtra("order_id", bean.getOrder_id())
                            .putExtra("type",mType));
                }
            });

            btnEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mType.equals(AppConstant.TYPE_WAIT_RECEIPT)) {
                        collectGoods(bean.getOrder_id());
                    } else if (mType.equals(AppConstant.TYPE_COMPLETE_GOODS)) {
                        delCompleted(bean.getOrder_id());
                    }
                }
            });

        }
    }

    private void collectGoods(final int orderId) {
        mDialog = new SelfDialogBase(mContext);
        mDialog.setTitle("您是否已收到该订单商品？");
        mDialog.setYesOnclickListener("已收货", new SelfDialogBase.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                getDataWithCollectGoods(orderId);
                mDialog.dismiss();
            }
        });
        mDialog.setNoOnclickListener("未收货", new SelfDialogBase.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void delCompleted(final int orderId) {
        mDialog = new SelfDialogBase(mContext);
        mDialog.setTitle("确认删除此订单？");
        mDialog.setYesOnclickListener("删除", new SelfDialogBase.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                getDataWithDelCompleted(orderId);
                mDialog.dismiss();
            }
        });
        mDialog.setNoOnclickListener("取消", new SelfDialogBase.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    private void getDataWithCollectGoods(int orderId) {
        OkGo.<String>post(Urls.COLLECT_GOODS)
                .tag(this)
                .params("order_id", orderId)
                .params("member_id", mMemberId)
                .params("token", mToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "collect_goods request succeeded --->" + body);

                        OrderStatusBean bean = mGson.fromJson(body, OrderStatusBean.class);
                        switch (bean.getCode()) {
                            case 1:
                                refreshList(mList);
                                Toast.makeText(mContext, "确认收货成功", Toast.LENGTH_SHORT).show();
                                break;
                            case 0:
                                Toast.makeText(mContext, "操作失败", Toast.LENGTH_SHORT).show();
                                break;
                            case 4:
                                Toast.makeText(mContext, "该会员没有订单", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

    private void getDataWithDelCompleted(int orderId) {
        OkGo.<String>post(Urls.DEL_COMPLETED)
                .tag(this)
                .params("order_id", orderId)
                .params("member_id", mMemberId)
                .params("token", mToken)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String body = response.body();
                        Log.e(AppConstant.TAG, "del_completed request succeeded --->" + body);

                        OrderStatusBean bean = mGson.fromJson(body, OrderStatusBean.class);
                        switch (bean.getCode()) {
                            case 1:
                                refreshList(mList);
                                Toast.makeText(mContext, "删除订单成功", Toast.LENGTH_SHORT).show();
                                break;
                            case 0:
                                Toast.makeText(mContext, "操作失败", Toast.LENGTH_SHORT).show();
                                break;
                            case 3:
                                Toast.makeText(mContext, "该订单不存在", Toast.LENGTH_SHORT).show();
                                break;
                            case 4:
                                Toast.makeText(mContext, "订单为完成,不能删除", Toast.LENGTH_SHORT).show();
                                break;
                            case 7:
                                Toast.makeText(mContext, "该会员没有订单", Toast.LENGTH_SHORT).show();
                                break;
                            default:
                                break;
                        }
                    }
                });
    }

}
