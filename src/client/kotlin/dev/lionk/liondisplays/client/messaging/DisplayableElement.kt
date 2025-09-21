package dev.lionk.liondisplays.client.messaging

import net.minecraft.client.gui.DrawContext

abstract class DisplayableElement(
    val type: DisplayableElementType,
    val data: String,
    var displayAttachments: DisplayAttachments = DisplayAttachments.TOP_LEFT,
    var offsetX:Int = 9,
    var offsetY:Int = 9,
    var color: Int = 0xFFFFFFFF.toInt()
) {
    abstract fun render(context: DrawContext)
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