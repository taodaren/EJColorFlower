package cn.eejing.ejcolorflower.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.model.lite.JetModeConfigLite;
import cn.eejing.ejcolorflower.util.LogUtil;

import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_DELAY;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_INTERVAL;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_RIDE;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_STREAM;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_TOGETHER;

/**
 * 主控模式适配器
 */

public class CtMasterSetAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private List<JetModeConfigLite> mListJetModeCfg;

    private View.OnClickListener mClickListener;
    private View.OnLongClickListener mLongClickListener;

    public void setClickListener(View.OnClickListener clickListener) {
        this.mClickListener = clickListener;
    }

    public void setLongClickListener(View.OnLongClickListener listener) {
        this.mLongClickListener = listener;
    }

    public CtMasterSetAdapter(Context context, List<JetModeConfigLite> list) {
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mListJetModeCfg = new ArrayList<>();
        this.mListJetModeCfg.addAll(list);
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
        return mListJetModeCfg.size();
    }

    public void refreshList(List<JetModeConfigLite> list) {
        if (list != null) {
            mListJetModeCfg.clear();
            addList(list);
        }
    }

    private void addList(List<JetModeConfigLite> list) {
        mListJetModeCfg.addAll(list);
        notifyDataSetChanged();
    }

    class MasterModeHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_master_jet_mode)        ImageView      imgJetMode;

        MasterModeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(int position) {
                LogUtil.i("CtSetGroupActivity", "setData: " + mListJetModeCfg.get(position).getJetType());
                switch (mListJetModeCfg.get(position).getJetType()) {
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
                    case CONFIG_DELAY:
                        imgJetMode.setImageResource(R.drawable.ic_jet_mode_stop);
                        break;
                }

            imgJetMode.setTag(getAdapterPosition());
            imgJetMode.setOnLongClickListener(mLongClickListener);
            imgJetMode.setOnClickListener(mClickListener);
        }
    }

}
