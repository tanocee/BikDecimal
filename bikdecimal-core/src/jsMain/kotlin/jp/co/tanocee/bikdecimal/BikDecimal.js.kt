package jp.co.tanocee.bikdecimal

actual typealias NativeBigDecimal = JsBigDecimal

actual class BikDecimal {
    private val value: JsBigDecimal

    actual constructor(value: String) {
        this.value = JsBigDecimal.fromString(value)
    }

    actual constructor(value: Double) {
        this.value = JsBigDecimal.fromDouble(value)
    }

    actual constructor(value: Long) {
        this.value = JsBigDecimal.fromLong(value)
    }

    actual constructor(value: String, scale: Int, roundingMode: RoundingMode) {
        this.value = if (scale >= 0) {
            JsBigDecimal.fromString(value).setScale(scale, roundingMode)
        } else {
            JsBigDecimal.fromString(value)
        }
    }

    actual constructor(value: Double, scale: Int, roundingMode: RoundingMode) {
        this.value = if (scale >= 0) {
            JsBigDecimal.fromDouble(value).setScale(scale, roundingMode)
        } else {
            JsBigDecimal.fromDouble(value)
        }
    }

    actual constructor(value: Long, scale: Int, roundingMode: RoundingMode) {
        this.value = if (scale >= 0) {
            JsBigDecimal.fromLong(value).setScale(scale, roundingMode)
        } else {
            JsBigDecimal.fromLong(value)
        }
    }

    private constructor(jsBigDecimal: JsBigDecimal) {
        this.value = jsBigDecimal
    }

    actual val nativeBigDecimal: NativeBigDecimal
        get() = value

    actual val scale: Int
        get() = value.scale

    actual fun toPlainString(): String = value.toPlainString()

    actual fun toDouble(): Double = value.toDouble()

    actual fun toLong(): Long = value.toLong()

    actual fun negative(): BikDecimal = BikDecimal(value.negate())

    actual fun setScale(newScale: Int, roundingMode: RoundingMode): BikDecimal =
        BikDecimal(value.setScale(newScale, roundingMode))

    actual operator fun plus(other: BikDecimal): BikDecimal =
        BikDecimal(value + other.value)

    actual operator fun minus(other: BikDecimal): BikDecimal =
        BikDecimal(value - other.value)

    actual operator fun times(other: BikDecimal): BikDecimal =
        BikDecimal(value * other.value)

    actual operator fun div(other: BikDecimal): BikDecimal =
        BikDecimal(value / other.value)

    actual fun divide(other: BikDecimal, scale: Int, roundingMode: RoundingMode): BikDecimal =
        BikDecimal(value.divide(other.value, scale, roundingMode))

    actual operator fun compareTo(other: BikDecimal): Int = value.compareTo(other.value)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is BikDecimal) return false
        return value.compareTo(other.value) == 0
    }

    override fun hashCode(): Int = value.hashCode()

    override fun toString(): String = toPlainString()

    actual companion object {
        actual val ZERO: BikDecimal = BikDecimal(JsBigDecimal.ZERO)
        actual val ONE: BikDecimal = BikDecimal(JsBigDecimal.ONE)
    }
}
