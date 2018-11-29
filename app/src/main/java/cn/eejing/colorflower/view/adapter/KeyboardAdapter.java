package cn.eejing.colorflower.view.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;


import java.util.List;

import cn.eejing.colorflower.R;

/**
 * 支付键盘输入适配器
 */

public class KeyboardAdapter extends RecyclerView.Adapter<KeyboardAdapter.KeyboardHolder> {

    private Context mContext;
    private List<String> mList;
    private OnKeyboardClickListener mListener;

    public KeyboardAdapter(Context context, List<String> mList) {
        this.mContext = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public KeyboardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_keyboard, parent, false);
        KeyboardHolder holder = new KeyboardHolder(view);
        setListener(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull KeyboardHolder holder, int position) {
        switch (position) {
            default:
                holder.tvKey.setText(mList.get(position));
                break;
            case 9:
                holder.tvKey.setBackgroundResource(R.drawable.selector_item_del);
                break;
            case 11:
                holder.rlDel.setVisibility(View.VISIBLE);
                holder.tvKey.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    private void setListener(final KeyboardHolder holder) {
        holder.tvKey.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onKeyClick(view, holder, holder.getAdapterPosition());
            }
        });
        holder.rlDel.setOnClickListener(view -> {
            if (mListener != null) {
                mListener.onDeleteClick(view, holder, holder.getAdapterPosition());
            }
        });
    }

    class KeyboardHolder extends RecyclerView.ViewHolder {
        RelativeLayout rlDel;
        TextView tvKey;

        KeyboardHolder(View itemView) {
            super(itemView);
            tvKey = itemView.findViewById(R.id.tv_key);
            rlDel = itemView.findViewById(R.id.rl_del);
        }
    }

    public interface OnKeyboardClickListener {

        void onKeyClick(View view, RecyclerView.ViewHolder holder, int position);

        void onDeleteClick(View view, RecyclerView.ViewHolder holder, int position);
    }

    public void setOnKeyboardClickListener(OnKeyboardClickListener listener) {
        this.mListener = listener;
    }
}
