package dev.thestaticvoid.stcm.item.curios;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.TooltipFlag;
import top.theillusivec4.curios.api.*;
import net.minecraft.network.chat.Component;

import java.util.*;

public class FireproofRing extends STCMCurioItem {

    public static final int FIRE_RESISTANCE_EFFECT_DURATION = 600;

    public FireproofRing(Properties properties) {
		super(properties);
	}

    public static void grantFireResistance(ItemStack fireproofRingStack, LivingEntity livingEntity) {
        if (!livingEntity.hasEffect(MobEffects.FIRE_RESISTANCE)) {
            livingEntity.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, FIRE_RESISTANCE_EFFECT_DURATION, 0, true, false));
        }
	}

	@Override
	public void curioTick(SlotContext slotContext, ItemStack stack) {
		LivingEntity entity = slotContext.entity();
        if (entity.isOnFire()) {
            entity.setRemainingFireTicks(0);
        }
	}

    @Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag type) {
		tooltip.add(Component.literal(""));
        tooltip.add(Component.translatable("tooltip.stcm.fireproof_ring.line1"));
        tooltip.add(Component.translatable("tooltip.stcm.fireproof_ring.line2"));
		super.appendHoverText(stack, context, tooltip, type);
	}
}
