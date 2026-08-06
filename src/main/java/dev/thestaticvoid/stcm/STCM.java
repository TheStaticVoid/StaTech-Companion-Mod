package dev.thestaticvoid.stcm;

import com.mojang.logging.LogUtils;
import dev.thestaticvoid.stcm.block.STCMBlock;
import dev.thestaticvoid.stcm.block.entity.STCMBlockEntity;
import dev.thestaticvoid.stcm.entity.STCMEntity;
import dev.thestaticvoid.stcm.event.STCMEvent;
import dev.thestaticvoid.stcm.event.UseItemOnBlockHandler;
import dev.thestaticvoid.stcm.guideme.STCMGuide;
import dev.thestaticvoid.stcm.item.STCMCreativeModeTabs;
import dev.thestaticvoid.stcm.item.STCMItem;
import dev.thestaticvoid.stcm.screen.EnhancedForgeHammerScreen;
import dev.thestaticvoid.stcm.screen.STCMMenuTypes;
import dev.thestaticvoid.stcm.space.SpaceItems;
import dev.thestaticvoid.stcm.world.STCMWorld;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

@Mod(STCM.MODID)
public class STCM {
    public static final String MODID = "stcm";
    public static final Logger LOGGER = LogUtils.getLogger();

    public STCM(IEventBus modEventBus, ModContainer modContainer) {
        SpaceItems.init();
        STCMGuide.init();

        modContainer.registerConfig(ModConfig.Type.COMMON, STCMConfig.CONFIG_SPEC);

        STCMBlock.init(modEventBus);
        STCMBlockEntity.init(modEventBus);
        STCMMenuTypes.init(modEventBus);
        STCMItem.init(modEventBus);
        STCMCreativeModeTabs.init(modEventBus);
        STCMWorld.init(modEventBus);
        STCMEntity.init(modEventBus);
        STCMEvent.init(NeoForge.EVENT_BUS);
    }

    public static ResourceLocation Id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
