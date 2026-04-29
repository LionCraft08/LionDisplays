package dev.lionk.liondisplays.mixin.client;

import dev.lionk.liondisplays.client.LionAPI.screens.CustomLoadingScreen;
import dev.lionk.liondisplays.client.reconfiguring.VelocityRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.multiplayer.ServerReconfigScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftClientMixin {

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void onSetScreen(Screen screen, CallbackInfo ci) {
        // Check if the screen being set is the one we want to replace
        if (screen instanceof ServerReconfigScreen) {
            if (VelocityRegistration.INSTANCE.isPlayingAnimation()){
                Component originalTitle = screen.getTitle();

                // Create and set your replacement screen
                ((Minecraft)(Object)this).setScreen(new CustomLoadingScreen(originalTitle));

                // Cancel the original call to prevent ReconfiguringScreen from being set
                ci.cancel();
            }
        }
    }
}
