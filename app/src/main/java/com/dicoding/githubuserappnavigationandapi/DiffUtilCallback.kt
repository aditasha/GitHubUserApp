package com.dicoding.githubuserappnavigationandapi

import androidx.recyclerview.widget.DiffUtil
import com.dicoding.githubuserappnavigationandapi.response.UserItem

class DiffUtilCallback(
    private val oldData: ArrayList<UserItem>,
    private val newData: ArrayList<UserItem>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldData.size
    }

    override fun getNewListSize(): Int {
        return newData.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldData[oldItemPosition].hashCode() == newData[newItemPosition].hashCode()
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldData[oldItemPosition].login == newData[newItemPosition].login
    }
}