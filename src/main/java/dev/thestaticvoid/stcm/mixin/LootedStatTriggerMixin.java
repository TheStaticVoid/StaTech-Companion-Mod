package dev.thestaticvoid.stcm.mixin;

import dev.thestaticvoid.stcm.STCMConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import noobanidus.mods.lootr.common.advancement.LootedStatTrigger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LootedStatTrigger.class)
public class LootedStatTriggerMixin {
  @Inject(method = "trigger", at = @At(value = "HEAD"))
  private static void grantExperience(ServerPlayer player, CallbackInfo ci) {
    final RandomSource random = RandomSource.createThreadSafe();
    Integer min = STCMConfig.CONFIG.lootrMinXp.get();
    Integer max = STCMConfig.CONFIG.lootrMaxXp.get();
    player.giveExperiencePoints(random.nextInt(min, max));
    player.level()
        .playSound(
            null,
            player.getX(),
            player.getY(),
            player.getZ(),
            SoundEvents.EXPERIENCE_ORB_PICKUP,
            SoundSource.PLAYERS,
            0.2f,
            (random.nextFloat() - random.nextFloat()) * 0.35f + 0.9f);
  }
}
