package dev.thestaticvoid.stcm.guideme;

import aztech.modern_industrialization.guidebook.MultiblockShapeCompiler;
import dev.thestaticvoid.stcm.StaTechCompanionMod;
import guideme.Guide;
import guideme.scene.element.SceneElementTagCompiler;
import net.minecraft.resources.ResourceLocation;

public class StaTechGuide {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(StaTechCompanionMod.MODID, "guide");
    private static Guide guide;

    public static void init() {
        guide = Guide.builder(ID)
                .extension(SceneElementTagCompiler.EXTENSION_POINT, new MultiblockShapeCompiler())
                .build();
    }

    private StaTechGuide() {}
}
