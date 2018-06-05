package cn.eejing.ejcolorflower.ui.activity;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.app.Urls;
import cn.eejing.ejcolorflower.model.request.GoodsDetailsBean;
import cn.eejing.ejcolorflower.ui.adapter.GoodsDetailsAdapter;
import cn.eejing.ejcolorflower.ui.base.BaseActivity;

/**
 * 商品详情
 */

public class MaGoodsDetailsActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MaGoodsDetailsActivity";

    @BindView(R.id.img_title_back)
    ImageView imgTitleBack;
    @BindView(R.id.rv_goods_details)
    PullLoadMoreRecyclerView rvGoodsDetails;

    private Gson mGson;
    private List<GoodsDetailsBean.DataBean> mList;
    private GoodsDetailsAdapter mAdapter;
    private int mGoodsId;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ma_goods_details;
    }

    @Override
    public void initView() {
        mGoodsId = getIntent().getIntExtra("goods_id", 0);
        mGson = new Gson();
        mList = new ArrayList<>();

        setToolbar(getIntent().getStringExtra("name"), View.VISIBLE);
    }

    @Override
    public void initData() {
        getDataWithGoodsDetails();
    }

    @Override
    public void initListener() {
        imgTitleBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_title_back:
                finish();
                break;
            default:
                break;
        }
    }

    private void initRecyclerView() {
        // 设置布局
        rvGoodsDetails.setLinearLayout();
        // 绑定适配器
        mAdapter = new GoodsDetailsAdapter(this, mList);
        rvGoodsDetails.setAdapter(mAdapter);

        // 不需要上拉刷新
        rvGoodsDetails.setPushRefreshEnable(false);
        rvGoodsDetails.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                mList.clear();
                getDataWithGoodsDetails();
            }

            @Override
            public void onLoadMore() {

            }
        });
        // 刷新结束
        rvGoodsDetails.setPullLoadMoreCompleted();
    }

    private void getDataWithGoodsDetails() {
        OkGo.<String>post(Urls.GOODS_DETAILS)
                .tag(this)
                .params("goods_id", mGoodsId)
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 String body = response.body();
                                 Log.e(AppConstant.TAG, "goods_details request succeeded--->" + body);

                                 GoodsDetailsBean bean = mGson.fromJson(body, GoodsDetailsBean.class);
                                 mList.add(bean.getData());
                                 initRecyclerView();
                                 // 刷新结束
                                 rvGoodsDetails.setPullLoadMoreCompleted();
                             }

                             @Override
                             public void onError(Response<String> response) {
                                 super.onError(response);
                             }
                         }
                );

    }

}
