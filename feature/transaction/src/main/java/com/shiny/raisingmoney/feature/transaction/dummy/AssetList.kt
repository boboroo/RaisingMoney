package com.shiny.raisingmoney.feature.transaction.dummy

import com.shiny.raisingmoney.feature.transaction.R

// TODO 자산 항목 편집 기능 추가되면 Local DB로 관리하기.
val AssetRes: List<Int> = listOf(
    R.string.asset_cash,
    R.string.asset_bank,
    R.string.asset_debit_card,
    R.string.asset_credit_card,
    R.string.asset_local_currency_card,
)