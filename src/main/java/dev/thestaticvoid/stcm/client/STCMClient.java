package dev.thestaticvoid.stcm.client;

import dev.thestaticvoid.stcm.STCM;
import dev.thestaticvoid.stcm.screen.EnhancedForgeHammerScreen;
import dev.thestaticvoid.stcm.screen.STCMMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = STCM.MODID, value = Dist.CLIENT)
public class STCMClient {
    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(STCMMenuTypes.ENHANCED_FORGE_HAMMER_MENU.get(), EnhancedForgeHammerScreen::new);
    }
}
