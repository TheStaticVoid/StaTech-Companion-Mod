package dev.thestaticvoid.stcm.network;


import aztech.modern_industrialization.network.BasePacket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.thestaticvoid.stcm.STCM;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class STCMPacket {
    static final Map<Class<? extends BasePacket>, CustomPacketPayload.Type<?>> packetTypes = new HashMap<>();
    private static final List<Registration<?>> registrations = new ArrayList<>();

    private record Registration<P extends BasePacket>(CustomPacketPayload.Type<P> packetType, Class<P> clazz,
            StreamCodec<? super RegistryFriendlyByteBuf, P> packetCodec) {}

    private static <P extends BasePacket> void register(String path, Class<P> clazz,
            StreamCodec<? super RegistryFriendlyByteBuf, P> packetConstructor) {
        var type = new CustomPacketPayload.Type<P>(STCM.id(path));
        packetTypes.put(clazz, type);
        registrations.add(new Registration<>(type, clazz, packetConstructor));
    }

    static {
        register("neoforge_hammer_move_recipe", NeoForgeHammerMoveRecipePacket.class, NeoForgeHammerMoveRecipePacket.STREAM_CODEC);
    }

    public static void init(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar("1");

        for (var reg : registrations) {
            register(registrar, reg);
        }
    }

    private static <P extends BasePacket> void register(PayloadRegistrar registrar, Registration<P> reg) {
        registrar.playBidirectional(reg.packetType, reg.packetCodec, (packet, context) -> {
            packet.handle(new BasePacket.Context(reg.clazz, context));
        });
    }
}
