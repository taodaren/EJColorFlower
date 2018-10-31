package cn.eejing.ejcolorflower.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.model.request.AddrDefBean;
import cn.eejing.ejcolorflower.model.request.AddrListBean;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.view.activity.MaAddrModifyActivity;

public class AddrManageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<AddrListBean.DataBean> mList;
    private String mMemberId, mToken;
    private Gson mGson;
    private int lastSelectedPosition;

    private View.OnClickListener mOnClickListener;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public AddrManageAdapter(Context context, List<AddrListBean.DataBean> list, String memberId, String token) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mList = new ArrayList<>();
        this.mList.addAll(list);
        this.mMemberId = memberId;
        this.mToken = token;
        this.mGson = new Gson();
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
            switch (bean.getStatus()) {
                case 1:
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

            tvName.setText(bean.getName());
            tvPhone.setText(bean.getMobile());
            tvAddress.setText(mContext.getResources().getString(R.string.text_shipping_address) + bean.getAddress_all());

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

    private void getDataWithAddressDef(final int position) {
        OkGo.<String>post(Urls.ADDRESS_DEF)
                .tag(this)
                .params("member_id", mMemberId)
                .params("address_id", mList.get(position).getId())
                .params("token", mToken)
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 String body = response.body();
                                 Log.e(AppConstant.TAG, "address_def request succeeded--->" + body);

                                 AddrDefBean bean = mGson.fromJson(body, AddrDefBean.class);
                                 switch (bean.getCode()) {
                                     case 1:
                                         // 如果最后选中 position 与当前不一致，执行下列操作（解决点击已选中状态问题）
                                         if (lastSelectedPosition != position) {
                                             // 设置选中
                                             mList.get(position).setStatus(1);
                                             // 设置取消选中
                                             mList.get(lastSelectedPosition).setStatus(0);
                                             notifyDataSetChanged();
                                         }
                                         break;
                                     default:
                                         break;
                                 }
                             }

                             @Override
                             public void onError(Response<String> response) {
                                 super.onError(response);
                             }
                         }
                );
    }

}
