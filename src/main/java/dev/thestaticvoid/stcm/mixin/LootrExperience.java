package dev.thestaticvoid.stcm.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import noobanidus.mods.lootr.common.api.MenuBuilder;
import noobanidus.mods.lootr.common.api.data.ILootrInfoProvider;
import org.spongepowered.asm.mixin.Mixin;
import noobanidus.mods.lootr.common.impl.DefaultLootrAPIImpl;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DefaultLootrAPIImpl.class)
public class LootrExperience {
    @Inject(method = "handleProviderOpen(Lnoobanidus/mods/lootr/common/api/data/ILootrInfoProvider;Lnet/minecraft/server/level/ServerPlayer;Lnoobanidus/mods/lootr/common/api/MenuBuilder;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;awardStat(Lnet/minecraft/stats/Stat;)V"))
    private static void grantExperience(ILootrInfoProvider provider, ServerPlayer player, MenuBuilder menuBuilder, CallbackInfo ci){
        final RandomSource random = RandomSource.createThreadSafe();

        player.giveExperiencePoints(random.nextInt(15, 20));
    provider
        .getInfoLevel()
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
