package dev.lionk.liondisplays.client.messaging


object DisplayData {
    val values = mutableMapOf<String, DisplayableElement>()
    var maxDistanceXZ: Double = 10.0
    var maxDistanceY: Double = 4.0

    fun getElement(id: String): DisplayableElement? {
        return values[id]
    }

    fun hasDisplayableElement(id: String): Boolean{
        return values.containsKey(id)&&values[id] !=null
    }

}
