package cn.eejing.ejcolorflower.view.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
        ((MasterModeHolder) holder).setData(mList.get(position));
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

        private void onClickModeJet(final MasterCtrlModeEntity bean) {
            imgJetMode.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int millis = (int) mList.get(getAdapterPosition()).getMillis();

                    switch (bean.getType()) {
                        case CONFIG_STREAM:
                            mContext.startActivity(new Intent(mContext, CoConfigStreamActivity.class).putExtra("group_id", millis));
                            break;
                        case CONFIG_RIDE:
                            mContext.startActivity(new Intent(mContext, CoConfigRideActivity.class).putExtra("group_id", millis));
                            break;
                        case CONFIG_INTERVAL:
                            mContext.startActivity(new Intent(mContext, CoConfigIntervalActivity.class).putExtra("group_id", millis));
                            break;
                        case CONFIG_TOGETHER:
                            mContext.startActivity(new Intent(mContext, CoConfigTogetherActivity.class).putExtra("group_id", millis));
                            break;
                        default:
                            break;
                    }
                }
            });
        }

        public void setData(final MasterCtrlModeEntity bean) {
            switch (bean.getType()) {
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
                default:
                    break;
            }

            onClickModeJet(bean);

            imgJetMode.setTag(getAdapterPosition());
            imgJetMode.setOnLongClickListener(mLongClickListener);
        }
    }

}
