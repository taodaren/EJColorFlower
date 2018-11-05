package cn.eejing.colorflower.util;

import android.support.v7.widget.RecyclerView;

import cn.eejing.colorflower.presenter.IShowListener;

/**
 * 计算列表滚动的高度，根据此类去判断显示还是隐藏
 */

public class FabScrollListener extends RecyclerView.OnScrollListener {
    private IShowListener mListener;
    private static final int THRESHOLD = 20;
    private int distance = 0;
    private boolean visible = true;

    public FabScrollListener(IShowListener listener) {
        mListener = listener;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (distance > THRESHOLD && visible) {
            visible = false;
            mListener.setHideListener();
            distance = 0;
        } else if (distance < -20 && !visible) {
            visible = true;
            mListener.setShowListener();
            distance = 0;
        }

        if (visible && dy > 0 || (!visible && dy < 0)) {
            distance += dy;
        }
    }
}
