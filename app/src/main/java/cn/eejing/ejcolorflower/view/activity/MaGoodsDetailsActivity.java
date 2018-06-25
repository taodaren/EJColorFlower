package cn.eejing.ejcolorflower.view.activity;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.Button;

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
import cn.eejing.ejcolorflower.presenter.Urls;
import cn.eejing.ejcolorflower.model.request.GoodsDetailsBean;
import cn.eejing.ejcolorflower.view.adapter.GoodsDetailsAdapter;
import cn.eejing.ejcolorflower.view.base.BaseActivity;
import cn.eejing.ejcolorflower.util.SelfDialogBase;

/**
 * 商品详情
 */

public class MaGoodsDetailsActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.rv_goods_details)
    PullLoadMoreRecyclerView rvGoodsDetails;
    @BindView(R.id.btn_customer_service)
    Button btnCustomerService;
    @BindView(R.id.btn_buy_now)
    Button btnBuyNow;

    private Gson mGson;
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
        btnCustomerService.setOnClickListener(this);
        btnBuyNow.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_customer_service:
                showDialog();
                break;
            case R.id.btn_buy_now:
                Intent intent = new Intent(this, MaOrderConfirmActivity.class);
                intent.putExtra("goods_id", mGoodsId);
                jumpToActivity(intent);
                break;
            default:
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
        mDialog.setYesOnclickListener("呼叫", new SelfDialogBase.onYesOnclickListener() {
            @Override
            public void onYesClick() {
                // 拨打客服电话
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + mPhone));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                mDialog.dismiss();
            }
        });
        mDialog.setNoOnclickListener("取消", new SelfDialogBase.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                mDialog.dismiss();
            }
        });
        mDialog.show();
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
                                 mPhone = bean.getData().getPhone();
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
