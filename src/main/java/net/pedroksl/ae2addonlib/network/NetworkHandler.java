package net.pedroksl.ae2addonlib.network;

import java.util.function.Consumer;
import java.util.function.Function;

import com.mojang.logging.LogUtils;

import org.slf4j.Logger;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.event.EventNetworkChannel;

import appeng.core.AppEng;
import appeng.core.sync.network.TargetPoint;

/**
 * Handles the registration of network packets. Provides helper functions to make the registration process easier.
 * The recommended way to use this class is to extend it with a static class and implement {@link #init()}
 * to add calls to register the mod's packets.
 */
public abstract class NetworkHandler {
    private static final Logger LOG = LogUtils.getLogger();

    private final ResourceLocation channel;
    private final Consumer<AddonPacket> clientHandler;

    private int id = 0;
    private final Object2IntMap<Class<? extends AddonPacket>> idMap = new Object2IntOpenHashMap<>();
    private final Int2ObjectMap<Function<FriendlyByteBuf, AddonPacket>> factoryMap = new Int2ObjectOpenHashMap<>();

    /**
     * Constructs the handler saving the modId for future use.
     * @param modId The MOD_ID of the extender mod.
     */
    public NetworkHandler(String modId) {
        this.channel = new ResourceLocation(modId, "channel");
        EventNetworkChannel ec = NetworkRegistry.ChannelBuilder.named(channel)
                .networkProtocolVersion(() -> "1")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .eventNetworkChannel();
        ec.registerObject(this);

        this.clientHandler =
                DistExecutor.unsafeRunForDist(() -> () -> NetworkHandler::onPacketData, () -> () -> pkt -> {});
    }

    private static void onPacketData(AddonPacket packet) {
        try {
            packet.clientPacketData(Minecraft.getInstance().player);
        } catch (final IllegalArgumentException e) {
            LOG.error("Failed handling packet", e);
        }
    }

    /**
     * Entry point for addon packet registration. Override this and call the register methods inside.
     * Usage example: {@link LibNetworkHandler#init()}.
     * This method should be called inside the common setup listener for your mod, as seen in: {@link net.pedroksl.ae2addonlib.AE2AddonLib#commonSetup(FMLCommonSetupEvent)}.
     */
    public abstract void init();

    /**
     * Method to register a packet.
     * @param clazz The class of the packet.
     * @param factory The constructor of the packet.
     */
    protected void registerPacket(Class<? extends AddonPacket> clazz, Function<FriendlyByteBuf, AddonPacket> factory) {
        factoryMap.put(id, factory);
        idMap.put(clazz, id);
        id++;
    }

    /**
     * Event handler for server packets.
     * @param ev The server packet event.
     */
    @SubscribeEvent
    public void serverPacket(final NetworkEvent.ClientCustomPayloadEvent ev) {
        try {
            NetworkEvent.Context ctx = ev.getSource().get();
            ctx.setPacketHandled(true);
            var packet = deserializePacket(ev.getPayload());
            var player = ctx.getSender();
            ctx.enqueueWork(() -> {
                try {
                    packet.serverPacketData(player);
                } catch (final IllegalArgumentException e) {
                    LOG.warn(String.valueOf(e));
                }
            });
        } catch (final RunningOnDifferentThreadException ignored) {

        }
    }

    /**
     * Event handler for client packets.
     * @param ev The client packet event.
     */
    @SubscribeEvent
    public void clientPacket(NetworkEvent.ServerCustomPayloadEvent ev) {
        if (ev instanceof NetworkEvent.ServerCustomPayloadLoginEvent) {
            return;
        }
        if (this.clientHandler != null) {
            try {
                NetworkEvent.Context ctx = ev.getSource().get();
                ctx.setPacketHandled(true);

                var packet = deserializePacket(ev.getPayload());

                ctx.enqueueWork(() -> this.clientHandler.accept(packet));
            } catch (RunningOnDifferentThreadException ignored) {

            }
        }
    }

    private AddonPacket deserializePacket(FriendlyByteBuf payload) {
        var packetId = payload.readInt();
        return factoryMap.get(packetId).apply(payload);
    }

    /**
     * Sends a client packet to all players.
     * @param message The packet to send.
     */
    public void sendToAll(AddonPacket message) {
        var server = AppEng.instance().getCurrentServer();
        if (server != null) {
            try {
                var id = getPacketId(message.getClass());
                server.getPlayerList()
                        .broadcastAll(message.toPacket(id, NetworkDirection.PLAY_TO_CLIENT, this.channel));
            } catch (Exception e) {
                // Already handled
            }
        }
    }

    /**
     * Sends a client packet to a players.
     * @param message The packet to send.
     * @param player The player to send the packet.
     */
    public void sendTo(AddonPacket message, ServerPlayer player) {
        try {
            var id = getPacketId(message.getClass());
            player.connection.send(message.toPacket(id, NetworkDirection.PLAY_TO_CLIENT, this.channel));
        } catch (Exception e) {
            // Already handled
        }
    }

    /**
     * Sends a client packet to all players in an area.
     * @param message The packet to send.
     * @param point The taget point of the packet.
     */
    public void sendToAllAround(AddonPacket message, TargetPoint point) {
        var server = AppEng.instance().getCurrentServer();
        if (server != null) {
            try {
                var id = getPacketId(message.getClass());
                Packet<?> pkt = message.toPacket(id, NetworkDirection.PLAY_TO_CLIENT, this.channel);
                server.getPlayerList()
                        .broadcast(point.excluded, point.x, point.y, point.z, point.r2, point.level.dimension(), pkt);
            } catch (Exception e) {
                // Already handled
            }
        }
    }

    /**
     * Sends a packet to the server.
     * @param message The packet to send.
     */
    public void sendToServer(AddonPacket message) {
        assert Minecraft.getInstance().getConnection() != null;
        try {
            var id = getPacketId(message.getClass());
            Minecraft.getInstance()
                    .getConnection()
                    .send(message.toPacket(id, NetworkDirection.PLAY_TO_SERVER, this.channel));
        } catch (Exception e) {
            // Already handled
        }
    }

    private int getPacketId(Class<? extends AddonPacket> c) {
        var id = idMap.getOrDefault(c, -1);
        if (id == -1) {
            LOG.error("Unregistered packet with of class {}", c.getName());
        }
        return id;
    }
}
