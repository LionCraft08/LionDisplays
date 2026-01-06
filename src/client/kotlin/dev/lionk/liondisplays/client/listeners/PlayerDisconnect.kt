package dev.lionk.liondisplays.client.listeners

import dev.lionk.liondisplays.client.messaging.DisplayData
import net.minecraft.block.entity.VaultBlockEntity
import net.minecraft.client.MinecraftClient
import net.minecraft.client.network.ClientPlayNetworkHandler

object PlayerDisconnect {
    fun onDisconnect(handler: ClientPlayNetworkHandler, client: MinecraftClient){
        DisplayData.values.clear()
    }
}