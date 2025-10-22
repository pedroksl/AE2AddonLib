package net.pedroksl.ae2addonlib.network.clientPacket;

import java.util.List;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.pedroksl.ae2addonlib.client.screens.OutputDirectionScreen;

import appeng.api.orientation.RelativeSide;
import appeng.core.network.ClientboundPacket;
import appeng.core.network.CustomAppEngPayload;

/**
 * Record used to define the packet used to update the client on the block entity's enabled/disabled output directions.
 * @param sides A set containing all enabled {@link RelativeSide}.
 */
public record OutputDirectionUpdatePacket(Set<RelativeSide> sides) implements ClientboundPacket {

    public static final StreamCodec<RegistryFriendlyByteBuf, OutputDirectionUpdatePacket> STREAM_CODEC =
            StreamCodec.composite(
                    NeoForgeStreamCodecs.enumCodec(RelativeSide.class)
                            .apply(ByteBufCodecs.list())
                            .map(Set::copyOf, List::copyOf),
                    OutputDirectionUpdatePacket::sides,
                    OutputDirectionUpdatePacket::new);

    public static final Type<OutputDirectionUpdatePacket> TYPE =
            CustomAppEngPayload.createType("output_direction_update_client");

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    @Override
    public void handleOnClient(Player player) {
        if (Minecraft.getInstance().screen instanceof OutputDirectionScreen screen) {
            screen.update(this.sides);
        }
    }
}
