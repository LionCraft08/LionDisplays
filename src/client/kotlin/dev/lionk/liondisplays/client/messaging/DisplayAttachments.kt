package dev.lionk.liondisplays.client.messaging

enum class DisplayAttachments (){
    TOP_LEFT,
    TOP_CENTER,
    TOP_RIGHT,
    MIDDLE_LEFT,
    MIDDLE_CENTER,
    MIDDLE_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_CENTER,
    BOTTOM_RIGHT;

    fun isTop() : Boolean{
        return this.toString().contains("TOP")
    }
    fun isMiddle() : Boolean{
        return this.toString().contains("MIDDLE")
    }
    fun isBottom() : Boolean{
        return this.toString().contains("BOTTOM")
    }
    fun isCentered() : Boolean{
        return this.toString().contains("CENTER")
    }
    fun isLeft() : Boolean{
        return this.toString().contains("LEFT")
    }
    fun isRight() : Boolean{
        return this.toString().contains("RIGHT")
    }
}