package dev.thestaticvoid.stcm.space.client;

import dev.thestaticvoid.stcm.StaTechCompanionMod;
import dev.thestaticvoid.stcm.space.SpaceDimensionEffects;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;

@EventBusSubscriber(modid = StaTechCompanionMod.MODID, value = Dist.CLIENT)
public class SpaceEventBus {
    @SubscribeEvent
    public static void onRegisterDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(ResourceLocation.fromNamespaceAndPath(StaTechCompanionMod.MODID, "space"), new SpaceDimensionEffects());
    }
}