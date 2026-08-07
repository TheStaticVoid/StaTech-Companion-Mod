package dev.thestaticvoid.stcm.screen;

import aztech.modern_industrialization.MI;
import aztech.modern_industrialization.blocks.forgehammer.ForgeHammerRecipe;
import aztech.modern_industrialization.client.util.RenderHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.crafting.RecipeHolder;


public class NeoForgeHammerScreen extends AbstractContainerScreen<NeoForgeHammerMenu> {
    private static final ResourceLocation FORGE_HAMMER_GUI = MI.id("textures/gui/container/forge_hammer.png");
    private static final int X_OFFSET = 61;
    private static final int Y_OFFSET = 14;
    private final NeoForgeHammerMenu menu;

    public NeoForgeHammerScreen(NeoForgeHammerMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.menu = menu;
    }

    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int i = this.leftPos + X_OFFSET;
        int j = this.topPos + Y_OFFSET + 2;
        int x1 = (int) Math.floor((mouseX - (double) i) / (double) 16.0F);
        int y1 = (int) Math.floor((mouseY - (double) j) / (double) 18.0F);
        if (x1 >= 0 && x1 <= 3 && y1 >= 0 && y1 <= 2) {
            int id = x1 + y1 * 4;
            if (id < this.menu.getAvailableRecipeCount()) {
                Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 1.0F));
                this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, id);
                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    protected void renderBg(GuiGraphics guiGraphics, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(FORGE_HAMMER_GUI, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
        int l = this.leftPos + 61;
        int m = this.topPos + 14;
        this.renderRecipeBackground(guiGraphics, mouseX, mouseY, l, m);
        this.renderRecipeIcons(guiGraphics, l, m);
    }

    private void renderRecipeIcons(GuiGraphics guiGraphics, int x, int y) {
        for(int i = 0; i < this.menu.getAvailableRecipeCount(); ++i) {
            int k = x + i % 4 * 16;
            int l = i / 4;
            int m = y + l * 18 + 2;
            RenderHelper.renderAndDecorateItem(guiGraphics, this.font, ((ForgeHammerRecipe)((RecipeHolder)this.menu.getAvailableRecipes().get(i)).value()).result(), k, m);
        }

    }

    private void renderRecipeBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, int x, int y) {
        for(int i = 0; i < this.menu.getAvailableRecipeCount(); ++i) {
            int k = x + i % 4 * 16;
            int l = i / 4;
            int m = y + l * 18 + 2;
            int n = this.imageHeight;
            if (i == this.menu.getSelectedRecipe()) {
                n += 18;
            } else if (mouseX >= k && mouseY >= m && mouseX < k + 16 && mouseY < m + 18) {
                n += 36;
            }

            guiGraphics.blit(FORGE_HAMMER_GUI, k, m - 1, 0, n, 16, 18);
        }

    }

    protected void renderTooltip(GuiGraphics guiGraphics, int x, int y) {
        super.renderTooltip(guiGraphics, x, y);
        int x1 = this.leftPos + 61;
        int y1 = this.topPos + 14;

        for(int l = 0; l < this.menu.getAvailableRecipeCount(); ++l) {
            int n = x1 + l % 4 * 16;
            int o = y1 + l / 4 * 18 + 2;
            if (x >= n && x < n + 16 && y >= o && y < o + 18) {
                guiGraphics.renderTooltip(this.font, ((ForgeHammerRecipe)((RecipeHolder)this.menu.getAvailableRecipes().get(l)).value()).result(), x, y);
            }
        }

    }
}
