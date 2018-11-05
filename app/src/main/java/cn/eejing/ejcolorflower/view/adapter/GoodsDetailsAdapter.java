package cn.eejing.ejcolorflower.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.bingoogolapple.bgabanner.BGABanner;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;
import cn.eejing.ejcolorflower.model.request.GoodsDetailsBean;
import cn.eejing.ejcolorflower.util.LogUtil;

public class GoodsDetailsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_BANNER = 0;
    private static final int TYPE_LAYOUT = 1;
    private static final int TYPE_WEB_VIEW = 2;

    private Context mContext;
    private LayoutInflater mInflater;
    private List<GoodsDetailsBean.DataBean> mList;

    public GoodsDetailsAdapter(Context context, List<GoodsDetailsBean.DataBean> list) {
        this.mContext = context;
        this.mInflater = LayoutInflater.from(mContext);
        this.mList = new ArrayList<>();
        this.mList.addAll(list);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_BANNER:
                return new BannerViewHolder(mInflater.inflate(R.layout.type_goods_banner, parent, false));
            case TYPE_LAYOUT:
                return new LayoutViewHolder(mInflater.inflate(R.layout.type_goods_layout, parent, false));
            case TYPE_WEB_VIEW:
                return new WebViewHolder(mInflater.inflate(R.layout.type_goods_web, parent, false));
            default:
                LogUtil.e(AppConstant.TAG, "onCreateViewHolder: is null");
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (getItemViewType(position)) {
            case TYPE_BANNER:
                ((BannerViewHolder) holder).setData(mList.get(0));
                break;
            case TYPE_LAYOUT:
                ((LayoutViewHolder) holder).setData(mList.get(0));
                break;
            case TYPE_WEB_VIEW:
                ((WebViewHolder) holder).setData(mList.get(0));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mList.size() + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_BANNER;
        } else if (position == getItemCount() - 1) {
            return TYPE_WEB_VIEW;
        } else {
            return TYPE_LAYOUT;
        }
    }

    class BannerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.banner_goods_dtl)        BGABanner bgaBanner;

        BannerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(GoodsDetailsBean.DataBean bean) {
            /* 设置是否开启自动轮播，需要在 setDataConn 方法之前调用，并且调了该方法后必须再调用一次 setDataConn 方法
               例如根据图片当图片数量大于 1 时开启自动轮播，等于 1 时不开启自动轮播 */
            bgaBanner.setAutoPlayAble(bean.getImage().size() > 1);

            bgaBanner.setAdapter((BGABanner.Adapter<ImageView, String>) (banner, itemView, model, position) -> Glide.with(mContext)
                    .load(model)
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.shape_add_effect)
                            .error(R.drawable.shape_add_effect)
                            .dontAnimate()
                            .centerCrop())
                    .into(itemView));
            bgaBanner.setData(bean.getImage(), null);

//            // 监听 banner 点击事件
//            bgaBanner.setDelegate((banner, itemView, model, position) -> Toast.makeText(banner.getContext(), "click " + position, Toast.LENGTH_SHORT).show());
        }

    }

    class LayoutViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.tv_name)                  TextView tvName;
        @BindView(R.id.tv_money)                 TextView tvMoney;
        @BindView(R.id.tv_stock)                 TextView tvStock;
        @BindView(R.id.tv_basics_postage)        TextView tvBasicsPostage;
        @BindView(R.id.tv_postage)               TextView tvPostage;
        @BindView(R.id.tv_sold)                  TextView tvSold;

        LayoutViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("SetTextI18n")
        public void setData(GoodsDetailsBean.DataBean bean) {
            tvName.setText(bean.getName());
            tvMoney.setText(mContext.getResources().getString(R.string.rmb) + bean.getMoney());
            tvBasicsPostage.setText(mContext.getResources().getString(R.string.basic_postage) + bean.getBasics_postage() + mContext.getResources().getString(R.string.yuan));
            tvPostage.setText(mContext.getResources().getString(R.string.postage_before) + bean.getPostage() + mContext.getResources().getString(R.string.postage_after));
            tvSold.setText(mContext.getResources().getString(R.string.sold) + bean.getSold());
            if (bean.getStock() == 1) {
                tvStock.setText(mContext.getResources().getString(R.string.stock_have));
            } else if (bean.getStock() == 0) {
                tvStock.setText(mContext.getResources().getString(R.string.stock_no));
            }
        }

        @Override
        public void onClick(View view) {

        }
    }

    class WebViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.web_goods_dtl)        WebView webGoodsDtl;

        WebViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("SetJavaScriptEnabled")
        public void setData(GoodsDetailsBean.DataBean bean) {
            webGoodsDtl.loadUrl(bean.getUrl());

            WebSettings webSettings = webGoodsDtl.getSettings();
            // 5.0 以上开启混合模式加载
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
            webSettings.setLoadWithOverviewMode(true);
            webSettings.setUseWideViewPort(true);
            // 允许 js 代码
            webSettings.setJavaScriptEnabled(true);
            // 允许 SessionStorage/LocalStorage 存储
            webSettings.setDomStorageEnabled(true);
            // 禁用放缩
            webSettings.setDisplayZoomControls(false);
            webSettings.setBuiltInZoomControls(false);
            // 禁用文字缩放
            webSettings.setTextZoom(100);
            // 10M 缓存，api 18 后，系统自动管理。
            webSettings.setAppCacheMaxSize(10 * 1024 * 1024);
            // 允许缓存，设置缓存位置
            webSettings.setAppCacheEnabled(true);
            webSettings.setAppCachePath(mContext.getDir("appcache", 0).getPath());
            // 允许 WebView 使用 File 协议
            webSettings.setAllowFileAccess(true);
            // 不保存密码
            webSettings.setSavePassword(false);
            // 自动加载图片
            webSettings.setLoadsImagesAutomatically(true);
        }

        @Override
        public void onClick(View view) {

        }
    }

}
