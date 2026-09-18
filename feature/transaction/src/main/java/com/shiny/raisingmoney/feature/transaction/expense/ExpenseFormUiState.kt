package com.shiny.raisingmoney.feature.transaction.expense

import com.shiny.raisingmoney.feature.transaction.model.Category
import com.shiny.raisingmoney.feature.transaction.type.ActiveInput
import com.shiny.raisingmoney.feature.transaction.util.getTodayUtcMillis
import java.math.BigDecimal
import java.math.RoundingMode

/** "지출" 폼 화면 상태. UI(ExpenseContent)는 이 값만으로 그려지는 stateless 함수다. */
data class ExpenseFormUiState(
    val dateMillis: Long,
    val amount: String = "",
    val category: Category? = null,
    val assetRes: Int? = null,
    val content: String = "",
    val isImportantContent: Boolean = false,
    val additionalInput: String = "",
    val activeField: ActiveInput = ActiveInput.NONE,
    val showDatePicker: Boolean = false,
    val isSaved: Boolean = false,
)

/** [event]를 반영한 다음 상태. 순수 함수라 ViewModel 없이도 단위 테스트 가능. */
fun ExpenseFormUiState.handleEvent(event: ExpenseFormEvent): ExpenseFormUiState = when (event) {
    ExpenseFormEvent.DateRowClicked -> copy(
        isSaved = false,
        activeField = ActiveInput.NONE,
        showDatePicker = true
    )
    is ExpenseFormEvent.DatePicked -> copy(
        dateMillis = event.millis ?: dateMillis,
        showDatePicker = false,
    )
    ExpenseFormEvent.AmountRowClicked -> copy(
        isSaved = false,
        activeField = ActiveInput.AMOUNT
    )
    ExpenseFormEvent.AmountBackspacePressed -> copy(amount = amount.dropLast(1))
    ExpenseFormEvent.AmountEqualsPressed -> copy(
        amount = evaluateAmountExpression(amount)?.toString() ?: amount
    )
    ExpenseFormEvent.AmountConfirmPressed -> copy(
        amount = evaluateAmountExpression(amount)?.toString() ?: amount,
        activeField = ActiveInput.NONE,
    )
    is ExpenseFormEvent.AmountKeyPressed -> copy(amount = nextAmountExpression(amount, event.key))
    ExpenseFormEvent.AmountKeypadClosed -> copy(activeField = ActiveInput.NONE)
    ExpenseFormEvent.CategoryRowClicked -> copy(
        isSaved = false,
        activeField = ActiveInput.CATEGORY
    )
    is ExpenseFormEvent.CategoryPicked -> copy(
        category = event.category,
        activeField = ActiveInput.NONE
    )
    ExpenseFormEvent.CategoryPickerClosed -> copy(activeField = ActiveInput.NONE)
    ExpenseFormEvent.AssetRowClicked -> copy(
        isSaved = false,
        activeField = ActiveInput.ASSET
    )
    is ExpenseFormEvent.AssetPicked -> copy(
        assetRes = event.assetRes,
        activeField = ActiveInput.NONE
    )
    ExpenseFormEvent.AssetPickerClosed -> copy(activeField = ActiveInput.NONE)
    is ExpenseFormEvent.ContentChanged -> copy(content = event.value)
    ExpenseFormEvent.ContentFocused -> copy(
        isSaved = false,
        activeField = ActiveInput.CONTENT
    )
    ExpenseFormEvent.ContentCleared -> copy(content = "")
    ExpenseFormEvent.ContentDone -> copy(activeField = ActiveInput.NONE)
    ExpenseFormEvent.ImportantMarkToggled -> copy(isImportantContent = !isImportantContent)
    is ExpenseFormEvent.AdditionalInputChanged -> copy(additionalInput = event.value)
    ExpenseFormEvent.AdditionalInputFocused -> copy(
        isSaved = false,
        activeField = ActiveInput.ADDITIONAL_INPUT
    )
    ExpenseFormEvent.AdditionalInputDone -> copy(activeField = ActiveInput.NONE)
    ExpenseFormEvent.SaveClicked -> copy(
        isSaved = true,
        activeField = ActiveInput.NONE
    )
    ExpenseFormEvent.ContinueClicked -> copy(
        amount = "",
        category = null,
        assetRes = null,
        content = "",
        additionalInput = "",
        activeField = ActiveInput.NONE,
    )
    ExpenseFormEvent.DeleteClicked -> copy(
        dateMillis = getTodayUtcMillis(),
        amount = "",
        category = null,
        assetRes = null,
        content = "",
        additionalInput = "",
        activeField = ActiveInput.NONE,
        isSaved = false,
    )
}

