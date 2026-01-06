package dev.lionk.liondisplays.client.messaging

import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.datafixers.util.Pair
import com.mojang.serialization.DataResult
import com.mojang.serialization.JsonOps
import dev.lionk.liondisplays.client.configuration.ModConfig
import dev.lionk.liondisplays.client.renderer.LionRenderEngine
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gl.RenderPipelines
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.render.BufferBuilder
import net.minecraft.client.render.GameRenderer
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexFormats
import net.minecraft.client.render.entity.state.EntityRenderState
import net.minecraft.item.ItemStack
import net.minecraft.text.Text
import net.minecraft.text.TextCodecs
import net.minecraft.util.Identifier
import net.minecraft.util.math.MathHelper
import net.minecraft.util.math.RotationAxis
import net.minecraft.util.math.Vec3d
import org.joml.Matrix4f
import org.joml.Quaternionf
import org.joml.Vector3f
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
    override fun render(context: DrawContext) {
        val position = LionRenderEngine.calculatePosition(this, context.scaledWindowHeight, context.scaledWindowWidth)
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
    override fun render(context: DrawContext) {
        val position = LionRenderEngine.calculatePosition(this, context.scaledWindowHeight, context.scaledWindowWidth)
        context.drawStrokedRectangle(position.x, position.y, width, height, color)
    }
}

