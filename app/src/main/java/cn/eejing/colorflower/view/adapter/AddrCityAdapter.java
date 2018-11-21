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
import cn.eejing.colorflower.view.activity.MaAddrDistrictActivity;

import static cn.eejing.colorflower.app.AppConstant.ADDRESS_CITY;
import static cn.eejing.colorflower.app.AppConstant.ADDRESS_ID_CITY;
import static cn.eejing.colorflower.app.AppConstant.ADDRESS_ID_PROVINCE;
import static cn.eejing.colorflower.app.AppConstant.ADDRESS_PROVINCE;

/**
 * 市级地区适配器
 */

public class AddrCityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<AreaSelectBean.DataBean> mList;
    private String mProvince, mProvinceId;

    public AddrCityAdapter(Context context, List<AreaSelectBean.DataBean> list, String province, String provinceId) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mList = new ArrayList<>();
        this.mList.addAll(list);
        this.mProvince = province;
        this.mProvinceId = provinceId;
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
        @BindView(R.id.tv_addr_citys)        TextView tvCitys;

        String city, cityId;

        CitysViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(AreaSelectBean.DataBean bean) {
            city = bean.getName();
            cityId = String.valueOf(bean.getId());
            tvCitys.setText(city);
        }

        @OnClick(R.id.layout_addr_citys)
        public void onViewClicked() {
            mContext.startActivity(new Intent(mContext, MaAddrDistrictActivity.class)
                    .putExtra(ADDRESS_PROVINCE, mProvince)
                    .putExtra(ADDRESS_ID_PROVINCE, mProvinceId)
                    .putExtra(ADDRESS_CITY, city)
                    .putExtra(ADDRESS_ID_CITY, cityId)
            );
        }
    }

}
