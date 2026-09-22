package com.shiny.raisingmoney.feature.transaction.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.text.isDigitsOnly
import com.shiny.raisingmoney.core.designsystem.icon.CameraGlyph
import com.shiny.raisingmoney.core.designsystem.icon.CopyGlyph
import com.shiny.raisingmoney.core.designsystem.theme.BorderGray
import com.shiny.raisingmoney.core.designsystem.theme.Coral
import com.shiny.raisingmoney.core.designsystem.theme.HairLine
import com.shiny.raisingmoney.core.designsystem.theme.KeypadDivider
import com.shiny.raisingmoney.core.designsystem.theme.KeypadHeader
import com.shiny.raisingmoney.core.designsystem.theme.KeypadKeyText
import com.shiny.raisingmoney.core.designsystem.theme.RaisingMoneyTheme
import com.shiny.raisingmoney.core.designsystem.theme.ScreenBackground
import com.shiny.raisingmoney.core.designsystem.theme.SectionBand
import com.shiny.raisingmoney.core.designsystem.theme.SheetGridLine
import com.shiny.raisingmoney.core.designsystem.theme.TextPrimary
import com.shiny.raisingmoney.core.designsystem.theme.TextSecondary
import com.shiny.raisingmoney.core.designsystem.theme.TextTertiary
import com.shiny.raisingmoney.core.designsystem.util.dpFontSize
import com.shiny.raisingmoney.core.designsystem.util.noRippleClick
import com.shiny.raisingmoney.core.designsystem.util.rippleClick
import com.shiny.raisingmoney.feature.transaction.R
import com.shiny.raisingmoney.feature.transaction.dummy.AssetRes
import com.shiny.raisingmoney.feature.transaction.dummy.Categories
import com.shiny.raisingmoney.feature.transaction.model.Category
import com.shiny.raisingmoney.feature.transaction.type.ActiveInput
import com.shiny.raisingmoney.feature.transaction.util.formatAmount
import com.shiny.raisingmoney.feature.transaction.util.formatDate
import com.shiny.raisingmoney.feature.transaction.util.getTodayUtcMillis

/**
 * "지출" 탭 전용 입력 영역: 스크롤되는 입력 폼 + 하단 커스텀 키패드/피커/액션바.
 * [uiState]만으로 그려지는 stateless 컴포저블 — 실제 상태는 [TransactionViewModel]이 들고 있고,
 * 모든 사용자 조작은 [onEvent]로 올려보낸다.
 * `FocusRequester`/`ScrollState`처럼 컴포지션에 묶여 ViewModel로 옮길 수 없는 것만
 * 로컬 remember로 남겨놨다.
 */
