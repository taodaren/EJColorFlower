package cn.eejing.colorflower.view.activity;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzy.okgo.model.HttpParams;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;
import com.wx.wheelview.widget.WheelView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.http.OkGoBuilder;
import cn.eejing.colorflower.model.request.BuyRecordBean;
import cn.eejing.colorflower.presenter.Callback;
import cn.eejing.colorflower.presenter.Urls;
import cn.eejing.colorflower.util.BottomDialog;
import cn.eejing.colorflower.util.LogUtil;
import cn.eejing.colorflower.util.ToastUtil;
import cn.eejing.colorflower.view.adapter.BuyRecordAdapter;
import cn.eejing.colorflower.view.adapter.MyWheelAdapter;
import cn.eejing.colorflower.view.base.BaseActivity;

import static cn.eejing.colorflower.util.DateUtil.FORMAT_ALL;
import static cn.eejing.colorflower.util.DateUtil.FORMAT_HMS;
import static cn.eejing.colorflower.util.DateUtil.FORMAT_YMD;
import static cn.eejing.colorflower.util.DateUtil.date2TimeStamp;
import static cn.eejing.colorflower.util.DateUtil.getCurDate;
import static cn.eejing.colorflower.util.DateUtil.getLastDay;

/**
 * 购买记录
 */

public class MiBuyRecordActivity extends BaseActivity {
    @BindView(R.id.rv_buy_record)    PullLoadMoreRecyclerView rvBuy;
    @BindView(R.id.tv_not_pay)       TextView tvNotPay;

    private static final String TAG = "MiBuyRecordActivity";
    private List<BuyRecordBean.DataBean> mList;
    private BuyRecordAdapter mAdapter;
    private BottomDialog mBottomDialog;
    private String mStartStamp, mEndStamp;

    @Override
    protected int layoutViewId() {
        return R.layout.activity_mi_buy_record;
    }

    @Override
    public void initView() {
        setToolbar("购买记录", View.VISIBLE, null, View.GONE);
        mList = new ArrayList<>();
        mStartStamp = date2TimeStamp("2018-01-01 00:00:00", FORMAT_ALL);
        mEndStamp = date2TimeStamp(getCurDate(FORMAT_ALL), FORMAT_ALL);
        initRecyclerView();
    }

    @Override
    public void setToolbar(String title, int titleVisibility, String menu, int menuVisibility) {
        super.setToolbar(title, titleVisibility, menu, menuVisibility);
        ImageView imgRecord = findViewById(R.id.img_vip_toolbar);
        ViewGroup.LayoutParams params = imgRecord.getLayoutParams();
        params.height = 60;
        params.width = 60;
        imgRecord.setLayoutParams(params);
        imgRecord.setVisibility(View.VISIBLE);
        imgRecord.setImageDrawable(getResources().getDrawable(R.drawable.ic_buy_record));
    }

    @Override
    public void initData() {
        getDataWithBuyRecord(mStartStamp, mEndStamp);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDataWithBuyRecord(mStartStamp, mEndStamp);
    }

    private void initRecyclerView() {
        rvBuy.setLinearLayout();
        mAdapter = new BuyRecordAdapter(this, mList);
        rvBuy.setAdapter(mAdapter);
        rvBuy.setPushRefreshEnable(false);
        rvBuy.setOnPullLoadMoreListener(new PullLoadMoreRecyclerView.PullLoadMoreListener() {
            @Override
            public void onRefresh() {
                getDataWithBuyRecord(mStartStamp, mEndStamp);
            }

            @Override
            public void onLoadMore() {
            }
        });
        rvBuy.setPullLoadMoreCompleted();
    }

