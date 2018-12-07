package cn.eejing.colorflower.view.activity;

import android.view.View;

import com.lzy.okgo.model.HttpParams;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.http.OkGoBuilder;
import cn.eejing.colorflower.model.request.AreaSelectBean;
import cn.eejing.colorflower.presenter.Callback;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.adapter.AddrDistrictAdapter;
import cn.eejing.colorflower.view.base.BaseActivity;

import static cn.eejing.colorflower.app.AppConstant.ADDRESS_CITY;
import static cn.eejing.colorflower.app.AppConstant.ADDRESS_ID_CITY;
import static cn.eejing.colorflower.app.AppConstant.ADDRESS_ID_PROVINCE;
import static cn.eejing.colorflower.app.AppConstant.ADDRESS_PROVINCE;

/**
 * 区级地区
 */

public class MaAddrDistrictActivity extends BaseActivity {
    private static final String TAG = "MaAddrDistrictActivity";

    @BindView(R.id.rv_citys)        PullLoadMoreRecyclerView rvCitys;

    private List<AreaSelectBean.DataBean> mList;
    private AddrDistrictAdapter mAdapter;
    private String mProvince, mProvinceId, mCity, mCityId;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ma_addr_citys;
    }

    @Override
    public void initView() {
        setToolbar("选择地区", View.VISIBLE, null, View.GONE);
        mProvince = getIntent().getStringExtra(ADDRESS_PROVINCE);
        mProvinceId = getIntent().getStringExtra(ADDRESS_ID_PROVINCE);
        mCity = getIntent().getStringExtra(ADDRESS_CITY);
        mCityId = getIntent().getStringExtra(ADDRESS_ID_CITY);
        mList = new ArrayList<>();
        initRecyclerView();
    }

    @Override
    public void initData() {
        getDataWithAreas();
    }

    private void initRecyclerView() {
        // 设置布局
        rvCitys.setLinearLayout();
        // 绑定适配器
        mAdapter = new AddrDistrictAdapter(this, mList, mProvince, mProvinceId, mCity, mCityId);
        rvCitys.setAdapter(mAdapter);

        // 不需要上拉刷新
        rvCitys.setPushRefreshEnable(false);
        rvCitys.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                getDataWithAreas();
            }

            @Override
            public void onLoadMore() {

            }
        });
        // 刷新结束
        rvCitys.setPullLoadMoreCompleted();
    }

    @SuppressWarnings("unchecked")
    private void getDataWithAreas() {
        OkGoBuilder.getInstance().setToken(MainActivity.getAppCtrl().getToken());
        HttpParams params = new HttpParams();
        params.put("area_id", mCityId);

        OkGoBuilder.getInstance().Builder(this)
                .url(Urls.AREA_SELECT)
                .method(OkGoBuilder.POST)
                .params(params)
                .cls(AreaSelectBean.class)
                .callback(new Callback<AreaSelectBean>() {
                    @Override
                    public void onSuccess(AreaSelectBean bean, int id) {
                        LogUtil.d(TAG, "地区选择 请求成功");

                        switch (bean.getCode()) {
                            case 1:
                                mList = bean.getData();
                                // 刷新数据
                                mAdapter.refreshList(mList);
                                // 刷新结束
                                rvCitys.setPullLoadMoreCompleted();
                                break;
                            default:
                                ToastUtil.showShort(bean.getMessage());
                                break;
                        }
                    }

                    @Override
                    public void onError(Throwable e, int id) {
                    }
                }).build();
    }

}
