package dev.lionk.liondisplays.client.reconfiguring

import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier
import java.util.function.Function

@JvmRecord
data class ScreenC2SPayload(val content: ByteArray) : CustomPayload {
    override fun getId(): CustomPayload.Id<out CustomPayload> {
        return ID
    }

    companion object {
        val DISPLAY_PAYLOAD_ID: Identifier = Identifier.of("lionvelocity", "connection")
        val ID: CustomPayload.Id<ScreenC2SPayload> = CustomPayload.Id<ScreenC2SPayload>(DISPLAY_PAYLOAD_ID)
        val RAW_BYTE_ARRAY_CODEC: PacketCodec<RegistryByteBuf, ByteArray> = object : PacketCodec<RegistryByteBuf, ByteArray> {
            override fun encode(buf: RegistryByteBuf, value: ByteArray) {
                buf.writeBytes(value)
            }

            override fun decode(buf: RegistryByteBuf): ByteArray {
                val bytes = ByteArray(buf.readableBytes())
                buf.readBytes(bytes)
                return bytes
            }
        }
        val CODEC: PacketCodec<RegistryByteBuf, ScreenC2SPayload> =
            PacketCodec.tuple<RegistryByteBuf, ScreenC2SPayload, ByteArray>(
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
