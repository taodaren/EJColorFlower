package cn.eejing.colorflower.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.view.View;

import com.lzy.okgo.model.HttpParams;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.http.OkGoBuilder;
import cn.eejing.colorflower.model.request.GoodsDetailsBean;
import cn.eejing.colorflower.presenter.Callback;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.view.adapter.GoodsDetailsAdapter;
import cn.eejing.colorflower.view.base.BaseActivity;
import cn.eejing.colorflower.view.customize.SelfDialogBase;

/**
 * 商品详情
 */

public class MaGoodsDetailsActivity extends BaseActivity {
    private static final String TAG = "MaGoodsDetailsActivity";

    @BindView(R.id.rv_goods_details)        PullLoadMoreRecyclerView rvGoodsDetails;

    private List<GoodsDetailsBean.DataBean> mList;
    private int mGoodsId;
    private String mPhone;
    private SelfDialogBase mDialog;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_ma_goods_details;
    }

    @Override
    public void initView() {
        mGoodsId = getIntent().getIntExtra("goods_id", 0);
        mList = new ArrayList<>();
    }

    @Override
    public void initData() {
        getDataWithGoodsDetails();
    }

    @OnClick({R.id.rimg_del_return, R.id.btn_customer_service, R.id.btn_buy_now})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rimg_del_return:
                finish();
                break;
            case R.id.btn_customer_service:
                showDialog();
                break;
            case R.id.btn_buy_now:
                Intent intent = new Intent(this, MaOrderConfirmActivity.class);
                intent.putExtra("goods_id", mGoodsId);
                jumpToActivity(intent);
                break;
        }
    }

    private void initRecyclerView() {
        // 设置布局
        rvGoodsDetails.setLinearLayout();
        // 绑定适配器
        rvGoodsDetails.setAdapter(new GoodsDetailsAdapter(this, mList));

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

    private void showDialog() {
        mDialog = new SelfDialogBase(this);
        mDialog.setTitle(mPhone);
        mDialog.setYesOnclickListener("呼叫", () -> {
            // 拨打客服电话
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mPhone));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            mDialog.dismiss();
        });
        mDialog.setNoOnclickListener("取消", () -> mDialog.dismiss());
        mDialog.show();
    }

    @SuppressWarnings("unchecked")
    private void getDataWithGoodsDetails() {
        OkGoBuilder<GoodsDetailsBean> builder = new OkGoBuilder<>();
        builder.setToken(MainActivity.getAppCtrl().getToken());
        HttpParams params = new HttpParams();
        params.put("goods_id", mGoodsId);

        builder.Builder(this)
                .url(Urls.GOODS_DETAIL)
                .method(OkGoBuilder.POST)
                .params(params)
                .cls(GoodsDetailsBean.class)
                .callback(new Callback<GoodsDetailsBean>() {
                    @Override
                    public void onSuccess(GoodsDetailsBean bean, int id) {
                        LogUtil.d(TAG, "商品详情 请求成功");

                        mPhone = bean.getData().getServer_tel();
                        mList.add(bean.getData());
                        initRecyclerView();
                        // 刷新结束
                        rvGoodsDetails.setPullLoadMoreCompleted();
                    }

                    @Override
                    public void onError(Throwable e, int id) {
                    }
                }).build();
    }
}
