package net.pedroksl.ae2addonlib.registry;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue;
import net.minecraftforge.common.ForgeConfigSpec.DoubleValue;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.common.ForgeConfigSpec.IntValue;

/**
 * Helper class with useful config builder methods.
 */
public class ConfigRegistry {

    /**
     * Helper method to create a boolean config with a default value and a comment line.
     * @param builder The config builder.
     * @param name The name of the config.
     * @param defaultValue A default value to initialize the config.
     * @param comment A comment string that should be created for this config.
     * @return A {@link BooleanValue} config.
     */
    protected static BooleanValue define(
            ForgeConfigSpec.Builder builder, String name, boolean defaultValue, String comment) {
        builder.comment(comment);
        return define(builder, name, defaultValue);
    }

    /**
     * Helper method to create a boolean config with a default value.
     * @param builder The config builder.
     * @param name The name of the config.
     * @param defaultValue A default value to initialize the config.
     * @return A {@link BooleanValue} config.
     */
    protected static BooleanValue define(ForgeConfigSpec.Builder builder, String name, boolean defaultValue) {
        return builder.define(name, defaultValue);
    }

    /**
     * Helper method to create an int config with a default value and a comment line.
     * @param builder The config builder.
     * @param name The name of the config.
     * @param defaultValue A default value to initialize the config.
     * @param comment A comment string that should be created for this config.
     * @return A {@link IntValue} config.
     */
    protected static IntValue define(ForgeConfigSpec.Builder builder, String name, int defaultValue, String comment) {
        builder.comment(comment);
        return define(builder, name, defaultValue);
    }

    /**
     * Helper method to create an int config with a default value, a range and a comment line.
     * @param builder The config builder.
     * @param name The name of the config.
     * @param defaultValue A default value to initialize the config.
     * @param min The minimum allowed value.
     * @param max The maximum allowed value.
     * @param comment A comment string that should be created for this config.
     * @return A {@link IntValue} config.
     */
    protected static IntValue define(
            ForgeConfigSpec.Builder builder, String name, int defaultValue, int min, int max, String comment) {
        builder.comment(comment);
        return define(builder, name, defaultValue, min, max);
    }

    /**
     * Helper method to create an int config with a default value and a range.
     * @param builder The config builder.
     * @param name The name of the config.
     * @param defaultValue A default value to initialize the config.
     * @param min The minimum allowed value.
     * @param max The maximum allowed value.
     * @return A {@link IntValue} config.
     */
    protected static IntValue define(ForgeConfigSpec.Builder builder, String name, int defaultValue, int min, int max) {
        return builder.defineInRange(name, defaultValue, min, max);
    }

    /**
     * Helper method to create an int config with a default value.
     * @param builder The config builder.
     * @param name The name of the config.
     * @param defaultValue A default value to initialize the config.
     * @return A {@link IntValue} config.
     */
    protected static IntValue define(ForgeConfigSpec.Builder builder, String name, int defaultValue) {
        return define(builder, name, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Helper method to create a double config with a default value and a comment line.
     * @param builder The config builder.
     * @param name The name of the config.
     * @param defaultValue A default value to initialize the config.
     * @param comment A comment string that should be created for this config.
     * @return A {@link BooleanValue} config.
     */
    protected static ForgeConfigSpec.DoubleValue define(
            ForgeConfigSpec.Builder builder, String name, double defaultValue, String comment) {
        builder.comment(comment);
        return define(builder, name, defaultValue);
    }

    /**
     * Helper method to create an double config with a default value.
     * @param builder The config builder.
     * @param name The name of the config.
     * @param defaultValue A default value to initialize the config.
     * @return A {@link DoubleValue} config.
     */
    protected static ForgeConfigSpec.DoubleValue define(
            ForgeConfigSpec.Builder builder, String name, double defaultValue) {
        return define(builder, name, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    /**
     * Helper method to create a double config with a default value, a range and a comment line.
     * @param builder The config builder.
     * @param name The name of the config.
     * @param defaultValue A default value to initialize the config.
     * @param min The minimum allowed value.
     * @param max The maximum allowed value.
     * @param comment A comment string that should be created for this config.
     * @return A {@link BooleanValue} config.
     */
    protected static ForgeConfigSpec.DoubleValue define(
            ForgeConfigSpec.Builder builder, String name, double defaultValue, double min, double max, String comment) {
        builder.comment(comment);
        return define(builder, name, defaultValue, min, max);
    }

    /**
     * Helper method to create a double config with a default value and a range.
     * @param builder The config builder.
     * @param name The name of the config.
     * @param defaultValue A default value to initialize the config.
     * @param min The minimum allowed value.
     * @param max The maximum allowed value.
     * @return A {@link BooleanValue} config.
     */
    protected static ForgeConfigSpec.DoubleValue define(
            ForgeConfigSpec.Builder builder, String name, double defaultValue, double min, double max) {
        return builder.defineInRange(name, defaultValue, min, max);
    }

    /**
     * Helper method to create an enum config with a default value.
     * @param builder The config builder.
     * @param name The name of the config.
     * @param defaultValue A default value to initialize the config.
     * @param <T> The enum class
     * @return A {@link EnumValue} config.
     */
    protected static <T extends Enum<T>> ForgeConfigSpec.EnumValue<T> defineEnum(
            ForgeConfigSpec.Builder builder, String name, T defaultValue) {
        return builder.defineEnum(name, defaultValue);
    }

    /**
     * Helper method to create an enum config with a default value and a comment line.
     * @param builder The config builder.
     * @param name The name of the config.
     * @param defaultValue A default value to initialize the config.
     * @param comment A comment string that should be created for this config.
     * @param <T> The enum class
     * @return A {@link EnumValue} config.
     */
    protected static <T extends Enum<T>> ForgeConfigSpec.EnumValue<T> defineEnum(
            ForgeConfigSpec.Builder builder, String name, T defaultValue, String comment) {
        builder.comment(comment);
        return defineEnum(builder, name, defaultValue);
    }
}
