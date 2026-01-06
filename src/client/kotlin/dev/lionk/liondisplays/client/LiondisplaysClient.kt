package dev.lionk.liondisplays.client

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.lionk.liondisplays.client.configuration.ModConfig
import dev.lionk.liondisplays.client.listeners.PlayerDisconnect
import dev.lionk.liondisplays.client.messaging.DisplayAttachments
import dev.lionk.liondisplays.client.messaging.DisplayC2SPayload
import dev.lionk.liondisplays.client.messaging.DisplayData
import dev.lionk.liondisplays.client.messaging.DisplayS2CPayload
import dev.lionk.liondisplays.client.messaging.DisplayableCompass
import dev.lionk.liondisplays.client.messaging.DisplayableItem
import dev.lionk.liondisplays.client.messaging.DisplayableOutline
import dev.lionk.liondisplays.client.messaging.DisplayableSquare
import dev.lionk.liondisplays.client.messaging.DisplayableText
import dev.lionk.liondisplays.client.messaging.DisplayableTexture
import dev.lionk.liondisplays.client.messaging.MessageHandler
import dev.lionk.liondisplays.client.reconfiguring.ScreenC2SPayload
import dev.lionk.liondisplays.client.reconfiguring.ScreenS2CPayload
import dev.lionk.liondisplays.client.reconfiguring.VelocityRegistration
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.item.Items
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.RegistrationEnvironment
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import java.awt.Color
import java.util.function.Supplier

class LiondisplaysClient : ClientModInitializer {

    override fun onInitializeClient() {
        ModConfig.load()

        PayloadTypeRegistry.playS2C().register(DisplayS2CPayload.ID, DisplayS2CPayload.CODEC)
        PayloadTypeRegistry.playC2S().register(DisplayC2SPayload.ID, DisplayC2SPayload.CODEC)
        PayloadTypeRegistry.playS2C().register(ScreenS2CPayload.ID, ScreenS2CPayload.CODEC)
        PayloadTypeRegistry.playC2S().register(ScreenC2SPayload.ID, ScreenC2SPayload.CODEC)



        println("Registering LionDisplays Client")


        ClientPlayConnectionEvents.JOIN.register { handler, sender, client ->
            PlayerDisconnect.onDisconnect(handler, client)
            VelocityRegistration.hasLeftOldWorld = true
        }

        HudElementRegistry.attachElementAfter(
            VanillaHudElements.SCOREBOARD, Identifier.of("liondisplays", "display")
        ) { context, counter ->
            if (VelocityRegistration.isPlayingAnimation){
                context.fill(0, 0, context.scaledWindowWidth, context.scaledWindowHeight, Color(0f, 0f, 0f,
                    VelocityRegistration.getFadeValue()).rgb)
            }else
                for (element in DisplayData.values) {
                    element.value.render(context)
        //            val text = Text.of("Hallo, \nIch bin neu hier")
        //            if (text != null){
        //                val x: Int = context.scaledWindowWidth - textRenderer.getWidth(text) - 9
        //                val y = 9
        //                context.drawText(textRenderer, text, x, y, 100, 0xFFFFFFFF.toInt(), true)
        //                context.drawItemWithoutEntity(Items.ORANGE_DYE.defaultStack, 10, 10)
        //            }
                }
        }
        ClientPlayNetworking.registerGlobalReceiver(
            DisplayS2CPayload.ID
        ) { payload, context ->
            try {
                MessageHandler.handleIncomingMessage(payload.getString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        ClientPlayNetworking.registerGlobalReceiver(
            ScreenS2CPayload.ID
        ) { payload, context ->
            try {
                VelocityRegistration.handleIncomingMessage(payload.getString())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource>, registryAccess: CommandRegistryAccess, environment: RegistrationEnvironment ->
            dispatcher.register(
                CommandManager.literal("liondisplays")
                    .executes(Command { context: CommandContext<ServerCommandSource?>? ->
                        context!!.getSource()!!
                            .sendFeedback(Supplier { Text.literal("Called LionSystems Command.") }, false)
                        DisplayData.values.put("main", DisplayableText("LionSystems Network", 0xFFFF00FF.toInt()))
                        1
                    }).then(
                        CommandManager.argument<String>("type", StringArgumentType.string())
                            .executes { context: CommandContext<ServerCommandSource?>? ->
                                val args = context!!.getArgument<String>("type", String::class.java)
                                when (args) {
                                    "item" -> DisplayData.values.put(
                                        "item",
                                        DisplayableItem(Items.IRON_BARS.defaultStack).also {
                                            it.setDisplayAttachments(DisplayAttachments.TOP_LEFT)
                                            it.setOffsetX(11)
                                            it.setOffsetY(11)
                                        })

                                    "square" -> DisplayData.values.put("square", DisplayableSquare(20, 20,
                                        Color(0.9f, 0.9f, 0.9f, 0.5f).rgb).also {
                                        it.setDisplayAttachments(DisplayAttachments.TOP_LEFT)


                                    })
                                    "frame" -> DisplayData.values.put("frame", DisplayableOutline(20, 20,
                                        Color(0.2f, 0.2f, 0.2f, 0.7f).rgb).also {
                                        it.setDisplayAttachments(DisplayAttachments.TOP_LEFT)


                                    })

                                    "texture" -> DisplayData.values.put("texture", DisplayableTexture("textures/block/deepslate.png",
                                        32, 32))
                                    "compass" -> DisplayData.values.put("compass", DisplayableCompass(
                                        Vec3d(1000.0, 64.0, -100.0)
                                    ).also {
                                        it.setOffsetY(0)
                                        it.setOffsetX(0)
                                        it.setDisplayAttachments(DisplayAttachments.BOTTOM_RIGHT)
                                    })
                                }
                                1
                            })
            )
        })
    }
}
