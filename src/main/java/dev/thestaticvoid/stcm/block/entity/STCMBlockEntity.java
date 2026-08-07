package dev.thestaticvoid.stcm.block.entity;

import dev.thestaticvoid.stcm.STCM;
import dev.thestaticvoid.stcm.block.STCMBlock;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class STCMBlockEntity {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, STCM.ID);

    public static final Supplier<BlockEntityType<NeoForgeHammerBlockEntity>> ENHANCED_FORGE_HAMMER_BE =
            BLOCK_ENTITIES.register("enhanced_forge_hammer_be", () -> BlockEntityType.Builder.of(
                    NeoForgeHammerBlockEntity::new, STCMBlock.ENHANCED_FORGE_HAMMER.get()).build(null));

    public static void init(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
