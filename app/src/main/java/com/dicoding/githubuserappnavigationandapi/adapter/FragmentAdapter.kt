package com.dicoding.githubuserappnavigationandapi.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.dicoding.githubuserappnavigationandapi.DiffUtilCallback
import com.dicoding.githubuserappnavigationandapi.GlideApp
import com.dicoding.githubuserappnavigationandapi.R
import com.dicoding.githubuserappnavigationandapi.databinding.LayoutFollowBinding
import com.dicoding.githubuserappnavigationandapi.response.UserItem
import jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation

class FragmentAdapter :
    RecyclerView.Adapter<FragmentAdapter.ListViewHolder>() {
    private var listUser: ArrayList<UserItem> = ArrayList()
    private lateinit var onItemClickCallback: OnItemClickCallback

    fun addFollow(data: ArrayList<UserItem>) {
        val diffCallback = DiffUtilCallback(listUser, data)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        listUser.clear()
        listUser.addAll(data)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    interface OnItemClickCallback {
        fun onItemClicked(user: UserItem)
    }

    class ListViewHolder(var binding: LayoutFollowBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListViewHolder {
        val binding =
            LayoutFollowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListViewHolder, position: Int) {
        val image = listUser[position].avatarUrl
        holder.binding.rvUsername.text = listUser[position].login

        val color = ContextCompat.getColor(holder.itemView.context, R.color.github_orange)

        val circularProgressDrawable = CircularProgressDrawable(holder.itemView.context)
        circularProgressDrawable.setColorSchemeColors(color)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 15f
        circularProgressDrawable.start()

        GlideApp
            .with(holder.itemView.context)
            .load(image)
            .placeholder(circularProgressDrawable)
            .transform(CropCircleWithBorderTransformation(5, color))
            .into(holder.binding.rvProfileImage)

        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listUser[holder.adapterPosition])
        }
    }

    override fun getItemCount(): Int = listUser.size
}