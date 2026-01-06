package dev.lionk.liondisplays.mixin.client;

import dev.lionk.liondisplays.client.LionAPI.screens.CustomLoadingScreen;
import dev.lionk.liondisplays.client.reconfiguring.VelocityRegistration;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ReconfiguringScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "setScreen", at = @At("HEAD"), cancellable = true)
    private void onSetScreen(Screen screen, CallbackInfo ci) {
        // Check if the screen being set is the one we want to replace
        if (screen instanceof ReconfiguringScreen) {
            if (VelocityRegistration.INSTANCE.isPlayingAnimation()){
                Text originalTitle = screen.getTitle();

                // Create and set your replacement screen
                ((MinecraftClient)(Object)this).setScreen(new CustomLoadingScreen(originalTitle));

                // Cancel the original call to prevent ReconfiguringScreen from being set
                ci.cancel();
            }
        }
    }
}
