package dev.thestaticvoid.stcm.block;

import dev.thestaticvoid.stcm.STCM;
import dev.thestaticvoid.stcm.item.STCMItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class STCMBlock {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(STCM.ID);

    public static final DeferredBlock<Block> NEOFORGE_HAMMER = registerBlock("neoforge_hammer",
            () -> new NeoForgeHammerBlock(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.METAL)
                    .destroyTime(6.0F)
                    .explosionResistance(1200)
                    .sound(SoundType.ANVIL)
                    .requiresCorrectToolForDrops()
                    .dynamicShape()
                    .noOcclusion()));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        STCMItem.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }


    public static void init(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
