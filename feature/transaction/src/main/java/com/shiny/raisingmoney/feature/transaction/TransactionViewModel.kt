package com.shiny.raisingmoney.feature.transaction

import androidx.lifecycle.ViewModel
import com.shiny.raisingmoney.feature.transaction.type.TransactionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class TransactionViewModel @Inject constructor() : ViewModel() {

    private val _selectedTransactionType = MutableStateFlow(TransactionType.EXPENSE)
    val selectedTransactionType: StateFlow<TransactionType> = _selectedTransactionType.asStateFlow()

    fun selectTransactionType(type: TransactionType) {
        _selectedTransactionType.value = type
    }
}
