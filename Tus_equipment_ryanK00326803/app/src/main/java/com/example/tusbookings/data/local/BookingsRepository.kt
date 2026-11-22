package com.example.tusbookings.data.local

import androidx.room.withTransaction
import com.example.tusbookings.data.starterItems
import com.example.tusbookings.data.starterTypes
import com.example.tusbookings.model.Item
import com.example.tusbookings.model.Loan

class BookingsRepository(private val database: BookingsDatabase) {
    private val dao = database.bookingsDao()
    val items = dao.observeItems()
    val types = dao.observeTypes()

    fun loansFor(userUid: String) = dao.observeLoans(userUid)

    suspend fun seedIfEmpty() {
        database.withTransaction {
            if (dao.typeCount() == 0 && dao.itemCount() == 0) {
                dao.insertTypes(starterTypes)
                dao.insertItems(starterItems)
            }
        }
    }

    suspend fun saveItem(item: Item): Long = dao.insertItem(item)

    // Keeps units that are out on loan correct when the total is changed.
    suspend fun updateItem(item: Item) {
        database.withTransaction {
            val old = dao.findItem(item.id) ?: error("Item not found")
            val onLoan = old.unitsTotal - old.unitsAvailable
            check(item.unitsTotal >= onLoan) {
                "$onLoan unit(s) are out on loan, so the total can't be lower than that"
            }
            dao.updateItem(item.copy(unitsAvailable = item.unitsTotal - onLoan))
        }
    }

    suspend fun deleteItem(itemId: Int) {
        database.withTransaction {
            check(dao.loanCount(itemId) == 0) {
                "This item has loan history so it can't be deleted"
            }
            dao.deleteItem(itemId)
        }
    }

    // Returns false when there are not enough units left.
    suspend fun borrow(
        itemId: Int, userUid: String, units: Int, returnBy: String, reason: String
    ): Boolean = database.withTransaction {
        if (dao.takeUnits(itemId, units) == 0) {
            false
        } else {
            dao.insertLoan(
                Loan(
                    itemId = itemId, userUid = userUid, units = units,
                    returnBy = returnBy.trim(), reason = reason.trim()
                )
            )
            true
        }
    }

    suspend fun returnLoan(loanId: Int, userUid: String) {
        database.withTransaction {
            val loan = dao.findLoan(loanId)
            if (loan != null && dao.markReturned(loanId, userUid) == 1) {
                dao.giveBackUnits(loan.itemId, loan.units)
            }
        }
    }
}
