package com.enabot.mylibrary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * Created by zmp
 * Date: 2021/1/29 9:51
 * des: BaseRecycleViewListAdapter
 */
abstract class BaseRecycleViewListAdapter<V : ViewBinding, E>(
    var context: FragmentActivity, diff: DiffUtil.ItemCallback<E>
) : ListAdapter<E, BaseRecycleViewListAdapter.MyViewHolder<V>>(diff) {

    private val layoutInflater: LayoutInflater by lazy {
        LayoutInflater.from(context)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder<V> {
        val item = createItemView(layoutInflater, parent, viewType)
        return MyViewHolder(item)
    }

    override fun onBindViewHolder(holder: MyViewHolder<V>, position: Int) {
        bindItemView(holder.mViewBinding, holder, getItem(position))
    }


    override fun submitList(list: MutableList<E>?) {
        super.submitList(list?.toList())
    }

    fun getItemMode(position: Int): E {
        return super.getItem(position)
    }

    abstract fun createItemView(layoutInflater: LayoutInflater, parent: ViewGroup, viewType: Int): V

    abstract fun bindItemView(itemView: V, holder: MyViewHolder<V>, model: E)


    class MyViewHolder<V : ViewBinding>(val mViewBinding: V) : RecyclerView.ViewHolder(mViewBinding.root)
}