package com.shiny.raisingmoney.feature.transaction.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/** 로컬 기준 오늘 날짜의 UTC 자정 millis. */
fun getTodayUtcMillis(): Long {
    val now = Calendar.getInstance()
    return Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
        clear()
        set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DAY_OF_MONTH))
    }.timeInMillis
}

// DataPicker TimeZone과 SimpleDateFormat TimeZone을 UTC로 일치시켜서 하루 밀림 방지.
fun formatDate(utcMillis: Long): String =
    SimpleDateFormat("yyyy. M. d. (E)", Locale.KOREAN)
        .apply { timeZone = TimeZone.getTimeZone("UTC") }
        .format(Date(utcMillis))