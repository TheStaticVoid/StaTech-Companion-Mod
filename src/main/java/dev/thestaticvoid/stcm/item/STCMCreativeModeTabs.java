package dev.thestaticvoid.stcm.item;

import dev.thestaticvoid.stcm.STCM;
import dev.thestaticvoid.stcm.block.STCMBlock;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class STCMCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TAB = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, STCM.ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> COMAPNION_MOD_TAB = CREATIVE_MODE_TAB.register("stcm_tab",
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(STCMItem.SPACE_SHIELD.get()))
                    .title(Component.translatable("itemGroup.stcm"))
                    .displayItems(((itemDisplayParameters, output) -> {
                        output.accept(STCMItem.SPACE_SHIELD);
                        output.accept(STCMBlock.ENHANCED_FORGE_HAMMER);
                    })).build());

    public static void init(IEventBus eventBus) {
        CREATIVE_MODE_TAB.register(eventBus);
    }
}
