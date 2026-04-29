package dev.lionk.liondisplays.client.messaging

import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.mojang.authlib.minecraft.client.MinecraftClient
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.datafixers.util.Pair
import com.mojang.realmsclient.util.TextRenderingUtils
import com.mojang.serialization.DataResult
import com.mojang.serialization.JsonOps
import dev.lionk.liondisplays.client.configuration.ModConfig
import dev.lionk.liondisplays.client.renderer.LionRenderEngine
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.entity.DisplayRenderer
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.client.renderer.feature.TextFeatureRenderer
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3
import com.mojang.math.Axis
import net.minecraft.network.chat.ComponentSerialization
import net.minecraft.util.Mth
import org.joml.Quaternionf
import org.joml.Vector3f
import kotlin.let
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin


class DisplayableSquare(
    val width : Int,
    val height : Int,
    color:Int = 0xFFFFFFFF.toInt()
): DisplayableElement(
    DisplayableElementType.SQUARE,
    "null",
    DisplayAttachments.TOP_LEFT,
    null, null,
    color
) {
    override fun render(context: GuiGraphicsExtractor) {
        val position = LionRenderEngine.calculatePosition(this, context.guiHeight(), context.guiWidth())
        var x2: Int = position.x + width
        var y2:Int = position.y + height
        if (getDisplayAttachments().isRight()){
            x2 = position.x - width
        }
        if (getDisplayAttachments().isCentered()){
            x2 = position.x - width/2
            position.x += width/2
        }
        if (getDisplayAttachments().isBottom()){
            y2 = position.y - height
        }
        if (getDisplayAttachments().isCentered()){
            y2 = position.y - height/2
            position.y += height/2
        }

        context.fill(position.x, position.y, x2, y2, color)
    }
}
class DisplayableOutline(
    val width : Int,
    val height : Int,
    color:Int = 0xFFFFFFFF.toInt()
): DisplayableElement(
    DisplayableElementType.FRAME,
    "null",
    DisplayAttachments.TOP_LEFT,
    null, null,
    color
) {
    override fun render(context: GuiGraphicsExtractor) {
        val position = LionRenderEngine.calculatePosition(this, context.guiHeight(), context.guiWidth())
        context.outline(position.x, position.y, width, height, color)
    }
}

class DisplayableItem (
    val item: ItemStack
): DisplayableElement(
    DisplayableElementType.ITEM,
    "null"
) {
    override fun render(context: GuiGraphicsExtractor) {
        val position = LionRenderEngine.calculatePosition(this, context.guiHeight(), context.guiWidth())
        context.fakeItem(item, position.x, position.y)
    }
}


class DisplayableText(
    text: String,
    val maxWidth: Int = 1440,
    color:Int = 0xFFFFFFFF.toInt()
): DisplayableElement(
    DisplayableElementType.TEXT,
    text
){
    val text: Component = getTexts()

    private fun getTexts(): Component {
        try {
            val jsonElement: JsonElement? = JsonParser.parseString(super.data)
            val result: DataResult<Pair<Component?, JsonElement?>?>? = ComponentSerialization.CODEC.decode<JsonElement?>(JsonOps.INSTANCE, jsonElement)
            if (result != null){
                if (result.result().get().first != null)
                    return result.result().get().first!!
            }
        }catch (_: JsonParseException){}
        catch (_: JsonSyntaxException){}

        return Component.literal(super.data)
    }

    override fun render(context: GuiGraphicsExtractor) {
        val font = Minecraft.getInstance().font
        val position = LionRenderEngine.calculatePosition(this, context.guiHeight(), context.guiWidth())
        val additionalOffset = if (getDisplayAttachments().isCentered()) font.width(text)/2
        else if (getDisplayAttachments().isRight()) font.width(text)
        else 0
        context.textWithWordWrap(font, text, position.x - additionalOffset, position.y, maxWidth, color)
    }
}

