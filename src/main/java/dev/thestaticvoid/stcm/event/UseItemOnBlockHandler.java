package dev.thestaticvoid.stcm.event;

import aztech.modern_industrialization.MIBlock;
import dev.thestaticvoid.stcm.STCM;
import dev.thestaticvoid.stcm.entity.PrimedNuke;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

public class UseItemOnBlockHandler {

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
                    // event.cancelWithResult(ItemInteractionResult.CONSUME);
                }
            }
        }
    }
}
