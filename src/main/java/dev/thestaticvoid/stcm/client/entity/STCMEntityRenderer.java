package dev.thestaticvoid.stcm.client.entity;

import dev.thestaticvoid.stcm.entity.STCMEntity;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class STCMEntityRenderer {

    public static void init(FMLClientSetupEvent event) {
        EntityRenderers.register(STCMEntity.PRIMED_NUKE.get(), PrimedNukeRenderer::new);
    }
}