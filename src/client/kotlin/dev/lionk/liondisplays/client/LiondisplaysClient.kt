package dev.lionk.liondisplays.client

import com.mojang.brigadier.Command
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.context.CommandContext
import dev.lionk.liondisplays.client.messaging.DisplayAttachments
import dev.lionk.liondisplays.client.messaging.DisplayC2SPayload
import dev.lionk.liondisplays.client.messaging.DisplayData
import dev.lionk.liondisplays.client.messaging.DisplayS2CPayload
import dev.lionk.liondisplays.client.messaging.DisplayableCompass
import dev.lionk.liondisplays.client.messaging.DisplayableElementType
import dev.lionk.liondisplays.client.messaging.DisplayableEntity
import dev.lionk.liondisplays.client.messaging.DisplayableItem
import dev.lionk.liondisplays.client.messaging.DisplayableSquare
import dev.lionk.liondisplays.client.messaging.DisplayableText
import dev.lionk.liondisplays.client.messaging.DisplayableTexture
import dev.lionk.liondisplays.client.messaging.MessageHandler
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.client.render.entity.state.EntityRenderState
import net.minecraft.command.CommandRegistryAccess
import net.minecraft.entity.EntityType
import net.minecraft.entity.passive.SheepEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.command.CommandManager
import net.minecraft.server.command.CommandManager.RegistrationEnvironment
import net.minecraft.server.command.ServerCommandSource
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import net.minecraft.util.math.Vec3d
import java.util.function.Supplier

class LiondisplaysClient : ClientModInitializer {



    override fun onInitializeClient() {
        PayloadTypeRegistry.playS2C().register(DisplayS2CPayload.ID, DisplayS2CPayload.CODEC)
        PayloadTypeRegistry.playC2S().register(DisplayC2SPayload.ID, DisplayC2SPayload.CODEC)

        println("Registering LionDisplays Client")


        HudElementRegistry.attachElementAfter(
            VanillaHudElements.SCOREBOARD, Identifier.of("liondisplays", "display")
        ) { context, counter ->
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
        CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher: CommandDispatcher<ServerCommandSource?>?, registryAccess: CommandRegistryAccess?, environment: RegistrationEnvironment? ->
            dispatcher!!.register(
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
                                        DisplayableItem(Items.NETHERITE_SWORD.defaultStack).also {
                                            it.displayAttachments =
                                                DisplayAttachments.TOP_CENTER
                                        })

                                    "square" -> DisplayData.values.put("square", DisplayableSquare(50, 50).also {
                                        it.displayAttachments =
                                            DisplayAttachments.MIDDLE_LEFT
                                    })

                                    "texture" -> DisplayData.values.put("texture", DisplayableTexture("textures/block/deepslate.png",
                                        32, 32))
                                    "compass" -> DisplayData.values.put("compass", DisplayableCompass(
                                        Vec3d(1000.0, 64.0, -100.0)
                                    ).also {
                                        it.offsetY = 0
                                        it.offsetX = 0
                                        it.displayAttachments = DisplayAttachments.BOTTOM_RIGHT
                                    })
                                }
                                1
                            })
            )
        })
    }
}
