package com.example.bigwork.view;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bigwork.inter.onMoveAndSwipedListener;

public class ItemTouchHelperCallback extends ItemTouchHelper.Callback {


    private onMoveAndSwipedListener moveAndSwipedListener;
    public ItemTouchHelperCallback(onMoveAndSwipedListener listener) {
        moveAndSwipedListener = listener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
//        final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(0,swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {

        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        moveAndSwipedListener.onItemDismiss(viewHolder.getAdapterPosition());
    }
}