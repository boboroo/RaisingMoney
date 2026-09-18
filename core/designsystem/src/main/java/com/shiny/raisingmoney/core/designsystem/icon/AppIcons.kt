package com.shiny.raisingmoney.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Material 아이콘 세트에 없는, 앱 전용 커스텀 아이콘 모음.
 * 도메인 모델을 전혀 모르는 순수 그래픽 리소스.
 * 모든 path는 검정(Color.Black)으로 그려두고, 실제 색은 `Icon(tint = ...)`으로 입힌다.
 */

private var listLinesIcon: ImageVector? = null

/** 짧은 가로줄 3개 — 목록/리스트를 표시하는 보조 아이콘(예: 즐겨찾기 별모양 아이콘 옆에 사용). */
val ListLines: ImageVector
    get() = listLinesIcon ?: ImageVector.Builder(
        name = "ShortListLines",
        defaultWidth = 9.dp,
        defaultHeight = 10.5.dp,
        viewportWidth = 9f,
        viewportHeight = 10.5f,
    ).apply {
        path(fill = SolidColor(Color.Black)) {
            moveTo(0f, 0f)
            lineTo(9f, 0f)
            lineTo(9f, 1.5f)
            lineTo(0f, 1.5f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(0f, 4.5f)
            lineTo(9f, 4.5f)
            lineTo(9f, 6f)
            lineTo(0f, 6f)
            close()
        }
        path(fill = SolidColor(Color.Black)) {
            moveTo(0f, 9f)
            lineTo(9f, 9f)
            lineTo(9f, 10.5f)
            lineTo(0f, 10.5f)
            close()
        }
    }.build().also { listLinesIcon = it }
