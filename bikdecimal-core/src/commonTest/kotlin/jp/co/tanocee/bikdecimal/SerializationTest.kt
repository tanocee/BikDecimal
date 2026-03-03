package jp.co.tanocee.bikdecimal

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class SerializationTest {

    @Serializable
    data class TestData(
        val amount: BikDecimal,
        val label: String
    )

    @Test
    fun testSerialization() {
        val data = TestData(BikDecimal("123.456"), "test")
        val json = Json.encodeToString(TestData.serializer(), data)
        
        // JSON should contain the decimal as a string
        assertEquals("""{"amount":"123.456","label":"test"}""", json)
    }

    @Test
    fun testDeserialization() {
        val json = """{"amount":"123.456","label":"test"}"""
        val data = Json.decodeFromString(TestData.serializer(), json)
        
        assertEquals(BikDecimal("123.456"), data.amount)
        assertEquals("test", data.label)
    }

    @Test
    fun testPrecisionPreservation() {
        // Test with many decimal places to ensure no precision loss (which might happen with Double)
        val originalValue = "123.45678901234567890123456789"
        val data = TestData(BikDecimal(originalValue), "precision")
        val json = Json.encodeToString(TestData.serializer(), data)
        
        val decoded = Json.decodeFromString(TestData.serializer(), json)
        assertEquals(originalValue, decoded.amount.toPlainString())
    }
}
