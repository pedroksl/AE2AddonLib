package net.pedroksl.ae2addonlib.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.network.PacketDistributor;
import net.pedroksl.ae2addonlib.network.serverPacket.AddonHotkeyPacket;
import net.pedroksl.ae2addonlib.registry.HotkeyRegistry;

import appeng.core.network.ServerboundPacket;

public class Hotkeys {

    private static final Map<String, AddonHotkey> HOTKEYS = new HashMap<>();
    private final String modId;
    private boolean finalized;

    public Hotkeys(String modId) {
        this.modId = modId;
    }

    private AddonHotkey createHotkey(String id) {
        var defaultHotkey = getDefaultHotkey(id);

        if (finalized) {
            throw new IllegalStateException("Hotkey registration already finalized!");
        }
        return new AddonHotkey(
                modId, id, new KeyMapping("key." + modId + "." + id, defaultHotkey, "key." + modId + ".category"));
    }

    private void registerHotkey(AddonHotkey hotkey) {
        HOTKEYS.put(hotkey.name(), hotkey);
    }

    public void finalizeRegistration(Consumer<KeyMapping> register) {
        for (var value : HOTKEYS.values()) {
            register.accept(value.mapping());
        }
        finalized = true;
    }

    public void registerHotkey(String id) {
        registerHotkey(createHotkey(id));
    }

    public void checkHotkeys() {
        HOTKEYS.forEach((name, hotkey) -> hotkey.check());
    }

    @Nullable
    public AddonHotkey getHotkeyMapping(@Nullable String id) {
        return HOTKEYS.get(id);
    }

    public record AddonHotkey(String modId, String name, KeyMapping mapping) {
        public void check() {
            while (this.mapping().consumeClick()) {
                ServerboundPacket message = new AddonHotkeyPacket(this);
                PacketDistributor.sendToServer(message);
            }
        }
    }

    private int getDefaultHotkey(String id) {
        return HotkeyRegistry.getDefaultHotkey(modId, id);
    }
}
