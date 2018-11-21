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
import cn.eejing.colorflower.model.event.AddrSelectEvent;
import cn.eejing.colorflower.model.request.AreaSelectBean;
import cn.eejing.colorflower.view.base.BaseActivity;

/**
 * 区级地区适配器
 */

public class AddrDistrictAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity mContext;
    private LayoutInflater mInflater;
    private List<AreaSelectBean.DataBean> mList;
    private String mProvince, mProvinceId, mCity, mCityId;

    public AddrDistrictAdapter(Activity context, List<AreaSelectBean.DataBean> list, String province, String provinceId, String city, String cityId) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(context);
        this.mList = new ArrayList<>();
        this.mList.addAll(list);
        this.mProvince = province;
        this.mProvinceId = provinceId;
        this.mCity = city;
        this.mCityId = cityId;
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

        String district, districtId;

        CitysViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(AreaSelectBean.DataBean bean) {
            district = bean.getName();
            districtId = String.valueOf(bean.getId());
            tvCity.setText(district);
        }

        @OnClick(R.id.layout_addr_citys)
        public void onViewClicked() {
            BaseActivity.delActivity("citys");
            BaseActivity.delActivity("provinces");
            mContext.finish();
            EventBus.getDefault().post(new AddrSelectEvent(mProvince, mCity, district, mProvinceId, mCityId, districtId));
        }
    }

}
