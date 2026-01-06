package dev.lionk.liondisplays.client.messaging

import dev.lionk.liondisplays.client.configuration.ModConfig
import net.minecraft.client.gui.DrawContext

abstract class DisplayableElement(
    val type: DisplayableElementType,
    val data: String,
    private var displayAttachments: DisplayAttachments? = null,
    private var offsetX:Int? = null,

    private var offsetY:Int? = null,
    protected var color: Int = 0xFFFFFFFF.toInt()
) {
    abstract fun render(context: DrawContext)
    fun getOffsetX(): Int{
        return offsetX ?: ModConfig.offset
    }
    fun getOffsetY(): Int{
        return offsetY ?: ModConfig.offset
    }
    fun getDisplayAttachments(): DisplayAttachments{
        return displayAttachments ?: ModConfig.defaultAttachment
    }
    fun setDisplayAttachments(displayAttachments: DisplayAttachments?){
        this.displayAttachments = displayAttachments
    }
    fun setOffsetX(offset: Int?){
        this.offsetX = offset
    }
    fun setOffsetY(offset: Int?){
        this.offsetY = offset
    }

}

enum class DisplayableElementType{
    TEXT,
    ITEM,
    ENTITY,
    SIGN,
    BOOK,
    SQUARE,
    FRAME,
    TEXTURE,
    COMPASS,
    CUSTOM
}