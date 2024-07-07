package dev.glassmouse.plec.leathercompanion

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

class MainViewModel : ViewModel() {

    private val decimalFormatter = DecimalFormatter()

    data class State(
        val holeCount: String,
        val spacing: IronSpacing,
        val thickness: String,
        val finishingLength: String,
        val totalLength: Int?,
        val isCalculateEnabled: Boolean
    )

    sealed class StitchLength {
        abstract val holeCount: Int

        data class Hole(override val holeCount: Int) : StitchLength()
        data class Distance(val length: Int, val spacing: Double) : StitchLength() {
            override val holeCount: Int
                get() = floor(length / spacing).roundToInt()
        }
    }

    private val _state: MutableStateFlow<State> = MutableStateFlow(
        State(
            holeCount = "",
            spacing = IronSpacing.MM_4,
            thickness = "",
            finishingLength = "200",
            totalLength = null,
            isCalculateEnabled = false
        )
    )

    val state: StateFlow<State> = _state

    fun onHoleCountChanged(holeCount: String) {
        _state.value = _state.value.copy(holeCount = holeCount).updateIsCalculateEnabled()
    }

    fun onThicknessChanged(thickness: String) {
        _state.value = _state.value.copy(thickness = thickness).updateIsCalculateEnabled()
    }

    fun onSpacingChanged(spacing: IronSpacing) {
        _state.value = _state.value.copy(spacing = spacing).updateIsCalculateEnabled()
    }

    fun onFinishingLengthChanged(length: String) {
        _state.value = _state.value.copy(finishingLength = length)
    }

    fun onCalculateClick() {
        val cleanedUp = _state.value.cleanInput()
        _state.value =
            cleanedUp.copy(totalLength = calculateTotalLength(cleanedUp.calculationParams()))
    }

    private fun State.cleanInput(): State = copy(
        holeCount = decimalFormatter.cleanup(holeCount, discardDecimals = true),
        thickness = decimalFormatter.cleanup(thickness),
        finishingLength = decimalFormatter.cleanup(finishingLength, discardDecimals = true)
    )

    private fun State.calculationParams(): CalculationParams = CalculationParams(
        length = StitchLength.Hole(holeCount = holeCount.toIntOrNull() ?: 0),
        spacing = spacing.spacingMm,
        thickness = thickness.toDoubleOrNull() ?: 0.0,
        finishingLength = finishingLength.toIntOrNull() ?: 0,
    )

    private fun State.updateIsCalculateEnabled(): State =
        copy(isCalculateEnabled = this.areCalculationParamsValid())

    private fun State.areCalculationParamsValid(): Boolean = calculationParams().areValid()

    private fun CalculationParams.areValid(): Boolean =
        length.holeCount != 0 && spacing != 0.0 && thickness != 0.0

    private fun calculateTotalLength(params: CalculationParams): Int {
        if (!params.areValid()) {
            return 0
        }
        val (length, spacing, thickness, finishingLength) = params
        val holeCount = length.holeCount.coerceAtLeast(1)

        val horizontalTravel = ceil((holeCount - 1) * spacing * 2).roundToInt()
        val thicknessTravel = (holeCount * thickness * 2).roundToInt()

        return horizontalTravel + thicknessTravel + finishingLength
    }

    private data class CalculationParams(
        val length: StitchLength,
        val spacing: Double,
        val thickness: Double,
        val finishingLength: Int
    )
}