    @SuppressWarnings("unchecked")
    private void getDataWithBuyRecord(String startStamp, String endStamp) {
        OkGoBuilder.getInstance().setToken(MainActivity.getAppCtrl().getToken());
        HttpParams params = new HttpParams();
        params.put("start", startStamp);
        params.put("end", endStamp);

        OkGoBuilder.getInstance().Builder(this)
                .url(Urls.SALES_RECORD)
                .method(OkGoBuilder.POST)
                .params(params)
                .cls(BuyRecordBean.class)
                .callback(new Callback<BuyRecordBean>() {
                    @Override
                    public void onSuccess(BuyRecordBean bean, int id) {
                        LogUtil.d(TAG, "销售记录 请求成功");

                        switch (bean.getCode()) {
                            case 1:
                                rvBuy.setVisibility(View.VISIBLE);
                                tvNotPay.setVisibility(View.GONE);
                                mList = bean.getData();
                                // 刷新数据
                                mAdapter.refreshList(mList);
                                // 刷新结束
                                rvBuy.setPullLoadMoreCompleted();
                                break;
                            case 0:
                                rvBuy.setVisibility(View.GONE);
                                tvNotPay.setVisibility(View.VISIBLE);
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

    @OnClick(R.id.img_vip_toolbar)
    public void onViewClicked() {
        dialogTime();
    }

    private void dialogTime() {
        final WheelView wvStartYear, wvStartMonth, wvStartDay, wvEndYear, wvEndMonth, wvEndDay;
        TextView tvOk, tvCancel;

        WheelView.WheelViewStyle style = new WheelView.WheelViewStyle();
        // 选中字体颜色
        style.selectedTextColor = Color.parseColor("#333333");
        // 未选中字体颜色
        style.textColor = Color.parseColor("#B9B9B9");

        @SuppressLint("InflateParams")
        View outView = LayoutInflater.from(this).inflate(R.layout.layout_bottom_dialog, null);

        // 日期滚轮
        wvStartYear = outView.findViewById(R.id.start_year);
        wvStartMonth = outView.findViewById(R.id.start_month);
        wvStartDay = outView.findViewById(R.id.start_day);
        wvEndYear = outView.findViewById(R.id.end_year);
        wvEndMonth = outView.findViewById(R.id.end_month);
        wvEndDay = outView.findViewById(R.id.end_day);
        tvOk = outView.findViewById(R.id.tv_ok);
        tvCancel = outView.findViewById(R.id.tv_cancel);

        wvStartYear.setStyle(style);
        wvStartMonth.setStyle(style);
        wvStartDay.setStyle(style);
        wvEndYear.setStyle(style);
        wvEndMonth.setStyle(style);
        wvEndDay.setStyle(style);

        // 格式化当前时间，并转换为年月日整型数据
        SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YMD, Locale.getDefault());
        String[] split = sdf.format(new Date()).split("-");
        int currentYear = Integer.parseInt(split[0]);
        int currentMonth = Integer.parseInt(split[1]);
        int currentDay = Integer.parseInt(split[2]);

        // 开始时间
        wvStartYear.setWheelAdapter(new MyWheelAdapter(this));
        wvStartYear.setWheelData(getYearData(currentYear));
        wvStartYear.setSelection(0);
        wvStartMonth.setWheelAdapter(new MyWheelAdapter(this));
        wvStartMonth.setWheelData(getMonthData());
        wvStartMonth.setSelection(currentMonth - 1);
        wvStartDay.setWheelAdapter(new MyWheelAdapter(this));
        wvStartDay.setWheelData(getDayData(getLastDay(currentYear, currentMonth)));
        wvStartDay.setSelection(currentDay - 1);

        // 结束时间
        wvEndYear.setWheelAdapter(new MyWheelAdapter(this));
        wvEndYear.setWheelData(getYearData(currentYear));
        wvEndYear.setSelection(0);
        wvEndMonth.setWheelAdapter(new MyWheelAdapter(this));
        wvEndMonth.setWheelData(getMonthData());
        wvEndMonth.setSelection(currentMonth - 1);
        wvEndDay.setWheelAdapter(new MyWheelAdapter(this));
        wvEndDay.setWheelData(getDayData(getLastDay(currentYear, currentMonth)));
        wvEndDay.setSelection(currentDay - 1);

        // 确定
        tvOk.setOnClickListener(v -> {
            mBottomDialog.dismiss();
            Object selectStartYear, selectStartMonth, selectStartDay, selectEndYear, selectEndMonth, selectEndDay;
            String strStartTime, strEndTime, strStartMonth, strEndMonth, strStartDay, strEndDay;
            int niStartYear, niStartMonth, niStartDay, niEndYear, niEndMonth, niEndDay;

            selectStartYear = wvStartYear.getSelectionItem();
            selectStartMonth = wvStartMonth.getSelectionItem();
            selectStartDay = wvStartDay.getSelectionItem();
            selectEndYear = wvEndYear.getSelectionItem();
            selectEndMonth = wvEndMonth.getSelectionItem();
            selectEndDay = wvEndDay.getSelectionItem();

            niStartYear = Integer.valueOf(String.valueOf(selectStartYear));
            niStartMonth = Integer.valueOf(String.valueOf(selectStartMonth));
            niStartDay = Integer.valueOf(String.valueOf(selectStartDay));
            niEndYear = Integer.valueOf(String.valueOf(selectEndYear));
            niEndMonth = Integer.valueOf(String.valueOf(selectEndMonth));
            niEndDay = Integer.valueOf(String.valueOf(selectEndDay));

            if (niStartMonth < 10) {
                strStartMonth = "0" + String.valueOf(niStartMonth);
            } else {
                strStartMonth = String.valueOf(niStartMonth);
            }
            if (niStartDay < 10) {
                strStartDay = "0" + String.valueOf(niStartDay);
            } else {
                strStartDay = String.valueOf(niStartDay);
            }
            if (niEndMonth < 10) {
                strEndMonth = "0" + String.valueOf(niEndMonth);
            } else {
                strEndMonth = String.valueOf(niEndMonth);
            }
            if (niEndDay < 10) {
                strEndDay = "0" + String.valueOf(niEndDay);
            } else {
                strEndDay = String.valueOf(niEndDay);
            }

            if (niEndYear >= niStartYear) {
                if (niEndMonth >= niStartMonth) {
                    if (niEndDay >= niStartDay) {
                        strStartTime = niStartYear + "-" + strStartMonth + "-" + strStartDay + " 00:00:00";
                        if (niEndYear == niStartYear && niEndMonth == niStartMonth && niEndDay == niStartDay) {
                            strEndTime = niEndYear + "-" + strEndMonth + "-" + strEndDay + " 23:59:59";
                        } else {
                            strEndTime = niEndYear + "-" + strEndMonth + "-" + strEndDay + " " + getCurDate(FORMAT_HMS);
                        }
                        mStartStamp = date2TimeStamp(strStartTime, FORMAT_ALL);
                        mEndStamp = date2TimeStamp(strEndTime, FORMAT_ALL);
                        getDataWithBuyRecord(mStartStamp, mEndStamp);
                    } else {
                        ToastUtil.showShort("请输入正确的日期");
                    }
                } else {
                    ToastUtil.showShort("请输入正确的月份");
                }
            } else {
                ToastUtil.showShort("请输入正确的年份");
            }
        });

        // 取消
        tvCancel.setOnClickListener(v -> mBottomDialog.dismiss());

        // 防止弹出两个窗口
        if (mBottomDialog != null && mBottomDialog.isShowing()) {return;}
        mBottomDialog = new BottomDialog(this, R.style.ActionSheetDialogStyle);
        // 将布局设置给 Dialog
        mBottomDialog.setContentView(outView);
        // 显示对话框
        mBottomDialog.show();
    }

    private ArrayList<String> getYearData(int currentYear) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = currentYear; i >= 1900; i--) {
            list.add(String.valueOf(i));
        }
        return list;
    }

    private ArrayList<String> getMonthData() {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            list.add(String.valueOf(i));
        }
        return list;
    }

    private ArrayList<String> getDayData(int lastDay) {
        ArrayList<String> list = new ArrayList<>();
        for (int i = 1; i <= lastDay; i++) {
            list.add(String.valueOf(i));
        }
        return list;
    }

}
