package org.eneryleen.damage_indicator.networking.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.eneryleen.damage_indicator.Damage_indicator;

public record SpawnIndicatorPayload(double x, double y, double z, float damage, boolean isCritical) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<SpawnIndicatorPayload> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(Damage_indicator.MOD_ID, "spawn_indicator"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SpawnIndicatorPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, SpawnIndicatorPayload::x,
            ByteBufCodecs.DOUBLE, SpawnIndicatorPayload::y,
            ByteBufCodecs.DOUBLE, SpawnIndicatorPayload::z,
            ByteBufCodecs.FLOAT, SpawnIndicatorPayload::damage,
            ByteBufCodecs.BOOL, SpawnIndicatorPayload::isCritical,
            SpawnIndicatorPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
