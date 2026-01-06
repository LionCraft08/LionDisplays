package dev.lionk.liondisplays.client.reconfiguring

import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier
import java.util.function.Function

@JvmRecord
data class ScreenS2CPayload(val content: ByteArray) : CustomPayload {
    override fun getId(): CustomPayload.Id<out CustomPayload> {
        return ID
    }
    fun getString(): String{
        return String(content)
    }

    companion object {
        val DISPLAY_PAYLOAD_ID: Identifier = Identifier.of("lionvelocity", "connection")
        val ID: CustomPayload.Id<ScreenS2CPayload> = CustomPayload.Id<ScreenS2CPayload>(DISPLAY_PAYLOAD_ID)
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
        val CODEC: PacketCodec<RegistryByteBuf, ScreenS2CPayload> =
            PacketCodec.tuple<RegistryByteBuf, ScreenS2CPayload, ByteArray>(
                RAW_BYTE_ARRAY_CODEC,
                ScreenS2CPayload::content,
                Function { content: ByteArray -> ScreenS2CPayload(content) })


    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ScreenS2CPayload

        if (!content.contentEquals(other.content)) return false

        return true
    }

    override fun hashCode(): Int {
        return content.contentHashCode()
    }
}
