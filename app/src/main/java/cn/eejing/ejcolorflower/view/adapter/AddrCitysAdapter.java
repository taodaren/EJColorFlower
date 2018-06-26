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
import cn.eejing.ejcolorflower.model.request.AddrCitysBean;
import cn.eejing.ejcolorflower.view.activity.MaAddrAreasActivity;
import cn.eejing.ejcolorflower.view.activity.MaAddrCitysActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.ADDRESS_CITYS;
import static cn.eejing.ejcolorflower.app.AppConstant.ADDRESS_ID_CITYS;

public class AddrCitysAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<AddrCitysBean.DataBean> mList;

    public AddrCitysAdapter(Context context, List<AddrCitysBean.DataBean> list) {
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

    public void refreshList(List<AddrCitysBean.DataBean> list) {
        if (list != null) {
            mList.clear();
            addList(list);
        }
    }

    private void addList(List<AddrCitysBean.DataBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    class CitysViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_addr_citys)
        TextView tvCitys;
        private String city, cityId;

        CitysViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(AddrCitysBean.DataBean bean) {
            city = bean.getCity();
            cityId = bean.getCity_id();
            tvCitys.setText(bean.getCity());
        }

        @OnClick(R.id.layout_addr_citys)
        public void onViewClicked() {
            mContext.startActivity(new Intent(mContext, MaAddrAreasActivity.class)
                    .putExtra(ADDRESS_CITYS, city)
                    .putExtra(ADDRESS_ID_CITYS, cityId)
            );
        }
    }

}
