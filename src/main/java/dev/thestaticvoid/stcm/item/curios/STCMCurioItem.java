package dev.thestaticvoid.stcm.item.curios;

import java.util.Optional;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.type.capability.ICurioItem;

public abstract class STCMCurioItem extends Item implements ICurioItem {

  public STCMCurioItem(Properties properties) {
    super(properties);
  }

public static boolean hasEquipped(LivingEntity entity, Item item) {
		return getFirstEquipped(entity, item).isPresent();
	}
	
	public static Optional<ItemStack> getFirstEquipped(LivingEntity entity, Item item) {
		return CuriosApi.getCuriosInventory(entity).flatMap(inventory -> inventory.findFirstCurio(item)).map(SlotResult::stack);
	}

	@Override
	public boolean canEquip(SlotContext slotContext, ItemStack stack) {
    // Can only a single curio of that type be equipped at once?
    if (!canEquipMoreThanOne() && hasEquipped(slotContext.entity(), this)) {
      return false;
    } else {
      return true;
    }
	}
	
	public boolean canEquipMoreThanOne() {
		return false;
	}
	
	@Override
	public void onEquip(SlotContext slotContext, ItemStack prevStack, ItemStack stack) {
		ICurioItem.super.onEquip(slotContext, prevStack, stack);
	}
	
	@Override
	public void onUnequip(SlotContext slotContext, ItemStack newStack, ItemStack stack) {
		ICurioItem.super.onUnequip(slotContext, newStack, stack);
	}
}
