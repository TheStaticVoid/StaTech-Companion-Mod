package dev.thestaticvoid.stcm.space;

import net.minecraft.client.renderer.DimensionSpecialEffects;
import net.minecraft.world.phys.Vec3;

public class SpaceDimensionEffects extends DimensionSpecialEffects {
    public SpaceDimensionEffects() {
        super(
                -1000.0F,                             // cloud height
                false,                                          // has ground
                DimensionSpecialEffects.SkyType.NORMAL,         // sky type
                false,                                          // force bright lightmap
                false                                           // fog
        );
    }

    @Override
    public Vec3 getBrightnessDependentFogColor(Vec3 color, float brightness) {
        return new Vec3(0.0, 0.0, 0.0);
    }

    @Override
    public boolean isFoggyAt(int x, int z) {
        return false;
    }
}