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
public class CoDeviceRightAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<String> mRightList;

    private RightClickListener mListener;

    public interface RightClickListener {
        void onClickRight(View view, int position);
    }

    public CoDeviceRightAdapter(Context context, List<String> strings, RightClickListener listener) {
        this.mContext = context;
        this.mRightList = new ArrayList<>();
        this.mRightList.addAll(strings);
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mListener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = mLayoutInflater.inflate(R.layout.item_device_added, parent, false);
        return new RightViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ((RightViewHolder) holder).setData(mRightList.get(position), position);
    }

    @Override
    public int getItemCount() {
        return mRightList.size();
    }

    /**
     * 添加数据
     */
    public void addData(String deviceId) {
        String actionID = deviceId;
        // 在 list 中添加数据，并通知条目加入一条
        mRightList.add(actionID);
        notifyItemInserted(mRightList.size() - 1);
    }

    /**
     * 删除数据
     */
    public void removeData(int position) {
        mRightList.remove(position);
        notifyItemRemoved(position);
        // 删除动画
        if (position != mRightList.size()) {
            notifyItemRangeChanged(position, mRightList.size() - position);
        }
    }


    class RightViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sb_device_added)
        SuperButton sbAddedCan;

        public RightViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(String rightData, final int position) {
            sbAddedCan.setText(rightData);
            sbAddedCan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onClickRight(v, position);
                }
            });
        }
    }

}
