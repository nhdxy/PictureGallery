package com.anhdxy.picturegallery.adapter

import android.app.Activity
import android.content.Context
import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.anhdxy.picturegallery.R
import com.bumptech.glide.Glide

/**
 * Created by Andrnhd on 2018/3/9.
 */
class ChildPicturesAdapter(context: Activity, mDatas: ArrayList<String>, isSingle: Boolean) : RecyclerView.Adapter<ChildPicturesAdapter.ChildPicturesViewHolder>() {
    private var context: Context = context
    private var mDatas: ArrayList<String> = mDatas
    private var isSingle: Boolean = isSingle
    private var listener: ((Int) -> Unit)? = null
    private var listener2: ((Int) -> Unit)? = null
    private var width = 0

    constructor(context: Activity, mDatas: ArrayList<String>) : this(context, mDatas, false)

    init {
        width = context.resources.displayMetrics.widthPixels
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChildPicturesViewHolder {
        return ChildPicturesViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recycl_picture, parent, false))
    }

    override fun getItemCount(): Int = mDatas.size

    override
    fun onBindViewHolder(holder: ChildPicturesViewHolder, position: Int) {
        Glide.with(context).load(mDatas[position]).centerCrop().into(holder.iv_picture)
        if (isSingle) {
            holder.iv_add.visibility = View.GONE
        } else {
            holder.iv_add.visibility = View.VISIBLE
            holder.iv_add.setOnClickListener { listener?.invoke(position) }
        }
        val width_and_height = (width.toFloat() / 3).toInt()
        holder.iv_picture.layoutParams = ConstraintLayout.LayoutParams(width_and_height, width_and_height)
        holder.itemView.setOnClickListener { listener2?.invoke(position) }
    }

    inner class ChildPicturesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_picture: ImageView = itemView.findViewById(R.id.iv_picture)
        var iv_add: ImageView = itemView.findViewById(R.id.iv_add)
    }

    fun setOnItemAddListener(listener: (Int) -> Unit) {
        this.listener = listener
    }

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        listener2 = listener
    }
}