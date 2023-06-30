package com.mikula441.shoppingnotebook.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikula441.shoppingnotebook.R
import com.mikula441.shoppingnotebook.database.MainViewModel
import com.mikula441.shoppingnotebook.database.ShopListItemAdapter
import com.mikula441.shoppingnotebook.databinding.ActivityShopListBinding
import com.mikula441.shoppingnotebook.dialogs.EditListItemDialog
import com.mikula441.shoppingnotebook.entities.LibraryItem
import com.mikula441.shoppingnotebook.entities.ShopListItem
import com.mikula441.shoppingnotebook.entities.ShopListNameItem
import com.mikula441.shoppingnotebook.utils.ShareHelper

class ShopListActivity : AppCompatActivity(), ShopListItemAdapter.Listener {
    private lateinit var binding: ActivityShopListBinding
    private var shopListNameItem: ShopListNameItem? = null
    private lateinit var saveItem: MenuItem
    private var edItem: EditText? = null
    private var adapter: ShopListItemAdapter? = null
    private lateinit var textWatcher: TextWatcher
    private lateinit var defPref: SharedPreferences

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory(
            (applicationContext as MainApp).database
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        defPref = PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedTheme())
        super.onCreate(savedInstanceState)
        binding = ActivityShopListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initRcView()
        listItemObserver()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.shop_list_menu, menu)
        saveItem = menu?.findItem(R.id.save_item)!!
        val newItem = menu.findItem(R.id.new_item_this)
        edItem = newItem.actionView?.findViewById(R.id.edNewShopItem) as EditText
        newItem.setOnActionExpandListener(expandActionView())
        saveItem.isVisible = false
        textWatcher = textWatcher()
        return true
    }

    private fun textWatcher(): TextWatcher{
        return object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //Log.d("MyLog", "On Text Change: $s")

            }

            override fun afterTextChanged(s: Editable?) {
                mainViewModel.getAllLibraryItems("%$s%")
            }

        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_item -> {
                addNewShopItem(edItem?.text.toString())
            }
            R.id.delete_list -> {
                mainViewModel.deleteShopList(shopListNameItem?.id!!, true)
                finish()
            }
            R.id.clear_list -> {
                mainViewModel.deleteShopList(shopListNameItem?.id!!, false)
            }
            R.id.share_list -> {
                startActivity(Intent.createChooser(
                    ShareHelper.shareShopList(adapter?.currentList!!,
                        shopListNameItem?.name!!),
                    getString(R.string.share_list)
                ))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SuspiciousIndentation")
    private fun addNewShopItem(name: String){
        if (name.isEmpty()) return
            val item = ShopListItem(
                null,
                name,
                null,
                false,
                shopListNameItem?.id!!,
                0
            )
            edItem?.setText("")
            mainViewModel.insertShopListItem(item)
    }

    private fun listItemObserver(){
        mainViewModel.getAllItemFromList(shopListNameItem?.id!!)
            .observe(this) {
            adapter?.submitList(it)
            binding.tvEmpty.visibility =
                if(it.isEmpty()){
                    View.VISIBLE
                } else {
                    View.GONE
                }
        }
    }

    private fun libraryItemObserver(){
        mainViewModel.libraryItems.observe(this) {
            val tempShopList = ArrayList<ShopListItem>()
            it.forEach {item ->
                val shopItem = ShopListItem(
                    item.id,
                    item.name,
                    "",
                    false,
                    0,
                    0
                )
                tempShopList.add(shopItem)
            }
            adapter?.submitList(tempShopList)
        }
    }

    private fun initRcView() = with(binding){
        adapter = ShopListItemAdapter(this@ShopListActivity)
        rcView.layoutManager = LinearLayoutManager(this@ShopListActivity)
        rcView.adapter = adapter
    }

    private fun init(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            shopListNameItem = intent.getSerializableExtra(SHOP_LIST_NAME, ShopListNameItem::class.java)
        } else{
            @Suppress("DEPRECATION")
            shopListNameItem = intent.getSerializableExtra(SHOP_LIST_NAME) as ShopListNameItem
        }
        binding.tvEmpty.text = shopListNameItem?.name
    }

    private fun expandActionView(): MenuItem.OnActionExpandListener{
        return object: MenuItem.OnActionExpandListener{
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                saveItem.isVisible = true
                edItem?.addTextChangedListener(textWatcher)
                libraryItemObserver()
                mainViewModel.getAllItemFromList(shopListNameItem?.id!!).removeObservers(this@ShopListActivity)
                mainViewModel.getAllLibraryItems("%%")
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                saveItem.isVisible = false
                edItem?.removeTextChangedListener(textWatcher)
                invalidateOptionsMenu()
                mainViewModel.libraryItems.removeObservers(this@ShopListActivity)
                edItem?.setText("")
                listItemObserver()
                return true
            }
        }
    }

    companion object{
        const val SHOP_LIST_NAME = "shop_list_name"
    }

    override fun onClickItem(shopListItem: ShopListItem, state: Int) {
        when(state){
            ShopListItemAdapter.CHECK_BOX -> mainViewModel.updateShopListItem(shopListItem)
            ShopListItemAdapter.EDIT -> editListItem(shopListItem)
            ShopListItemAdapter.EDIT_LIBRARY -> editLibraryItem(shopListItem)
            ShopListItemAdapter.ADD_LIBRARY_ITEM -> addNewShopItem(shopListItem.name)
            ShopListItemAdapter.DELETE_LIBRARY_ITEM -> {
                mainViewModel.deleteLibraryItem(shopListItem.id!!)
                mainViewModel.getAllLibraryItems("%${edItem?.text.toString()}%")
            }
            ShopListItemAdapter.DELETE_SHOP_LIST_ITEM -> {
                mainViewModel.deleteShopListItem(shopListItem.id!!)
                mainViewModel.getAllItemFromList(shopListItem.listId)
            }
        }
    }

    private fun editListItem(item: ShopListItem){
        EditListItemDialog.showDialog(this, item, object: EditListItemDialog.Listener{
            override fun onClick(item: ShopListItem) {
                mainViewModel.updateShopListItem(item)
            }
        })
    }
    private fun editLibraryItem(item: ShopListItem){
        EditListItemDialog.showDialog(this, item, object: EditListItemDialog.Listener{
            override fun onClick(item: ShopListItem) {
                mainViewModel.updateLibraryItem(LibraryItem(item.id, item.name))
                mainViewModel.getAllLibraryItems("%${edItem?.text.toString()}%")
            }
        })
    }

    private fun saveItemCount(){
        var checkedItemCount = 0
        adapter?.currentList?.forEach {
            if (it.itemChecked) checkedItemCount ++
        }
        val tempShopListNameItem = shopListNameItem?.copy(
            allItemCounter = adapter?.itemCount!!,
            checkedItemCounter = checkedItemCount
        )
        mainViewModel.updateShopListName(tempShopListNameItem!!)
    }

    @Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")
    override fun onBackPressed() {
        saveItemCount()
        super.onBackPressed()
    }


    private fun getSelectedTheme(): Int{
        return if (defPref.getString("theme_key", "green") == "green"){
            R.style.Base_Theme_ShoppingNotebookGreen
        } else {
            R.style.Base_Theme_ShoppingNotebookViolet
        }
    }
}