package au.edu.jcu.assessment.utilityapp.domain.model

import androidx.annotation.StringRes
import au.edu.jcu.assessment.utilityapp.R

/** Scales the needs of every activity */
enum class Picky(@StringRes val label: Int, val scale: Double) {
    RELAXED(R.string.picky_relaxed, 0.7),
    NORMAL(R.string.picky_normal, 1.0),
    STRICT(R.string.picky_strict, 1.5)
}

/** How much data a test is allowed to move */
enum class TestDepth(
    @StringRes val label: Int,
    val downloadBytes: Long,
    val uploadBytes: Int,
    val approxMb: Int
) {
    QUICK(R.string.depth_quick, 0, 0, 1),
    STANDARD(R.string.depth_standard, 8_000_000, 2_000_000, 10),
    THOROUGH(R.string.depth_thorough, 25_000_000, 8_000_000, 33)
}

/** User choices that change the main screen display */
data class Settings(
    val usage: Usage = Usage.CALL,
    val picky: Picky = Picky.NORMAL,
    val depth: TestDepth = TestDepth.STANDARD,
    val dataSaver: Boolean = true,
    val haptics: Boolean = true,
    val useMbps: Boolean = true
)