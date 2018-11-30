package cn.eejing.colorflower.view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shuyu.gsyvideoplayer.video.StandardGSYVideoPlayer;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.eejing.colorflower.R;
import cn.eejing.colorflower.model.request.VideoListBean;

/**
 * 视频模块适配器
 */

public class TabVideoAdapter extends RecyclerView.Adapter<TabVideoAdapter.ViewHolder>{
    private static final String TAG = "TabVideoAdapter";
    private Context mContext;
    private List<VideoListBean.DataBean> mList;
    private LayoutInflater mLayoutInflater;
    private View.OnClickListener mOnClickListener;

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.mOnClickListener = onClickListener;
    }

    public TabVideoAdapter(Context context, List<VideoListBean.DataBean> list) {
        this.mContext = context;
        this.mList = list;
        this.mLayoutInflater = LayoutInflater.from(mContext);
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
        @BindView(R.id.detail_player)        StandardGSYVideoPlayer player;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @SuppressLint("SetTextI18n")
        public void setData(VideoListBean.DataBean bean) {
            // 增加title
            player.setUpLazy(bean.getUrl(), true, null, null, bean.getTitle());
            // 设置返回键
            player.getBackButton().setVisibility(View.GONE);
            // 设置全屏按键功能
            player.getFullscreenButton().setOnClickListener(v -> player.startWindowFullscreen(mContext, false, true));
            // 防止错位设置
            player.setPlayTag(TAG);
            player.setPlayPosition(getAdapterPosition());
            // 是否根据视频尺寸，自动选择竖屏全屏或者横屏全屏
            player.setAutoFullWithSize(true);
            // 音频焦点冲突时是否释放
            player.setReleaseWhenLossAudio(false);
            // 全屏动画
            player.setShowFullAnimation(true);
            // 小屏时不触摸滑动
            player.setIsTouchWiget(false);
        }
    }
}