class DisplayableItem (
    val item: ItemStack
): DisplayableElement(
    DisplayableElementType.ITEM,
    "null"
) {
    override fun render(context: DrawContext) {
        val position = LionRenderEngine.calculatePosition(this, context.scaledWindowHeight, context.scaledWindowWidth)
        context.drawItemWithoutEntity(item, position.x, position.y)
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
    val text: Text = getTexts()

    private fun getTexts(): Text {
        try {
            val jsonElement: JsonElement? = JsonParser.parseString(super.data)
            val result: DataResult<Pair<Text?, JsonElement?>?>? = TextCodecs.CODEC.decode<JsonElement?>(JsonOps.INSTANCE, jsonElement)
            if (result != null){
                if (result.result().get().first != null)
                    return result.result().get().first!!
            }
        }catch (_: JsonParseException){}
        catch (_: JsonSyntaxException){}

        return Text.literal(super.data)
    }

    override fun render(context: DrawContext) {
        val textRenderer = MinecraftClient.getInstance().textRenderer
        val position = LionRenderEngine.calculatePosition(this, context.scaledWindowHeight, context.scaledWindowWidth)
        val additionalOffset = if (getDisplayAttachments().isCentered()) textRenderer.getWidth(text)/2
        else if (getDisplayAttachments().isRight()) textRenderer.getWidth(text)
        else 0
        context.drawWrappedText(textRenderer, text, position.x - additionalOffset,position.y, maxWidth, color, true)
    }
}

class DisplayableEntity(
    val entity: EntityRenderState
): DisplayableElement(
    DisplayableElementType.ENTITY,
    "null"
) {
    override fun render(context: DrawContext) {
        context.addEntity(entity, 1.0f, Vector3f(), Quaternionf(), null, 9, 9, 100, 100)
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
    override fun render(context: DrawContext) {
        val position = LionRenderEngine.calculatePosition(this, context.scaledWindowHeight, context.scaledWindowWidth)
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
        context.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of(data), x2, y2, 0f, 0f, width, height, width, height);
    }
}

class DisplayableCompass(
    val pos: Vec3d
): DisplayableElement(
    DisplayableElementType.COMPASS,
    "null"
) {
    var startDistance: Double? = null
    override fun render(context: DrawContext) {
        val client = MinecraftClient.getInstance()
        val player = client.player
        val compassSize = 64
        if (player == null) return
        val playerPos = player.entityPos;
        val distance = pos.distanceTo(playerPos)
        val distanceY = abs(pos.y - playerPos.y)
        if (startDistance == null){
            startDistance = distance
        }


        val position = LionRenderEngine.calculatePosition(this, context.scaledWindowHeight, context.scaledWindowWidth)
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
        val displayHeight =isDisplayHeightIndicator(playerPos.y, pos.y)
        if (ModConfig.compassDistance) text+="${distance.roundToInt()}m"
        if (ModConfig.compassDistance&& displayHeight) text+="   |   "
        if (displayHeight) text+= if (playerPos.y-pos.y<0){
            "↑"
        }else "↓"


        context.getMatrices().pushMatrix();

        context.getMatrices().translate(centerX, centerY);

        context.drawCenteredTextWithShadow(
            MinecraftClient.getInstance().textRenderer,
            text,
            0,
            compassSize/2,
            0xFFFFFFFF.toInt()
        )

        context.getMatrices().rotate(Math.toRadians(360-player.yaw.toDouble()-180).toFloat());

        val texture = if (ModConfig.compassColoring){
            if (distance<= DisplayData.maxDistanceXZ&&distanceY<= DisplayData.maxDistanceY) compass_green
            else startDistance?.let {
                if (it<distance) compass_red
                else compass
            }
        } else {
            compass
        }
        context.drawTexture(
            RenderPipelines.GUI_TEXTURED,
            texture,
            -compassSize/2, -compassSize/2,          // Top-left corner position (X, Y)
            0f, 0f,          // UV coordinates of the texture to start drawing from
            compassSize,   // Width of the texture to draw
            compassSize,   // Height of the texture to draw
            compassSize,   // The total width of the texture file
            compassSize    // The total height of the texture file
        )

        context.matrices.popMatrix();

        val deltaX = pos.getX() - playerPos.getX();
        val deltaZ = pos.getZ() - playerPos.getZ();
        val angleToTarget = atan2(deltaZ, deltaX);

        val playerYaw = MathHelper.wrapDegrees(player.yaw)
        val playerRotation = Math.toRadians((playerYaw).toDouble())
        val finalAngle = angleToTarget - playerRotation


        // Define the size of your needle texture
        val needleWidth = 30;
        val needleHeight = 30;

        // Save the current matrix state so our rotation doesn't affect other UI elements
        context.getMatrices().pushMatrix();

        // 1. Translate to the center of the compass. This makes the center our rotation point.
        context.getMatrices().translate(centerX, centerY);

        // 2. Rotate the matrix by the final angle.
        // We add Math.PI / 2 because a texture drawn at (0,0) points "up" (negative Y),
        // but an angle of 0 in trigonometry points "right" (positive X). This corrects the offset.
        context.getMatrices().rotate(((finalAngle + Math.PI / 2).toFloat()));

        // 3. Draw the needle texture.
        // We draw it at (-width/2, -height/2) to make sure it's centered on the rotation point.
        context.drawTexture(
            RenderPipelines.GUI_TEXTURED,
            COMPASS_NEEDLE_TEXTURE,
            -needleWidth / 2, -needleHeight / 2, // Position relative to the new, rotated center
            0f, 0f,                                // UV coordinates
            needleWidth, needleHeight,           // Width and height to draw
            needleWidth, needleHeight            // Total texture file size
        );

        // Restore the original matrix state
        context.matrices.popMatrix();

    }
    private fun isDisplayHeightIndicator(playerY: Double, targetY: Double): Boolean{
        if (!ModConfig.heightIndicator) return false
        return (playerY-targetY).absoluteValue >= DisplayData.maxDistanceY
    }

    companion object{
        val COMPASS_NEEDLE_TEXTURE = Identifier.of("liondisplays", "textures/gui/compass_needle.png")
        val compass = Identifier.of("liondisplays", "textures/gui/compass.png")
        val compass_red = Identifier.of("liondisplays", "textures/gui/compass_red.png")
        val compass_green = Identifier.of("liondisplays", "textures/gui/compass_green.png")
    }
}

data class Position(
    var x: Int,
    var y : Int
)