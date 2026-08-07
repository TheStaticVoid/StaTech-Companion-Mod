package dev.thestaticvoid.stcm.client;

import dev.thestaticvoid.stcm.STCM;
import dev.thestaticvoid.stcm.client.entity.STCMEntityRenderer;
import dev.thestaticvoid.stcm.screen.NeoForgeHammerScreen;
import dev.thestaticvoid.stcm.screen.STCMMenuTypes;
import dev.thestaticvoid.stcm.world.dimension.SpaceDimensionEffects;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = STCM.ID, dist = Dist.CLIENT)
public class STCMClient {
    public STCMClient(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(STCMEntityRenderer::init);
    }

    @SubscribeEvent
    public static void onRegisterDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(STCM.id("space"), new SpaceDimensionEffects());
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(STCMMenuTypes.ENHANCED_FORGE_HAMMER_MENU.get(), NeoForgeHammerScreen::new);
    }
}
