package net.pedroksl.ae2addonlib.api;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Preconditions;

import org.jetbrains.annotations.NotNull;

import appeng.api.config.Setting;

/**
 * <p>Helper class responsible for the registering of {@link Setting}s.</p>
 * The recommended way to use this class is to create helper methods that remove the need to send the MOD_ID to
 * all static methods.
 */
public class SettingsRegistry {

    private static final Map<String, Map<String, Setting<?>>> SETTINGS_MAP = new HashMap<>();

    /**
     * {@link Setting} initialization for when all options of an enum are required.
     * @param modId The MOD_ID of the requesting mod.
     * @param name The name of the setting.
     * @param enumClass The class of the enum property.
     * @param <T> Class of the enum property.
     * @return The registered setting.
     */
    protected static synchronized <T extends Enum<T>> Setting<T> register(
            String modId, String name, Class<T> enumClass) {
        Preconditions.checkState(!getSettings(modId).containsKey(name));
        Setting<T> setting = new Setting<>(name, enumClass);
        SETTINGS_MAP.get(modId).put(name, setting);
        return setting;
    }

    /**
     * {@link Setting} initialization for when a sub set of options of an enum are required.
     * @param modId The MOD_ID of the requesting mod.
     * @param name The name of the setting.
     * @param firstOption The first option.
     * @param moreOptions The list of other options.
     * @param <T> Class of the enum property.
     * @return The registered setting.
     */
    @SafeVarargs
    protected static synchronized <T extends Enum<T>> Setting<T> register(
            String modId, String name, T firstOption, T... moreOptions) {
        Preconditions.checkState(!getSettings(modId).containsKey(name));
        Setting<T> setting = new Setting<>(name, firstOption.getDeclaringClass(), EnumSet.of(firstOption, moreOptions));
        SETTINGS_MAP.get(modId).put(name, setting);
        return setting;
    }

    /**
     * Get a {@link Setting} by name.
     * @param modId The MOD_ID of the requesting mod.
     * @param name The name of the setting.
     * @return If successful, the requested setting,
     */
    public static Setting<?> getOrThrow(String modId, @NotNull String name) {
        var setting = getSettings(modId).get(name);
        if (setting == null) {
            throw new IllegalArgumentException("Unknown setting '" + name + "'");
        }
        return setting;
    }

    private static Map<String, Setting<?>> getSettings(String modId) {
        if (!SETTINGS_MAP.containsKey(modId)) {
            SETTINGS_MAP.put(modId, new HashMap<>());
        }
        return SETTINGS_MAP.get(modId);
    }
}