class DisplayableEntity(
    val entity: EntityRenderState
): DisplayableElement(
    DisplayableElementType.ENTITY,
    "null"
) {
    override fun render(context: GuiGraphicsExtractor) {
        context.entity(entity, 1.0f, Vector3f(), Quaternionf(), null, 9, 9, 100, 100)
    }
}

class DisplayableTexture(
    text: String,
    val width: Int,
    val height: Int
): DisplayableElement(
    DisplayableElementType.TEXTURE,
    text
) {
    override fun render(context: GuiGraphicsExtractor) {
        val position = LionRenderEngine.calculatePosition(this, context.guiHeight(), context.guiWidth())
        var x2: Int = position.x + width
        var y2:Int = position.y + height
        if (getDisplayAttachments().isRight()){
            x2 = position.x - width
        }
        if (getDisplayAttachments().isCentered()){
            x2 = position.x - width/2
            position.x += width/2
        }
        if (getDisplayAttachments().isBottom()){
            y2 = position.y - height
        }
        if (getDisplayAttachments().isCentered()){
            y2 = position.y - height/2
            position.y += height/2
        }
        context.blit(
            RenderPipelines.GUI_TEXTURED,
            Identifier.parse(data),
            x2, y2,
            0.toFloat(), 0.toFloat(),
            width, height,
            width, height)
    }
}

