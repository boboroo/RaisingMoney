package com.shiny.raisingmoney.feature.transaction.expense

import androidx.lifecycle.ViewModel
import com.shiny.raisingmoney.feature.transaction.util.getTodayUtcMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ExpenseViewModel @Inject constructor() : ViewModel() {

    private val _expenseFormState = MutableStateFlow(ExpenseFormUiState(dateMillis = getTodayUtcMillis()))
    val expenseFormState: StateFlow<ExpenseFormUiState> = _expenseFormState.asStateFlow()

    /** TODO
     * 아직 저장소 호출 등 suspend 작업이 없어 `viewModelScope.launch`로 감싸지 않았다 —
     * 전부 동기적인 `StateFlow.update`라 코루틴을 쓸 이유가 없다. 나중에 저장/조회가
     * 실제 데이터 계층을 호출하게 되면 그 함수만 `viewModelScope.launch { }`로 감싼다.
     */
    fun onExpenseFormEvent(event: ExpenseFormEvent) {
        _expenseFormState.update { it.handleEvent(event) }
    }
}