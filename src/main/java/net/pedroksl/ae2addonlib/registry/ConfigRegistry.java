package net.pedroksl.ae2addonlib.registry;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public class ConfigRegistry {
    protected static BooleanValue define(
            ModConfigSpec.Builder builder, String name, boolean defaultValue, String comment) {
        builder.comment(comment);
        return define(builder, name, defaultValue);
    }

    protected static BooleanValue define(ModConfigSpec.Builder builder, String name, boolean defaultValue) {
        return builder.define(name, defaultValue);
    }

    protected static IntValue define(ModConfigSpec.Builder builder, String name, int defaultValue, String comment) {
        builder.comment(comment);
        return define(builder, name, defaultValue);
    }

    protected static ModConfigSpec.DoubleValue define(ModConfigSpec.Builder builder, String name, double defaultValue) {
        return define(builder, name, defaultValue, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    protected static ModConfigSpec.DoubleValue define(
            ModConfigSpec.Builder builder, String name, double defaultValue, String comment) {
        builder.comment(comment);
        return define(builder, name, defaultValue);
    }

    protected static ModConfigSpec.DoubleValue define(
            ModConfigSpec.Builder builder, String name, double defaultValue, double min, double max, String comment) {
        builder.comment(comment);
        return define(builder, name, defaultValue, min, max);
    }

    protected static ModConfigSpec.DoubleValue define(
            ModConfigSpec.Builder builder, String name, double defaultValue, double min, double max) {
        return builder.defineInRange(name, defaultValue, min, max);
    }

    protected static IntValue define(
            ModConfigSpec.Builder builder, String name, int defaultValue, int min, int max, String comment) {
        builder.comment(comment);
        return define(builder, name, defaultValue, min, max);
    }

    protected static IntValue define(ModConfigSpec.Builder builder, String name, int defaultValue, int min, int max) {
        return builder.defineInRange(name, defaultValue, min, max);
    }

    protected static IntValue define(ModConfigSpec.Builder builder, String name, int defaultValue) {
        return define(builder, name, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    protected static <T extends Enum<T>> ModConfigSpec.EnumValue<T> defineEnum(
            ModConfigSpec.Builder builder, String name, T defaultValue) {
        return builder.defineEnum(name, defaultValue);
    }

    protected static <T extends Enum<T>> ModConfigSpec.EnumValue<T> defineEnum(
            ModConfigSpec.Builder builder, String name, T defaultValue, String comment) {
        builder.comment(comment);
        return defineEnum(builder, name, defaultValue);
    }
}