/**
 * 피연산자 하나(연산자로 구분되는 각 숫자)의 최대 자리수.
 * 최종 결과가 [MAX_SAFE_AMOUNT](Long 기준 18자리)를 넘지 않으려면, 곱셈(n자리 × n자리 =
 * 최대 2n자리)이 가장 빡빡한 제약이 된다 — 9자리씩이면 곱해도 최대 18자리까지만 나온다.
 */
private const val MAX_OPERAND_DIGITS = 9
private val AmountOperators = setOf("+", "-", "×", "÷")

/** Long이 표현할 수 있는 범위 안에서, 금액으로 허용할 최댓값(18자리). */
private val MAX_SAFE_AMOUNT: BigDecimal = BigDecimal("999999999999999999")

/**
 * 금액 키패드의 숫자/연산자/소수점 키 입력을 [current] 식에 반영한 다음 식을 돌려준다.
 * 연산자·소수점이 연속으로 들어가거나 식이 비어 연산자로 시작하는 등 잘못된 입력은
 * 무시하고 [current]를 그대로 돌려준다. 숫자 입력은 전체 문자열 길이가 아니라, 현재
 * 입력 중인 피연산자(연산자 기준으로 나눈 숫자 하나)의 자리수가 [MAX_OPERAND_DIGITS]를
 * 넘지 않는 선에서만 허용한다.
 */
private fun nextAmountExpression(current: String, key: String): String {
    return when (key) {
        in AmountOperators -> {
            if (current.isEmpty() || current.last() in "+-×÷.") current else current + key
        }
        "." -> {
            val lastNumberSegment = current.takeLastWhile { it !in "+-×÷" }
            if ("." in lastNumberSegment) current else current + key
        }
        else -> { // 숫자
            val lastNumberSegment = current.takeLastWhile { it !in "+-×÷" }
            val digitCount = lastNumberSegment.count { it.isDigit() }
            if (digitCount >= MAX_OPERAND_DIGITS) current else current + key
        }
    }
}

/**
 * "3+1×2" 같은 단순 사칙연산 식을 계산한다. ×÷가 +-보다 먼저 계산되는 일반적인 우선순위를
 * 따르며, 결과는 반올림해 정수 원 단위 문자열로 돌려준다. 연산자로 끝나는 등 완성되지
 * 않은 식이거나 0으로 나누면 null(= 계산하지 않고 그대로 둔다).
 *
 * `Double` 대신 [BigDecimal]로 계산한다 — 피연산자가 [MAX_OPERAND_DIGITS]자리(9자리)까지
 * 허용되고 곱셈 결과가 18자리에 달할 수 있는데, `Double`의 유효자리(약 15~16자리)로는 그
 * 범위에서 오차가 생길 수 있기 때문이다. 최종 결과가 [MAX_SAFE_AMOUNT](Long 기준 18자리)를
 * 넘으면 문자열 길이가 아니라 실제 값 기준으로 판단해 null을 돌려준다(= 계산 취소).
 */
private fun evaluateAmountExpression(expr: String): Long? {
    if (expr.isEmpty()) return null
    var pos = 0

    fun peek(): Char? = expr.getOrNull(pos)

    fun parseNumber(): BigDecimal? {
        val start = pos
        while (peek()?.isDigit() == true || peek() == '.') pos++
        if (pos == start) return null
        return expr.substring(start, pos).toBigDecimalOrNull()
    }

    fun parseTerm(): BigDecimal? {
        var value = parseNumber() ?: return null
        while (true) {
            when (peek()) {
                '×' -> { pos++; value = value.multiply(parseNumber() ?: return null) }
                '÷' -> {
                    pos++
                    val divisor = parseNumber() ?: return null
                    if (divisor.signum() == 0) return null
                    value = value.divide(divisor, 10, RoundingMode.HALF_UP)
                }
                else -> return value
            }
        }
    }

    fun parseExpr(): BigDecimal? {
        var value = parseTerm() ?: return null
        while (true) {
            when (peek()) {
                '+' -> { pos++; value = value.add(parseTerm() ?: return null) }
                '-' -> { pos++; value = value.subtract(parseTerm() ?: return null) }
                else -> return value
            }
        }
    }

    val result = parseExpr() ?: return null
    if (pos != expr.length) return null // 식을 끝까지 못 읽었으면(예: "3+" ) 미완성으로 간주
    val rounded = result.setScale(0, RoundingMode.HALF_UP)
    if (rounded.abs() > MAX_SAFE_AMOUNT) return null // Long 기준 18자리 초과 — 계산하지 않고 그대로 둔다
    return rounded.toLong()
}
