package dev.thestaticvoid.stcm.guideme;

import aztech.modern_industrialization.guidebook.MultiblockShapeCompiler;
import dev.thestaticvoid.stcm.STCM;
import guideme.Guide;
import guideme.scene.element.SceneElementTagCompiler;
import net.minecraft.resources.ResourceLocation;

public class STCMGuide {
    public static final ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(STCM.ID, "guide");
    private static Guide guide;

    public static void init() {
        guide = Guide.builder(ID)
                .folder("guides/stcm/guide")
                .extension(SceneElementTagCompiler.EXTENSION_POINT, new MultiblockShapeCompiler())
                .build();
    }

    private STCMGuide() {}
}
