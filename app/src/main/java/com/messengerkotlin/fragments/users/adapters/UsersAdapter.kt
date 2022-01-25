package com.messengerkotlin.fragments.users.adapters


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.messengerkotlin.R
import com.messengerkotlin.databinding.UserItemBinding
import com.messengerkotlin.models.UserModel

class UsersAdapter(var onItemTouched: (String) -> Unit) :
    ListAdapter<UserModel, UsersAdapter.UserViewHolder>(DiffCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder(
            UserItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), onItemTouched
        )
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class DiffCallback : DiffUtil.ItemCallback<UserModel>() {
        override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
            return oldItem.username == newItem.username && oldItem.imageurl == newItem.imageurl
        }
    }


    class UserViewHolder(var binding: UserItemBinding, var onItemTouched: (String) -> Unit) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(userModel: UserModel) {
            Glide.with(itemView)
                .load(userModel.imageurl ?: R.mipmap.ic_launcher)
                .centerCrop()
                .circleCrop()
                .into(binding.userPhoto)

            binding.name.text = userModel.username
            //binding.lastMessage.text = userModel.lastMessage

            itemView.setOnClickListener {
                onItemTouched(userModel.id)
            }
        }
    }
}