package dev.thestaticvoid.stcm.mixin;

import aztech.modern_industrialization.client.compat.viewer.impl.emi.ManualPluginEmi;
import dev.emi.emi.api.EmiRegistry;
import dev.thestaticvoid.stcm.client.compat.viewer.emi.NeoForgeHammerRecipeHandler;
import dev.thestaticvoid.stcm.screen.STCMMenuTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ManualPluginEmi.class)
public class ManualPluginEmiMixin {
    @Inject(method = "register", at = @At("TAIL"))
    private void registerMixin(EmiRegistry registry, CallbackInfo ci) {
        registry.addRecipeHandler(STCMMenuTypes.NEOFORGE_HAMMER_MENU.get(), new NeoForgeHammerRecipeHandler());
    }
}
