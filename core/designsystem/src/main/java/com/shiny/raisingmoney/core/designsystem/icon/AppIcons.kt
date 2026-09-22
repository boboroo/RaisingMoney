package com.shiny.raisingmoney.core.designsystem.icon

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.PathBuilder
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/*
 * Material 아이콘 세트에 없는, 앱 전용 커스텀 아이콘 모음.
 * 도메인 모델을 전혀 모르는 순수 그래픽 리소스.
 * 모든 path는 검정(Color.Black)으로 그려두고, 실제 색은 `Icon(tint = ...)`으로 입힌다.
 */

/** 모서리가 둥근 사각형의 외곽선만 그리는 닫힌 path(칠하지 않고 stroke로만 그릴 용도). */
private fun PathBuilder.roundedRectOutline(left: Float, top: Float, right: Float, bottom: Float, radius: Float) {
    moveTo(left + radius, top)
    lineTo(right - radius, top)
    arcTo(radius, radius, 0f, isMoreThanHalf = false, isPositiveArc = true, x1 = right, y1 = top + radius)
    lineTo(right, bottom - radius)
    arcTo(radius, radius, 0f, isMoreThanHalf = false, isPositiveArc = true, x1 = right - radius, y1 = bottom)
    lineTo(left + radius, bottom)
    arcTo(radius, radius, 0f, isMoreThanHalf = false, isPositiveArc = true, x1 = left, y1 = bottom - radius)
    lineTo(left, top + radius)
    arcTo(radius, radius, 0f, isMoreThanHalf = false, isPositiveArc = true, x1 = left + radius, y1 = top)
    close()
}

/** 원의 외곽선만 그리는 닫힌 path(반원 arc 두 개로 구성). */
private fun PathBuilder.circleOutline(centerX: Float, centerY: Float, radius: Float) {
    moveTo(centerX - radius, centerY)
    arcTo(radius, radius, 0f, isMoreThanHalf = false, isPositiveArc = true, x1 = centerX + radius, y1 = centerY)
    arcTo(radius, radius, 0f, isMoreThanHalf = false, isPositiveArc = true, x1 = centerX - radius, y1 = centerY)
    close()
}

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

private var cameraGlyphIcon: ImageVector? = null

/** 카메라 모양 글리프 — 사진 첨부 버튼 등에 사용. */
val CameraGlyph: ImageVector
    get() = cameraGlyphIcon ?: ImageVector.Builder(
        name = "CameraGlyph",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        // 카메라 몸체
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.92f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            roundedRectOutline(left = 1.44f, top = 7.2f, right = 22.56f, bottom = 18.72f, radius = 2.88f)
        }
        // 뷰파인더 돌출부
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.92f,
            strokeLineCap = StrokeCap.Round,
            strokeLineJoin = StrokeJoin.Round,
        ) {
            moveTo(8.16f, 7.2f)
            lineTo(9.6f, 4.32f)
            lineTo(14.4f, 4.32f)
            lineTo(15.84f, 7.2f)
        }
        // 렌즈
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 1.92f,
        ) {
            circleOutline(centerX = 12f, centerY = 13.2f, radius = 3.6f)
        }
    }.build().also { cameraGlyphIcon = it }

private var copyGlyphIcon: ImageVector? = null

/** 겹친 사각형 두 개로 그리는 복사(copy) 글리프. */
val CopyGlyph: ImageVector
    get() = copyGlyphIcon ?: ImageVector.Builder(
        name = "CopyGlyph",
        defaultWidth = 24.dp,
        defaultHeight = 24.dp,
        viewportWidth = 24f,
        viewportHeight = 24f,
    ).apply {
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2.16f,
        ) {
            roundedRectOutline(left = 2.4f, top = 2.4f, right = 15.6f, bottom = 15.6f, radius = 2.4f)
        }
        path(
            fill = null,
            stroke = SolidColor(Color.Black),
            strokeLineWidth = 2.16f,
        ) {
            roundedRectOutline(left = 8.4f, top = 8.4f, right = 21.6f, bottom = 21.6f, radius = 2.4f)
        }
    }.build().also { copyGlyphIcon = it }
