package dev.lionk.liondisplays.client.messaging

import dev.lionk.liondisplays.Liondisplays
import dev.lionk.liondisplays.client.LionAPI.LionDisplayData
import dev.lionk.liondisplays.client.configuration.ModConfig
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.minecraft.client.Minecraft
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3
import java.util.*


object MessageHandler {
    fun handleIncomingMessage(message: String){
        if (!ModConfig.enabledServer || !ModConfig.enabled) return

        val command = message.substringBefore(":")
        when (command){
            "check_existing" -> Minecraft.getInstance().execute {
                if (ModConfig.enabledMessage) {
                    Minecraft.getInstance().player!!.sendSystemMessage(
                        Component.literal("[LionDisplays] You joined a Server that supports custom Displays!")
                    )
                }
                println("Received a LionDisplays Registration Message")
                ClientPlayNetworking.send(DisplayC2SPayload(("check_existing:"+ Liondisplays.getVersion()).toByteArray()))
            }
            "compass_data" ->{
                DisplayData.maxDistanceXZ = getStringAtIndex(1, message).toDouble()
                DisplayData.maxDistanceY = getStringAtIndex(2, message).toDouble()
            }
            "update_display"->{
                val ldd = LionDisplayData.getLionDisplayData(getStringFromIndex(1, message))
                var element: DisplayableElement? = null
                when (ldd.type){
                    "text" ->{
                        val maxWidth = ldd.getData("maxWidth")?.toInt() ?:1440
                        element = DisplayableText(ldd.getData("text"), maxWidth)
                    }
                    "item" ->{
                        val ist: ItemStack = deserializeItemStack(ldd.getData("item"))
                        element = DisplayableItem(ist)
                    }
                    "square" ->{
                        try {
                            element = DisplayableSquare(
                                ldd.getData("width").toInt(),
                                ldd.getData("height").toInt(),
                                ldd.getData("color").toInt()
                            )
                        }catch (e: NumberFormatException){
                            sendMessageToServer("error:NumberFormatException:${e.message}")
                        }
                    }
                    "frame" ->{
                        try {
                            element = DisplayableOutline(
                                ldd.getData("width").toInt(),
                                ldd.getData("height").toInt(),
                                ldd.getData("color").toInt()
                            )
                        }catch (e: NumberFormatException){
                            sendMessageToServer("error:NumberFormatException:${e.message}")
                        }
                    }
                    "compass" ->{
                        try {
                            element = DisplayableCompass(
                                Vec3(
                                    ldd.getData("x").toDouble(),
                                    ldd.getData("y").toDouble(),
                                    ldd.getData("z").toDouble()
                                ),
                                ldd.getData("dimension")
                            )
                        }catch (e: NumberFormatException){
                            sendMessageToServer("error:NumberFormatException:${e.message}")
                        }
                    }
                    "delete"->{
                        val displayName = ldd.id.lowercase()
                        DisplayData.values.remove(displayName)
                        println("Deleted ${ldd.id}")
                    }
                }
                if (element != null){
                    element.setOffsetX(ldd.offsetX)
                    element.setOffsetY(ldd.offsetY)
                    element.setDisplayAttachments(ldd.displayAttachment)
                    DisplayData.values[ldd.id] = element
                }
            }
            /*
            "update_display" ->{
                val displayName = getStringAtIndex(1, message).lowercase()
                val displayType = getStringAtIndex(2, message).lowercase()


                val offsetX = if (getStringAtIndex(3, message).equals("null", true)) ModConfig.offset
                    else getStringAtIndex(3, message).toInt()
                val offsetY = if (getStringAtIndex(4, message).equals("null", true)) ModConfig.offset
                else getStringAtIndex(4, message).toInt()
                val attachment = DisplayAttachments.valueOf(getStringAtIndex(5, message))
                var element: DisplayableElement? = null
                when (displayType){
                    "text" -> {
                        val maxWidth = getStringAtIndex(6, message).toInt()
                        element = DisplayableText(getStringFromIndex(7, message), maxWidth)
                    }
                    "item" -> {
                        val ist: ItemStack = deserializeItemStack(getStringFromIndex(6, message))
                        element = DisplayableItem(ist)
                    }
                    "square" -> {
                        try {
                            element = DisplayableSquare(
                                getStringAtIndex(7, message).toInt(),
                                getStringAtIndex(8, message).toInt(),
                                getStringAtIndex(6, message).toInt()
                            )
                        }catch (e: NumberFormatException){
                            sendMessageToServer("error:NumberFormatException:${e.message}")
                        }

                    }
                    "frame" -> {
                        try {
                            element = DisplayableOutline(
                                getStringAtIndex(7, message).toInt(),
                                getStringAtIndex(8, message).toInt(),
                                getStringAtIndex(6, message).toInt()
                            )
                        }catch (e: NumberFormatException){
                            sendMessageToServer("error:NumberFormatException:${e.message}")
                        }
                    }
                    "compass" -> {
                        try {
                            element = DisplayableCompass(
                                Vec3d(
                                    getStringAtIndex(6, message).toDouble(),
                                    getStringAtIndex(7, message).toDouble(),
                                    getStringAtIndex(8, message).toDouble()
                                )
                            )
                        }catch (e: NumberFormatException){
                            sendMessageToServer("error:NumberFormatException:${e.message}")
                        }

                    }
                }
                if (element != null){
                    element.offsetX = offsetX
                    element.offsetY = offsetY
                    element.displayAttachments = attachment
                    DisplayData.values.put(displayName, element)
                }
            }
             */
            "delete_display" -> {
                val displayName = getStringAtIndex(1, message).lowercase()
                DisplayData.values.remove(displayName)
            }
        }
    }
    private fun deserializeItemStack(item:String): ItemStack{
        val type = getStringAtIndex(0, item)
        val amount = getStringAtIndex(1, item).toInt()
        val glint = getStringAtIndex(2, item).toBoolean()
        val path = type.lowercase().replaceFirst("minecraft:", "", true)
        
        val itemStack = BuiltInRegistries.ITEM.get(Identifier.withDefaultNamespace(path)).get().value().defaultInstance
        itemStack.count = amount
        itemStack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, glint)
        return itemStack
    }
    private fun getStringFromIndex(index:Int, string: String): String{
        var s = string
        for (i in 0..index){
            if (i == 0) continue
            s = s.substringAfter(":")
        }
        return s
    }
    private fun getStringAtIndex(index:Int, string: String): String{
        return getStringFromIndex(index, string).substringBefore(":")
    }
    fun sendMessageToServer(message: String){
        ClientPlayNetworking.send(DisplayC2SPayload(message.toByteArray()))
    }
}