@Composable
internal fun ExpenseContent(
    uiState: ExpenseFormUiState,
    onEvent: (ExpenseFormEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val scroll = rememberScrollState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val contentRowFocusRequester = remember { FocusRequester() }

    val date = remember(uiState.dateMillis) { formatDate(uiState.dateMillis) }

    Column(modifier) {
        if (uiState.showDatePicker) {
            DatePickerModal(
                initialSelectedDateMillis = uiState.dateMillis,
                onClose = { selectedMillis ->
                    onEvent(ExpenseFormEvent.DatePicked(selectedMillis))
                },
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scroll),
        ) {
            FormRow(
                label = stringResource(R.string.label_date),
                active = uiState.showDatePicker,
                onClick = {
                    focusManager.clearFocus()
                    onEvent(ExpenseFormEvent.DateRowClicked)
                },
            ) {
                Text(
                    text = date,
                    color = TextPrimary,
                    fontSize = dpFontSize(17.dp),
                )
                Spacer(Modifier.weight(1f))
                RepeatInstallmentButton {
                    // TODO 반복/할부 선택하는 BottomSheet 띄우기
                }
            }

            FormRow(
                label = stringResource(R.string.label_amount),
                active = uiState.activeField == ActiveInput.AMOUNT,
                onClick = {
                    focusManager.clearFocus()
                    onEvent(ExpenseFormEvent.AmountRowClicked)
                },
            ) {
                ValueOrPlaceholder(
                    text = if (uiState.amount.isDigitsOnly()) {
                        formatAmount(uiState.amount)
                    } else {
                        uiState.amount
                    },
                )
            }

            FormRow(
                label = stringResource(R.string.label_category),
                active = uiState.activeField == ActiveInput.CATEGORY,
                onClick = {
                    focusManager.clearFocus()
                    onEvent(ExpenseFormEvent.CategoryRowClicked)
                },
            ) {
                ValueOrPlaceholder(text = uiState.category?.displayText() ?: "")
            }

            FormRow(
                label = stringResource(R.string.label_asset),
                active = uiState.activeField == ActiveInput.ASSET,
                onClick = {
                    focusManager.clearFocus()
                    onEvent(ExpenseFormEvent.AssetRowClicked)
                },
            ) {
                ValueOrPlaceholder(text = uiState.assetRes?.let { stringResource(it) } ?: "")
            }

            FormRow(
                label = stringResource(R.string.label_content),
                active = uiState.activeField == ActiveInput.CONTENT,
                onClick = {
                    onEvent(ExpenseFormEvent.ContentFocused)
                    contentRowFocusRequester.requestFocus()
                    keyboardController?.show() // NOTE: 키보드 표시를 위해 기기별 편차 대비로 추가된 코드.
                },
            ) {
                ContentRow(
                    value = uiState.content,
                    isImportantContent = uiState.isImportantContent,
                    active = uiState.activeField == ActiveInput.CONTENT,
                    focusRequester = contentRowFocusRequester,
                    onValueChange = { onEvent(ExpenseFormEvent.ContentChanged(it)) },
                    onImportantMarkClick = { onEvent(ExpenseFormEvent.ImportantMarkToggled) },
                    onFocused = { onEvent(ExpenseFormEvent.ContentFocused) },
                    onCleared = { onEvent(ExpenseFormEvent.ContentCleared) },
                    onDoneClick = {
                        focusManager.clearFocus()
                        onEvent(ExpenseFormEvent.ContentDone)
                    },
                )
            }

            Spacer(Modifier.height(12.dp))

            Box(
                Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .background(SectionBand),
            )

            AdditionalInputRow(
                value = uiState.additionalInput,
                onValueChange = { onEvent(ExpenseFormEvent.AdditionalInputChanged(it)) },
                onFocused = { onEvent(ExpenseFormEvent.AdditionalInputFocused) },
                onDoneClick = {
                    focusManager.clearFocus()
                    onEvent(ExpenseFormEvent.AdditionalInputDone)
                },
            )
        }

        ExpenseBottomBar(
            active = uiState.activeField,
            saved = uiState.isSaved,
            onAmountBackspace = { onEvent(ExpenseFormEvent.AmountBackspacePressed) },
            onAmountEquals = { onEvent(ExpenseFormEvent.AmountEqualsPressed) },
            onAmountConfirm = { onEvent(ExpenseFormEvent.AmountConfirmPressed) },
            onAmountKeyInput = { key -> onEvent(ExpenseFormEvent.AmountKeyPressed(key)) },
            onAmountKeypadClose = { onEvent(ExpenseFormEvent.AmountKeypadClosed) },
            onCategoryPick = { category -> onEvent(ExpenseFormEvent.CategoryPicked(category)) },
            onCategoryPickerClose = { onEvent(ExpenseFormEvent.CategoryPickerClosed) },
            onAssetPick = { assetRes -> onEvent(ExpenseFormEvent.AssetPicked(assetRes)) },
            onAssetPickerClose = { onEvent(ExpenseFormEvent.AssetPickerClosed) },
            onDelete = {
                // TODO local DB 내역 삭제 기능 추가
                onEvent(ExpenseFormEvent.DeleteClicked)
            },
            onCopy = { /* TODO 작성된 내역 복사하기 기능 추가 */ },
            onBookmark = { /* TODO 자주사용 내역 기능 추가 */ },
            onSave = {
                /* TODO
                    입력된 내용이 없다면 Toast 띄우고 저장되지 않도록 수정.
                    local DB에 내역 저장하기
                 */
                focusManager.clearFocus()
                onEvent(ExpenseFormEvent.SaveClicked)
            },
            onContinue = {
                focusManager.clearFocus()
                // TODO 입력된 내용 local DB에 저장하고 입력폼 비워주기.
                onEvent(ExpenseFormEvent.ContinueClicked)
            },
        )
    }
}

/**
 * 화면 하단의 동적 영역: [active] 값에 따라 금액 키패드 / 분류·자산 피커 /
 * 저장·계속 버튼 / 저장 후 액션 버튼 중 하나를 보여준다.
 * "지금 하단에 뭘 보여줄지"를 [ExpenseContent] 본문에서 분리해,
 * [ExpenseContent]가 상태 선언 → 폼 → 하단 영역 순으로 "무엇을 하는지" 읽히게 했다.
 */
@Composable
private fun ExpenseBottomBar(
    active: ActiveInput,
    saved: Boolean,
    onAmountBackspace: () -> Unit,
    onAmountEquals: () -> Unit,
    onAmountConfirm: () -> Unit,
    onAmountKeyInput: (String) -> Unit,
    onAmountKeypadClose: () -> Unit,
    onCategoryPick: (Category) -> Unit,
    onCategoryPickerClose: () -> Unit,
    onAssetPick: (Int) -> Unit,
    onAssetPickerClose: () -> Unit,
    onDelete: () -> Unit,
    onCopy: () -> Unit,
    onBookmark: () -> Unit,
    onSave: () -> Unit,
    onContinue: () -> Unit,
) {
    when (active) {
        ActiveInput.AMOUNT -> {
            /*
             * 문자열이 아니라 의미 있는 콜백(onAmountBackspace 등)으로 분기하므로,
             * 금액 키패드 안에서 로케일에 따라 달라질 수 있는 "확인" 라벨과 실제 계산 로직이 결합되지 않는다.
             */
            val confirmLabel = stringResource(R.string.keypad_confirm)
            AmountKeypad(
                onKey = { key ->
                    when (key) {
                        "⌫" -> onAmountBackspace()
                        "=" -> onAmountEquals()
                        confirmLabel -> onAmountConfirm()
                        else -> onAmountKeyInput(key)
                    }
                },
                onClose = onAmountKeypadClose,
            )
        }

        ActiveInput.CATEGORY -> PickerSheet(
            title = stringResource(R.string.label_category),
            cells = Categories.map { it.displayText() },
            onPick = { index -> onCategoryPick(Categories[index]) },
            onClose = onCategoryPickerClose,
        )

        ActiveInput.ASSET -> PickerSheet(
            title = stringResource(R.string.label_asset),
            cells = AssetRes.map { stringResource(it) },
            onPick = { index -> onAssetPick(AssetRes[index]) },
            onClose = onAssetPickerClose,
        )

        else -> if (saved) {
            SavedActionRow(onDelete = onDelete, onCopy = onCopy, onBookmark = onBookmark)
        } else {
            SaveButtons(onSave = onSave, onContinue = onContinue)
        }
    }
}

@Composable
private fun FormRow(
    label: String,
    active: Boolean,
    onClick: () -> Unit,
    trailingContent: @Composable RowScope.() -> Unit,
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 20.dp)
                .noRippleClick { onClick() },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // NOTE: label text가 길어서 글자가 짤리면 label의 문자수를 줄이는걸로.
            Text(
                text = label,
                modifier = Modifier.width(52.dp),
                color = TextSecondary,
                fontSize = dpFontSize(16.dp),
            )
            Spacer(Modifier.width(22.dp))
            trailingContent()
        }
        HorizontalDivider(
            modifier = Modifier.padding(start = 84.dp, end = 20.dp),
            color = if (active) Coral else HairLine,
            thickness = 1.dp,
        )
    }
}

