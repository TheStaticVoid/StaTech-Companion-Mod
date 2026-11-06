package dev.thestaticvoid.stcm.item;

import dev.thestaticvoid.stcm.STCM;
import dev.thestaticvoid.stcm.item.curios.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class STCMItem {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(STCM.ID);

    public static final DeferredItem<Item> ANGEL_RING = ITEMS.register(
            "angel_ring", () -> new AngelRing(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC))
    );

    public static final DeferredItem<Item> FIREPROOF_RING = ITEMS.register(
            "fireproof_ring", () -> new FireproofRing(new Item.Properties().rarity(Rarity.EPIC).fireResistant().stacksTo(1))
    );

    public static final DeferredItem<Item> PROSPECTOR_PICK = ITEMS.register(
            "prospector_pick", () -> new ProspectorPick(new Item.Properties().durability(128).stacksTo(1))
    );

    public static void init(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
