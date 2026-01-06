package dev.lionk.liondisplays.client.LionAPI.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;

public class CustomLoadingScreen extends Screen {
    public CustomLoadingScreen(Text title) {
        super(title);
    }

    // Make sure to @Override and implement your full custom render logic
    @Override
    public void render(DrawContext dc, int mouseX, int mouseY, float delta) {

        Screen self = (Screen) (Object)this;
        int width = self.width;
        int height = self.height;

        // 1. Draw a solid black background
        // The color 0xFF000000 is ARGB (Alpha=FF, Red=00, Green=00, Blue=00), which is solid black.
        dc.fill(0, 0, width, height, -0x1000000);
        Text message = Text.literal("Switching Servers... Please wait.");
        dc.drawCenteredTextWithShadow(this.textRenderer, message, this.width / 2 , this.height - 100, 0xFFFFFF);

        // Call super.render to handle widgets if you add any
        super.render(dc, mouseX, mouseY, delta);
    }

    // You might need to override tick() or other methods for animation
}