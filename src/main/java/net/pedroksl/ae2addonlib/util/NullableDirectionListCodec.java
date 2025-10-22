package net.pedroksl.ae2addonlib.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;

import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.Nullable;

/**
 * A list codec for {@link NullableDirection}.
 * Provides a fault-tolerant nullable direction codec.
 */
public class NullableDirectionListCodec implements Codec<List<@Nullable NullableDirection>> {
    private final Codec<NullableDirection> innerCodec;

    /**
     * List Codec constructor.
     * @param innerCodec The codec of the inner component of this list.
     */
    public NullableDirectionListCodec(Codec<NullableDirection> innerCodec) {
        this.innerCodec = innerCodec;
    }

    @Override
    public <T> DataResult<T> encode(List<@Nullable NullableDirection> input, DynamicOps<T> ops, T prefix) {
        final ListBuilder<T> builder = ops.listBuilder();

        for (var dir : input) {
            if (dir == null) {
                builder.add(ops.emptyMap());
            } else {
                builder.add(innerCodec.encodeStart(ops, dir));
            }
        }

        return builder.build(prefix);
    }

    @Override
    public <T> DataResult<Pair<List<@Nullable NullableDirection>, T>> decode(DynamicOps<T> ops, T input) {

        return ops.getList(input).setLifecycle(Lifecycle.stable()).flatMap(stream -> {
            var elements = new ArrayList<NullableDirection>();
            final Stream.Builder<T> failed = Stream.builder();
            final MutableObject<DataResult<Unit>> result =
                    new MutableObject<>(DataResult.success(Unit.INSTANCE, Lifecycle.stable()));

            stream.accept(t -> {
                if (ops.emptyMap().equals(t)) {
                    elements.add(null);
                } else {
                    DataResult<Pair<NullableDirection, T>> element = innerCodec.decode(ops, t);
                    element.error().ifPresent(e -> failed.add(t));
                    result.setValue(result.getValue()
                            .apply2stable(
                                    (r, v) -> {
                                        elements.add(v.getFirst());
                                        return r;
                                    },
                                    element));
                }
            });

            final T errors = ops.createList(failed.build());

            final Pair<List<NullableDirection>, T> pair = Pair.of(Collections.unmodifiableList(elements), errors);

            return result.getValue().map(unit -> pair).setPartial(pair);
        });
    }
}
