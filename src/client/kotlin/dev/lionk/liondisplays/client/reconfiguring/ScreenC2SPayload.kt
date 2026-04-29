package dev.lionk.liondisplays.client.reconfiguring

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier
import java.util.function.Function

@JvmRecord
data class ScreenC2SPayload(val content: ByteArray) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }

    companion object {
        val DISPLAY_PAYLOAD_ID: Identifier = Identifier.fromNamespaceAndPath("lionvelocity", "connection")
        val ID: CustomPacketPayload.Type<ScreenC2SPayload> = CustomPacketPayload.Type<ScreenC2SPayload>(DISPLAY_PAYLOAD_ID)
        val RAW_BYTE_ARRAY_CODEC: StreamCodec<RegistryFriendlyByteBuf, ByteArray> = object : StreamCodec<RegistryFriendlyByteBuf, ByteArray> {
            override fun encode(buf: RegistryFriendlyByteBuf, value: ByteArray) {
                buf.writeBytes(value)
            }

            override fun decode(buf: RegistryFriendlyByteBuf): ByteArray {
                val bytes = ByteArray(buf.readableBytes())
                buf.readBytes(bytes)
                return bytes
            }
        }
        val CODEC: StreamCodec<RegistryFriendlyByteBuf, ScreenC2SPayload> =
            StreamCodec.composite<RegistryFriendlyByteBuf, ScreenC2SPayload, ByteArray>(
                RAW_BYTE_ARRAY_CODEC,
                ScreenC2SPayload::content,
                Function { content: ByteArray -> ScreenC2SPayload(content) })
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScreenC2SPayload

        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        return content.contentHashCode()
    }
}
