package net.pedroksl.ae2addonlib.network.serverPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.ae2addonlib.client.Hotkeys;
import net.pedroksl.ae2addonlib.network.AddonPacket;
import net.pedroksl.ae2addonlib.registry.HotkeyRegistry;

import appeng.core.AELog;
import appeng.core.localization.PlayerMessages;

/**
 * Class used to define the packet used to handle hotkeys pressed on the server. It is sent automatically whenever
 * a registered key is pressed.
 */
public class AddonHotkeyPacket extends AddonPacket {

    private final String modId;
    private final String hotkey;

    /**
     * Constructs the packet from data in the stream.
     * @param stream The data stream.
     */
    public AddonHotkeyPacket(FriendlyByteBuf stream) {
        this.modId = stream.readUtf();
        this.hotkey = stream.readUtf();
    }

    /**
     * Constructs the packet to send to the stream.
     * @param modId The MOD_ID of the mod related to the pressed hotkey.
     * @param hotkey The registered id of the hotkey.
     */
    public AddonHotkeyPacket(String modId, String hotkey) {
        this.modId = modId;
        this.hotkey = hotkey;
    }

    /**
     * Convenience constructor that extracts the needed information from an {@link net.pedroksl.ae2addonlib.client.Hotkeys.AddonHotkey}.
     * @param hotkey The hotkey that was pressed and need to be handled by the server.
     */
    public AddonHotkeyPacket(Hotkeys.AddonHotkey hotkey) {
        this(hotkey.modId(), hotkey.name());
    }

    @Override
    protected void write(FriendlyByteBuf stream) {
        stream.writeUtf(modId);
        stream.writeUtf(hotkey);
    }

    public void serverPacketData(ServerPlayer player) {
        var actions = HotkeyRegistry.REGISTRY.get(modId).get(hotkey);
        if (actions == null) {
            player.sendSystemMessage(PlayerMessages.UnknownHotkey.text()
                    .copy()
                    .append(Component.translatable("key.advanced_ae." + hotkey)));
            AELog.warn("Player %s tried using unknown hotkey \"%s\"", player, hotkey);
            return;
        }

        for (var action : actions) {
            if (action.run(player)) {
                break;
            }
        }
    }
}
