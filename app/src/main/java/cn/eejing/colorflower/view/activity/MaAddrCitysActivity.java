package cn.eejing.colorflower.view.activity;

import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.app.AppConstant;
import cn.eejing.colorflower.model.request.AddrCitysBean;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.view.adapter.AddrCitysAdapter;
import cn.eejing.colorflower.view.base.BaseActivity;

import static cn.eejing.colorflower.app.AppConstant.ADDRESS_ID_PROVINCESS;
import static cn.eejing.colorflower.app.AppConstant.ADDRESS_PROVINCESS;

/**
 * 市
 */

public class MaAddrCitysActivity extends BaseActivity {

    @BindView(R.id.rv_citys)        PullLoadMoreRecyclerView rvCitys;

    private List<AddrCitysBean.DataBean> mList;
    private AddrCitysAdapter mAdapter;
    private String mProvincess, mProvincessId;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ma_addr_citys;
    }

    @Override
    public void initView() {
        setToolbar("选择地区", View.VISIBLE, null, View.GONE);
        addActivity("citys", this);
        mList = new ArrayList<>();
        mProvincess = getIntent().getStringExtra(ADDRESS_PROVINCESS);
        mProvincessId = getIntent().getStringExtra(ADDRESS_ID_PROVINCESS);

        initRecyclerView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        delActivity("citys");
    }

    @Override
    public void initData() {
        getDataWithCitys();
    }

    private void initRecyclerView() {
        // 设置布局
        rvCitys.setLinearLayout();
        // 绑定适配器
        mAdapter = new AddrCitysAdapter(this, mList, mProvincess);
        rvCitys.setAdapter(mAdapter);

        // 不需要上拉刷新
        rvCitys.setPushRefreshEnable(false);
        rvCitys.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                getDataWithCitys();
            }

            @Override
            public void onLoadMore() {

            }
        });
        // 刷新结束
        rvCitys.setPullLoadMoreCompleted();
    }

    private void getDataWithCitys() {
        OkGo.<String>post(Urls.CITYS)
                .tag(this)
                .params("province_id", mProvincessId)
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 String body = response.body();
                                 Log.e(AppConstant.TAG, "citys request succeeded--->" + body);

                                 Gson gson = new Gson();
                                 AddrCitysBean bean = gson.fromJson(body, AddrCitysBean.class);
                                 switch (bean.getCode()) {
                                     case 1:
                                         mList = bean.getData();
                                         // 刷新数据
                                         mAdapter.refreshList(mList);
                                         // 刷新结束
                                         rvCitys.setPullLoadMoreCompleted();
                                         break;
                                     default:
                                         break;
                                 }
                             }

                             @Override
                             public void onError(Response<String> response) {
                                 super.onError(response);
                             }
                         }
                );
    }

}
