package dev.lionk.liondisplays.client.LionAPI.screens;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

public class CustomLoadingScreen extends Screen {
    public CustomLoadingScreen(Component title) {
        super(title);
    }

    // Make sure to @Override and implement your full custom render logic
    @Override
    public void extractRenderState(@NonNull GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {

        Screen self = (Screen) (Object)this;
        int width = self.width;
        int height = self.height;

        // 1. Draw a solid black background
        // The color 0xFF000000 is ARGB (Alpha=FF, Red=00, Green=00, Blue=00), which is solid black.
        graphics.fill(0, 0, width, height, -0x1000000);
        Component message = Component.literal("Switching Servers... Please wait.");
        graphics.centeredText(Minecraft.getInstance().font, message, this.width / 2 , this.height - 100, 0xFFFFFF);

        // Call super.render to handle widgets if you add any
        super.extractRenderState(graphics, mouseX, mouseY, delta);
    }

    // You might need to override tick() or other methods for animation
}