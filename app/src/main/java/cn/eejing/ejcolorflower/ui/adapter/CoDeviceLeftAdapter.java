package cn.eejing.ejcolorflower.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.allen.library.SuperButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.app.AppConstant;

/**
 * 添加、移除设备 适配器
 */
public class CoDeviceLeftAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mInflater;
    private Context mContext;
    private List<String> mLeftList;

    public CoDeviceLeftAdapter(Context mContext, List<String> mLeftList) {
        this.mContext = mContext;
        this.mLeftList = new ArrayList<>();
        this.mLeftList.addAll(mLeftList);
        this.mInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = mInflater.inflate(R.layout.item_device_added, parent, false);
        return new LeftViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.e(AppConstant.TAG, "onBindViewHolder mLeftList size ---> " + mLeftList.size());
        ((LeftViewHolder) holder).setData(mLeftList.get(position));
    }

    @Override
    public int getItemCount() {
        return mLeftList.size();
    }

    class LeftViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sb_device_added)
        SuperButton sbAddedCan;

        public LeftViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(String leftData) {
            Log.e(AppConstant.TAG, "setData: " + leftData);
            if (leftData != null && leftData.length() > 0) {
                sbAddedCan.setText(leftData);
            }
        }
    }


}
