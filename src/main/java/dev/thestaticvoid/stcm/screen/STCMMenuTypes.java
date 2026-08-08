package dev.thestaticvoid.stcm.screen;

import dev.thestaticvoid.stcm.STCM;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class STCMMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, STCM.ID);

    public static final DeferredHolder<MenuType<?>, MenuType<NeoForgeHammerScreenHandler>> NEOFORGE_HAMMER_MENU =
            registerMenuType("neoforge_hammer_menu", NeoForgeHammerScreenHandler::new);

    private static <T extends AbstractContainerMenu>DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(String name, IContainerFactory<T> factory) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

    public static void init(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}
