package dev.thestaticvoid.stcm.world;

import dev.thestaticvoid.stcm.STCM;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageType;
import net.neoforged.bus.api.IEventBus;

public class STCMWorld {
    public static final ResourceKey<DamageType> NUKE_DAMAGE = ResourceKey.create(Registries.DAMAGE_TYPE, STCM.id("nuke_damage"));

    public static void init(IEventBus eventBus) {

    }
}
