package dev.thestaticvoid.stcm.entity;

import dev.thestaticvoid.stcm.STCM;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class STCMEntity {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(Registries.ENTITY_TYPE, STCM.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<PrimedNuke>> PRIMED_NUKE = ENTITY_TYPES.register("primed_nuke",
        () -> EntityType.Builder.<PrimedNuke>of(PrimedNuke::new, MobCategory.MISC).sized(1.0F, 1.0F).fireImmune().build("primed_nuke"));

    public static void init (IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
