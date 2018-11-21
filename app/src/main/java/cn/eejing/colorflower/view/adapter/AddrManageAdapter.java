package cn.eejing.colorflower.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
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

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.request.AddrListBean;
import cn.eejing.colorflower.model.request.CodeMsgBean;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.view.activity.MaAddrModifyActivity;
import cn.eejing.colorflower.view.activity.MainActivity;

/**
 * 地址管理适配器
 */

public class AddrManageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "AddrManageAdapter";
    private Context mContext;
    private LayoutInflater mInflater;
    private List<AddrListBean.DataBean> mList;
    private Gson mGson;
    private int lastSelectedPosition;

    private View.OnClickListener mOnClickListener;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public AddrManageAdapter(Context context, List<AddrListBean.DataBean> list) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mList = new ArrayList<>();
        this.mList.addAll(list);
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

    private void getDataWithAddressDef(final int position) {
        OkGo.<String>post(Urls.SET_DEF_ADDRESS)
                .tag(this)
                .params("address_id", mList.get(position).getId())
                .params("token", MainActivity.getAppCtrl().getToken())
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 String body = response.body();
                                 LogUtil.d(TAG, "设置默认地址 请求成功: " + body);

                                 CodeMsgBean bean = mGson.fromJson(body, CodeMsgBean.class);
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
                         }
                );
    }

}
