package com.mikula441.shoppingnotebook.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.mikula441.shoppingnotebook.entities.LibraryItem
import com.mikula441.shoppingnotebook.entities.NoteItem
import com.mikula441.shoppingnotebook.entities.ShopListNameItem
import com.mikula441.shoppingnotebook.entities.ShopListItem
import kotlinx.coroutines.flow.Flow

@Dao
interface Dao {
    @Query("SELECT * FROM note_list")
    fun getAllNotes(): Flow<List<NoteItem>>
    @Query("SELECT * FROM shopping_list_names")
    fun getAllShopNames(): Flow<List<ShopListNameItem>>
    @Query("SELECT * FROM shop_list_item WHERE listId LIKE :listId")
    fun getAllShopListItem(listId: Int): Flow<List<ShopListItem>>

    @Query("SELECT * FROM library WHERE name LIKE :name")
    suspend fun getAllLibraryItems(name: String): List<LibraryItem>

    @Query("DELETE FROM note_list WHERE id IS :id")
    suspend fun deleteNote(id: Int)
    @Query("DELETE FROM shopping_list_names WHERE id IS :id")
    suspend fun deleteShopListName(id: Int)
    @Query("DELETE FROM shop_list_item WHERE listId LIKE :listId")
    suspend fun deleteShopItemByListId(listId: Int)
    @Query("DELETE FROM library WHERE id LIKE :id")
    suspend fun deleteLibraryItem(id: Int)
    @Query("DELETE FROM shop_list_item WHERE id LIKE :id")
    suspend fun deleteShopListItem(id: Int)

    @Insert
    suspend fun insertNote(note: NoteItem)
    @Insert
    suspend fun insertShopListName(listName: ShopListNameItem)
    @Insert
    suspend fun insertShopListItem(shopListItem: ShopListItem)
    @Insert
    suspend fun insertLibraryItem(libraryItem: LibraryItem)

    @Update
    suspend fun updateNote(note: NoteItem)
    @Update
    suspend fun updateLibraryItem(item: LibraryItem)
    @Update
    suspend fun updateShopListName(shopListName: ShopListNameItem)
    @Update
    suspend fun updateShopListItem(shopListItem: ShopListItem)
}