@Composable
private fun RowScope.ValueOrPlaceholder(
    text: String,
    hintMessage: String? = null,
) {
    Text(
        text = text,
        modifier = Modifier.weight(1f),
        color = if (hintMessage.isNullOrBlank()) TextPrimary else TextTertiary,
        fontSize = dpFontSize(17.dp),
    )
}

@Composable
private fun RepeatInstallmentButton(
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier.rippleClick(shape = RoundedCornerShape(6.dp)) {
            onClick()
        },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            imageVector = Icons.Filled.Refresh,
            contentDescription = null,
            tint = TextSecondary,
            modifier = Modifier.size(22.dp),
        )
        Text(
            text = stringResource(R.string.repeat_installment),
            fontSize = dpFontSize(11.dp),
            color = TextSecondary,
        )
    }
}

@Composable
private fun ContentRow(
    value: String,
    isImportantContent: Boolean,
    active: Boolean,
    focusRequester: FocusRequester,
    onValueChange: (String) -> Unit,
    onImportantMarkClick: () -> Unit,
    onFocused: () -> Unit,
    onCleared: () -> Unit,
    onDoneClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = true,
            singleLine = true,
            textStyle = LocalTextStyle.current.copy(color = TextPrimary, fontSize = dpFontSize(17.dp)),
            cursorBrush = SolidColor(Coral),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onDoneClick() }),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .focusRequester(focusRequester)
                .onFocusEvent { if (it.isFocused) onFocused() },
        )
        Spacer(Modifier.width(14.dp))
        if (active && value.isNotEmpty()) {
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "지우기",
                tint = TextTertiary,
                modifier = Modifier
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(HairLine)
                    .padding(3.dp)
                    .rippleClick { onCleared() },
            )
            Spacer(Modifier.width(14.dp))
        }
        ImportantMarkButton(
            isImportantContent = isImportantContent,
            onImportantContentClick = onImportantMarkClick,
        )
    }
}

