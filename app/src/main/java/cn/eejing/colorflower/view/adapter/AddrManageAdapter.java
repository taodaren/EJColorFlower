package cn.eejing.colorflower.view.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.lzy.okgo.model.HttpParams;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.http.OkGoBuilder;
import cn.eejing.colorflower.model.request.AddrListBean;
import cn.eejing.colorflower.model.request.CodeMsgBean;
import cn.eejing.colorflower.presenter.Callback;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.view.activity.MaAddrModifyActivity;
import cn.eejing.colorflower.view.activity.MainActivity;

/**
 * 地址管理适配器
 */

public class AddrManageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "AddrManageAdapter";
    private Activity mContext;
    private LayoutInflater mInflater;
    private List<AddrListBean.DataBean> mList;
    private int lastSelectedPosition;

    private View.OnClickListener mOnClickListener;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public AddrManageAdapter(Activity context, List<AddrListBean.DataBean> list) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mList = new ArrayList<>();
        this.mList.addAll(list);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddressListHolder(mInflater.inflate(R.layout.item_address_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((AddressListHolder) holder).setData(mList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void refreshList(List<AddrListBean.DataBean> list) {
        if (list != null) {
            mList.clear();
            addList(list);
        }
    }

    private void addList(List<AddrListBean.DataBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    class AddressListHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_address_list_name)          TextView tvName;
        @BindView(R.id.tv_address_list_phone)         TextView tvPhone;
        @BindView(R.id.tv_address_list_address)       TextView tvAddress;
        @BindView(R.id.rbt_address_list_def)          RadioButton rbttDef;
        @BindView(R.id.btn_address_list_del)          Button btnDel;

        AddressListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint({"SetTextI18n"})
        public void setData(AddrListBean.DataBean bean, final int position) {
            // 判断默认状态，设置默认选中按钮
            switch (bean.getIs_default()) {
                case "1":
                    // 默认
                    lastSelectedPosition = position;
                    rbttDef.setButtonDrawable(R.drawable.ic_single_selected);
                    rbttDef.setTextColor(mContext.getResources().getColor(R.color.colorNavBar));
                    rbttDef.setChecked(true);
                    break;
                default:
                    rbttDef.setButtonDrawable(R.drawable.ic_single_unselected);
                    rbttDef.setTextColor(mContext.getResources().getColor(R.color.colorNoClick));
                    rbttDef.setChecked(false);
                    break;
            }

            tvName.setText(bean.getConsignee());
            tvPhone.setText(bean.getMobile());
            tvAddress.setText(mContext.getResources().getString(R.string.text_shipping_address) + bean.getAddress());

            btnDel.setTag(getAdapterPosition());
            btnDel.setOnClickListener(mOnClickListener);
        }

        @OnClick({R.id.rbt_address_list_def})
        void clickDefault() {
            getDataWithAddressDef(getAdapterPosition());
        }

        @OnClick(R.id.btn_address_list_edit)
        void clickEdit() {
            Bundle bundle = new Bundle();
            bundle.putString("type", "edit");
            bundle.putSerializable("address_info", mList.get(getAdapterPosition()));

            mContext.startActivity(new Intent(mContext, MaAddrModifyActivity.class).putExtras(bundle));
        }

    }

    @SuppressWarnings("unchecked")
    private void getDataWithAddressDef(final int position) {
        OkGoBuilder.getInstance().setToken(MainActivity.getAppCtrl().getToken());
        HttpParams params = new HttpParams();
        params.put("address_id", mList.get(position).getId());

        OkGoBuilder.getInstance().Builder(mContext)
                .url(Urls.SET_DEF_ADDRESS)
                .method(OkGoBuilder.POST)
                .params(params)
                .cls(CodeMsgBean.class)
                .callback(new Callback<CodeMsgBean>() {
                    @Override
                    public void onSuccess(CodeMsgBean bean, int id) {
                        LogUtil.d(TAG, "设置默认地址 请求成功");

                        switch (bean.getCode()) {
                            case 1:
                                // 如果最后选中 position 与当前不一致，执行下列操作（解决点击已选中状态问题）
                                if (lastSelectedPosition != position) {
                                    // 设置选中
                                    mList.get(position).setIs_default("1");
                                    // 设置取消选中
                                    mList.get(lastSelectedPosition).setIs_default("0");
                                    notifyDataSetChanged();
                                }
                                break;
                            default:
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e, int id) {
                    }
                }).build();
    }

}
