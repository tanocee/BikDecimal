package jp.co.tanocee.bikdecimal

import java.math.BigDecimal

actual typealias NativeBigDecimal = BigDecimal

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

  private constructor(bigDecimal: BigDecimal) {
    this.value = bigDecimal
  }

  actual val nativeBigDecimal: NativeBigDecimal
    get() = value

  actual fun toPlainString(): String = value.toPlainString()

  actual fun toDouble(): Double = value.toDouble()

  actual fun toLong(): Long = value.toLong()

  actual fun negative(): BikDecimal = BikDecimal(value.negate())

  actual operator fun plus(other: BikDecimal): BikDecimal =
    BikDecimal(value.plus(other.value))

  actual operator fun minus(other: BikDecimal): BikDecimal =
    BikDecimal(value.minus(other.value))

  actual operator fun times(other: BikDecimal): BikDecimal =
    BikDecimal(value.times(other.value))

  actual operator fun div(other: BikDecimal): BikDecimal =
    BikDecimal(value.div(other.value))

  actual operator fun compareTo(other: BikDecimal): Int = value.compareTo(other.value)

  actual companion object {
    actual val ZERO: BikDecimal = BikDecimal(BigDecimal.ZERO)
    actual val ONE: BikDecimal = BikDecimal(BigDecimal.ONE)
  }
}
