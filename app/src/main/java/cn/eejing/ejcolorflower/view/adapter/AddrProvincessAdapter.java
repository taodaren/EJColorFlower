package cn.eejing.ejcolorflower.view.adapter;

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
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.model.request.AddrProvincesBean;
import cn.eejing.ejcolorflower.view.activity.MaAddrAreasActivity;
import cn.eejing.ejcolorflower.view.activity.MaAddrCitysActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.ADDRESS_ID_PROVINCESS;
import static cn.eejing.ejcolorflower.app.AppConstant.ADDRESS_PROVINCESS;

public class AddrProvincessAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<AddrProvincesBean.DataBean> mList;

    public AddrProvincessAdapter(Context context, List<AddrProvincesBean.DataBean> list) {
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

    public void refreshList(List<AddrProvincesBean.DataBean> list) {
        if (list != null) {
            mList.clear();
            addList(list);
        }
    }

    private void addList(List<AddrProvincesBean.DataBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    class CitysViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_addr_citys)
        TextView tvCitys;
        private String province,provinceId;

        CitysViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(AddrProvincesBean.DataBean bean) {
            province = bean.getProvince();
            provinceId = bean.getProvince_id();
            tvCitys.setText(bean.getProvince());
        }

        @OnClick(R.id.layout_addr_citys)
        public void onViewClicked() {
            mContext.startActivity(new Intent(mContext, MaAddrCitysActivity.class)
                    .putExtra(ADDRESS_PROVINCESS, province)
                    .putExtra(ADDRESS_ID_PROVINCESS, provinceId)
            );
        }
    }

}
