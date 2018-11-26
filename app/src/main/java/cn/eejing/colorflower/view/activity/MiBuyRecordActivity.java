package cn.eejing.colorflower.view.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import java.util.ArrayList;

import butterknife.BindView;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.request.BuyRecordBean;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.adapter.BuyRecordAdapter;
import cn.eejing.colorflower.view.adapter.VipListAdapter;
import cn.eejing.colorflower.view.base.BaseActivity;

import static cn.eejing.colorflower.app.AppConstant.LEVEL_VVIP_USER;

/**
 * 购买记录
 */

public class MiBuyRecordActivity extends BaseActivity {
    @BindView(R.id.rv_buy_record)    PullLoadMoreRecyclerView rvBuy;

    private static final String TAG = "MiBuyRecordActivity";
//    private List<BuyRecordBean.DataBean> mList;
    private BuyRecordAdapter mAdapter;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_mi_buy_record;
    }

    @Override
    public void initView() {
        setToolbar("购买记录", View.VISIBLE, null, View.GONE);
//        mList = new ArrayList<>();
//        initRecyclerView();
    }

    @Override
    public void setToolbar(String title, int titleVisibility, String menu, int menuVisibility) {
        super.setToolbar(title, titleVisibility, menu, menuVisibility);
        ImageView imgRecord = findViewById(R.id.img_vip_toolbar);
        imgRecord.setVisibility(View.VISIBLE);
        imgRecord.setImageDrawable(getResources().getDrawable(R.drawable.ic_buy_record));
    }

    @Override
    public void initData() {
        getDataWithBuyRecord();
    }

    private void initRecyclerView() {
        rvBuy.setLinearLayout();
//        mAdapter = new BuyRecordAdapter(this, mList);
        rvBuy.setAdapter(mAdapter);
        rvBuy.setPushRefreshEnable(false);
        rvBuy.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                getDataWithBuyRecord();
            }

            @Override
            public void onLoadMore() {
            }
        });
        rvBuy.setPullLoadMoreCompleted();
    }

    private void getDataWithBuyRecord() {
        OkGo.<String>post(Urls.SALES_RECORD)
                .tag(this)
                .params("token", MainActivity.getAppCtrl().getToken())
                .params("start", "时间戳格式")
                .params("end", "时间戳格式")
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 String body = response.body();
                                 LogUtil.d(TAG, "销售记录 请求成功: " + body);

//                                 Gson gson = new Gson();
//                                 BuyRecordBean bean = gson.fromJson(body, BuyRecordBean.class);
//                                 switch (bean.getCode()) {
//                                     case 1:
//                                         mList = bean.getData();
//                                         // 刷新数据
//                                         mAdapter.refreshList(mList);
//                                         // 刷新结束
//                                         rvBuy.setPullLoadMoreCompleted();
//                                         break;
//                                     default:
//                                         ToastUtil.showShort(bean.getMessage());
//                                         break;
//                                 }
                             }
                         }
                );

    }
}
