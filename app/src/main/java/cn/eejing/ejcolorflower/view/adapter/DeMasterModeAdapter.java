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
import butterknife.OnClick;
import butterknife.OnLongClick;
import cn.eejing.ejcolorflower.R;
import cn.eejing.ejcolorflower.model.lite.MasterModeEntity;
import cn.eejing.ejcolorflower.model.request.AddMasterModeBean;
import cn.eejing.ejcolorflower.util.SelfDialogBase;
import cn.eejing.ejcolorflower.view.activity.CoConfigIntervalActivity;
import cn.eejing.ejcolorflower.view.activity.CoConfigRideActivity;
import cn.eejing.ejcolorflower.view.activity.CoConfigStreamActivity;
import cn.eejing.ejcolorflower.view.activity.CoConfigTogetherActivity;
import cn.eejing.ejcolorflower.view.activity.DeMasterModeActivity;

import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_INTERVAL;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_RIDE;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_STREAM;
import static cn.eejing.ejcolorflower.app.AppConstant.CONFIG_TOGETHER;

public class DeMasterModeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<AddMasterModeBean> mList;

    public DeMasterModeAdapter(Context context, List<AddMasterModeBean> list) {
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

    public void refreshList(List<AddMasterModeBean> list) {
        if (list != null) {
            mList.clear();
            addList(list);
        }
    }

    private void addList(List<AddMasterModeBean> list) {
        mList.addAll(list);
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

        private void onLongClickRemove(final AddMasterModeBean bean) {
            layoutJet.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    showDialog(bean.getMode());
                    return true;
                }
            });
        }

        private void onClickModeJet(final AddMasterModeBean bean) {
            layoutJet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (bean.getMode()) {
                        case CONFIG_STREAM:
                            mContext.startActivity(new Intent(mContext, CoConfigStreamActivity.class));
                            break;
                        case CONFIG_RIDE:
                            mContext.startActivity(new Intent(mContext, CoConfigRideActivity.class));
                            break;
                        case CONFIG_INTERVAL:
                            mContext.startActivity(new Intent(mContext, CoConfigIntervalActivity.class));
                            break;
                        case CONFIG_TOGETHER:
                            mContext.startActivity(new Intent(mContext, CoConfigTogetherActivity.class));
                            break;
                        default:
                            break;
                    }
                }
            });
        }

        public void setData(final AddMasterModeBean bean) {
            textJetEffect.setText(bean.getMode());
            switch (bean.getMode()) {
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

        private void showDialog(final String mode) {
            dialog = new SelfDialogBase(mContext);
            dialog.setTitle("确定要删除");
            dialog.setYesOnclickListener("确定", new SelfDialogBase.onYesOnclickListener() {
                @Override
                public void onYesClick() {
                    // 删除喷射效果
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
