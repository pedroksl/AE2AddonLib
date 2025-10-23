package net.pedroksl.ae2addonlib.network.clientPacket;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.pedroksl.ae2addonlib.client.screens.OutputDirectionScreen;
import net.pedroksl.ae2addonlib.network.AddonPacket;

import appeng.api.orientation.RelativeSide;

/**
 * Class used to define the packet used to update the client on the block entity's enabled/disabled output directions.
 */
public class OutputDirectionUpdatePacket extends AddonPacket {

    private final Set<RelativeSide> sides;

    /**
     * Constructs the packet from data in the stream.
     * @param stream The data stream.
     */
    public OutputDirectionUpdatePacket(FriendlyByteBuf stream) {
        var size = stream.readInt();
        this.sides = new HashSet<>(size);
        for (var i = 0; i < size; i++) {
            sides.add(stream.readEnum(RelativeSide.class));
        }
    }

    /**
     * Constructs the packet to send to the stream.
     * @param sides A set containing all enabled {@link RelativeSide}s.
     */
    public OutputDirectionUpdatePacket(Set<RelativeSide> sides) {
        this.sides = new HashSet<>(sides.size());
        this.sides.addAll(sides);
    }

    @Override
    protected void write(FriendlyByteBuf stream) {
        stream.writeInt(sides.size());
        for (var side : sides) {
            stream.writeEnum(side);
        }
    }

    @Override
    public void clientPacketData(Player player) {
        if (Minecraft.getInstance().screen instanceof OutputDirectionScreen screen) {
            screen.update(this.sides);
        }
    }
}
