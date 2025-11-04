package jp.co.tanocee.bikdecimal

import java.math.BigDecimal
import java.math.RoundingMode as JavaRoundingMode

actual typealias NativeBigDecimal = BigDecimal

/**
 * Convert BikDecimal RoundingMode to Java RoundingMode
 */
internal fun RoundingMode.toJavaRoundingMode(): JavaRoundingMode = when (this) {
  RoundingMode.UP -> JavaRoundingMode.UP
  RoundingMode.DOWN -> JavaRoundingMode.DOWN
  RoundingMode.CEILING -> JavaRoundingMode.CEILING
  RoundingMode.FLOOR -> JavaRoundingMode.FLOOR
  RoundingMode.HALF_UP -> JavaRoundingMode.HALF_UP
  RoundingMode.HALF_DOWN -> JavaRoundingMode.HALF_DOWN
  RoundingMode.HALF_EVEN -> JavaRoundingMode.HALF_EVEN
  RoundingMode.UNNECESSARY -> JavaRoundingMode.UNNECESSARY
}

actual class BikDecimal {
  private val value: BigDecimal

  actual constructor(value: String) {
    this.value = BigDecimal(value)
  }

  actual constructor(value: Double) {
    this.value = BigDecimal.valueOf(value)
  }

  actual constructor(value: Long) {
    this.value = BigDecimal.valueOf(value)
  }

  actual constructor(value: String, scale: Int, roundingMode: RoundingMode) {
    this.value = if (scale >= 0) {
      BigDecimal(value).setScale(scale, roundingMode.toJavaRoundingMode())
    } else {
      BigDecimal(value)
    }
  }

  actual constructor(value: Double, scale: Int, roundingMode: RoundingMode) {
    this.value = if (scale >= 0) {
      BigDecimal.valueOf(value).setScale(scale, roundingMode.toJavaRoundingMode())
    } else {
      BigDecimal.valueOf(value)
    }
  }

  actual constructor(value: Long, scale: Int, roundingMode: RoundingMode) {
    this.value = if (scale >= 0) {
      BigDecimal.valueOf(value).setScale(scale, roundingMode.toJavaRoundingMode())
    } else {
      BigDecimal.valueOf(value)
    }
  }

  private constructor(bigDecimal: BigDecimal) {
    this.value = bigDecimal
  }

  actual val nativeBigDecimal: NativeBigDecimal
    get() = value

  actual val scale: Int
    get() = value.scale()

  actual fun toPlainString(): String = value.toPlainString()

  actual fun toDouble(): Double = value.toDouble()

  actual fun toLong(): Long = value.toLong()

  actual fun negative(): BikDecimal = BikDecimal(value.negate())

  actual fun setScale(newScale: Int, roundingMode: RoundingMode): BikDecimal =
    BikDecimal(value.setScale(newScale, roundingMode.toJavaRoundingMode()))

  actual operator fun plus(other: BikDecimal): BikDecimal =
    BikDecimal(value.plus(other.value))

  actual operator fun minus(other: BikDecimal): BikDecimal =
    BikDecimal(value.minus(other.value))

  actual operator fun times(other: BikDecimal): BikDecimal =
    BikDecimal(value.times(other.value))

  actual operator fun div(other: BikDecimal): BikDecimal =
    BikDecimal(value.div(other.value))

  actual fun divide(other: BikDecimal, scale: Int, roundingMode: RoundingMode): BikDecimal =
    BikDecimal(value.divide(other.value, scale, roundingMode.toJavaRoundingMode()))

  actual operator fun compareTo(other: BikDecimal): Int = value.compareTo(other.value)

  actual companion object {
    actual val ZERO: BikDecimal = BikDecimal(BigDecimal.ZERO)
    actual val ONE: BikDecimal = BikDecimal(BigDecimal.ONE)
  }
}
