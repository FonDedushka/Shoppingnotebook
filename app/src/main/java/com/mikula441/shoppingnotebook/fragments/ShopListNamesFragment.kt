package com.mikula441.shoppingnotebook.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mikula441.shoppingnotebook.activities.MainApp
import com.mikula441.shoppingnotebook.activities.ShopListActivity
import com.mikula441.shoppingnotebook.database.MainViewModel
import com.mikula441.shoppingnotebook.database.ShopListNameAdapter
import com.mikula441.shoppingnotebook.databinding.FragmentShopListNamesBinding
import com.mikula441.shoppingnotebook.dialogs.DeleteDialog
import com.mikula441.shoppingnotebook.dialogs.NewListDialog
import com.mikula441.shoppingnotebook.entities.ShopListNameItem
import com.mikula441.shoppingnotebook.utils.TimeManager

class ShopListNamesFragment : BaseFragment(), ShopListNameAdapter.Listener {
    private lateinit var binding: FragmentShopListNamesBinding
    private lateinit var adapter: ShopListNameAdapter
    private lateinit var defPref: SharedPreferences

    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory(
            (context?.applicationContext as MainApp).database
        )
    }
    override fun onClickNew() {
        NewListDialog.showDialog(activity as AppCompatActivity, object: NewListDialog.Listener{
            override fun onClick(name: String) {
                val shopListName = ShopListNameItem(
                    null,
                    name,
                    TimeManager.getCurrentTime(),
                    0,
                    0,
                    ""
                )
                mainViewModel.insertShopListName(shopListName)
            }
        }, "")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopListNamesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        observer()
    }

    @SuppressLint("UseRequireInsteadOfGet")
    private fun initRcView() = with(binding){
        rcView.layoutManager = LinearLayoutManager(activity)
        defPref = PreferenceManager.getDefaultSharedPreferences(this@ShopListNamesFragment.activity!!)
        adapter = ShopListNameAdapter(this@ShopListNamesFragment, defPref)
        rcView.adapter = adapter
    }

    private fun observer(){
        mainViewModel.allShopListNameItem.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ShopListNamesFragment()
    }

    override fun deleteItem(id: Int) {
        DeleteDialog.showDialog(context as AppCompatActivity, object: DeleteDialog.Listener{
            override fun onClick() {
                mainViewModel.deleteShopList(id, true)
            }
        })
    }

    @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE")
    override fun onClickItem(shopListNameItem: ShopListNameItem) {
        val i = Intent(activity, ShopListActivity::class.java).apply {
            putExtra(ShopListActivity.SHOP_LIST_NAME, shopListNameItem)
        }
        startActivity(i)
    }

    override fun editItem(shopListNameItem: ShopListNameItem) {
        NewListDialog.showDialog(activity as AppCompatActivity, object: NewListDialog.Listener{
            override fun onClick(name: String) {
                mainViewModel.updateShopListName(
                    shopListNameItem.copy(name = name)
                )
            }
        }, shopListNameItem.name)
    }
}