package net.wiselywisp.thebeneath.enchantment;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;


public final class EmptyAttunementNetwork {
    public record Toggle(boolean active) implements CustomPacketPayload {
        public static final Type<Toggle> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath("thebeneath", "empty_attunement_toggle"));
        public static final StreamCodec<ByteBuf, Toggle> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.BOOL, Toggle::active, Toggle::new);

        @Override
        public Type<? extends CustomPacketPayload> type() {
            return TYPE;
        }
    }

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(Toggle.TYPE, Toggle.STREAM_CODEC,
                (payload, context) -> {
                    if (context.player() instanceof ServerPlayer player) {
                        EmptyAttunementServer.setActive(player, payload.active());
                    }
                });
    }

    private EmptyAttunementNetwork() {}
}
