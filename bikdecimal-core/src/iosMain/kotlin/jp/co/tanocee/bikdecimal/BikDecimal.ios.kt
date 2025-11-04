package jp.co.tanocee.bikdecimal

import platform.Foundation.NSDecimalNumber
import platform.Foundation.NSDecimalNumberHandler
import platform.Foundation.NSRoundingMode
import kotlin.math.pow

actual typealias NativeBigDecimal = NSDecimalNumber

/**
 * Convert BikDecimal RoundingMode to NSRoundingMode
 * NSRoundingMode values: NSRoundPlain = 0, NSRoundDown = 1, NSRoundUp = 2, NSRoundBankers = 3
 */
internal fun RoundingMode.toNSRoundingModeValue(): NSRoundingMode = when (this) {
  RoundingMode.UP -> NSRoundingMode.NSRoundUp
  RoundingMode.DOWN -> NSRoundingMode.NSRoundDown
  RoundingMode.CEILING -> NSRoundingMode.NSRoundUp // NSRoundUp (NSDecimalNumber doesn't have CEILING)
  RoundingMode.FLOOR -> NSRoundingMode.NSRoundDown // NSRoundDown (NSDecimalNumber doesn't have FLOOR)
  RoundingMode.HALF_UP -> NSRoundingMode.NSRoundPlain
  RoundingMode.HALF_DOWN -> NSRoundingMode.NSRoundDown
  RoundingMode.HALF_EVEN -> NSRoundingMode.NSRoundBankers
  RoundingMode.UNNECESSARY -> NSRoundingMode.NSRoundPlain
}

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

  actual constructor(value: String, scale: Int, roundingMode: RoundingMode) {
    val decimal = NSDecimalNumber(string = value)
    this.value = if (scale >= 0) {
      val handler = NSDecimalNumberHandler.decimalNumberHandlerWithRoundingMode(
        roundingMode = roundingMode.toNSRoundingModeValue(),
        scale = scale.toShort(),
        raiseOnExactness = false,
        raiseOnOverflow = true,
        raiseOnUnderflow = true,
        raiseOnDivideByZero = true
      )
      val multiplier = NSDecimalNumber(double = 10.0.pow(scale))
      decimal.decimalNumberByMultiplyingBy(multiplier, handler)
        .decimalNumberByDividingBy(multiplier, handler)
    } else {
      decimal
    }
  }

  actual constructor(value: Double, scale: Int, roundingMode: RoundingMode) {
    val decimal = NSDecimalNumber(double = value)
    this.value = if (scale >= 0) {
      val handler = NSDecimalNumberHandler.decimalNumberHandlerWithRoundingMode(
        roundingMode = roundingMode.toNSRoundingModeValue(),
        scale = scale.toShort(),
        raiseOnExactness = false,
        raiseOnOverflow = true,
        raiseOnUnderflow = true,
        raiseOnDivideByZero = true
      )
      val multiplier = NSDecimalNumber(double = 10.0.pow(scale))
      decimal.decimalNumberByMultiplyingBy(multiplier, handler)
        .decimalNumberByDividingBy(multiplier, handler)
    } else {
      decimal
    }
  }

  actual constructor(value: Long, scale: Int, roundingMode: RoundingMode) {
    val decimal = NSDecimalNumber(longLong = value)
    this.value = if (scale >= 0) {
      val handler = NSDecimalNumberHandler.decimalNumberHandlerWithRoundingMode(
        roundingMode = roundingMode.toNSRoundingModeValue(),
        scale = scale.toShort(),
        raiseOnExactness = false,
        raiseOnOverflow = true,
        raiseOnUnderflow = true,
        raiseOnDivideByZero = true
      )
      val multiplier = NSDecimalNumber(double = 10.0.pow(scale))
      decimal.decimalNumberByMultiplyingBy(multiplier, handler)
        .decimalNumberByDividingBy(multiplier, handler)
    } else {
      decimal
    }
  }

  private constructor(decimalNumber: NSDecimalNumber) {
    this.value = decimalNumber
  }

  actual val nativeBigDecimal: NativeBigDecimal
    get() = value

  actual val scale: Int
    get() {
      // Calculate scale from the string representation
      val str = value.stringValue
      val decimalIndex = str.indexOf('.')
      return if (decimalIndex >= 0) {
        str.length - decimalIndex - 1
      } else {
        0
      }
    }

  actual fun toPlainString(): String = value.stringValue

  actual fun toDouble(): Double = value.doubleValue

  actual fun toLong(): Long = value.longLongValue

  actual fun negative(): BikDecimal =
    BikDecimal(value.decimalNumberByMultiplyingBy(NSDecimalNumber(-1.0)))

  actual fun setScale(newScale: Int, roundingMode: RoundingMode): BikDecimal {
    val handler = NSDecimalNumberHandler.decimalNumberHandlerWithRoundingMode(
      roundingMode = roundingMode.toNSRoundingModeValue(),
      scale = newScale.toShort(),
      raiseOnExactness = false,
      raiseOnOverflow = true,
      raiseOnUnderflow = true,
      raiseOnDivideByZero = true
    )

    // Multiply and divide by 1 to apply rounding
    val multiplier = NSDecimalNumber(double = 10.0.pow(newScale))
    val rounded = value.decimalNumberByMultiplyingBy(multiplier, handler)
      .decimalNumberByDividingBy(multiplier, handler)

    return BikDecimal(rounded)
  }

  actual operator fun plus(other: BikDecimal): BikDecimal =
    BikDecimal(value.decimalNumberByAdding(other.value))

  actual operator fun minus(other: BikDecimal): BikDecimal =
    BikDecimal(value.decimalNumberBySubtracting(other.value))

  actual operator fun times(other: BikDecimal): BikDecimal =
    BikDecimal(value.decimalNumberByMultiplyingBy(other.value))

  actual operator fun div(other: BikDecimal): BikDecimal =
    BikDecimal(value.decimalNumberByDividingBy(other.value))

  actual fun divide(other: BikDecimal, scale: Int, roundingMode: RoundingMode): BikDecimal {
    val handler = NSDecimalNumberHandler.decimalNumberHandlerWithRoundingMode(
      roundingMode = roundingMode.toNSRoundingModeValue(),
      scale = scale.toShort(),
      raiseOnExactness = false,
      raiseOnOverflow = true,
      raiseOnUnderflow = true,
      raiseOnDivideByZero = true
    )

    return BikDecimal(value.decimalNumberByDividingBy(other.value, handler))
  }

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
