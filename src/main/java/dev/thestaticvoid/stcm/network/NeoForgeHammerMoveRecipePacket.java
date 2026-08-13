package dev.thestaticvoid.stcm.network;

import aztech.modern_industrialization.network.MIStreamCodecs;
import dev.thestaticvoid.stcm.screen.NeoForgeHammerScreenHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;

public record NeoForgeHammerMoveRecipePacket(int containedId, ResourceLocation recipeId, int fillAction, int amount) implements BasePacket {
    public static final StreamCodec<ByteBuf, NeoForgeHammerMoveRecipePacket> STREAM_CODEC = StreamCodec.composite(
            MIStreamCodecs.BYTE,
            NeoForgeHammerMoveRecipePacket::containedId,
            ResourceLocation.STREAM_CODEC,
            NeoForgeHammerMoveRecipePacket::recipeId,
            MIStreamCodecs.BYTE,
            NeoForgeHammerMoveRecipePacket::fillAction,
            ByteBufCodecs.INT,
            NeoForgeHammerMoveRecipePacket::amount,
            NeoForgeHammerMoveRecipePacket::new);

    @Override
    public void handle(Context ctx) {
        ctx.assertOnServer();

        AbstractContainerMenu menu = ctx.getPlayer().containerMenu;
        if (menu.containerId == containedId && menu instanceof NeoForgeHammerScreenHandler fh) {
            fh.moveRecipe(recipeId, fillAction, amount);
        }
    }
}
