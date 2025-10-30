package jp.co.tanocee.bikdecimal

import platform.Foundation.NSDecimalNumber

actual typealias NativeBigDecimal = NSDecimalNumber

actual class BikDecimal {
  private val value: NSDecimalNumber

  actual constructor(value: String) {
    this.value = NSDecimalNumber(string = value)
  }

  actual constructor(value: Double) {
    this.value = NSDecimalNumber(double = value)
  }

  actual constructor(value: Long) {
    this.value = NSDecimalNumber(longLong = value)
  }

  private constructor(decimalNumber: NSDecimalNumber) {
    this.value = decimalNumber
  }

  actual val nativeBigDecimal: NativeBigDecimal
    get() = value

  actual fun toPlainString(): String = value.stringValue

  actual fun toDouble(): Double = value.doubleValue

  actual fun toLong(): Long = value.longLongValue

  actual fun negative(): BikDecimal =
    BikDecimal(value.decimalNumberByMultiplyingBy(NSDecimalNumber(-1.0)))

  actual operator fun plus(other: BikDecimal): BikDecimal =
    BikDecimal(value.decimalNumberByAdding(other.value))

  actual operator fun minus(other: BikDecimal): BikDecimal =
    BikDecimal(value.decimalNumberBySubtracting(other.value))

  actual operator fun times(other: BikDecimal): BikDecimal =
    BikDecimal(value.decimalNumberByMultiplyingBy(other.value))

  actual operator fun div(other: BikDecimal): BikDecimal =
    BikDecimal(value.decimalNumberByDividingBy(other.value))

  actual operator fun compareTo(other: BikDecimal): Int =
    value.compare(other.value).toInt()

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other !is BikDecimal) return false
    return value.compare(other.value).toInt() == 0
  }

  override fun hashCode(): Int = value.hashCode()

  override fun toString(): String = toPlainString()

  actual companion object {
    actual val ZERO: BikDecimal = BikDecimal(NSDecimalNumber.zero)
    actual val ONE: BikDecimal = BikDecimal(NSDecimalNumber.one)
  }
}
