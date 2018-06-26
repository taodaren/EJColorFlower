package cn.eejing.ejcolorflower.view.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.model.request.AddrListBean;
import cn.eejing.ejcolorflower.util.TextColorSizeHelper;

public class AddrSelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity mContext;
    private LayoutInflater mInflater;
    private List<AddrListBean.DataBean> mList;

    public AddrSelectAdapter(Activity context, List<AddrListBean.DataBean> list) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mList = new ArrayList<>();
        this.mList.addAll(list);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddressSelectHolder(mInflater.inflate(R.layout.item_address_select, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((AddressSelectHolder) holder).setData(mList.get(position));
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

    class AddressSelectHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.layout_address_select)
        LinearLayout linearLayout;
        @BindView(R.id.tv_address_select_name)
        TextView tvName;
        @BindView(R.id.tv_address_select_phone)
        TextView tvPhone;
        @BindView(R.id.tv_address_select_address)
        TextView tvAddress;

        AddressSelectHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @SuppressLint("SetTextI18n")
        public void setData(AddrListBean.DataBean bean) {
            String defAddress = mContext.getResources().getString(R.string.text_address_def) + bean.getAddress_all();
            String[] defColor = {"[", "默", "认", "地", "址", "]"};

            switch (bean.getStatus()) {
                case 1:
                    // 默认地址
                    tvAddress.setText(TextColorSizeHelper.getTextSpan(mContext, mContext.getColor(R.color.colorPrimary), defAddress, defColor));
                    break;
                default:
                    // 新增地址
                    tvAddress.setText(bean.getAddress_all());
                    break;
            }
            tvName.setText(bean.getName());
            tvPhone.setText(bean.getMobile());
        }

        @OnClick(R.id.layout_address_select)
        public void clickLayout() {
            mContext.finish();
        }
    }

}
