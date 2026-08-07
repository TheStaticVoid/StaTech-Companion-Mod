package dev.thestaticvoid.stcm.client.entity;

import dev.thestaticvoid.stcm.STCM;
import dev.thestaticvoid.stcm.entity.STCMEntity;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

@EventBusSubscriber(modid = STCM.ID, value = Dist.CLIENT)
public class STCMEntityRenderer {

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        EntityRenderers.register(STCMEntity.PRIMED_NUKE.get(), PrimedNukeRenderer::new);
    }
}