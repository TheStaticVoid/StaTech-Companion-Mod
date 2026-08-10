package dev.thestaticvoid.stcm.screen;

import aztech.modern_industrialization.MI;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;


public class NeoForgeHammerScreen extends AbstractContainerScreen<NeoForgeHammerScreenHandler> {
    public static final ResourceLocation FORGE_HAMMER_GUI = MI.id("textures/gui/container/forge_hammer.png");
    private static final int X_OFFSET = 61, Y_OFFSET = 14;
    private final NeoForgeHammerScreenHandler handler;
    public NeoForgeHammerScreen(NeoForgeHammerScreenHandler handler, Inventory playerInventory, Component title) {
        super(handler, playerInventory, title);
        this.handler = handler;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(FORGE_HAMMER_GUI, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        int l = this.leftPos + X_OFFSET;
        int m = this.topPos + Y_OFFSET;
        // this.renderRecipeBackground(guiGraphics, mouseX, mouseY, l, m);
        // this.renderRecipeIcons(guiGraphics, l, m);
    }
}
