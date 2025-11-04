package jp.co.tanocee.bikdecimal

/**
 * The native BigDecimal representation for the current platform.
 */
expect class NativeBigDecimal

/**
 * Rounding mode to apply when a result cannot be represented exactly.
 */
enum class RoundingMode {
  /**
   * Rounding mode to round away from zero.
   * Always increments the digit prior to a non-zero discarded fraction.
   */
  UP,

  /**
   * Rounding mode to round towards zero.
   * Never increments the digit prior to a discarded fraction (truncates).
   */
  DOWN,

  /**
   * Rounding mode to round towards positive infinity.
   */
  CEILING,

  /**
   * Rounding mode to round towards negative infinity.
   */
  FLOOR,

  /**
   * Rounding mode to round towards "nearest neighbor" unless both neighbors are equidistant,
   * in which case round up.
   */
  HALF_UP,

  /**
   * Rounding mode to round towards "nearest neighbor" unless both neighbors are equidistant,
   * in which case round down.
   */
  HALF_DOWN,

  /**
   * Rounding mode to round towards the "nearest neighbor" unless both neighbors are equidistant,
   * in which case, round towards the even neighbor.
   */
  HALF_EVEN,

  /**
   * Rounding mode to assert that the requested operation has an exact result,
   * hence no rounding is necessary. If this rounding mode is specified on an operation
   * that yields an inexact result, an ArithmeticException is thrown.
   */
  UNNECESSARY
}

/**
 * A multiplatform arbitrary-precision decimal type.
 */
expect class BikDecimal {
  constructor(value: String)
  constructor(value: Double)
  constructor(value: Long)

  /**
   * Creates a BikDecimal from a String value with specified scale and rounding mode.
   *
   * @param value the string representation of the decimal value
   * @param scale the number of digits to the right of the decimal point. Use -1 to keep the original scale.
   * @param roundingMode the rounding mode to apply when adjusting the scale
   */
  constructor(value: String, scale: Int, roundingMode: RoundingMode)

  /**
   * Creates a BikDecimal from a Double value with specified scale and rounding mode.
   *
   * @param value the double value
   * @param scale the number of digits to the right of the decimal point. Use -1 to keep the original scale.
   * @param roundingMode the rounding mode to apply when adjusting the scale
   */
  constructor(value: Double, scale: Int, roundingMode: RoundingMode)

  /**
   * Creates a BikDecimal from a Long value with specified scale and rounding mode.
   *
   * @param value the long value
   * @param scale the number of digits to the right of the decimal point. Use -1 to keep the original scale.
   * @param roundingMode the rounding mode to apply when adjusting the scale
   */
  constructor(value: Long, scale: Int, roundingMode: RoundingMode)

  /**
   * The native BigDecimal representation for the current platform.
   */
  val nativeBigDecimal: NativeBigDecimal

  /**
   * Returns the scale of this BikDecimal (the number of digits to the right of the decimal point).
   */
  val scale: Int

  /**
   * Returns the string representation of this BikDecimal without an exponent field.
   */
  fun toPlainString(): String

  /**
   * Converts this BikDecimal to a Double.
   */
  fun toDouble(): Double

  /**
   * Converts this BikDecimal to a Long.
   */
  fun toLong(): Long

  /**
   * Returns the negation of this BikDecimal.
   */
  fun negative(): BikDecimal

  /**
   * Returns a BikDecimal whose scale is the specified value, and whose value is
   * numerically equal to this BikDecimal's.
   *
   * @param newScale scale of the BikDecimal value to be returned
   * @param roundingMode The rounding mode to apply
   * @return a BikDecimal whose scale is the specified value
   */
  fun setScale(newScale: Int, roundingMode: RoundingMode = RoundingMode.HALF_UP): BikDecimal

  operator fun plus(other: BikDecimal): BikDecimal
  operator fun minus(other: BikDecimal): BikDecimal
  operator fun times(other: BikDecimal): BikDecimal
  operator fun div(other: BikDecimal): BikDecimal

  /**
   * Returns a BikDecimal whose value is (this / other), with the specified scale and rounding mode.
   *
   * @param other value by which this BikDecimal is to be divided
   * @param scale scale of the BikDecimal quotient to be returned
   * @param roundingMode rounding mode to apply
   * @return this / other
   */
  fun divide(other: BikDecimal, scale: Int, roundingMode: RoundingMode = RoundingMode.HALF_UP): BikDecimal

  operator fun compareTo(other: BikDecimal): Int

  companion object {
    /**
     * The constant value of zero.
     */
    val ZERO: BikDecimal

    /**
     * The constant value of one.
     */
    val ONE: BikDecimal
  }
}

/**
 * Converts the string to a BikDecimal. If the conversion fails, returns the default value.
 */
fun String.toBikDecimal(default: BikDecimal = BikDecimal.ZERO): BikDecimal {
  return runCatching {
    BikDecimal(this)
  }.getOrElse {
    default
  }
}

/**
 * Sums the BikDecimal values produced by the selector function for each element in the iterable.
 */
fun <T> Iterable<T>.sumOf(selector: (T) -> BikDecimal): BikDecimal {
  var sum = BikDecimal.ZERO
  for (element in this) {
    sum += selector(element)
  }
  return sum
}
