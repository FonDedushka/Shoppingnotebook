package com.mikula441.shoppingnotebook.dialogs

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.mikula441.shoppingnotebook.databinding.EditListItemDialogBinding
import com.mikula441.shoppingnotebook.entities.ShopListItem

object EditListItemDialog {

    fun showDialog(context: Context, item: ShopListItem, listener: Listener){
        var dialog: AlertDialog? = null
        val builder = AlertDialog.Builder(context)
        val binding = EditListItemDialogBinding
            .inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        binding.apply {
            edName.setText(item.name)
            edInfo.setText(item.itemInfo)
            if (item.itemsType == 1) {
                edInfo.visibility = View.GONE
            }
            bUpdate.setOnClickListener {
                if (edName.text.toString().isNotEmpty()){
                    val itemInfo = edInfo.text.toString().ifEmpty {
                        null
                    }
                   listener.onClick(item.copy(name = edName.text.toString(),
                   itemInfo = itemInfo))
                }
                dialog?.dismiss()
            }
        }
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(null)
        dialog.show()
    }
    interface Listener{
        fun onClick(item: ShopListItem)
    }
}