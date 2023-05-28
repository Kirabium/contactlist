package com.virgile.listuser.ui.contactlist


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.virgile.listuser.databinding.ItemBinding
import com.virgile.listuser.model.Contact

class ListRecyclerViewAdapter(private val fragment: ListOwner) :
    ListAdapter<Contact, ListRecyclerViewAdapter.ViewHolder>(ItemCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), fragment)
    }

    class ViewHolder(private val binding: ItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Contact, fragment: ListOwner) {
            binding.firstname.text = item.firstname
            binding.lastname.text = item.lastname
            binding.city.text = item.city

            binding.root.setOnClickListener {
                fragment.navigateToDetails(item)
            }

            Glide.with(binding.root)
                .load(item.picture)
                .into(binding.avatar)
        }
    }

    private class ItemCallback : DiffUtil.ItemCallback<Contact>() {
        override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem.firstname == newItem.firstname &&
                    oldItem.lastname == newItem.lastname &&
                    oldItem.city == newItem.city &&
                    oldItem.picture == newItem.picture
        }

        override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
            return oldItem == newItem
        }
    }

    fun removeItem(position: Int) {
        val currentList = currentList.toMutableList()
        fragment.deleteItem(position, currentList[position])
    }

}
