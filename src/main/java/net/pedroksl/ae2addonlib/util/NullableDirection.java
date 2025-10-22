package net.pedroksl.ae2addonlib.util;

import java.util.List;
import java.util.function.IntFunction;

import com.mojang.serialization.Codec;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;

import io.netty.buffer.ByteBuf;

/**
 * A version of {@link Direction} that also contains the null direction.
 * Used in the {@link NullableDirectionListCodec}.
 */
public enum NullableDirection implements StringRepresentable {
    /**
     * The down direction.
     */
    DOWN(0, Direction.DOWN),
    /**
     * The up direction.
     */
    UP(1, Direction.UP),
    /**
     * The north direction.
     */
    NORTH(2, Direction.NORTH),
    /**
     * The south direction.
     */
    SOUTH(3, Direction.SOUTH),
    /**
     * The west direction.
     */
    WEST(4, Direction.WEST),
    /**
     * The east direction.
     */
    EAST(5, Direction.EAST),
    /**
     * The null direction.
     */
    NULLDIR(6, null);

    /**
     * A function to get the correct direction from the ordinal value.
     */
    public static final IntFunction<NullableDirection> BY_ID =
            ByIdMap.continuous(NullableDirection::getIndex, values(), ByIdMap.OutOfBoundsStrategy.WRAP);

    private final int index;
    private final Direction dir;

    NullableDirection(int index, @Nullable Direction dir) {
        this.index = index;
        this.dir = dir;
    }

    /**
     * Getter function.
     * @return The index member.
     */
    public int getIndex() {
        return this.index;
    }

    /**
     * Getter function.
     * @return The {@link Direction}.
     */
    public Direction getDirection() {
        return this.dir;
    }

    /**
     * Factory method to construct this enum from a {@link Direction}.
     * @param dir The desired direction.
     * @return The {@link NullableDirection} instance.
     */
    public static NullableDirection fromDirection(Direction dir) {
        if (dir == null) return NULLDIR;

        return switch (dir) {
            case Direction.DOWN -> DOWN;
            case Direction.UP -> UP;
            case Direction.NORTH -> NORTH;
            case Direction.SOUTH -> SOUTH;
            case Direction.WEST -> WEST;
            case Direction.EAST -> EAST;
        };
    }

    /**
     * The coded for this enumeration.
     */
    @SuppressWarnings("deprecation")
    public static final EnumCodec<NullableDirection> CODEC = StringRepresentable.fromEnum(NullableDirection::values);

    /**
     * The stream codec for this enumeration.
     */
    public static final StreamCodec<ByteBuf, NullableDirection> STREAM_CODEC =
            ByteBufCodecs.idMapper(BY_ID, NullableDirection::getIndex);

    /**
     * A list codec that accepts missing items.
     */
    public static final Codec<List<@Nullable NullableDirection>> FAULT_TOLERANT_NULLABLE_LIST_CODEC =
            new NullableDirectionListCodec(CODEC);

    @Override
    public @NotNull String getSerializedName() {
        if (this.dir == null) {
            return "null";
        }

        return this.dir.name();
    }
}
