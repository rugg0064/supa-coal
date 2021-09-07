package net.rugg0064.my.cool.mod;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BoxScreen extends HandledScreen<ScreenHandler> {
    private BoxScreenHandler screenHandler;
    //A path to the gui texture. In this example we use the texture from the dispenser
    public static final Identifier[] TEXTURES = new Identifier[]{
            new Identifier("coolmod", "textures/gui/ovenguis/1x1.png"),
            new Identifier("coolmod", "textures/gui/ovenguis/2x2.png"),
            new Identifier("coolmod", "textures/gui/ovenguis/3x3.png"),
            new Identifier("coolmod", "textures/gui/ovenguis/4x4.png"),
            new Identifier("coolmod", "textures/gui/ovenguis/5x5.png"),
            new Identifier("coolmod", "textures/gui/ovenguis/6x6.png"),
            new Identifier("coolmod", "textures/gui/ovenguis/7x7.png")
    };

    public BoxScreen(ScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        this.backgroundWidth = 176;
        this.backgroundHeight = 240;
        this.playerInventoryTitleX = 8;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
        screenHandler = (BoxScreenHandler) handler;
    }

    @Override
    protected void drawBackground(MatrixStack matrices, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        RenderSystem.setShaderTexture(0, TEXTURES[screenHandler.size - 1]);
;
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;
        drawTexture(matrices, x, y, 0, 0, backgroundWidth, backgroundHeight);
    }

    @Override
    public void render(MatrixStack matrices, int mouseX, int mouseY, float delta) {
        renderBackground(matrices);
        super.render(matrices, mouseX, mouseY, delta);
        drawMouseoverTooltip(matrices, mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        // Center the title
        titleX = (backgroundWidth - textRenderer.getWidth(title)) / 2;
    }
}

