package cn.eejing.ejcolorflower.util;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ViewGroup;

import cn.eejing.ejcolorflower.presenter.ItemTouchHelperAdapter;

/**
 * 用于 RecyclerView 滑动删除、拖动
 */
public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private ItemTouchHelperAdapter mAdapter;

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                            float dX, float dY, int actionState, boolean isCurrentlyActive) {
        // 仅对侧滑状态下的效果做出改变
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // 如果 dX 小于等于删除方块的宽度，那么我们把该方块滑出来
            if (Math.abs(dX) <= getSlideLimitation(viewHolder)) {
                viewHolder.itemView.scrollTo(-(int) dX, 0);
            }
        } else {
            // 拖拽状态下不做改变，需要调用父类的方法
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
    }

    // 该方法用于返回可以滑动的方向
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        // 允许上下的拖动
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        // 只允许从右向左侧滑
        int swipeFlags = ItemTouchHelper.LEFT;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    // 该方法返回 true 时，表示支持长按拖动
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    // 该方法返回 true 时，表示如果用户触摸并左右滑动了 View，那么可以执行滑动删除操作，即可以调用到 onSwiped()方法。默认是返回 true
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    // 当用户拖动一个 Item 进行上下移动从旧的位置到新的位置的时候会调用该方法
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        // onItemMove 是接口方法 数据删除
//        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    // 当用户左右滑动 Item 达到删除条件时，会调用该方法
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        // onItemDismiss 是接口方法 数据交换
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    /**
     * 获取删除方块的宽度
     */
    public int getSlideLimitation(RecyclerView.ViewHolder viewHolder) {
        ViewGroup viewGroup = (ViewGroup) viewHolder.itemView;
        return viewGroup.getChildAt(1).getLayoutParams().width;
    }


}
