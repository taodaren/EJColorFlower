package cn.eejing.colorflower.view.activity;

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
import cn.eejing.colorflower.model.request.AddrProvincesBean;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.adapter.AddrProvincessAdapter;
import cn.eejing.colorflower.view.base.BaseActivity;

/**
 * 省
 */

public class MaAddrProvincesActivity extends BaseActivity {

    @BindView(R.id.rv_citys)        PullLoadMoreRecyclerView rvCitys;

    private List<AddrProvincesBean.DataBean> mList;
    private AddrProvincessAdapter mAdapter;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ma_addr_citys;
    }

    @Override
    public void initView() {
        setToolbar("选择地区", View.VISIBLE, null, View.GONE);
        mList = new ArrayList<>();

        addActivity("provinces", this);
        initRecyclerView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        delActivity("provinces");
    }

    @Override
    public void initData() {
        getDataWithProvinces();
    }

    private void initRecyclerView() {
        // 设置布局
        rvCitys.setLinearLayout();
        // 绑定适配器
        mAdapter = new AddrProvincessAdapter(this, mList);
        rvCitys.setAdapter(mAdapter);

        // 不需要上拉刷新
        rvCitys.setPushRefreshEnable(false);
        rvCitys.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                getDataWithProvinces();
            }

            @Override
            public void onLoadMore() {

            }
        });
        // 刷新结束
        rvCitys.setPullLoadMoreCompleted();
    }

    private void getDataWithProvinces() {
        OkGo.<String>post(Urls.PROVINCES)
                .tag(this)
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 String body = response.body();
                                 LogUtil.e(AppConstant.TAG, "provinces request succeeded--->" + body);

                                 Gson gson = new Gson();
                                 AddrProvincesBean bean = gson.fromJson(body, AddrProvincesBean.class);
                                 switch (bean.getCode()) {
                                     case 1:
                                         mList = bean.getData();
                                         // 刷新数据
                                         mAdapter.refreshList(mList);
                                         // 刷新结束
                                         rvCitys.setPullLoadMoreCompleted();
                                         break;
                                     default:
                                         ToastUtil.showShort("" + bean.getCode());
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