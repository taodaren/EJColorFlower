package cn.eejing.ejcolorflower.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allen.library.SuperButton;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.eejing.ejcolorflower.R;

/**
 * @创建者 Taodaren
 * @描述
 */
public class CoDeviceLeftAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mInflater;
    private Context mContext;
    private List<String> mLeftList;

    private LeftClickListener mListener;

    public interface LeftClickListener {
        void onClickLeft(View view, int position);
    }

    public CoDeviceLeftAdapter(Context mContext, List<String> mLeftList, LeftClickListener listener) {
        this.mContext = mContext;
        this.mLeftList = new ArrayList<>();
        this.mLeftList.addAll(mLeftList);
        this.mInflater = LayoutInflater.from(mContext);
        this.mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = mInflater.inflate(R.layout.item_device_added, parent, false);
        return new LeftViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ((LeftViewHolder) holder).setData(mLeftList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mLeftList.size();
    }

    /**
     * 添加数据
     */
    public void addData(String deviceId) {
        // 在 list 中添加数据，并通知条目加入一条
        mLeftList.add(deviceId);
        notifyItemInserted(mLeftList.size() - 1);
    }

    /**
     * 删除数据
     */
    public void removeData(int position) {
        mLeftList.remove(position);
        // 删除动画
        notifyItemRemoved(position);
        if (position != mLeftList.size()) {
            notifyItemRangeChanged(position, mLeftList.size() - position);
        }
    }


    class LeftViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sb_device_added)
        SuperButton sbAddedCan;

        public LeftViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(String leftData, final int position) {
            sbAddedCan.setText(leftData);
            sbAddedCan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClickLeft(v, position);
                }
            });
        }

    }


}
