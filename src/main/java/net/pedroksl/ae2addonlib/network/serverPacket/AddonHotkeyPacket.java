package net.pedroksl.ae2addonlib.network.serverPacket;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.pedroksl.ae2addonlib.client.Hotkeys;
import net.pedroksl.ae2addonlib.registry.HotkeyRegistry;

import appeng.core.AELog;
import appeng.core.localization.PlayerMessages;
import appeng.core.network.CustomAppEngPayload;
import appeng.core.network.ServerboundPacket;

/**
 * Record used to define the packet used to handle hotkeys pressed on the server. It is sent automatically whenever
 * a registered key is pressed.
 * @param modId The MOD_ID of the mod related to the pressed hotkey.
 * @param hotkey The registered id of the hotkey.
 */
public record AddonHotkeyPacket(String modId, String hotkey) implements ServerboundPacket {
    public static final StreamCodec<RegistryFriendlyByteBuf, AddonHotkeyPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            AddonHotkeyPacket::modId,
            ByteBufCodecs.STRING_UTF8,
            AddonHotkeyPacket::hotkey,
            AddonHotkeyPacket::new);

    public static final Type<AddonHotkeyPacket> TYPE = CustomAppEngPayload.createType("lib_hotkey");

    @Override
    public Type<AddonHotkeyPacket> type() {
        return TYPE;
    }

    /**
     * Convenience constructor that extracts the needed information from an {@link net.pedroksl.ae2addonlib.client.Hotkeys.AddonHotkey}.
     * @param hotkey The hotkey that was pressed and need to be handled by the server.
     */
    public AddonHotkeyPacket(Hotkeys.AddonHotkey hotkey) {
        this(hotkey.modId(), hotkey.name());
    }

    public void handleOnServer(ServerPlayer player) {
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
