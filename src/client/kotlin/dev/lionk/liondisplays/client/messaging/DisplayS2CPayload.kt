package dev.lionk.liondisplays.client.messaging

import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.Identifier
import java.util.function.Function

@JvmRecord
data class DisplayS2CPayload(val content: ByteArray) : CustomPacketPayload {
    override fun type(): CustomPacketPayload.Type<out CustomPacketPayload> {
        return ID
    }
    fun getString(): String{
        return String(content)
    }

    companion object {
        val DISPLAY_PAYLOAD_ID: Identifier = Identifier.fromNamespaceAndPath("lionapi", "display_communication")
        val ID: CustomPacketPayload.Type<DisplayS2CPayload> = CustomPacketPayload.Type<DisplayS2CPayload>(DISPLAY_PAYLOAD_ID)
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
        val CODEC: StreamCodec<RegistryFriendlyByteBuf, DisplayS2CPayload> =
            StreamCodec.composite<RegistryFriendlyByteBuf, DisplayS2CPayload, ByteArray>(
                RAW_BYTE_ARRAY_CODEC,
                DisplayS2CPayload::content,
                Function { content: ByteArray -> DisplayS2CPayload(content) })


    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DisplayS2CPayload

        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        return content.contentHashCode()
    }
}
