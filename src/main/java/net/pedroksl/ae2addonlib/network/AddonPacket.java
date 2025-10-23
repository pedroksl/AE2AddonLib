package net.pedroksl.ae2addonlib.network;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;

import io.netty.buffer.Unpooled;

public abstract class AddonPacket {

    private FriendlyByteBuf p;

    public void serverPacketData(ServerPlayer player) {
        throw new UnsupportedOperationException("This packet does not implement a server side handler.");
    }

    public void clientPacketData(Player player) {
        throw new UnsupportedOperationException("This packet does not implement a client side handler.");
    }

    protected abstract void write(FriendlyByteBuf stream);

    protected final void configureWrite(FriendlyByteBuf data) {
        data.capacity(data.readableBytes());
        this.p = data;
    }

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
