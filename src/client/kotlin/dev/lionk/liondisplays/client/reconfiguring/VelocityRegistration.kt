package dev.lionk.liondisplays.client.reconfiguring

import dev.lionk.liondisplays.client.messaging.DisplayC2SPayload
import kotlinx.coroutines.Runnable
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking

object VelocityRegistration {
    var isPlayingAnimation = false
    var hasLeftOldWorld = false
    var hasJoinedNewWorld = false
    var startTime: Long = 0
    fun handleIncomingMessage(message: String){
        when(message.substringBefore(":")){
            "velocity_start_fade" -> {
                isPlayingAnimation = true
                hasLeftOldWorld = false
                startTime = System.currentTimeMillis()
            }
        }
    }
    fun getFadeValue(): Float {
        var timeTaken = System.currentTimeMillis() - startTime
        if (hasLeftOldWorld){
            if (!hasJoinedNewWorld) {
                hasJoinedNewWorld = true
                startTime = System.currentTimeMillis()
                timeTaken = System.currentTimeMillis() - startTime
            }

            if (timeTaken>1000){
                endAnimation()
                return 0f
            }else if (timeTaken < 1000){
                return 1f-(timeTaken/1000f)
            }else{
                return 0f
            }
        }

        if (timeTaken > 1500){
            startTime = 0
            return 0f
        }
        else if (timeTaken < 1000){
            return timeTaken/(1000f)
        }else {
            return 1.0f
        }
    }
    fun endAnimation(){
        isPlayingAnimation = false
        hasLeftOldWorld = false
        startTime = 0
        hasJoinedNewWorld = false

    }
    fun sendMessageToServer(message: String){
        ClientPlayNetworking.send(ScreenC2SPayload(message.toByteArray()))
    }
}