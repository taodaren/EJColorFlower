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
import android.widget.TextView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.model.lite.MasterCtrlModeEntity;
import cn.eejing.ejcolorflower.util.SelfDialogBase;
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

public class DeMasterModeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<MasterCtrlModeEntity> mList;
    private boolean isClickItem = true;

    public DeMasterModeAdapter(Context context, List<MasterCtrlModeEntity> list) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mList = new ArrayList<>();
        this.mList.addAll(list);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MasterModeHolder(mLayoutInflater.inflate(R.layout.item_jet_effect, parent, false));
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

    public void isClickItem(boolean isClick) {
        isClickItem = isClick;
        notifyDataSetChanged();
    }

    class MasterModeHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.layout_mode_jet)            LinearLayout layoutJet;
        @BindView(R.id.img_mode_jet_effect)        ImageView imgJetEffect;
        @BindView(R.id.tv_mode_jet_effect)         TextView textJetEffect;

        SelfDialogBase dialog;

        MasterModeHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        private void onLongClickRemove(final MasterCtrlModeEntity bean) {
            if (isClickItem) {
                layoutJet.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showDialog(bean);
                        return true;
                    }
                });
            } else {
                layoutJet.setOnLongClickListener(null);
            }
        }

        private void onClickModeJet(final MasterCtrlModeEntity bean) {
            if (isClickItem) {
                layoutJet.setOnClickListener(new View.OnClickListener() {
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
            } else {
                layoutJet.setOnClickListener(null);
            }
        }

        public void setData(final MasterCtrlModeEntity bean) {
            textJetEffect.setText(bean.getType());
            switch (bean.getType()) {
                case CONFIG_STREAM:
                    imgJetEffect.setImageResource(R.drawable.ic_config_stream);
                    break;
                case CONFIG_RIDE:
                    imgJetEffect.setImageResource(R.drawable.ic_config_ride);
                    break;
                case CONFIG_INTERVAL:
                    imgJetEffect.setImageResource(R.drawable.ic_config_interval);
                    break;
                case CONFIG_TOGETHER:
                    imgJetEffect.setImageResource(R.drawable.ic_config_together);
                    break;
                default:
                    break;
            }

            onClickModeJet(bean);
            onLongClickRemove(bean);
        }

        private void showDialog(final MasterCtrlModeEntity bean) {
            dialog = new SelfDialogBase(mContext);
            dialog.setTitle("确定要删除");
            dialog.setYesOnclickListener("确定", new SelfDialogBase.onYesOnclickListener() {
                @Override
                public void onYesClick() {
                    // 删除喷射效果
                    LitePal.deleteAll(MasterCtrlModeEntity.class, "millis = ?", String.valueOf(mList.get(getAdapterPosition()).getMillis()));
                    mList.remove(getAdapterPosition());
                    notifyDataSetChanged();
                    dialog.dismiss();
                }
            });
            dialog.setNoOnclickListener("取消", new SelfDialogBase.onNoOnclickListener() {
                @Override
                public void onNoClick() {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }

}
