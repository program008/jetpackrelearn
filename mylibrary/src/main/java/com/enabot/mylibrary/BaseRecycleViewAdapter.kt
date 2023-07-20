package com.enabot.mylibrary

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * Created by zmp
 * Date: 2021/1/29 9:51
 * des: RecycleViewAdapter适配
 */
abstract class BaseRecycleViewAdapter<V : ViewBinding, E>(
    var context: Context,
    var mList: MutableList<E>
) : RecyclerView.Adapter<BaseRecycleViewAdapter.MyViewHolder<V>>() {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder<V> {
        val item = createItemView(layoutInflater, parent, viewType)
        return MyViewHolder(item)
    }

    override fun onBindViewHolder(holder: MyViewHolder<V>, position: Int) {
        bindItemView(holder.mViewBinding, holder, position)
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    abstract fun createItemView(layoutInflater: LayoutInflater, parent: ViewGroup, viewType: Int): V

    abstract fun bindItemView(itemView: V, holder: MyViewHolder<V>, position: Int)


    class MyViewHolder<V : ViewBinding>(val mViewBinding: V) :
        RecyclerView.ViewHolder(mViewBinding.root)
}