package net.pedroksl.ae2addonlib.client;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.annotation.Nullable;

import net.minecraft.client.KeyMapping;
import net.pedroksl.ae2addonlib.network.AddonPacket;
import net.pedroksl.ae2addonlib.network.LibNetworkHandler;
import net.pedroksl.ae2addonlib.network.serverPacket.AddonHotkeyPacket;
import net.pedroksl.ae2addonlib.registry.HotkeyRegistry;

import appeng.api.features.HotkeyAction;

/**
 * <p>Client registry and holder class.</p>
 * Interacts with {@link HotkeyRegistry} to finalize registration on the client instance. Responsible for creating the mappings,
 * initializing them with the default hotkey and linking the key presses to an {@link AddonHotkeyPacket} that notifies the server.
 */
public class Hotkeys {

    private static final Map<String, AddonHotkey> HOTKEYS = new HashMap<>();
    private final String modId;
    private boolean finalized;

    /**
     * Constructor for this class.
     * @param modId The MOD_ID of the inheritor's mod.
     */
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

    /**
     * <p>Finalizes the hotkey registration on the client instance.</p>
     * This method adds all pre-registered hotkeys to the actual hotkey pool.
     * @param register The consumer of key mapping present in {@link net.minecraftforge.client.event.RegisterKeyMappingsEvent#register(KeyMapping)}.
     */
    public void finalizeRegistration(Consumer<KeyMapping> register) {
        for (var value : HOTKEYS.values()) {
            register.accept(value.mapping());
        }
        finalized = true;
    }

    /**
     * Registers a hotkey. Should be called by the static inheritor instance during the registration
     * process in {@link HotkeyRegistry#register(HotkeyAction, String)}.
     * @param id The hotkey id.
     */
    public void registerHotkey(String id) {
        registerHotkey(createHotkey(id));
    }

    /**
     * Checks all registered hotkeys to see if they should be activated. This method should be called in the
     * {@link net.minecraftforge.event.TickEvent.ClientTickEvent} event.
     */
    public void checkHotkeys() {
        HOTKEYS.forEach((name, hotkey) -> hotkey.check());
    }

    /**
     * Gets the mapping for a string id.
     * @param id The id of the mapping.
     * @return The hotkey, if found.
     */
    @Nullable
    public AddonHotkey getHotkeyMapping(@Nullable String id) {
        return HOTKEYS.get(id);
    }

    /**
     * Record to define a hotkey. Contains the necessary information to check for presses and notify the server if they happened.
     * @param modId The MOD_ID of the owner's mod.
     * @param name The string id of the hotkey mapping.
     * @param mapping The {@link KeyMapping}.
     */
    public record AddonHotkey(String modId, String name, KeyMapping mapping) {
        /**
         * Method to check if the hotkey has been pressed and should be consumed.
         */
        public void check() {
            while (this.mapping().consumeClick()) {
                AddonPacket message = new AddonHotkeyPacket(this);
                LibNetworkHandler.INSTANCE.sendToServer(message);
            }
        }
    }

    private int getDefaultHotkey(String id) {
        return HotkeyRegistry.getDefaultHotkey(modId, id);
    }
}
