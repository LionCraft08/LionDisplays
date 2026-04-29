package dev.lionk.liondisplays.client.listeners

import dev.lionk.liondisplays.client.messaging.DisplayData
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientPacketListener

object PlayerDisconnect {
    fun onDisconnect(handler: ClientPacketListener, client: Minecraft){
        DisplayData.values.clear()
    }
}