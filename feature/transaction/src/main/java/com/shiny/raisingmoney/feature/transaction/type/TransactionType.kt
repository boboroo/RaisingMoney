package com.shiny.raisingmoney.feature.transaction.type

import androidx.annotation.StringRes
import com.shiny.raisingmoney.feature.transaction.R

enum class TransactionType(@StringRes val labelRes: Int) {
    INCOME(R.string.transaction_type_income),
    EXPENSE(R.string.transaction_type_expense),
    TRANSFER(R.string.transaction_type_transfer),
}