package dev.thestaticvoid.stcm.client;

import dev.thestaticvoid.stcm.STCM;
import dev.thestaticvoid.stcm.data.MaterialLoader;
import dev.thestaticvoid.stcm.screen.NeoForgeHammerScreen;
import dev.thestaticvoid.stcm.screen.STCMMenuTypes;
import dev.thestaticvoid.stcm.world.dimension.SpaceDimensionEffects;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = STCM.ID, value = Dist.CLIENT)
public class STCMClient {
    @SubscribeEvent
    public static void onRegisterDimensionSpecialEffects(RegisterDimensionSpecialEffectsEvent event) {
        event.register(STCM.id("space"), new SpaceDimensionEffects());
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(STCMMenuTypes.NEOFORGE_HAMMER_MENU.get(), NeoForgeHammerScreen::new);
    }

    @SubscribeEvent
    private static void onReloadClientResources(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(MaterialLoader.INSTANCE);
    }
}
