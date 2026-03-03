package jp.co.tanocee.bikdecimal

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

/**
 * Serializer for BikDecimal that handles it as a String to preserve precision.
 */
object BikDecimalSerializer : KSerializer<BikDecimal> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("BikDecimal", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: BikDecimal) {
        encoder.encodeString(value.toPlainString())
    }

    override fun deserialize(decoder: Decoder): BikDecimal {
        return BikDecimal(decoder.decodeString())
    }
}
