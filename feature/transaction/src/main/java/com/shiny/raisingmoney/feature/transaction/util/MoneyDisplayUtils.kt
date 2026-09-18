package com.shiny.raisingmoney.feature.transaction.util

fun formatAmount(raw: String): String {
    if (raw.isEmpty()) return ""
    val n = raw.toLongOrNull() ?: return ""
    return "%,d원".format(n)
}
