package dev.lionk.liondisplays.client.messaging

import net.minecraft.network.RegistryByteBuf
import net.minecraft.network.codec.PacketCodec
import net.minecraft.network.codec.PacketCodecs
import net.minecraft.network.packet.CustomPayload
import net.minecraft.util.Identifier
import java.util.function.Function

@JvmRecord
data class DisplayS2CPayload(val content: ByteArray) : CustomPayload {
    override fun getId(): CustomPayload.Id<out CustomPayload> {
        return ID
    }
    fun getString(): String{
        return String(content)
    }

    companion object {
        val DISPLAY_PAYLOAD_ID: Identifier = Identifier.of("lionapi", "display_communication")
        val ID: CustomPayload.Id<DisplayS2CPayload> = CustomPayload.Id<DisplayS2CPayload>(DISPLAY_PAYLOAD_ID)
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
        val CODEC: PacketCodec<RegistryByteBuf, DisplayS2CPayload> =
            PacketCodec.tuple<RegistryByteBuf, DisplayS2CPayload, ByteArray>(
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
