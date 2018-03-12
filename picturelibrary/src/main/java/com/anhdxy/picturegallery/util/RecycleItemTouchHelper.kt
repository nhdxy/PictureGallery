package com.anhdxy.picturegallery.util

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper

/**
 * Created by Andrnhd on 2018/3/9.
 */
class RecycleItemTouchHelper() : ItemTouchHelper.Callback() {
    private var listener: ((Int) -> Unit)? = null

    constructor(listener: (Int) -> Unit):this(){
        this.listener = listener
    }

    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(0,ItemTouchHelper.UP)
    }

    override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener?.invoke(viewHolder.adapterPosition)
    }

    override fun isLongPressDragEnabled(): Boolean {
        return false
    }

    override fun isItemViewSwipeEnabled(): Boolean {
        return true
    }
}