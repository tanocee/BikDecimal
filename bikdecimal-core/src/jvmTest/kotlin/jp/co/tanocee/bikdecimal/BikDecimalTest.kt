package jp.co.tanocee.bikdecimal

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

class BikDecimalTest {

    // --- Constructor Tests ---

    @Test
    fun constructFromString() {
        val value = BikDecimal("123.45")
        assertEquals("123.45", value.toPlainString())
    }

    @Test
    fun constructFromDouble() {
        val value = BikDecimal(3.14)
        assertEquals(3.14, value.toDouble())
    }

    @Test
    fun constructFromLong() {
        val value = BikDecimal(42L)
        assertEquals(42L, value.toLong())
    }

    @Test
    fun constructWithScaleAndRounding() {
        val value = BikDecimal("123.456", 2, RoundingMode.HALF_UP)
        assertEquals("123.46", value.toPlainString())
    }

    @Test
    fun constructWithScaleNegativeKeepsOriginal() {
        val value = BikDecimal("123.456", -1, RoundingMode.HALF_UP)
        assertEquals("123.456", value.toPlainString())
    }

    // --- Arithmetic Tests ---

    @Test
    fun addition() {
        val a = BikDecimal("123.45")
        val b = BikDecimal("67.89")
        val result = a + b
        assertEquals("191.34", result.toPlainString())
    }

    @Test
    fun subtraction() {
        val a = BikDecimal("123.45")
        val b = BikDecimal("67.89")
        val result = a - b
        assertEquals("55.56", result.toPlainString())
    }

    @Test
    fun multiplication() {
        val a = BikDecimal("123.45")
        val b = BikDecimal("67.89")
        val result = a * b
        assertEquals("8381.0205", result.toPlainString())
    }

    @Test
    fun divideWithScaleAndRounding() {
        val a = BikDecimal("10")
        val b = BikDecimal("3")
        val result = a.divide(b, 4, RoundingMode.HALF_UP)
        assertEquals("3.3333", result.toPlainString())
    }

    // --- Comparison Tests ---

    @Test
    fun compareLessThan() {
        val a = BikDecimal("100")
        val b = BikDecimal("200")
        assertTrue(a < b)
    }

    @Test
    fun compareGreaterThan() {
        val a = BikDecimal("200")
        val b = BikDecimal("100")
        assertTrue(a > b)
    }

    @Test
    fun compareEqual() {
        val a = BikDecimal("100")
        val b = BikDecimal("100")
        assertTrue(a.compareTo(b) == 0)
    }

    // --- Conversion Tests ---

    @Test
    fun toPlainString() {
        val value = BikDecimal("123.456")
        assertEquals("123.456", value.toPlainString())
    }

    @Test
    fun toDouble() {
        val value = BikDecimal("123.456")
        assertEquals(123.456, value.toDouble(), 0.0001)
    }

    @Test
    fun toLong() {
        val value = BikDecimal("123.456")
        assertEquals(123L, value.toLong())
    }

    // --- Scale Tests ---

    @Test
    fun scale() {
        val value = BikDecimal("123.45")
        assertEquals(2, value.scale)
    }

    @Test
    fun setScaleUp() {
        val value = BikDecimal("123.4")
        val result = value.setScale(3)
        assertEquals("123.400", result.toPlainString())
    }

    @Test
    fun setScaleDown() {
        val value = BikDecimal("123.456")
        val result = value.setScale(2, RoundingMode.HALF_UP)
        assertEquals("123.46", result.toPlainString())
    }

    // --- Negative Test ---

    @Test
    fun negative() {
        val value = BikDecimal("42.5")
        val negated = value.negative()
        assertEquals("-42.5", negated.toPlainString())
    }

    @Test
    fun negativeOfNegative() {
        val value = BikDecimal("-42.5")
        val result = value.negative()
        assertEquals("42.5", result.toPlainString())
    }

    // --- Constants Tests ---

    @Test
    fun zeroConstant() {
        assertEquals("0", BikDecimal.ZERO.toPlainString())
    }

    @Test
    fun oneConstant() {
        assertEquals("1", BikDecimal.ONE.toPlainString())
    }

    // --- Extension Function Tests ---

    @Test
    fun toBikDecimalValid() {
        val result = "100.5".toBikDecimal()
        assertEquals("100.5", result.toPlainString())
    }

    @Test
    fun toBikDecimalInvalidReturnsDefault() {
        val result = "invalid".toBikDecimal()
        assertEquals("0", result.toPlainString())
    }

    @Test
    fun toBikDecimalInvalidReturnsCustomDefault() {
        val result = "invalid".toBikDecimal(BikDecimal.ONE)
        assertEquals("1", result.toPlainString())
    }

    @Test
    fun sumOfCollection() {
        data class Product(val name: String, val price: String)

        val products = listOf(
            Product("Apple", "1.20"),
            Product("Banana", "0.80"),
            Product("Orange", "1.50")
        )
        val total = products.sumOf { it.price.toBikDecimal() }
        assertEquals("3.50", total.toPlainString())
    }

    // --- Equals Tests ---

    @Test
    fun equalsWithSameValue() {
        val a = BikDecimal("100")
        val b = BikDecimal("100")
        assertTrue(a == b)
    }

    @Test
    fun equalsWithDifferentScale() {
        val a = BikDecimal("100")
        val b = BikDecimal("100.00")
        assertTrue(a == b)
    }

    @Test
    fun notEquals() {
        val a = BikDecimal("100")
        val b = BikDecimal("200")
        assertFalse(a == b)
    }

    // --- NativeBigDecimal Test ---

    @Test
    fun nativeBigDecimalAccess() {
        val value = BikDecimal("123.45")
        val native = value.nativeBigDecimal
        assertEquals(java.math.BigDecimal("123.45"), native)
    }
}
