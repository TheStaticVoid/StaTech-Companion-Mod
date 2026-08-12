package dev.thestaticvoid.stcm.client.compat.viewer.emi;

import aztech.modern_industrialization.client.compat.viewer.usage.ForgeHammerCategory;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.thestaticvoid.stcm.network.NeoForgeHammerMoveRecipePacket;
import dev.thestaticvoid.stcm.screen.NeoForgeHammerScreenHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class NeoForgeHammerRecipeHandler implements StandardRecipeHandler<NeoForgeHammerScreenHandler> {
    @Override
    public List<Slot> getInputSources(NeoForgeHammerScreenHandler handler) {
        List<Slot> inputs = new ArrayList<>();
        // Player inventory
        for (int i = 0; i < 36; ++i) {
            inputs.add(handler.getSlot(i));
        }
        // Extra
        inputs.add(handler.input);
        inputs.add(handler.tool);
        return inputs;
    }

    @Override
    public List<Slot> getCraftingSlots(NeoForgeHammerScreenHandler handler) {
        return List.of(handler.input, handler.tool);
    }

    @Override
    @Nullable
    public Slot getOutputSlot(NeoForgeHammerScreenHandler handler) {
        return handler.output;
    }

    @Override
    public boolean supportsRecipe(EmiRecipe recipe) {
        return recipe.getCategory().getId().equals(ForgeHammerCategory.ID);
    }

    @Override
    public boolean craft(EmiRecipe recipe, EmiCraftContext<NeoForgeHammerScreenHandler> context) {
        new NeoForgeHammerMoveRecipePacket(
                context.getScreenHandler().containerId,
                recipe.getId(),
                switch (context.getDestination()) {
                    case NONE -> 0;
                    case CURSOR -> 1;
                    case INVENTORY -> 2;
                },
                context.getAmount()).sendToServer();

        Minecraft.getInstance().setScreen(context.getScreen());
        return true;
    }
}
