package cn.eejing.ejcolorflower.ui.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import cn.eejing.ejcolorflower.app.AppConstant;

/**
 * 添加、移除设备 适配器
 */
public class CoDeviceAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mInflater;
    private Context mContext;
    private List<String> mLeftList, mRightList;

    public CoDeviceAdapter(Context mContext, List<String> mLeftList, List<String> mRightList) {
        Log.e(AppConstant.TAG, "CoDeviceAdapter mLeftList: " + mLeftList);
        Log.e(AppConstant.TAG, "CoDeviceAdapter mRightList: " + mRightList);
        this.mContext = mContext;
        this.mLeftList = mLeftList;
        this.mRightList = mRightList;
        this.mInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class LeftViewHolder extends RecyclerView.ViewHolder {

        public LeftViewHolder(View itemView) {
            super(itemView);
        }
    }

    class RightViewHolder extends RecyclerView.ViewHolder {

        public RightViewHolder(View itemView) {
            super(itemView);
        }
    }

}
