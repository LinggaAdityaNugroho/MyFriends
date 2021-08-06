package com.example.myfriends

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.anggapambudi.myfriend.BitmapConverter
import com.bumptech.glide.Glide
import com.example.myfriends.databinding.ItemRvNewFriendBinding
import kotlinx.android.synthetic.main.item_rv_new_friend.view.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class FriendAdapter(
    private val context: Context,
    private val item: ArrayList<FriendModel>
) : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    class FriendViewHolder(binding: ItemRvNewFriendBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ItemRvNewFriendBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friendModel = item[position]
        var photo = ""
        photo = friendModel.photoProfile
        val bitmap = BitmapConverter().stringToBitmap(context, photo)

        holder.itemView.tvName.text = friendModel.name
        holder.itemView.tvSchool.text = friendModel.school
        holder.itemView.tvGithub.text = friendModel.github
        holder.itemView.imgPhoto.setImageBitmap(bitmap)

        holder.itemView.onClick {
            val moveRead = Intent(holder.itemView.context, DetailActivity::class.java)
                .putExtra("KEY_ID", friendModel.id)
                .putExtra("KEY_NAME", friendModel.name)
                .putExtra("KEY_SCHOOL", friendModel.school)
                .putExtra("KEY_GITHUB", friendModel.github)
                .putExtra("KEY_IMG", photo)
            holder.itemView.context.startActivity(moveRead)
        }
    }

    override fun getItemCount(): Int {
        //return item.size
        return Math.min(item.size, 5)
    }

    fun setData(data: List<FriendModel>) {
        item.clear()
        item.addAll(data)
        notifyDataSetChanged()
    }
}