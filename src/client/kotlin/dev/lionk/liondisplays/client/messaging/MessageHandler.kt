package dev.lionk.liondisplays.client.messaging

import dev.lionk.liondisplays.Liondisplays
import dev.lionk.liondisplays.client.LionAPI.LionDisplayData
import dev.lionk.liondisplays.client.LiondisplaysClient
import dev.lionk.liondisplays.client.configuration.ModConfig
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.item.v1.FabricItemStack
import net.minecraft.client.MinecraftClient
import net.minecraft.component.DataComponentTypes
import net.minecraft.component.type.PotionContentsComponent
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.PotionItem
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtSizeTracker
import net.minecraft.potion.Potion
import net.minecraft.predicate.item.PotionContentsPredicate
import net.minecraft.registry.Registries
import net.minecraft.registry.entry.RegistryEntry
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import java.io.ByteArrayInputStream
import java.lang.NumberFormatException
import java.util.*


object MessageHandler {
    fun handleIncomingMessage(message: String){
        if (!ModConfig.enabledServer || !ModConfig.enabled) return

        val command = message.substringBefore(":")
        when (command){
            "check_existing" -> MinecraftClient.getInstance().execute {
                if (ModConfig.enabledMessage) {
                    MinecraftClient.getInstance().player!!.sendMessage(
                        Text.of("[LionDisplays] You joined a Server that supports custom Displays!"), false
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
                                Vec3d(
                                    ldd.getData("x").toDouble(),
                                    ldd.getData("y").toDouble(),
                                    ldd.getData("z").toDouble()
                                )
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
        val customData = getStringAtIndex(3, item)
        val cmd = getStringAtIndex(4, item)
        var itemStack = Registries.ITEM.get(Identifier.ofVanilla(type.lowercase()
            .replaceFirst("minecraft:", "", true))).defaultStack
        itemStack.count = amount
        itemStack.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, glint)
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