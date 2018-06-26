package cn.eejing.ejcolorflower.view.activity;

import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.List;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.model.request.AddrProvincesBean;
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.view.adapter.AddrAreasAdapter;
import cn.eejing.ejcolorflower.view.base.BaseActivity;

/**
 * 县
 */

public class MaAddrAreasActivity extends BaseActivity {

    @BindView(R.id.rv_citys)
    PullLoadMoreRecyclerView rvCitys;

    private List<AddrProvincesBean.DataBean> mList;
    private AddrAreasAdapter mAdapter;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ma_addr_citys;
    }

    @Override
    public void initView() {
        setToolbar("选择地区", View.VISIBLE);
        initRecyclerView();
    }

    @Override
    public void initData() {
        getDataWithProvinces();
    }

    private void initRecyclerView() {
        // 设置布局
        rvCitys.setLinearLayout();
        // 绑定适配器
        mAdapter = new AddrAreasAdapter(this, mList);
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
        OkGo.<String>get(Urls.PROVINCES)
                .tag(this)
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 String body = response.body();
                                 Log.e(AppConstant.TAG, "provinces request succeeded--->" + body);

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
