package cn.eejing.colorflower.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.request.AreaSelectBean;
import cn.eejing.colorflower.view.activity.MaAddrCityActivity;

import static cn.eejing.colorflower.app.AppConstant.ADDRESS_ID_PROVINCE;
import static cn.eejing.colorflower.app.AppConstant.ADDRESS_PROVINCE;

/**
 * 省级地区适配器
 */

public class AddrProvinceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<AreaSelectBean.DataBean> mList;

    public AddrProvinceAdapter(Context context, List<AreaSelectBean.DataBean> list) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mList = new ArrayList<>();
        this.mList.addAll(list);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new CitysViewHolder(mInflater.inflate(R.layout.item_addr_citys, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((CitysViewHolder) holder).setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void refreshList(List<AreaSelectBean.DataBean> list) {
        if (list != null) {
            mList.clear();
            addList(list);
        }
    }

    private void addList(List<AreaSelectBean.DataBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    class CitysViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_addr_citys)        TextView tvCity;

        String province, provinceId;

        CitysViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(AreaSelectBean.DataBean bean) {
            province = bean.getName();
            provinceId = String.valueOf(bean.getId());
            tvCity.setText(province);
        }

        @OnClick(R.id.layout_addr_citys)
        public void onViewClicked() {
            mContext.startActivity(new Intent(mContext, MaAddrCityActivity.class)
                    .putExtra(ADDRESS_PROVINCE, province)
                    .putExtra(ADDRESS_ID_PROVINCE, provinceId)
            );
        }
    }

}
