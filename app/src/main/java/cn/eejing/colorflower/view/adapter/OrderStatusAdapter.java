package cn.eejing.colorflower.view.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.model.HttpParams;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.http.OkGoBuilder;
import cn.eejing.colorflower.model.request.CodeMsgBean;
import cn.eejing.colorflower.model.request.OrderListBean;
import cn.eejing.colorflower.presenter.Callback;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.view.customize.SelfDialogBase;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.activity.MaOrderPayActivity;
import cn.eejing.colorflower.view.activity.MainActivity;
import cn.eejing.colorflower.view.activity.MiOrderActivity;
import cn.eejing.colorflower.view.activity.MiOrderDetailsActivity;

import static cn.eejing.colorflower.app.AppConstant.TYPE_COMPLETE_GOODS;
import static cn.eejing.colorflower.app.AppConstant.TYPE_WAIT_PAYMENT;
import static cn.eejing.colorflower.app.AppConstant.TYPE_WAIT_RECEIPT;
import static cn.eejing.colorflower.app.AppConstant.TYPE_WAIT_SHIP;

/**
 * 订单状态适配器
 */

public class OrderStatusAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "OrderStatusAdapter";
    private Activity mContext;
    private LayoutInflater mInflater;
    private List<OrderListBean.DataBean> mList;
    private String mType;
    private SelfDialogBase mDialog;

    public OrderStatusAdapter(Activity context, List<OrderListBean.DataBean> list, String type) {
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

    public void refreshList(List<OrderListBean.DataBean> list) {
        mList.clear();
        addList(list);
    }

    private void addList(List<OrderListBean.DataBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_order_status)             ImageView imgGoods;
        @BindView(R.id.tv_order_status_name)         TextView  tvName;
        @BindView(R.id.tv_order_status_money_rmb)    TextView  tvMoneyRmb;
        @BindView(R.id.tv_order_status_num)          TextView  tvNum;
        @BindView(R.id.tv_order_status_quantity)     TextView  tvQuantity;
        @BindView(R.id.tv_order_status_postage)      TextView  tvPostage;
        @BindView(R.id.tv_order_status_money)        TextView  tvMoney;
        @BindView(R.id.btn_order_status)             Button    btnEdit;
        @BindView(R.id.btn_order_cancel)             Button    btnCancel;
        View outItem;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            outItem = itemView;
        }

        @SuppressLint("SetTextI18n")
        public void setData(final OrderListBean.DataBean bean) {
            Glide.with(mContext).load(bean.getGoods_img()).into(imgGoods);
            tvName.setText(bean.getGoods_name());
            tvMoneyRmb.setText(mContext.getString(R.string.rmb) + bean.getGoods_price());
            tvNum.setText(mContext.getString(R.string.text_multiply) + bean.getQuantity());
            tvQuantity.setText(mContext.getString(R.string.text_common) + bean.getQuantity() + mContext.getString(R.string.text_items));
            tvPostage.setText(mContext.getString(R.string.postage) + 0 + mContext.getString(R.string.yuan));
            tvMoney.setText(mContext.getString(R.string.text_total) + bean.getTotal_amount() + mContext.getString(R.string.yuan));

            switch (mType) {
                case TYPE_WAIT_PAYMENT:
                    btnCancel.setText("取消订单");
                    btnEdit.setText("立即支付");
                    break;
                case TYPE_WAIT_SHIP:
                    btnCancel.setVisibility(View.GONE);
                    btnEdit.setVisibility(View.GONE);
                    break;
                case TYPE_WAIT_RECEIPT:
                    btnCancel.setVisibility(View.GONE);
                    btnEdit.setText("确认收货");
                    break;
                case TYPE_COMPLETE_GOODS:
                    btnCancel.setVisibility(View.GONE);
                    btnEdit.setText("删除");
                    break;
                default:
                    break;
            }

            outItem.setOnClickListener(v -> mContext.startActivity(new Intent(mContext, MiOrderDetailsActivity.class)
                    .putExtra("order_id", bean.getOrder_sn())
                    .putExtra("type", mType)
            ));

            btnEdit.setOnClickListener(view -> {
                switch (mType) {
                    case TYPE_WAIT_PAYMENT:
                        ((MiOrderActivity) mContext).jumpToActivity(new Intent(mContext, MaOrderPayActivity.class)
                                .putExtra("order_no", bean.getOrder_sn())
                                .putExtra("total_price", Double.parseDouble(bean.getTotal_amount()))
                        );
                        break;
                    case TYPE_WAIT_RECEIPT:
                        confirmReceipt(bean.getOrder_sn());
                        break;
                    case TYPE_COMPLETE_GOODS:
                        delOrder(bean.getOrder_sn());
                        break;
                }
            });

            btnCancel.setOnClickListener(v -> cancelOrder(bean.getOrder_sn()));
        }
    }

    private void confirmReceipt(final String orderSn) {
        mDialog = new SelfDialogBase(mContext);
        mDialog.setTitle("您是否已收到该订单商品？");
        mDialog.setYesOnclickListener("已收货", () -> {
            getDataWithConfirmReceipt(orderSn);
            mDialog.dismiss();
        });
        mDialog.setNoOnclickListener("未收货", () -> mDialog.dismiss());
        mDialog.show();
    }

    private void cancelOrder(final String orderSn) {
        mDialog = new SelfDialogBase(mContext);
        mDialog.setTitle("确认取消此订单？");
        mDialog.setYesOnclickListener("确认", () -> {
            getDataWithDelOrder(orderSn, "取消订单成功");
            mDialog.dismiss();
        });
        mDialog.setNoOnclickListener("再想想", () -> mDialog.dismiss());
        mDialog.show();
    }

    private void delOrder(final String orderSn) {
        mDialog = new SelfDialogBase(mContext);
        mDialog.setTitle("确认删除此订单？");
        mDialog.setYesOnclickListener("删除", () -> {
            getDataWithDelOrder(orderSn, "删除订单成功");
            mDialog.dismiss();
        });
        mDialog.setNoOnclickListener("取消", () -> mDialog.dismiss());
        mDialog.show();
    }

    @SuppressWarnings("unchecked")
    private void getDataWithConfirmReceipt(String orderSn) {
        OkGoBuilder.getInstance().setToken(MainActivity.getAppCtrl().getToken());
        HttpParams params = new HttpParams();
        params.put("order_sn", orderSn);

        OkGoBuilder.getInstance().Builder(mContext)
                .url(Urls.CONFIRM_RECEIPT)
                .method(OkGoBuilder.POST)
                .params(params)
                .cls(CodeMsgBean.class)
                .callback(new Callback<CodeMsgBean>() {
                    @Override
                    public void onSuccess(CodeMsgBean bean, int id) {
                        LogUtil.d(TAG, "确认收货 请求成功");

                        switch (bean.getCode()) {
                            case 1:
                                refreshList(mList);
                                ToastUtil.showShort("确认收货成功");
                                break;
                            default:
                                ToastUtil.showShort(bean.getMessage());
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e, int id) {
                    }
                }).build();
    }

    @SuppressWarnings("unchecked")
    private void getDataWithDelOrder(String orderSn, String successMsg) {
        OkGoBuilder.getInstance().setToken(MainActivity.getAppCtrl().getToken());
        HttpParams params = new HttpParams();
        params.put("order_sn", orderSn);

        OkGoBuilder.getInstance().Builder(mContext)
                .url(Urls.DEL_ORDER)
                .method(OkGoBuilder.POST)
                .params(params)
                .cls(CodeMsgBean.class)
                .callback(new Callback<CodeMsgBean>() {
                    @Override
                    public void onSuccess(CodeMsgBean bean, int id) {
                        LogUtil.d(TAG, "删除订单 请求成功");

                        switch (bean.getCode()) {
                            case 1:
                                refreshList(mList);
                                ToastUtil.showShort(successMsg);
                                break;
                            default:
                                ToastUtil.showShort(bean.getMessage());
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e, int id) {
                    }
                }).build();
    }

}
