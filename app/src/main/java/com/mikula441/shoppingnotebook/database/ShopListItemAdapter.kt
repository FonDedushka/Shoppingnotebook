package com.mikula441.shoppingnotebook.database

import android.annotation.SuppressLint
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mikula441.shoppingnotebook.R
import com.mikula441.shoppingnotebook.databinding.ShopListItemBinding
import com.mikula441.shoppingnotebook.databinding.ShopListLibraryItemBinding
import com.mikula441.shoppingnotebook.entities.ShopListItem

class ShopListItemAdapter(private val listener: Listener) : ListAdapter<ShopListItem, ShopListItemAdapter.ItemHolder>(ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return if (viewType == 0) {
            ItemHolder.createShopItem(parent)
        }
        else {
            ItemHolder.createLibraryItem(parent)
        }
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        return if (getItem(position).itemsType == 0) {
            holder.setItemData(getItem(position), listener)
        }
        else {
            holder.setLibraryData(getItem(position), listener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).itemsType
    }

    class ItemHolder(val view: View): RecyclerView.ViewHolder(view){

        fun setItemData(shopListItem: ShopListItem, listener: Listener){
            val binding = ShopListItemBinding.bind(view)
            binding.apply {
                tvName.text = shopListItem.name
                tvInfo.text = shopListItem.itemInfo
                tvInfo.visibility = infoVisibility(shopListItem)
                chBox.isChecked = shopListItem.itemChecked
                setPaintFlagAndColor(binding)
                chBox.setOnClickListener {
                    listener
                        .onClickItem(shopListItem.copy(itemChecked = chBox.isChecked), CHECK_BOX)
                }
                imDelete.setOnClickListener {
                    listener.onClickItem(shopListItem, DELETE_SHOP_LIST_ITEM)
                }
                imEditItem.setOnClickListener {
                    listener.onClickItem(shopListItem, EDIT)
                }
                itemView.setOnClickListener {
                    listener.onClickItem(shopListItem, ADD_LIBRARY_ITEM)
                }
            }
        }

        fun setLibraryData(shopListItem: ShopListItem, listener: Listener){
            val binding = ShopListLibraryItemBinding.bind(view)
            binding.apply {
                tvName.text = shopListItem.name
                imEditLibrary.setOnClickListener {
                    listener.onClickItem(shopListItem, EDIT_LIBRARY)
                }
                imDeleteLibrary.setOnClickListener {
                    listener.onClickItem(shopListItem, DELETE_LIBRARY_ITEM)
                }
                itemView.setOnClickListener {
                    listener.onClickItem(shopListItem, ADD_LIBRARY_ITEM)
                }
            }
        }

        @SuppressLint("ResourceAsColor")
        private fun setPaintFlagAndColor(binding: ShopListItemBinding) = with(binding) {
            if (chBox.isChecked) {
                tvName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                tvInfo.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                tvName.setTextColor(
                    ContextCompat
                        .getColor(binding.root.context, R.color.grey)
                )
                tvInfo.setTextColor(
                    ContextCompat
                        .getColor(binding.root.context, R.color.grey)
                )
            } else {
                tvName.paintFlags = Paint.ANTI_ALIAS_FLAG
                tvInfo.paintFlags = Paint.ANTI_ALIAS_FLAG
                tvName.setTextColor(
                    ContextCompat
                        .getColor(binding.root.context, R.color.black)
                )
                tvInfo.setTextColor(
                    ContextCompat
                        .getColor(binding.root.context, R.color.black)
                )
            }
        }

        private fun infoVisibility(shopListItem: ShopListItem): Int{
            return if (shopListItem.itemInfo.isNullOrEmpty()){
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        companion object{
            fun createShopItem(parent: ViewGroup): ItemHolder{
                return ItemHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.shop_list_item, parent, false))
            }
            fun createLibraryItem(parent: ViewGroup): ItemHolder{
                return ItemHolder(LayoutInflater.from(parent.context)
                    .inflate(R.layout.shop_list_library_item, parent, false))
            }
        }
    }

    class ItemComparator: DiffUtil.ItemCallback<ShopListItem>(){
        override fun areItemsTheSame(oldItem: ShopListItem, newItem: ShopListItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShopListItem, newItem: ShopListItem): Boolean {
            return oldItem == newItem
        }
    }

    interface Listener{
        fun onClickItem(shopListItem : ShopListItem, state: Int)
    }

    companion object{
        const val EDIT = 0
        const val CHECK_BOX = 1
        const val EDIT_LIBRARY = 2
        const val DELETE_LIBRARY_ITEM = 3
        const val DELETE_SHOP_LIST_ITEM = 5
        const val ADD_LIBRARY_ITEM = 4
    }
}