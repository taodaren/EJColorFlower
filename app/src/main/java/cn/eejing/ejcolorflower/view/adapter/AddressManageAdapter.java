package cn.eejing.ejcolorflower.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.model.request.AddressListBean;

public class AddressManageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<AddressListBean.DataBean> mList;
    private int lastSelectedPosition = -1;

    public AddressManageAdapter(Context context, List<AddressListBean.DataBean> list) {
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

    public void refreshList(List<AddressListBean.DataBean> list) {
        if (list != null) {
            mList.clear();
            addList(list);
        }
    }

    private void addList(List<AddressListBean.DataBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    class AddressListHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_address_list_name)
        TextView tvName;
        @BindView(R.id.tv_address_list_phone)
        TextView tvPhone;
        @BindView(R.id.tv_address_list_address)
        TextView tvAddress;
        @BindView(R.id.rbt_address_list_def)
        RadioButton rbttDef;
        @BindView(R.id.btn_address_list_edit)
        Button btnEdit;
        @BindView(R.id.btn_address_list_del)
        Button btnDel;

        AddressListHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("SetTextI18n")
        public void setData(AddressListBean.DataBean bean, int position) {
            // 由于只允许选择一个单选按钮，因此此条件将取消选中以前的选择
            rbttDef.setChecked(lastSelectedPosition == position);

            tvName.setText(bean.getName());
            tvPhone.setText(bean.getMobile());
            tvAddress.setText(mContext.getResources().getString(R.string.text_shipping_address) + bean.getAddress_all());
        }

        @RequiresApi(api = Build.VERSION_CODES.M)
        @OnClick(R.id.rbt_address_list_def)
        public void clickDefault() {
            lastSelectedPosition = getAdapterPosition();
            notifyDataSetChanged();
        }

        @OnClick(R.id.btn_address_list_edit)
        public void clickEdit() {
            Toast.makeText(mContext, "clickEdit", Toast.LENGTH_SHORT).show();
        }

        @OnClick(R.id.btn_address_list_del)
        public void clickDelete() {
            Toast.makeText(mContext, "clickDelete", Toast.LENGTH_SHORT).show();
        }
    }

}
