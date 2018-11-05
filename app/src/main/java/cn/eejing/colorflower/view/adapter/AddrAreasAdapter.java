package cn.eejing.colorflower.view.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.request.AddrAreasBean;
import cn.eejing.colorflower.view.base.BaseActivity;

public class AddrAreasAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity mContext;
    private LayoutInflater mInflater;
    private List<AddrAreasBean.DataBean> mList;
    private String mProvincess, mCity;

    public AddrAreasAdapter(Activity context, List<AddrAreasBean.DataBean> list, String provincess, String city) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mList = new ArrayList<>();
        this.mList.addAll(list);
        this.mProvincess = provincess;
        this.mCity = city;
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

    public void refreshList(List<AddrAreasBean.DataBean> list) {
        if (list != null) {
            mList.clear();
            addList(list);
        }
    }

    private void addList(List<AddrAreasBean.DataBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    class CitysViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_addr_citys)
        TextView tvCitys;
        private String areas;

        CitysViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(AddrAreasBean.DataBean bean) {
            areas = bean.getArea();
            tvCitys.setText(areas);
        }

        @OnClick(R.id.layout_addr_citys)
        public void onViewClicked() {
//            Intent intent = new Intent(mContext, MaAddrAddActivity.class);
//            intent.putExtra(ADDRESS_PROVINCESS, mProvincess);
//            intent.putExtra(ADDRESS_CITYS, mCity);
//            intent.putExtra(ADDRESS_AREAS, areas);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            mContext.startActivity(intent);
            BaseActivity.delActivity("citys");
            BaseActivity.delActivity("provinces");
            mContext.finish();
            EventBus.getDefault().post(mProvincess + "  " + mCity + "  " + areas);
        }
    }

}