class DisplayableCompass(
    val pos: Vec3,
    dimension: String?
): DisplayableElement(
    DisplayableElementType.COMPASS,
    if(dimension?.startsWith("minecraft:")?:false) dimension
    else "minecraft:${dimension?:"null"}"
) {
    var coordinateScale: Double = 1.0
    constructor(pos: Vec3) : this(pos, null)

    init {
        val player = Minecraft.getInstance().player
        if (player != null) {
            if((player.level().dimension().identifier().toString()) == dimension) {
                coordinateScale = player.level().dimensionType().coordinateScale
            } else {
                if (dimension != null && dimension.contains("nether", true)) {
                    coordinateScale = 8.0
                }
            }
        }
    }

    fun getCoordinates(): Vec3 {
        val player = Minecraft.getInstance().player
        val currentScale = player?.level()?.dimensionType()?.coordinateScale ?: 1.0
        val targetScale = coordinateScale / currentScale
        if(targetScale != 1.0 && ModConfig.dimensionManagement == CompassDimensionHandling.CONVERT){
            return Vec3(pos.x * targetScale, pos.y, pos.z * targetScale)
        }
        return pos
    }

    var startDistance: Double? = null
    override fun render(context: GuiGraphicsExtractor) {

        val client = Minecraft.getInstance()
        val player = client.player
        val compassSize = 64
        if (player == null) return
        var worldName = player.level().dimension().identifier().toString()
        if (worldName.equals("minecraft:overworld", true)) worldName = "minecraft:world"
        val canDisplay = (((worldName == data) || ModConfig.dimensionManagement != CompassDimensionHandling.ERROR))
        val playerPos = player.position()
        val pos = getCoordinates()
        val distance = pos.distanceTo(playerPos)
        val distanceY = abs(pos.y - playerPos.y)
        if (startDistance == null){
            startDistance = distance
        }


        val position = LionRenderEngine.calculatePosition(this, context.guiHeight(), context.guiWidth())
        var x2: Int = position.x
        var y2:Int = position.y

        if (getDisplayAttachments().isRight()){
            x2 = position.x - compassSize
        }
        if (getDisplayAttachments().isCentered()){
            x2 = position.x - compassSize/2
            position.x = position.x+compassSize/2
        }
        if (getDisplayAttachments().isBottom()){
            y2 = position.y - compassSize
            if (ModConfig.heightIndicator|| ModConfig.compassDistance) y2-=10
        }
        if (getDisplayAttachments().isMiddle()){
            y2 = position.y - compassSize/2
            position.y = position.y+compassSize/2
        }

        val centerX: Float = (x2 + compassSize / 2).toFloat();
        val centerY: Float = (y2 + compassSize / 2).toFloat();

        var text = ""
        if(canDisplay) {
            val displayHeight = isDisplayHeightIndicator(playerPos.y, pos.y)
            if (ModConfig.compassDistance) text += "${distance.roundToInt()}m"
            if (ModConfig.compassDistance && displayHeight) text += "   |   "
            if (displayHeight) text += if (playerPos.y - pos.y < 0) {
                "↑"
            } else "↓"
        }else text+="WRONG DIMENSION"


        context.pose().pushMatrix();

        context.pose().translate(centerX, centerY);

        val mcText = if(canDisplay) Component.literal(text) else Component.translatable("compass.wrong_dimension").withColor(0xFFFF0000.toInt())
        context.centeredText(
            Minecraft.getInstance().font,
            mcText,
            0,
            compassSize/2,
            0xFFFFFFFF.toInt()
        )

        context.pose().rotate(Math.toRadians((360.0 - player.getYRot().toDouble() - 180.0)).toFloat())

        val texture = if (ModConfig.compassColoring){
            if (distance<= DisplayData.maxDistanceXZ&&distanceY<= DisplayData.maxDistanceY) compass_green
            else startDistance?.let {
                if (it<distance) compass_red
                else compass
            }
        } else {
            compass
        }
        context.blit(
            RenderPipelines.GUI_TEXTURED,
            texture!!,
            -compassSize/2, -compassSize/2,          // Top-left corner position (X, Y)
            0.toFloat(), 0.toFloat(),          // UV coordinates of the texture to start drawing from
            compassSize,   // Width of the texture to draw
            compassSize,   // Height of the texture to draw
            compassSize,   // The total width of the texture file
            compassSize    // The total height of the texture file
        )

        context.pose().popMatrix();

        val deltaX = pos.x - playerPos.x;
        val deltaZ = pos.z - playerPos.z;
        val angleToTarget = atan2(deltaZ, deltaX);

        val playerYaw = Mth.wrapDegrees(player.getYRot())
        val playerRotation = Math.toRadians((playerYaw).toDouble())
        val finalAngle = angleToTarget - playerRotation

        // Define the size of your needle texture
        val needleWidth = 30;
        val needleHeight = 30;

        // Save the current matrix state so our rotation doesn't affect other UI elements
        context.pose().pushMatrix();

        // 1. Translate to the center of the compass. This makes the center our rotation point.
        context.pose().translate(centerX, centerY);

        // 2. Rotate the matrix by the final angle.
        context.pose().rotate((finalAngle + Math.PI / 2).toFloat())

        // 3. Draw the needle texture.
        if(canDisplay)
            context.blit(
                RenderPipelines.GUI_TEXTURED,
                COMPASS_NEEDLE_TEXTURE,
                -needleWidth / 2, -needleHeight / 2, // Position relative to the new, rotated center
                0.toFloat(), 0.toFloat(),                                // UV coordinates
                needleWidth, needleHeight,           // Width and height to draw
                needleWidth, needleHeight           // Total texture file size
            )

        // Restore the original matrix state
        context.pose().popMatrix();

    }

    private fun isDisplayHeightIndicator(playerY: Double, targetY: Double): Boolean{
        if (!ModConfig.heightIndicator) return false
        return (playerY-targetY).absoluteValue >= DisplayData.maxDistanceY
    }

    companion object{
        val COMPASS_NEEDLE_TEXTURE = Identifier.parse("liondisplays:textures/gui/compass_needle.png")
        val compass = Identifier.fromNamespaceAndPath("liondisplays", "textures/gui/compass.png")
        val compass_red = Identifier.fromNamespaceAndPath("liondisplays", "textures/gui/compass_red.png")
        val compass_green = Identifier.fromNamespaceAndPath("liondisplays", "textures/gui/compass_green.png")
    }
}

data class Position(
    var x: Int,
    var y : Int
)