package dev.thestaticvoid.stcm.client;

import dev.thestaticvoid.stcm.STCM;
import dev.thestaticvoid.stcm.client.entity.STCMEntityRenderer;
import dev.thestaticvoid.stcm.screen.EnhancedForgeHammerScreen;
import dev.thestaticvoid.stcm.screen.STCMMenuTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = STCM.MODID, dist = Dist.CLIENT)
public class STCMClient {
    public STCMClient(IEventBus modBus, ModContainer modContainer) {
        modBus.addListener(STCMEntityRenderer::init);
    }

//    @SubscribeEvent
//    public static void onClientSetup(FMLClientSetupEvent event) {
//        STCMEntityRenderer.init(event);
//    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(STCMMenuTypes.ENHANCED_FORGE_HAMMER_MENU.get(), EnhancedForgeHammerScreen::new);
    }
}
