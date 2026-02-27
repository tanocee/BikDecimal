package jp.co.tanocee.bikdecimal

/**
 * A pure-Kotlin BigDecimal implementation for the JS platform.
 *
 * Internally stores numbers as an unscaled integer value (String of digits) and a scale.
 * For example, 123.45 is stored as unscaledValue = "12345" and scale = 2.
 */
class JsBigDecimal private constructor(
    private val unscaledValue: String, // Sign-less digit string (e.g. "12345")
    private val _scale: Int,           // Number of decimal places
    private val negative: Boolean      // true if the value is negative
) : Comparable<JsBigDecimal> {

    val scale: Int get() = _scale

    companion object {
        val ZERO = JsBigDecimal("0", 0, false)
        val ONE = JsBigDecimal("1", 0, false)

        /**
         * Create a JsBigDecimal from a string.
         */
        fun fromString(value: String): JsBigDecimal {
            val trimmed = value.trim()
            if (trimmed.isEmpty()) throw NumberFormatException("Empty string")

            val isNeg = trimmed.startsWith('-')
            val abs =
                if (isNeg) trimmed.substring(1) else if (trimmed.startsWith('+')) trimmed.substring(1) else trimmed

            // Handle scientific notation (e.g. "1.23E+10")
            val eIndex = abs.indexOfFirst { it == 'e' || it == 'E' }
            if (eIndex >= 0) {
                val mantissa = abs.substring(0, eIndex)
                val exponent = abs.substring(eIndex + 1).toInt()
                val base = fromString((if (isNeg) "-" else "") + mantissa)
                return base.movePointRight(exponent)
            }

            val dotIndex = abs.indexOf('.')
            val (digits, scale) = if (dotIndex >= 0) {
                val intPart = abs.substring(0, dotIndex)
                val fracPart = abs.substring(dotIndex + 1)
                // Validate
                if ((intPart + fracPart).any { !it.isDigit() }) throw NumberFormatException("Invalid number: $value")
                Pair(intPart + fracPart, fracPart.length)
            } else {
                if (abs.any { !it.isDigit() }) throw NumberFormatException("Invalid number: $value")
                Pair(abs, 0)
            }

            // Remove leading zeros but keep at least one digit
            val stripped = digits.trimStart('0').ifEmpty { "0" }
            val isZero = stripped == "0"

            return JsBigDecimal(stripped, scale, if (isZero) false else isNeg)
        }

        /**
         * Create a JsBigDecimal from a Double.
         */
        fun fromDouble(value: Double): JsBigDecimal {
            // Use toString which gives a faithful representation
            return fromString(value.toBigDecimalString())
        }

        /**
         * Create a JsBigDecimal from a Long.
         */
        fun fromLong(value: Long): JsBigDecimal {
            return fromString(value.toString())
        }

        /**
         * Pads two digit-strings so they have the same length (left-padding with zeros).
         */
        private fun padToSameLength(a: String, b: String): Pair<String, String> {
            val maxLen = maxOf(a.length, b.length)
            return Pair(a.padStart(maxLen, '0'), b.padStart(maxLen, '0'))
        }

        /**
         * Add two unsigned digit strings.
         */
        internal fun addUnsigned(a: String, b: String): String {
            val (pa, pb) = padToSameLength(a, b)
            val result = StringBuilder()
            var carry = 0
            for (i in pa.length - 1 downTo 0) {
                val sum = (pa[i] - '0') + (pb[i] - '0') + carry
                result.append(sum % 10)
                carry = sum / 10
            }
            if (carry > 0) result.append(carry)
            return result.reverse().toString()
        }

        /**
         * Subtract two unsigned digit strings. Assumes a >= b.
         */
        internal fun subtractUnsigned(a: String, b: String): String {
            val (pa, pb) = padToSameLength(a, b)
            val result = StringBuilder()
            var borrow = 0
            for (i in pa.length - 1 downTo 0) {
                var diff = (pa[i] - '0') - (pb[i] - '0') - borrow
                if (diff < 0) {
                    diff += 10
                    borrow = 1
                } else {
                    borrow = 0
                }
                result.append(diff)
            }
            return result.reverse().toString().trimStart('0').ifEmpty { "0" }
        }

        /**
         * Multiply two unsigned digit strings.
         */
        internal fun multiplyUnsigned(a: String, b: String): String {
            val result = IntArray(a.length + b.length)
            for (i in a.length - 1 downTo 0) {
                for (j in b.length - 1 downTo 0) {
                    val mul = (a[i] - '0') * (b[j] - '0')
                    val p1 = i + j
                    val p2 = i + j + 1
                    val sum = mul + result[p2]
                    result[p2] = sum % 10
                    result[p1] += sum / 10
                }
            }
            return result.joinToString("").trimStart('0').ifEmpty { "0" }
        }

        /**
         * Compare two unsigned digit strings.
         * Returns -1, 0, or 1.
         */
        internal fun compareUnsigned(a: String, b: String): Int {
            val (pa, pb) = padToSameLength(a, b)
            return pa.compareTo(pb)
        }

        /**
         * Divide unsigned digit strings a / b, returning quotient with `precision` extra digits.
         * Returns (quotient digits, remainder digits)
         */
        internal fun divideUnsigned(a: String, b: String, extraDigits: Int): Pair<String, String> {
            if (b == "0") throw ArithmeticException("Division by zero")

            // Extend a with extra zeros for precision
            val dividend = a + "0".repeat(extraDigits)

            val result = StringBuilder()
            var remainder = "0"
            for (digit in dividend) {
                remainder = (if (remainder == "0") "" else remainder) + digit
                remainder = remainder.trimStart('0').ifEmpty { "0" }

                var count = 0
                while (compareUnsigned(remainder, b) >= 0) {
                    remainder = subtractUnsigned(remainder, b)
                    count++
                }
                result.append(count)
            }

            return Pair(
                result.toString().trimStart('0').ifEmpty { "0" },
                remainder
            )
        }
    }

    /**
     * Shift the decimal point right by n positions.
     * Negative n shifts left.
     */
    private fun movePointRight(n: Int): JsBigDecimal {
        val newScale = _scale - n
        return if (newScale >= 0) {
            JsBigDecimal(unscaledValue, newScale, negative)
        } else {
            // Need to append zeros
            val padded = unscaledValue + "0".repeat(-newScale)
            JsBigDecimal(padded, 0, negative)
        }
    }

    /**
     * Normalize both operands to the same scale by padding with zeros.
     */
    private fun alignScale(other: JsBigDecimal): Triple<String, String, Int> {
        val maxScale = maxOf(_scale, other._scale)
        val a = unscaledValue + "0".repeat(maxScale - _scale)
        val b = other.unscaledValue + "0".repeat(maxScale - other._scale)
        return Triple(a, b, maxScale)
    }

    // --- Arithmetic ---

    operator fun plus(other: JsBigDecimal): JsBigDecimal {
        val (a, b, scale) = alignScale(other)
        return if (negative == other.negative) {
            // Same sign: add magnitudes
            val sum = addUnsigned(a, b)
            JsBigDecimal(sum, scale, negative && sum != "0")
        } else {
            // Different sign: subtract smaller from larger
            val cmp = compareUnsigned(a, b)
            when {
                cmp == 0 -> JsBigDecimal("0", scale, false)
                cmp > 0 -> {
                    val diff = subtractUnsigned(a, b)
                    JsBigDecimal(diff, scale, negative) // Keep sign of larger magnitude (this)
                }

                else -> {
                    val diff = subtractUnsigned(b, a)
                    JsBigDecimal(diff, scale, other.negative) // Keep sign of larger magnitude (other)
                }
            }
        }
    }

    operator fun minus(other: JsBigDecimal): JsBigDecimal {
        return this + other.negate()
    }

    operator fun times(other: JsBigDecimal): JsBigDecimal {
        val product = multiplyUnsigned(unscaledValue, other.unscaledValue)
        val newScale = _scale + other._scale
        val isNeg = negative != other.negative && product != "0"
        return JsBigDecimal(product, newScale, isNeg)
    }

    /**
     * Division with default scale (max of both operands' scales + 10 for precision).
     */
    operator fun div(other: JsBigDecimal): JsBigDecimal {
        return divide(other, maxOf(_scale, other._scale, 10), RoundingMode.HALF_UP)
    }

    /**
     * Division with specified scale and rounding mode.
     */
    fun divide(other: JsBigDecimal, scale: Int, roundingMode: RoundingMode = RoundingMode.HALF_UP): JsBigDecimal {
        if (other.unscaledValue == "0") throw ArithmeticException("Division by zero")

        // Align to same scale first
        val (a, b, alignedScale) = alignScale(other)
        // After alignment both have alignedScale, so a/b gives integer-like result
        // We need `scale` decimal places in the result, so we need `scale` extra digits
        val (quotientDigits, remainder) = divideUnsigned(a, b, scale)

        // The quotient now has `scale` extra digits (the fractional part)
        val isNeg = negative != other.negative && quotientDigits != "0"

        // We need to apply rounding based on the remainder
        val result = JsBigDecimal(quotientDigits, scale, isNeg)

        // Check if rounding is needed
        if (remainder == "0") {
            return result
        }

        if (roundingMode == RoundingMode.UNNECESSARY) {
            throw ArithmeticException("Rounding necessary")
        }

        return result.applyRounding(remainder, b, roundingMode, isNeg)
    }

    /**
     * Apply rounding by examining the remainder.
     */
    private fun applyRounding(
        remainder: String,
        divisor: String,
        roundingMode: RoundingMode,
        isNeg: Boolean
    ): JsBigDecimal {
        // Determine if we should round up
        val shouldRoundUp = when (roundingMode) {
            RoundingMode.UP -> true // Always round away from zero
            RoundingMode.DOWN -> false // Always truncate
            RoundingMode.CEILING -> !isNeg // Round towards positive infinity
            RoundingMode.FLOOR -> isNeg // Round towards negative infinity
            RoundingMode.HALF_UP -> {
                // Compare 2*remainder with divisor
                val doubleRemainder = addUnsigned(remainder, remainder)
                compareUnsigned(doubleRemainder, divisor) >= 0
            }

            RoundingMode.HALF_DOWN -> {
                val doubleRemainder = addUnsigned(remainder, remainder)
                compareUnsigned(doubleRemainder, divisor) > 0
            }

            RoundingMode.HALF_EVEN -> {
                val doubleRemainder = addUnsigned(remainder, remainder)
                val cmp = compareUnsigned(doubleRemainder, divisor)
                when {
                    cmp > 0 -> true
                    cmp < 0 -> false
                    else -> {
                        // Exactly half: round to even (last digit is odd -> round up)
                        val lastDigit = unscaledValue.last().digitToInt()
                        lastDigit % 2 != 0
                    }
                }
            }

            RoundingMode.UNNECESSARY -> throw ArithmeticException("Rounding necessary")
        }

        return if (shouldRoundUp) {
            // Add 1 to the unscaled value
            val incremented = addUnsigned(unscaledValue, "1")
            JsBigDecimal(incremented, _scale, negative)
        } else {
            this
        }
    }

    /**
     * Set the scale of this value.
     */
    fun setScale(newScale: Int, roundingMode: RoundingMode = RoundingMode.HALF_UP): JsBigDecimal {
        if (newScale == _scale) return this

        if (newScale > _scale) {
            // Increase scale: pad with zeros
            val padded = unscaledValue + "0".repeat(newScale - _scale)
            return JsBigDecimal(padded, newScale, negative)
        }

        // Decrease scale: need to round
        val diff = _scale - newScale
        if (diff >= unscaledValue.length) {
            // All digits are in the fractional part being removed
            return applyScaleRounding("0", unscaledValue, diff, roundingMode)
        }

        val keepDigits = unscaledValue.substring(0, unscaledValue.length - diff)
        val removedDigits = unscaledValue.substring(unscaledValue.length - diff)

        return applyScaleRounding(keepDigits, removedDigits, diff, roundingMode)
    }

    private fun applyScaleRounding(
        keepDigits: String,
        removedDigits: String,
        diff: Int,
        roundingMode: RoundingMode
    ): JsBigDecimal {
        val newScale = _scale - diff

        val shouldRoundUp = when (roundingMode) {
            RoundingMode.UP -> removedDigits.any { it != '0' }
            RoundingMode.DOWN -> false
            RoundingMode.CEILING -> !negative && removedDigits.any { it != '0' }
            RoundingMode.FLOOR -> negative && removedDigits.any { it != '0' }
            RoundingMode.HALF_UP -> {
                removedDigits.isNotEmpty() && (removedDigits[0] - '0') >= 5
            }

            RoundingMode.HALF_DOWN -> {
                if (removedDigits.isEmpty()) false
                else if ((removedDigits[0] - '0') > 5) true
                else if ((removedDigits[0] - '0') < 5) false
                else removedDigits.substring(1).any { it != '0' } // Exactly 5: check remaining
            }

            RoundingMode.HALF_EVEN -> {
                if (removedDigits.isEmpty()) false
                else {
                    val firstRemoved = removedDigits[0] - '0'
                    if (firstRemoved > 5) true
                    else if (firstRemoved < 5) false
                    else {
                        // Exactly at midpoint?
                        val hasMore = removedDigits.substring(1).any { it != '0' }
                        if (hasMore) true
                        else {
                            // Round to even
                            val lastKeptDigit = if (keepDigits.isEmpty() || keepDigits == "0") 0
                            else keepDigits.last() - '0'
                            lastKeptDigit % 2 != 0
                        }
                    }
                }
            }

            RoundingMode.UNNECESSARY -> {
                if (removedDigits.any { it != '0' }) throw ArithmeticException("Rounding necessary")
                false
            }
        }

        val resultDigits = if (shouldRoundUp) {
            addUnsigned(keepDigits.ifEmpty { "0" }, "1")
        } else {
            keepDigits.ifEmpty { "0" }
        }

        val isZero = resultDigits.trimStart('0').ifEmpty { "0" } == "0"
        return JsBigDecimal(resultDigits.trimStart('0').ifEmpty { "0" }, newScale, if (isZero) false else negative)
    }

    fun negate(): JsBigDecimal {
        if (unscaledValue == "0") return this
        return JsBigDecimal(unscaledValue, _scale, !negative)
    }

    fun toPlainString(): String {
        val sign = if (negative) "-" else ""

        if (_scale <= 0) {
            // No decimal point, possibly append zeros if scale is negative
            val digits = unscaledValue + "0".repeat(-_scale)
            return sign + digits
        }

        // Pad with leading zeros if needed
        val padded = unscaledValue.padStart(_scale + 1, '0')
        val intPart = padded.substring(0, padded.length - _scale)
        val fracPart = padded.substring(padded.length - _scale)
        return "$sign$intPart.$fracPart"
    }

    fun toDouble(): Double = toPlainString().toDouble()

    fun toLong(): Long = toPlainString().toDouble().toLong()

    override fun compareTo(other: JsBigDecimal): Int {
        // Handle signs
        if (negative && !other.negative) return -1
        if (!negative && other.negative) return 1

        // Both same sign, compare magnitudes
        val (a, b, _) = alignScale(other)
        val cmp = compareUnsigned(a, b)

        return if (negative) -cmp else cmp
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is JsBigDecimal) return false
        return compareTo(other) == 0
    }

    override fun hashCode(): Int {
        // Normalize: remove trailing zeros for consistent hashing
        return toPlainString().trimEnd('0').trimEnd('.').hashCode()
    }

    override fun toString(): String = toPlainString()
}

/**
 * Convert a Double to its string representation without scientific notation.
 */
internal fun Double.toBigDecimalString(): String {
    // Use Kotlin's toString() which gives a correct representation
    val s = this.toString()
    // Handle scientific notation (e.g., 1.0E10)
    if ('E' in s || 'e' in s) {
        val parts = s.split(Regex("[eE]"))
        val mantissa = parts[0]
        val exponent = parts[1].toInt()

        val dotIndex = mantissa.indexOf('.')
        val digits = mantissa.replace(".", "")
        val isNeg = digits.startsWith('-')
        val absDigits = if (isNeg) digits.substring(1) else digits

        val intLen = if (dotIndex >= 0) {
            if (isNeg) dotIndex - 1 else dotIndex
        } else {
            absDigits.length
        }

        val newDotPos = intLen + exponent
        val result: String
        if (newDotPos >= absDigits.length) {
            result = absDigits + "0".repeat(newDotPos - absDigits.length)
        } else if (newDotPos <= 0) {
            result = "0." + "0".repeat(-newDotPos) + absDigits
        } else {
            result = absDigits.substring(0, newDotPos) + "." + absDigits.substring(newDotPos)
        }

        return (if (isNeg) "-" else "") + result
    }
    return s
}
