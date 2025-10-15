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
import net.neoforged.fml.loading.FMLEnvironment;
import net.pedroksl.ae2addonlib.util.ArmorHotkeyAction;

import appeng.api.features.HotkeyAction;
import appeng.hotkeys.CuriosHotkeyAction;
import appeng.hotkeys.InventoryHotkeyAction;

public class HotkeyRegistry {
    public static final Logger LOG = LogUtils.getLogger();

    public static final Map<String, Map<String, List<HotkeyAction>>> REGISTRY = new HashMap<>();
    private static final Map<String, Function<String, Integer>> HOTKEY_GETTER = new HashMap<>();
    private final String modId;
    private final Consumer<String> clientRegister;

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

    protected void register(ItemLike item, InventoryHotkeyAction.Opener opener, String id) {
        register(new InventoryHotkeyAction(item, opener), id);
        register(new CuriosHotkeyAction(item, opener), id);
    }

    protected void registerArmorAction(ItemLike item, ArmorHotkeyAction.Opener opener, String id) {
        register(new ArmorHotkeyAction(item, opener), id);
    }

    protected synchronized void register(HotkeyAction hotkeyAction, String id) {
        if (REGISTRY.get(this.modId).containsKey(id)) {
            REGISTRY.get(this.modId).get(id).addFirst(hotkeyAction);
        } else {
            REGISTRY.get(this.modId).put(id, new ArrayList<>(List.of(hotkeyAction)));
            this.clientRegister.accept(id);
        }
    }

    public static int getDefaultHotkey(String modId, String id) {
        try {
            return HOTKEY_GETTER.get(modId).apply(id);
        } catch (IllegalArgumentException ignored) {
            return GLFW.GLFW_KEY_UNKNOWN;
        }
    }
}
