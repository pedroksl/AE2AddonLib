package net.pedroksl.ae2addonlib.registry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import com.mojang.logging.LogUtils;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import net.minecraft.world.level.ItemLike;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.pedroksl.ae2addonlib.util.ArmorHotkeyAction;

import appeng.api.features.HotkeyAction;
import appeng.hotkeys.InventoryHotkeyAction;

/**
 * <p>Class responsible for the registering of hotkeys.</p>
 * Statically holds all instantiating mod's deferred registers and provides simple to use methods for interacting with them.
 * The recommended way to use this class is to extend it with a static registry class. Additionally, you can create
 * helper methods that remove the need to send the MOD_ID to all static methods.
 */
public class HotkeyRegistry {
    private static final Logger LOG = LogUtils.getLogger();

    /**
     * A Map of all the registry maps, of all handled addons. Each entry in this map contains the addon's hotkeys in
     * a separate map, with their id's as the map key.
     */
    public static final Map<String, Map<String, List<HotkeyAction>>> REGISTRY = new HashMap<>();

    private static final Map<String, Function<String, Integer>> HOTKEY_GETTER = new HashMap<>();
    private final String modId;
    private final Consumer<String> clientRegister;

    /**
     * Registry constructor. Takes in the constructing mod's id to use as a key for its maps as well as initializes
     * the Deferred register and BLOCKS map.
     * @param modId The MOD_ID of the mod creating this instance.
     * @param defaultHotkeyGetter A function that return the default hotkey for a given hotkey id.
     * @param clientRegister Client function responsible for finishing registration. Most likely will be a reference to
     * {@link net.pedroksl.ae2addonlib.client.Hotkeys#createHotkey(String)} from the inheritor's instance.
     */
    public HotkeyRegistry(
            String modId, Function<String, Integer> defaultHotkeyGetter, Consumer<String> clientRegister) {
        if (REGISTRY.containsKey(modId) && FMLEnvironment.dist.isClient()) {
            LOG.error("Tried to initialize HotkeyRegistry on Client Dist with mod id {}", modId);
            throw new IllegalStateException();
        }

        this.modId = modId;
        this.clientRegister = clientRegister;
        REGISTRY.put(modId, new HashMap<>());
        HOTKEY_GETTER.put(modId, defaultHotkeyGetter);
    }

    /**
     * Registers a hotkey that interacts with an inventory item. This method will add an action that will look for an
     * item entity in the player's inventory slots. It will also look for the item in curio slots, and the provided api
     * is installed.
     * @param item The item to look for when triggering the hotkey.
     * @param opener What should run when the hotkey is triggered.
     * @param id An identifier string for the registered action.
     */
    protected void register(ItemLike item, InventoryHotkeyAction.Opener opener, String id) {
        register(new InventoryHotkeyAction(stack -> stack.is(item.asItem()), opener), id);
        // register(new CuriosHotkeyAction(item, opener), id);
    }

    /**
     * Registers a hotkey that relates to an equipped item. This method will add an action that will look for an item
     * entity in the player's equipped armor slots.
     * @param item The item to look for when triggering the hotkey.
     * @param opener What should run when the hotkey is triggered.
     * @param id An identifier string for the registered action.
     */
    protected void registerArmorAction(ItemLike item, ArmorHotkeyAction.Opener opener, String id) {
        register(new ArmorHotkeyAction(item, opener), id);
    }

    /**
     * Registration method for hotkeys. Takes in an instance of {@link HotkeyAction} and the hotkey id.
     * It also keeps a copy of the action on the server for future reference.
     * @param hotkeyAction The hotkey action to be registered.
     * @param id The id of the hotkey action to be registered.
     */
    protected synchronized void register(HotkeyAction hotkeyAction, String id) {
        if (REGISTRY.get(this.modId).containsKey(id)) {
            REGISTRY.get(this.modId).get(id).add(0, hotkeyAction);
        } else {
            REGISTRY.get(this.modId).put(id, new ArrayList<>(List.of(hotkeyAction)));
            if (FMLEnvironment.dist.isClient()) {
                this.clientRegister.accept(id);
            }
        }
    }

    /**
     * Static method to get the default hotkey of an action during hotkey registration.
     * @param modId The MOD_ID of the requesting mod.
     * @param id The id of the hotkey action to be registered.
     * @return The {@link GLFW} int id of the default hotkey.
     */
    public static int getDefaultHotkey(String modId, String id) {
        try {
            return HOTKEY_GETTER.get(modId).apply(id);
        } catch (IllegalArgumentException ignored) {
            return GLFW.GLFW_KEY_UNKNOWN;
        }
    }
}
