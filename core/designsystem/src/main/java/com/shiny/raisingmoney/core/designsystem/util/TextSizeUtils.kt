package com.shiny.raisingmoney.core.designsystem.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

/**
 * `fontSize`는 [TextUnit](sp/em)만 받기 때문에, dp 값을 화면 밀도만 반영해 sp로 변환한다.
 * `Dp.toSp()`(밀도만 반영)와 달리 [androidx.compose.ui.unit.sp]는 사용자의 폰트 스케일
 * 설정에도 반응하는데, 이 화면은 폰트 스케일과 무관하게 항상 같은 크기로 보여야 하는
 * 디자인 요구사항이 있어 의도적으로 폰트 스케일을 무시한다.
 */
@Composable
fun dpFontSize(dp: Dp): TextUnit = with(LocalDensity.current) { dp.toSp() }
