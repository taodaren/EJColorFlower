package cn.eejing.colorflower.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.request.VideoListBean;
import cn.eejing.colorflower.view.customize.MyJzvdStd;
import cn.jzvd.Jzvd;

/**
 * 视频模块适配器
 */

public class TabVideoAdapter extends RecyclerView.Adapter<TabVideoAdapter.ViewHolder> {
    private static final String TAG = "TabVideoAdapter";
    private List<VideoListBean.DataBean> mList;
    private LayoutInflater mLayoutInflater;

    public TabVideoAdapter(Context context, List<VideoListBean.DataBean> list) {
        this.mList = list;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(mLayoutInflater.inflate(R.layout.item_video_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void refreshList(List<VideoListBean.DataBean> list) {
        if (list != null) {
            mList.clear();
            addList(list);
        }
    }

    private void addList(List<VideoListBean.DataBean> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.detail_player)        MyJzvdStd jzPlayer;
        @BindView(R.id.tv_video_title)       TextView tvTitle;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(VideoListBean.DataBean bean) {
            tvTitle.setText(bean.getTitle());
            jzPlayer.setUp(bean.getUrl(), bean.getTitle(), Jzvd.SCREEN_WINDOW_LIST);
            Glide.with(jzPlayer.getContext()).load(bean.getThumbnail_url()).into(jzPlayer.thumbImageView);
            // 让缩略图撑满整个控件
            jzPlayer.thumbImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }
}
