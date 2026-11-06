package dev.thestaticvoid.stcm.event;

import java.util.Optional;
import aztech.modern_industrialization.MIBlock;
import dev.thestaticvoid.stcm.STCM;
import dev.thestaticvoid.stcm.entity.PrimedNuke;
import dev.thestaticvoid.stcm.item.*;
import dev.thestaticvoid.stcm.item.curios.FireproofRing;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;
import net.neoforged.neoforge.event.entity.living.*;
import top.theillusivec4.curios.api.*;
import top.theillusivec4.curios.api.type.capability.*;

@EventBusSubscriber(modid = STCM.ID)
public class STCMEventListeners {

    @SubscribeEvent
    public static void onPlayerUseItem(UseItemOnBlockEvent event) {
        if (!event.getLevel().isClientSide()) {
            UseOnContext context = event.getUseOnContext();
            if (context.getItemInHand().is(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("minecraft", "flint_and_steel")))) {
                if (context.getLevel().getBlockState(context.getClickedPos()).is(MIBlock.NUKE.get())) {
                    STCM.LOGGER.info("context - x: {}, y: {}, z: {}", context.getClickedPos().getX(), context.getClickedPos().getY(), context.getClickedPos().getZ());
                    context.getLevel().removeBlock(context.getClickedPos(), false);
                    PrimedNuke primedNuke = new PrimedNuke(context.getLevel(),
                            (double)context.getClickedPos().getX() + 0.5,
                            (double)context.getClickedPos().getY(),
                            (double)context.getClickedPos().getZ() + 0.5,
                            context.getPlayer());
                    context.getLevel().addFreshEntity(primedNuke);
                    context.getLevel().playSound(null, primedNuke.getX(), primedNuke.getY(), primedNuke.getZ(), SoundEvents.TNT_PRIMED, SoundSource.BLOCKS, 1.0F, 1.0F);
                    context.getLevel().gameEvent(context.getPlayer(), GameEvent.PRIME_FUSE, context.getClickedPos());
                    event.cancelWithResult(ItemInteractionResult.CONSUME);
                }
            }
        }
    }

    @SubscribeEvent
    private static void onIncomingDamage(LivingIncomingDamageEvent event) {
        LivingEntity entity = event.getEntity();
		DamageSource source = event.getSource();
		
		// If the player is damaged by fire / lava and wears an fireproof ring:
		// prevent damage and grant fire resistance
		if (source.is(DamageTypeTags.IS_FIRE)) {
			Optional<ItemStack> fireproofRingStack = FireproofRing.getFirstEquipped(entity, STCMItem.FIREPROOF_RING.get());
			if (fireproofRingStack.isPresent()) {
				if (source.is(DamageTypes.LAVA)) {
					FireproofRing.grantFireResistance(fireproofRingStack.get(), entity);
					event.setCanceled(true);
				} else {
					event.setCanceled(true);
				}
			}
        }
    }
}
