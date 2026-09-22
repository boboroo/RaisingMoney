package com.shiny.raisingmoney.feature.transaction

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shiny.raisingmoney.core.designsystem.icon.ListLines
import com.shiny.raisingmoney.core.designsystem.theme.AdBackground
import com.shiny.raisingmoney.core.designsystem.theme.AdTextDim
import com.shiny.raisingmoney.core.designsystem.theme.Coral
import com.shiny.raisingmoney.core.designsystem.theme.HairLine
import com.shiny.raisingmoney.core.designsystem.theme.KeypadKeyText
import com.shiny.raisingmoney.core.designsystem.theme.RaisingMoneyTheme
import com.shiny.raisingmoney.core.designsystem.theme.ScreenBackground
import com.shiny.raisingmoney.core.designsystem.theme.SurfaceDark
import com.shiny.raisingmoney.core.designsystem.theme.TabTrack
import com.shiny.raisingmoney.core.designsystem.theme.TabUnselectedText
import com.shiny.raisingmoney.core.designsystem.theme.TextPrimary
import com.shiny.raisingmoney.core.designsystem.theme.TextTertiary
import com.shiny.raisingmoney.core.designsystem.util.dpFontSize
import com.shiny.raisingmoney.core.designsystem.util.noRippleClick
import com.shiny.raisingmoney.core.designsystem.util.rippleClick
import com.shiny.raisingmoney.feature.transaction.expense.ExpenseContent
import com.shiny.raisingmoney.feature.transaction.expense.ExpenseFormEvent
import com.shiny.raisingmoney.feature.transaction.expense.ExpenseFormUiState
import com.shiny.raisingmoney.feature.transaction.expense.ExpenseViewModel
import com.shiny.raisingmoney.feature.transaction.type.TransactionType
import com.shiny.raisingmoney.feature.transaction.util.getTodayUtcMillis

/**
 * 이 모듈(feature:transaction)의 public 진입점. `app`은 이 함수만 호출한다.
 * ViewModel 구독만 하고 로직은 없다 — 실제 렌더링은 아래 internal stateless 오버로드가 한다.
 */
@Composable
fun TransactionScreen(
    modifier: Modifier = Modifier,
    viewModel: TransactionViewModel = hiltViewModel(),
    expenseViewModel: ExpenseViewModel = hiltViewModel(),
) {
    val selectedTransactionType by viewModel.selectedTransactionType.collectAsStateWithLifecycle()
    val expenseFormState by expenseViewModel.expenseFormState.collectAsStateWithLifecycle()

    TransactionScreen(
        selectedTransactionType = selectedTransactionType,
        onSelectTransactionType = viewModel::selectTransactionType,
        expenseFormState = expenseFormState,
        onExpenseFormEvent = expenseViewModel::onExpenseFormEvent,
        modifier = modifier,
    )
}

/* -------------------------------------------------------------------------
  State + 콜백만으로 그려지는 stateless 오버로드. Preview/테스트가 직접 호출.
------------------------------------------------------------------------- */
@Composable
internal fun TransactionScreen(
    selectedTransactionType: TransactionType,
    onSelectTransactionType: (TransactionType) -> Unit,
    expenseFormState: ExpenseFormUiState,
    onExpenseFormEvent: (ExpenseFormEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .windowInsetsPadding(WindowInsets.systemBars.union(WindowInsets.ime)),
    ) {
        TopBar(
            title = stringResource(selectedTransactionType.labelRes),
            onBackClick = { /* TODO 기입된 내역 리스트 화면으로 이동 */ },
            onFavoriteClick = { /* TODO 자주 입력된 내역 리스트 화면으로 이동 */ },
        )
        TransactionTypeTabs(selected = selectedTransactionType, onSelect = onSelectTransactionType)
        when (selectedTransactionType) {
            TransactionType.EXPENSE -> ExpenseContent(
                uiState = expenseFormState,
                onEvent = onExpenseFormEvent,
                modifier = Modifier.weight(1f),
            )

            TransactionType.INCOME, TransactionType.TRANSFER -> {
                // TODO 수입/이체 입력 폼 추가
                Spacer(Modifier.weight(1f))
            }
        }
        AdBanner()
    }
}

@Composable
private fun TopBar(
    title: String,
    onBackClick: () -> Unit,
    onFavoriteClick: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(horizontal = 10.dp),
    ) {
        Row(
            modifier = Modifier.align(Alignment.CenterStart),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = "뒤로",
                tint = TextPrimary,
                modifier = Modifier
                    .size(30.dp)
                    .rippleClick(shape = CircleShape) {
                        onBackClick()
                    },
            )
            Text(
                text = stringResource(R.string.household_ledger_title),
                fontSize = dpFontSize(17.dp),
                color = KeypadKeyText,
                modifier = Modifier.noRippleClick {
                    onBackClick()
                },
            )
        }

        Text(
            text = title,
            modifier = Modifier.align(Alignment.Center),
            style = MaterialTheme.typography.titleLarge,
            color = TextPrimary,
        )

        FavoriteListIcon(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(45.dp)
                .rippleClick(shape = CircleShape) {
                    onFavoriteClick()
                }
                .padding(end = 6.dp),
        )
    }
    HorizontalDivider(color = HairLine)
}

@Composable
private fun FavoriteListIcon(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "즐겨찾기",
            tint = TextTertiary,
            modifier = Modifier.size(24.dp),
        )
        Icon(
            imageVector = ListLines,
            contentDescription = "즐겨찾기",
            tint = TextTertiary,
            modifier = Modifier.size(width = 9.dp, height = 10.5.dp),
        )
    }
}

@Composable
private fun TransactionTypeTabs(
    selected: TransactionType,
    onSelect: (TransactionType) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(TabTrack)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        TransactionType.entries.forEach { type ->
            val isSelected = type == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .then(
                        if (isSelected) {
                            Modifier
                                .background(Color.White)
                                .border(1.5.dp, Coral, RoundedCornerShape(8.dp))
                        } else {
                            Modifier
                        },
                    )
                    .rippleClick { onSelect(type) },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = stringResource(type.labelRes),
                    color = if (isSelected) Coral else TabUnselectedText,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = dpFontSize(16.dp),
                )
            }
        }
    }
}

/* -------------------------------------------------------------------------
  Ad banner (placeholder — no third-party branding)
------------------------------------------------------------------------- */
@Composable
private fun AdBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 50.dp)
            .background(AdBackground)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceDark),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                stringResource(R.string.ad_badge),
                color = AdTextDim,
                fontSize = dpFontSize(11.dp),
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text("광고", color = Color.White, fontSize = dpFontSize(13.dp), fontWeight = FontWeight.SemiBold)
            Text("광고 영역 자리표시자", color = AdTextDim, fontSize = dpFontSize(11.dp))
        }
        Spacer(Modifier.weight(1f))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(SurfaceDark)
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Text("열기", color = Color.White, fontSize = dpFontSize(12.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun TransactionScreenPreview() {
    RaisingMoneyTheme {
        TransactionScreen(
            selectedTransactionType = TransactionType.EXPENSE,
            onSelectTransactionType = {},
            expenseFormState = ExpenseFormUiState(dateMillis = getTodayUtcMillis()),
            onExpenseFormEvent = {},
        )
    }
}
