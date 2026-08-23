package earth.terrarium.athena.impl.client.models.ctm;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import earth.terrarium.athena.api.client.models.AthenaQuad;
import earth.terrarium.athena.api.client.utils.CtmState;
import earth.terrarium.athena.impl.client.models.materials.MaterialStorage;

import java.util.List;

public interface CtmProvider {
    List<AthenaQuad> get(CtmState state, float depth);

    sealed interface Type permits FourSliceCtmProvider.Type, FourtySevenSliceCtmProvider.Type, SingleSpriteCtmProvider.Type {

        /**
         * The three formats do not share a serialized shape - a four slice is an object of named materials, a 47
         * slice is a template string containing {@code [$index]} and a single sprite is a plain material - so they
         * can not be chained with {@link Codec#withAlternative}, which always encodes through the first codec of the
         * chain and would therefore write a 47 slice or a single sprite out as a four slice. Decoding tries each
         * format in turn, encoding delegates to the format the value actually belongs to.
         */
        Codec<Type> CODEC = new Codec<>() {
            @Override
            public <T> DataResult<Pair<Type, T>> decode(DynamicOps<T> ops, T input) {
                // A 47 slice is checked before a single sprite because both are strings, and a four slice last
                // because it is the only one of the three that is an object.
                var slice47 = upcast(FourtySevenSliceCtmProvider.CODEC.decode(ops, input));
                if (slice47.isSuccess()) {
                    return slice47;
                }

                var single = upcast(SingleSpriteCtmProvider.CODEC.decode(ops, input));
                if (single.isSuccess()) {
                    return single;
                }

                var slice4 = upcast(FourSliceCtmProvider.CODEC.decode(ops, input));
                if (slice4.isSuccess()) {
                    return slice4;
                }

                return DataResult.error(() -> "Not a valid ctm texture: expected a 47 slice template containing '[$index]', a single sprite, or a four slice object");
            }

            @Override
            public <T> DataResult<T> encode(Type input, DynamicOps<T> ops, T prefix) {
                return input.encode(ops, prefix);
            }

            @Override
            public String toString() {
                return "AthenaCtmProviderType";
            }
        };

        private static <A extends Type, T> DataResult<Pair<Type, T>> upcast(DataResult<Pair<A, T>> result) {
            return result.map((pair) -> pair.<Type>mapFirst((type) -> type));
        }

        CtmProvider build(MaterialStorage materials);

        /**
         * Encodes this provider through its own codec, so that each format round trips to the shape it was read from.
         */
        <T> DataResult<T> encode(DynamicOps<T> ops, T prefix);
    }
}
