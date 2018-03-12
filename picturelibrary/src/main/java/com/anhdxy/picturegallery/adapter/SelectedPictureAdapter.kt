package com.anhdxy.picturegallery.adapter

import android.content.Context
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
class SelectedPictureAdapter(context: Context, mDatas: ArrayList<String>) : RecyclerView.Adapter<SelectedPictureAdapter.SelectedPictureViewHolder>() {
    private var context: Context = context
    private var mDatas: ArrayList<String> = mDatas
    private var listener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedPictureViewHolder {
        return SelectedPictureViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recycl_image, parent, false))
    }

    override fun getItemCount(): Int = mDatas.size

    override fun onBindViewHolder(holder: SelectedPictureViewHolder, position: Int) {
        Glide.with(context).load(mDatas[position]).centerCrop().into(holder.iv_image)
        holder.itemView.setOnClickListener { listener?.invoke(position) }
    }

    fun refresh(datas: ArrayList<String>): SelectedPictureAdapter {
        this.mDatas = datas
        return this
    }

    inner class SelectedPictureViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_image = itemView.findViewById<ImageView>(R.id.iv_image)
    }

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        this.listener = listener
    }
}