package com.shiny.raisingmoney.feature.transaction.expense

import com.shiny.raisingmoney.feature.transaction.model.Category

/**
 * 사용자가 [ExpenseContent]지출 폼에서 일으킬 수 있는 모든 이벤트.
 * Screen은 이 중 하나를 ViewModel로 올려보내기만 한다.
 */
sealed interface ExpenseFormEvent {
    data object DateRowClicked : ExpenseFormEvent
    data class DatePicked(val millis: Long?) : ExpenseFormEvent

    data object AmountRowClicked : ExpenseFormEvent
    data object AmountBackspacePressed : ExpenseFormEvent
    data object AmountEqualsPressed : ExpenseFormEvent
    data object AmountConfirmPressed : ExpenseFormEvent
    data class AmountKeyPressed(val key: String) : ExpenseFormEvent
    data object AmountKeypadClosed : ExpenseFormEvent

    data object CategoryRowClicked : ExpenseFormEvent
    data class CategoryPicked(val category: Category) : ExpenseFormEvent
    data object CategoryPickerClosed : ExpenseFormEvent

    data object AssetRowClicked : ExpenseFormEvent
    data class AssetPicked(val assetRes: Int) : ExpenseFormEvent
    data object AssetPickerClosed : ExpenseFormEvent

    data class ContentChanged(val value: String) : ExpenseFormEvent
    data object ContentFocused : ExpenseFormEvent
    data object ContentCleared : ExpenseFormEvent
    data object ContentDone : ExpenseFormEvent
    data object ImportantMarkToggled : ExpenseFormEvent

    data class AdditionalInputChanged(val value: String) : ExpenseFormEvent
    data object AdditionalInputFocused : ExpenseFormEvent
    data object AdditionalInputDone : ExpenseFormEvent

    data object SaveClicked : ExpenseFormEvent
    data object ContinueClicked : ExpenseFormEvent
    data object DeleteClicked : ExpenseFormEvent
}