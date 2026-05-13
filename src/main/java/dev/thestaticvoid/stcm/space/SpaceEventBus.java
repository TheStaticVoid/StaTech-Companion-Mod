package dev.thestaticvoid.stcm.space;

import dev.thestaticvoid.stcm.StaTechCompanionMod;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = StaTechCompanionMod.MODID)
public class SpaceEventBus {
    private static final double GRAVITY_MULTIPLIER = 0.67; // 6 7 !!!!!
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (isPlayerInSpace(player) && !player.isCreative()) {
            // Check for space shield item in inventory
            if (tickCounter % 100 == 0) {
                tickCounter = 0;
                if (!player.getInventory().contains(SpaceItems.SPACE_SHIELD.toStack())) {
                   player.sendSystemMessage(Component.translatable("event.player.stcm.space_suit_warning").withColor(ChatFormatting.YELLOW.getColor()));
                   player.hurt(player.damageSources().inWall(), 6.0F);
                }
            }
            tickCounter++;


            // Apply slow fall effect
            Vec3 vel = player.getDeltaMovement();
            if (!player.onGround()) {
                player.setDeltaMovement(vel.x, vel.y * GRAVITY_MULTIPLIER, vel.z);
            }
        }
    }

    private static boolean isPlayerInSpace(Player player) {
        return player.level().dimension().equals(ResourceKey.create(Registries.DIMENSION,
                ResourceLocation.fromNamespaceAndPath(StaTechCompanionMod.MODID, "space")));
    }
}