@Composable
private fun ImportantMarkButton(
    isImportantContent: Boolean,
    onImportantContentClick: () -> Unit,
) {
    val borderShape = RoundedCornerShape(6.dp)
    Box(
        modifier = Modifier
            .size(26.dp)
            .border(
                1.5.dp,
                if (isImportantContent) Coral else TextTertiary,
                borderShape,
            )
            .rippleClick(shape = borderShape) {
                onImportantContentClick()
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "!",
            color = if (isImportantContent) Coral else TextTertiary,
            fontWeight = FontWeight.Bold,
            fontSize = dpFontSize(14.dp),
        )
    }
}

@Composable
private fun AdditionalInputRow(
    value: String,
    onValueChange: (String) -> Unit,
    onFocused: () -> Unit,
    onDoneClick: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }
    var focused by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(Modifier.padding(horizontal = 20.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .noRippleClick {
                    focusRequester.requestFocus()
                    keyboardController?.show()
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = LocalTextStyle.current.copy(
                    color = TextPrimary,
                    fontSize = dpFontSize(16.dp),
                ),
                cursorBrush = SolidColor(Coral),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        onDoneClick()
                    },
                ),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (!focused && value.isEmpty()) {
                            Text(
                                stringResource(R.string.additional_input_hint),
                                color = TextSecondary,
                                fontSize = dpFontSize(16.dp),
                            )
                        }
                        innerTextField()
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
                    .onFocusEvent {
                        focused = it.isFocused
                        if (it.isFocused) onFocused()
                    },
            )
            Spacer(Modifier.width(12.dp))
            Icon(
                imageVector = CameraGlyph,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .rippleClick {
                        /* TODO
                        ImagePicker 뜨도록 수정.
                        Image 추가하면 하단에 뜨도록 수정.
                         */
                    }
                    .padding(10.dp),
            )
        }
        HorizontalDivider(color = if (focused) Coral else HairLine)
        Spacer(Modifier.height(14.dp))
        /* TODO ImagePicker로 첨부한 이미지 보여주기
        if (image != null) {

        }*/
        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun SaveButtons(onSave: () -> Unit, onContinue: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
                .height(56.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Coral)
                .rippleClick { onSave() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.action_save),
                color = Color.White,
                fontSize = dpFontSize(17.dp),
                fontWeight = FontWeight.Bold,
            )
        }
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(56.dp)
                .clip(RoundedCornerShape(10.dp))
                .border(1.dp, BorderGray, RoundedCornerShape(10.dp))
                .rippleClick { onContinue() },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = stringResource(R.string.action_continue),
                color = TextPrimary,
                fontSize = dpFontSize(17.dp),
            )
        }
    }
}

@Composable
private fun SavedActionRow(
    onDelete: () -> Unit,
    onCopy: () -> Unit,
    onBookmark: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedPill(
            text = stringResource(R.string.action_delete),
            modifier = Modifier.weight(1f),
            onClick = onDelete,
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp),
            )
        }
        OutlinedPill(
            text = stringResource(R.string.action_copy),
            modifier = Modifier.weight(1f),
            onClick = onCopy,
        ) {
            Icon(
                imageVector = CopyGlyph,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp),
            )
        }
        OutlinedPill(
            text = stringResource(R.string.action_mark_favorite),
            modifier = Modifier.weight(1f),
            onClick = onBookmark,
        ) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun OutlinedPill(
    text: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    leading: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .height(52.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = 1.dp,
                color = BorderGray,
                shape = RoundedCornerShape(10.dp),
            )
            .rippleClick { onClick() },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        leading()
        Spacer(Modifier.width(6.dp))
        Text(
            text = text,
            color = TextPrimary,
            fontSize = dpFontSize(14.dp),
        )
    }
}

