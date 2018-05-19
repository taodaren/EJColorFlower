package cn.eejing.ejcolorflower.ui.adapter;

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

    public CoDeviceRightAdapter(Context context, List<String> strings) {
        this.mContext = context;
        this.mRightList = new ArrayList<>();
        this.mRightList.addAll(strings);
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = mLayoutInflater.inflate(R.layout.item_device_added, parent, false);
        return new RightViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RightViewHolder) holder).setData(mRightList.get(position));
    }

    @Override
    public int getItemCount() {
        return mRightList.size();
    }


    class RightViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sb_device_added)
        SuperButton sbAddedCan;

        public RightViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(String rightData) {
            sbAddedCan.setText(rightData);
        }
    }
}
