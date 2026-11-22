package com.example.tusbookings.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tusbookings.data.local.BookingsDatabase
import com.example.tusbookings.data.local.BookingsRepository
import com.example.tusbookings.model.Item
import com.example.tusbookings.model.ItemType
import com.example.tusbookings.model.LoanSummary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Source
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class BookingsUiState(
    val items: List<Item> = emptyList(),
    val types: List<ItemType> = emptyList(),
    val loans: List<LoanSummary> = emptyList(),
    val isLoading: Boolean = true,
    val loadError: String? = null,
    val isSaving: Boolean = false,
    val message: String? = null
) {
    fun typeName(typeId: Int): String =
        types.firstOrNull { it.id == typeId }?.name ?: "Other"

    fun findItem(id: Int?): Item? = items.firstOrNull { it.id == id }

    val loansOut: Int get() = loans.count { !it.returned }
}

class BookingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = BookingsRepository(BookingsDatabase.getInstance(application))
    private val userUid = MutableStateFlow<String?>(null)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val userLoans = userUid.flatMapLatest { uid ->
        if (uid == null) flowOf(emptyList()) else repository.loansFor(uid)
    }

    private val mutableUiState = MutableStateFlow(BookingsUiState())
    val uiState = mutableUiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                repository.seedIfEmpty()
                combine(repository.items, repository.types, userLoans) { items, types, loans ->
                    Triple(items, types, loans)
                }.collect { (items, types, loans) ->
                    mutableUiState.update {
                        it.copy(items = items, types = types, loans = loans,
                            isLoading = false, loadError = null)
                    }
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                mutableUiState.update {
                    it.copy(isLoading = false,
                        loadError = error.message ?: "Unable to load equipment")
                }
            }
        }
    }

    fun setUser(uid: String?) {
        userUid.value = uid
        mutableUiState.update { it.copy(message = null) }
    }

    fun clearMessage() {
        mutableUiState.update { it.copy(message = null) }
    }

    fun borrow(item: Item, units: Int, returnBy: String, reason: String) {
        val uid = userUid.value ?: return
        if (mutableUiState.value.isSaving) return
        mutableUiState.update { it.copy(isSaving = true, message = null) }
        viewModelScope.launch {
            try {
                val saved = repository.borrow(item.id, uid, units, returnBy, reason)
                val text = if (saved) {
                    "Loan confirmed: $units x ${item.name}, return by ${returnBy.trim()}"
                } else {
                    "Not enough units left for ${item.name}"
                }
                mutableUiState.update { it.copy(message = text) }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                mutableUiState.update { it.copy(message = error.message ?: "Unable to save loan") }
            } finally {
                mutableUiState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun returnLoan(loanId: Int) {
        val uid = userUid.value ?: return
        if (mutableUiState.value.isSaving) return
        mutableUiState.update { it.copy(isSaving = true, message = null) }
        viewModelScope.launch {
            try {
                repository.returnLoan(loanId, uid)
                mutableUiState.update { it.copy(message = "Item returned - thanks!") }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                mutableUiState.update { it.copy(message = error.message ?: "Unable to return item") }
            } finally {
                mutableUiState.update { it.copy(isSaving = false) }
            }
        }
    }

    private suspend fun requireAdmin() {
        val user = FirebaseAuth.getInstance().currentUser
            ?: error("Admin sign in required")
        val document = suspendCancellableCoroutine<DocumentSnapshot> { continuation ->
            FirebaseFirestore.getInstance().collection("users")
                .document(user.uid).get(Source.SERVER)
                .addOnSuccessListener {
                    if (continuation.isActive) continuation.resume(it)
                }
                .addOnFailureListener {
                    if (continuation.isActive) continuation.resumeWithException(it)
                }
        }
        check(FirebaseAuth.getInstance().currentUser?.uid == user.uid &&
                document.getString("role") == "admin") { "Admin access required" }
    }

    fun saveItem(item: Item) {
        if (mutableUiState.value.isSaving) return
        mutableUiState.update { it.copy(isSaving = true, message = null) }
        viewModelScope.launch {
            try {
                requireAdmin()
                repository.saveItem(item)
                mutableUiState.update { it.copy(message = "Added ${item.name}") }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                mutableUiState.update { it.copy(message = error.message ?: "Unable to add item") }
            } finally {
                mutableUiState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun updateItem(item: Item) {
        if (mutableUiState.value.isSaving) return
        mutableUiState.update { it.copy(isSaving = true, message = null) }
        viewModelScope.launch {
            try {
                requireAdmin()
                repository.updateItem(item)
                mutableUiState.update { it.copy(message = "Updated ${item.name}") }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                mutableUiState.update { it.copy(message = error.message ?: "Unable to update item") }
            } finally {
                mutableUiState.update { it.copy(isSaving = false) }
            }
        }
    }

    fun deleteItem(itemId: Int) {
        if (mutableUiState.value.isSaving) return
        mutableUiState.update { it.copy(isSaving = true, message = null) }
        viewModelScope.launch {
            try {
                requireAdmin()
                repository.deleteItem(itemId)
                mutableUiState.update { it.copy(message = "Item deleted") }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (error: Exception) {
                mutableUiState.update { it.copy(message = error.message ?: "Unable to delete item") }
            } finally {
                mutableUiState.update { it.copy(isSaving = false) }
            }
        }
    }
}
