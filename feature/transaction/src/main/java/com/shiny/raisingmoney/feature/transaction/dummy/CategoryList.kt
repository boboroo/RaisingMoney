package com.shiny.raisingmoney.feature.transaction.dummy

import com.shiny.raisingmoney.feature.transaction.R
import com.shiny.raisingmoney.feature.transaction.model.Category

// TODO 분류 항목 편집 기능 추가되면 Local DB로 관리하기.
val Categories = listOf(
    Category("🍜", R.string.category_food),
    Category("🛒", R.string.category_mart_convenience),
    Category("🪑", R.string.category_household_goods),
    Category("🧥", R.string.category_fashion_beauty),
    Category("🧘", R.string.category_health),
    Category("🖼️", R.string.category_culture),
    Category("📙", R.string.category_self_development),
    Category("🚕", R.string.category_transport_car),
    Category("🏠", R.string.category_housing),
    Category("☎️", R.string.category_communication),
    Category("🎁", R.string.category_events_dues),
    Category("👩‍❤️‍👨", R.string.category_parents),
    Category("👶", R.string.category_childcare),
    Category("", R.string.category_etc),
)