package jp.co.tanocee.bikdecimal

/**
 * The native BigDecimal representation for the current platform.
 */
expect class NativeBigDecimal

/**
 * A multiplatform arbitrary-precision decimal type.
 */
expect class BikDecimal {
  constructor(value: String)
  constructor(value: Double)
  constructor(value: Long)

  /**
   * The native BigDecimal representation for the current platform.
   */
  val nativeBigDecimal: NativeBigDecimal

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

  operator fun plus(other: BikDecimal): BikDecimal
  operator fun minus(other: BikDecimal): BikDecimal
  operator fun times(other: BikDecimal): BikDecimal
  operator fun div(other: BikDecimal): BikDecimal

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
fun String.toKMPBigDecimal(default: BikDecimal = BikDecimal.ZERO): BikDecimal {
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
