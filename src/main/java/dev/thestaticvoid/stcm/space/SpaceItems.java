package dev.thestaticvoid.stcm.space;

import dev.thestaticvoid.stcm.StaTechCompanionMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;

public class SpaceItems {
    public static final DeferredItem<Item> SPACE_SHIELD = StaTechCompanionMod.ITEMS.registerSimpleItem(
            "space_shield", new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
    );

    // Used to make sure items are ready before being registered
    public static void init() {}
}
