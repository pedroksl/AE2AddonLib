package net.pedroksl.ae2addonlib.network;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;

import io.netty.buffer.Unpooled;

/**
 * Base packet class used by {@link NetworkHandler}. Addon packets should extend this and implement either {@link #serverPacketData},
 * {@link #clientPacketData} for server packets or client packets respectively, or both for bidirectional packets.
 */
public abstract class AddonPacket {

    /**
     * Holds the packet's data.
     */
    private FriendlyByteBuf p;

    /**
     * Handler for server packets. Should be overridden by server packets or bidirectional packets.
     * @param player The server player.
     */
    public void serverPacketData(ServerPlayer player) {
        throw new UnsupportedOperationException("This packet does not implement a server side handler.");
    }

    /**
     * Handler for client packets. Should be overridden by client packets or bidirectional packets.
     * @param player The local player.
     */
    public void clientPacketData(Player player) {
        throw new UnsupportedOperationException("This packet does not implement a client side handler.");
    }

    /**
     * Method called when serializing the packet into the stream.
     * @param stream The stream to write into.
     */
    protected abstract void write(FriendlyByteBuf stream);

    private void configureWrite(FriendlyByteBuf data) {
        data.capacity(data.readableBytes());
        this.p = data;
    }

    /**
     * Method called by {@link NetworkHandler} when serializing packets.
     * @param packetId The packet id, known by the network handler.
     * @param direction The direction of the connection.
     * @param channel The channel used by this connection.
     * @return A constructed packet.
     */
    public Packet<?> toPacket(int packetId, NetworkDirection direction, ResourceLocation channel) {
        var data = new FriendlyByteBuf(Unpooled.buffer());
        data.writeInt(packetId);
        write(data);
        configureWrite(data);

        if (this.p.array().length > 2 * 1024 * 1024) // 2k walking room :)
        {
            throw new IllegalArgumentException(
                    "Sorry AE2AddonLib made a " + this.p.array().length + " byte packet by accident!");
        }

        return direction.buildPacket(Pair.of(p, 0), channel).getThis();
    }
}
