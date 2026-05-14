package net.pedroksl.ae2addonlib.util;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import org.jspecify.annotations.Nullable;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.pedroksl.ae2addonlib.core.AE2AddonLib;

public record ColoredItemTintSource(Colors color) implements net.minecraft.client.color.item.ItemTintSource {
    public static final Identifier ID = AE2AddonLib.makeId("fluidColor");

    public static final MapCodec<ColoredItemTintSource> MAP_CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(Colors.CODEC.fieldOf("color").forGetter(ColoredItemTintSource::color))
                    .apply(builder, ColoredItemTintSource::new));

    @Override
    public int calculate(ItemStack itemStack, @Nullable ClientLevel clientLevel, @Nullable LivingEntity livingEntity) {
        return this.color.argb(255);
    }

    @Override
    public MapCodec<? extends net.minecraft.client.color.item.ItemTintSource> type() {
        return MAP_CODEC;
    }
}
