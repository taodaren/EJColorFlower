package cn.eejing.ejcolorflower.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.model.lite.MasterCtrlModeEntity;
import cn.eejing.ejcolorflower.view.activity.CoConfigIntervalActivity;
import cn.eejing.ejcolorflower.view.activity.CoConfigRideActivity;
import cn.eejing.ejcolorflower.view.activity.CoConfigStreamActivity;
import cn.eejing.ejcolorflower.view.activity.CoConfigTogetherActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_INTERVAL;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_RIDE;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_STOP;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_STREAM;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_TOGETHER;

/**
 * 主控模式适配器
 */

public class CtMasterSetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<MasterCtrlModeEntity> mList;

    private View.OnLongClickListener mLongClickListener;

    public void setLongClickListener(View.OnLongClickListener listener) {
        mLongClickListener = listener;
    }

    public CtMasterSetAdapter(Context context, List<MasterCtrlModeEntity> list) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mList = new ArrayList<>();
        this.mList.addAll(list);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MasterModeHolder(mLayoutInflater.inflate(R.layout.item_jet_mode, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MasterModeHolder) holder).setData(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void refreshList(List<MasterCtrlModeEntity> list) {
        if (list != null) {
            mList.clear();
            addList(list);
        }
    }

    private void addList(List<MasterCtrlModeEntity> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    class MasterModeHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_master_jet_mode)        ImageView      imgJetMode;

        MasterModeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(int position) {
                Log.i("CtSetGroupActivity", "setData: " + mList.get(position).getType());
                switch (mList.get(position).getType()) {
                    case CONFIG_STREAM:
                        imgJetMode.setImageResource(R.drawable.ic_jet_mode_stream);
                        break;
                    case CONFIG_RIDE:
                        imgJetMode.setImageResource(R.drawable.ic_jet_mode_ride);
                        break;
                    case CONFIG_INTERVAL:
                        imgJetMode.setImageResource(R.drawable.ic_jet_mode_interval);
                        break;
                    case CONFIG_TOGETHER:
                        imgJetMode.setImageResource(R.drawable.ic_jet_mode_together);
                        break;
                    case CONFIG_STOP:
                        imgJetMode.setImageResource(R.drawable.ic_jet_mode_stop);
                        break;
                }

            onClickModeJet();

            imgJetMode.setTag(getAdapterPosition());
            imgJetMode.setOnLongClickListener(mLongClickListener);
        }

        private void onClickModeJet() {
            imgJetMode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    long groupId = mList.get(getAdapterPosition()).getMillis();
                        switch (mList.get(getAdapterPosition()).getType()) {
                            case CONFIG_STREAM:
                                mContext.startActivity(new Intent(mContext, CoConfigStreamActivity.class).putExtra("group_id", groupId));
                                break;
                            case CONFIG_RIDE:
                                mContext.startActivity(new Intent(mContext, CoConfigRideActivity.class).putExtra("group_id", groupId));
                                break;
                            case CONFIG_INTERVAL:
                                mContext.startActivity(new Intent(mContext, CoConfigIntervalActivity.class).putExtra("group_id", groupId));
                                break;
                            case CONFIG_TOGETHER:
                                mContext.startActivity(new Intent(mContext, CoConfigTogetherActivity.class).putExtra("group_id", groupId));
                                break;
                            default:
                                break;
                        }
                }
            });
        }
    }

}
