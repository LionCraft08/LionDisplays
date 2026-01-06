package dev.lionk.liondisplays.client.renderer

import dev.lionk.liondisplays.client.messaging.DisplayableElement
import dev.lionk.liondisplays.client.messaging.Position

object LionRenderEngine {
    fun calculatePosition(element: DisplayableElement, windowHeight: Int, windowWidth: Int): Position {
        return Position(
            getX(element, windowWidth, element.getOffsetX()),
            getY(element, windowHeight, element.getOffsetY()))

    }
    private fun getY(element: DisplayableElement, windowHeight:Int, offsetY:Int):Int{
        if (element.getDisplayAttachments().isTop()) return offsetY
        if (element.getDisplayAttachments().isMiddle()) return windowHeight / 2 + offsetY
        return windowHeight-offsetY
    }
    private fun getX(element: DisplayableElement, windowWidth:Int, offsetX:Int):Int{
        return if (element.getDisplayAttachments().isLeft()) offsetX
        else if (element.getDisplayAttachments().isCentered()) windowWidth/2+offsetX
        else windowWidth-offsetX
    }
}