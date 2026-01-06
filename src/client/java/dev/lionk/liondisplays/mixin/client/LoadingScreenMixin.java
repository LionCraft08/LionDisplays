package dev.lionk.liondisplays.mixin.client;

import dev.lionk.liondisplays.client.reconfiguring.VelocityRegistration;
import net.minecraft.client.gui.DrawContext;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.world.LevelLoadingScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelLoadingScreen.class)
public abstract class LoadingScreenMixin {
    /**
     * @author LionK.dev
     * @reason Smoother Fades between Servers
     */
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(DrawContext context, int mouseX, int mouseY, float deltaTicks, CallbackInfo ci) {
        // Get the width and height of the screen
        Screen self = (Screen) (Object)this;
        if (!VelocityRegistration.INSTANCE.isPlayingAnimation()){
            return;
        }
        int width = self.width;
        int height = self.height;

        // 1. Draw a solid black background
        // The color 0xFF000000 is ARGB (Alpha=FF, Red=00, Green=00, Blue=00), which is solid black.
        context.fill(0, 0, width, height, -0x1000000);
        ci.cancel();
    }
}
