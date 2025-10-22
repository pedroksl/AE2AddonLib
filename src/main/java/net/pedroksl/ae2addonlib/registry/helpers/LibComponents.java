package net.pedroksl.ae2addonlib.registry.helpers;

import java.util.function.Consumer;

import com.mojang.serialization.Codec;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.pedroksl.ae2addonlib.AE2AddonLib;
import net.pedroksl.ae2addonlib.registry.ComponentRegistry;

/**
 * <p>Lib provided component set.</p>
 * This class contains commonly-used component types. Additional components will likely be added in the future.
 */
public class LibComponents extends ComponentRegistry {

    /**
     * The class' singleton instance, used to request registration.
     */
    public static final LibComponents INSTANCE = new LibComponents();

    LibComponents() {
        super(AE2AddonLib.MOD_ID);
    }

    /**
     * A Simple NBT tag {@link DataComponentType}.
     * It's capable of holding a {@link CompoundTag}.
     */
    public static final DataComponentType<CompoundTag> NBT_TAG =
            register("generic_nbt", builder -> builder.persistent(CompoundTag.CODEC)
                    .networkSynchronized(ByteBufCodecs.COMPOUND_TAG));

    /**
     * An integer {@link DataComponentType} to save tint color data in an {@link net.minecraft.world.item.ItemStack}.
     */
    public static final DataComponentType<Integer> TINT_COLOR_TAG =
            register("tint_color", builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT));

    private static <T> DataComponentType<T> register(String name, Consumer<DataComponentType.Builder<T>> customizer) {
        return register(AE2AddonLib.MOD_ID, name, customizer);
    }
}
