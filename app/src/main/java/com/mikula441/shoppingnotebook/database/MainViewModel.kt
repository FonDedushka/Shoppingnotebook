package com.mikula441.shoppingnotebook.database

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.mikula441.shoppingnotebook.entities.LibraryItem
import com.mikula441.shoppingnotebook.entities.NoteItem
import com.mikula441.shoppingnotebook.entities.ShopListItem
import com.mikula441.shoppingnotebook.entities.ShopListNameItem
import kotlinx.coroutines.launch

class MainViewModel(database: MainDataBase): ViewModel() {
    private val dao = database.getDao()
    val libraryItems = MutableLiveData<List<LibraryItem>>()
    val allNotes: LiveData<List<NoteItem>> = dao.getAllNotes().asLiveData()
    val allShopListNameItem: LiveData<List<ShopListNameItem>> = dao.getAllShopNames().asLiveData()

    fun getAllItemFromList(listId: Int): LiveData<List<ShopListItem>>{
        return dao.getAllShopListItem(listId).asLiveData()
    }

    fun getAllLibraryItems(name: String) = viewModelScope.launch{
        libraryItems.postValue(dao.getAllLibraryItems(name))
    }

    fun insertNote(note: NoteItem) =viewModelScope.launch {
        dao.insertNote(note)
    }

    fun insertShopListName(listName: ShopListNameItem) =viewModelScope.launch {
        dao.insertShopListName(listName)
    }

    fun insertShopListItem(shopListItem: ShopListItem) =viewModelScope.launch {
        dao.insertShopListItem(shopListItem)
        if (!isLibraryItemExists(shopListItem.name))dao
            .insertLibraryItem(LibraryItem(null, shopListItem.name))
    }

    fun updateNote(note: NoteItem) = viewModelScope.launch {
        dao.updateNote(note)
    }
    fun updateLibraryItem(item: LibraryItem) = viewModelScope.launch {
        dao.updateLibraryItem(item)
    }

    fun updateShopListName(shopListNameItem: ShopListNameItem) = viewModelScope.launch {
        dao.updateShopListName(shopListNameItem)
    }
    fun updateShopListItem(shopListItem: ShopListItem) = viewModelScope.launch {
        dao.updateShopListItem(shopListItem)
    }

    fun deleteNote(id:Int) =viewModelScope.launch {
        dao.deleteNote(id)
    }

    fun deleteLibraryItem(id:Int) =viewModelScope.launch {
        dao.deleteLibraryItem(id)
    }
    fun deleteShopListItem(id:Int) =viewModelScope.launch {
        dao.deleteShopListItem(id)
    }

    fun deleteShopList(id:Int, deleteList: Boolean) =viewModelScope.launch {
        if (deleteList)dao.deleteShopListName(id)
        dao.deleteShopItemByListId(id)
    }

    private suspend fun isLibraryItemExists(name: String): Boolean{
        return dao.getAllLibraryItems(name).isNotEmpty()
    }



    @Suppress("UNCHECKED_CAST")
    class MainViewModelFactory(private val database: MainDataBase): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)){
                return MainViewModel(database) as T
            }
            throw IllegalArgumentException("Unknown ViewModelClass")
        }
    }
}