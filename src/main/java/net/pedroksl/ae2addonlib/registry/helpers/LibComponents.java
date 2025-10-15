package net.pedroksl.ae2addonlib.registry.helpers;

import java.util.function.Consumer;

import com.mojang.serialization.Codec;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.pedroksl.ae2addonlib.AE2AddonLib;
import net.pedroksl.ae2addonlib.registry.ComponentRegistry;

public class LibComponents extends ComponentRegistry {

    public static final LibComponents INSTANCE = new LibComponents();

    LibComponents() {
        super(AE2AddonLib.MOD_ID);
    }

    public static final DataComponentType<CompoundTag> NBT_TAG =
            register("generic_nbt", builder -> builder.persistent(CompoundTag.CODEC)
                    .networkSynchronized(ByteBufCodecs.COMPOUND_TAG));
    public static final DataComponentType<Integer> TINT_COLOR_TAG =
            register("tint_color", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    protected static <T> DataComponentType<T> register(String name, Consumer<DataComponentType.Builder<T>> customizer) {
        return register(AE2AddonLib.MOD_ID, name, customizer);
    }
}