@Composable
private fun AmountKeypad(onKey: (String) -> Unit, onClose: () -> Unit) {
    val confirmLabel = stringResource(R.string.keypad_confirm)
    val keypadRows = remember(confirmLabel) {
        listOf(
            listOf("+", "-", "×", "÷"),
            listOf("7", "8", "9", "="),
            listOf("4", "5", "6", "."),
            listOf("1", "2", "3", "⌫"),
            listOf("", "0", "", confirmLabel),
        )
    }

    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(KeypadHeader)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(R.string.label_amount),
                color = Color.White,
                fontSize = dpFontSize(16.dp),
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "🌐",
                modifier = Modifier.rippleClick { /* TODO 보조 화폐 설정화면으로 이동 */ },
                fontSize = dpFontSize(18.dp),
            )
            Spacer(Modifier.width(20.dp))
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "닫기",
                tint = Color.White,
                modifier = Modifier
                    .size(22.dp)
                    .rippleClick { onClose() },
            )
        }

        keypadRows.forEach { row ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            ) {
                row.forEach { key ->
                    val isConfirm = key == confirmLabel
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .then(
                                if (key.isEmpty()) {
                                    Modifier
                                } else {
                                    Modifier.border(
                                        0.5.dp,
                                        KeypadDivider,
                                    )
                                },
                            )
                            .background(if (isConfirm) Coral else Color.White)
                            .rippleClick(enabled = key.isNotEmpty()) { onKey(key) },
                        contentAlignment = Alignment.Center,
                    ) {
                        if (key.isNotEmpty()) {
                            Text(
                                text = key,
                                color = if (isConfirm) Color.White else KeypadKeyText,
                                fontSize = if (isConfirm) dpFontSize(17.dp) else dpFontSize(20.dp),
                                fontWeight = if (isConfirm) FontWeight.Bold else FontWeight.Normal,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PickerSheet(
    title: String,
    cells: List<String>,
    onPick: (Int) -> Unit,
    onClose: () -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(KeypadHeader)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                color = Color.White,
                fontSize = dpFontSize(16.dp),
            )
            Spacer(Modifier.weight(1f))
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "편집",
                tint = Color.White,
                modifier = Modifier
                    .size(20.dp)
                    .rippleClick { /* TODO 항목 순서 변경 화면으로 이동 */ },
            )
            Spacer(Modifier.width(20.dp))
            Icon(
                imageVector = Icons.Filled.Close,
                contentDescription = "닫기",
                tint = Color.White,
                modifier = Modifier
                    .size(22.dp)
                    .rippleClick { onClose() },
            )
        }

        val cellAddStr = stringResource(R.string.add_item)
        val addItemIndex = cells.size
        val allCells = cells + cellAddStr
        allCells.withIndex().chunked(3).forEach { rowCells ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(64.dp),
            ) {
                rowCells.forEach { (index, label) ->
                    val isAddItem = index == addItemIndex
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .border(
                                width = 0.5.dp,
                                color = SheetGridLine,
                            )
                            .rippleClick {
                                if (isAddItem) {
                                    /* TODO 항목 추가 화면으로 이동. 항목 최대 갯수 지정 필요. */
                                } else {
                                    onPick(index)
                                }
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = label,
                            color = if (isAddItem) TextSecondary else TextPrimary,
                            fontSize = dpFontSize(15.dp),
                            textAlign = TextAlign.Center,
                        )
                    }
                }
                repeat(3 - rowCells.size) {
                    Box(
                        Modifier
                            .weight(1f)
                            .fillMaxSize()
                            .border(0.5.dp, SheetGridLine),
                    )
                }
            }
        }
    }
}

/**
 * Material3 [DatePickerDialog]. 확인 버튼 또는 바깥 영역 터치로 닫히며,
 * 두 경우 모두 현재 선택된 날짜(UTC 자정 millis, 없으면 null)를 [onClose]로 전달한다.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerModal(
    initialSelectedDateMillis: Long?,
    onClose: (selectedDateMillis: Long?) -> Unit,
) {
    val state = rememberDatePickerState(initialSelectedDateMillis = initialSelectedDateMillis)
    DatePickerDialog(
        onDismissRequest = { onClose(state.selectedDateMillis) },
        confirmButton = {
            TextButton(onClick = { onClose(state.selectedDateMillis) }) {
                Text(stringResource(R.string.keypad_confirm))
            }
        },
    ) {
        DatePicker(state = state)
    }
}

/* ------------------------------------------------------------------------- */
/*  Preview                                                                  */
/* ------------------------------------------------------------------------- */

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun ExpenseContentPreview() {
    RaisingMoneyTheme {
        Column(
            Modifier
                .fillMaxSize()
                .background(ScreenBackground),
        ) {
            ExpenseContent(
                uiState = ExpenseFormUiState(dateMillis = getTodayUtcMillis()),
                onEvent = {},
                modifier = Modifier.weight(1f),
            )
        }
    }
}
