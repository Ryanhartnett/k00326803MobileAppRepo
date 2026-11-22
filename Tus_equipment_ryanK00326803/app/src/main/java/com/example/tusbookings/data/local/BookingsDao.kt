package com.example.tusbookings.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.tusbookings.model.Item
import com.example.tusbookings.model.ItemType
import com.example.tusbookings.model.Loan
import com.example.tusbookings.model.LoanSummary
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingsDao {
    @Query("SELECT * FROM item_types ORDER BY id")
    fun observeTypes(): Flow<List<ItemType>>

    @Query("SELECT * FROM items ORDER BY name")
    fun observeItems(): Flow<List<Item>>

    @Query("SELECT COUNT(*) FROM item_types")
    suspend fun typeCount(): Int

    @Query("SELECT COUNT(*) FROM items")
    suspend fun itemCount(): Int

    @Insert
    suspend fun insertTypes(types: List<ItemType>)

    @Insert
    suspend fun insertItems(items: List<Item>)

    @Insert
    suspend fun insertItem(item: Item): Long

    @Query("SELECT * FROM items WHERE id = :itemId")
    suspend fun findItem(itemId: Int): Item?

    @Update
    suspend fun updateItem(item: Item)

    @Query("DELETE FROM items WHERE id = :itemId")
    suspend fun deleteItem(itemId: Int)

    // Loans

    @Insert
    suspend fun insertLoan(loan: Loan): Long

    @Query("SELECT * FROM loans WHERE id = :loanId")
    suspend fun findLoan(loanId: Int): Loan?

    @Query("SELECT COUNT(*) FROM loans WHERE itemId = :itemId")
    suspend fun loanCount(itemId: Int): Int

    // Only takes units if enough are left; returns rows changed (0 or 1).
    @Query("""
        UPDATE items SET unitsAvailable = unitsAvailable - :units
        WHERE id = :itemId AND unitsAvailable >= :units
    """)
    suspend fun takeUnits(itemId: Int, units: Int): Int

    @Query("UPDATE items SET unitsAvailable = unitsAvailable + :units WHERE id = :itemId")
    suspend fun giveBackUnits(itemId: Int, units: Int)

    @Query("""
        UPDATE loans SET returned = 1
        WHERE id = :loanId AND userUid = :userUid AND returned = 0
    """)
    suspend fun markReturned(loanId: Int, userUid: String): Int

    @Query("""
        SELECT l.id, l.itemId, i.name, l.units, l.returnBy, l.reason, l.returned
        FROM loans AS l JOIN items AS i ON i.id = l.itemId
        WHERE l.userUid = :userUid
        ORDER BY l.returned ASC, l.id DESC
    """)
    fun observeLoans(userUid: String): Flow<List<LoanSummary>>
}
