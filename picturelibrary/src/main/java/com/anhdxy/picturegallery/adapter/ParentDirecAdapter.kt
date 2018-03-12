package com.anhdxy.picturegallery.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.anhdxy.picturegallery.R
import com.anhdxy.picturegallery.bean.ParentDirecBean
import com.bumptech.glide.Glide

/**
 * Created by Andrnhd on 2018/3/9.
 */
class ParentDirecAdapter(context: Context, mDatas: ArrayList<ParentDirecBean>) : RecyclerView.Adapter<ParentDirecAdapter.ParentDirecViewHolder>() {
    private var mDatas: ArrayList<ParentDirecBean> = mDatas
    private var context: Context = context
    private var listener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ParentDirecViewHolder {
        return ParentDirecViewHolder(LayoutInflater.from(context).inflate(R.layout.item_recycl_parent, parent, false))
    }

    override fun getItemCount(): Int = mDatas.size

    override fun onBindViewHolder(holder: ParentDirecAdapter.ParentDirecViewHolder, position: Int) {
        val item = mDatas[position]
        Glide.with(context).load(item.firstPicture).centerCrop().into(holder.iv_picture)
        holder.tv_name.text = item.parentName
        holder.tv_picture_size.text = "${item.pictureSize}"
        holder.itemView.setOnClickListener { listener?.invoke(position) }
    }

    inner class ParentDirecViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_picture: ImageView = itemView.findViewById(R.id.iv_picture)
        var tv_name: TextView = itemView.findViewById(R.id.tv_name)
        var tv_picture_size: TextView = itemView.findViewById(R.id.tv_picture_size)
    }

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        this.listener = listener
    }
}