package com.shiny.raisingmoney.feature.transaction.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

data class Category(val emoji: String, @StringRes val nameRes: Int) {
    @Composable
    fun displayText(): String {
        val name = stringResource(nameRes)
        return if (emoji.isEmpty()) name else "$emoji $name"
    }
}
