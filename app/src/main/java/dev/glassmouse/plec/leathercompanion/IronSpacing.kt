package dev.glassmouse.plec.leathercompanion

enum class IronSpacing(val spacingMm: Double) {
    MM_2(2.0),
    MM_2_3(2.3),
    MM_2_45(2.45),
    MM_2_7(2.7),
    MM_3(3.0),
    MM_3_38(3.38),
    MM_3_85(3.85),
    MM_4(4.0),
    MM_4_3(4.3),
    MM_5(5.0),
    MM_5_2(5.2);

    val label: String
        get() = "$spacingMm mm"
}