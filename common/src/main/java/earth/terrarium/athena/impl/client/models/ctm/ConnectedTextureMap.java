package earth.terrarium.athena.impl.client.models.ctm;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import earth.terrarium.athena.api.client.models.AthenaQuad;
import earth.terrarium.athena.api.client.utils.CtmState;
import earth.terrarium.athena.impl.client.models.materials.MaterialStorage;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConnectedTextureMap {

    private final EnumMap<Direction, CtmProvider> quads = new EnumMap<>(Direction.class);

    public ConnectedTextureMap() {}

    public List<AthenaQuad> getQuads(Direction direction, CtmState state, float depth) {
        var provider = quads.get(direction);
        if (provider == null) {
            throw new IllegalStateException("No CTM provider for direction " + direction);
        }
        return provider.get(state, depth);
    }

    public Map<Direction, List<AthenaQuad>> getDefaultQuads(Direction direction, CtmState state, float depth) {
        return Object2ObjectMaps.singleton(direction, getQuads(direction, state, depth));
    }

    public record DirectionalCtmProviders(
        Optional<CtmProvider.Type> defaultProvider,
        Map<Direction, CtmProvider.Type> directions
    ) {
        private static final Codec<DirectionalCtmProviders> NON_DIRECTIONAL_CODEC = CtmProvider.Type.CODEC.flatComapMap(
            DirectionalCtmProviders::new,
            (providers) -> providers.defaultProvider().isPresent() && providers.directions().isEmpty() ?
                DataResult.success(providers.defaultProvider().get()) :
                DataResult.error(() -> "Can not encode DirectionalCtmProviders to a single provider when default provider isn't present or there are directional providers defined")
        );

        // Each direction shares the object with "default", so the entries are read by name rather than through a map
        // codec, which would try to parse "default" as a direction and fail.
        private static final MapCodec<DirectionalCtmProviders> FULL_DIRECTIONAL_MAP_CODEC = new MapCodec<>() {
            @Override
            public <T> Stream<T> keys(DynamicOps<T> ops) {
                return Stream.concat(
                    Stream.of("default"),
                    Arrays.stream(Direction.values()).map(StringRepresentable::getSerializedName)
                ).map(ops::createString);
            }

            @Override
            public <T> DataResult<DirectionalCtmProviders> decode(DynamicOps<T> ops, MapLike<T> input) {
                DataResult<Optional<CtmProvider.Type>> defaultProvider = read(ops, input, "default").map(Optional::ofNullable);

                if (defaultProvider.isError()) {
                    return defaultProvider.map((provider) -> null);
                }

                Map<Direction, CtmProvider.Type> directions = new EnumMap<>(Direction.class);

                for (Direction direction : Direction.values()) {
                    DataResult<CtmProvider.Type> provider = read(ops, input, direction.getSerializedName());

                    if (provider.isError()) {
                        return provider.map((value) -> null);
                    }

                    provider.result().ifPresent((value) -> directions.put(direction, value));
                }

                if (directions.size() < Direction.values().length && defaultProvider.getOrThrow().isEmpty()) {
                    return DataResult.error(() -> {
                        String names = Arrays.stream(Direction.values())
                            .map(StringRepresentable::getSerializedName)
                            .collect(Collectors.joining(", "));

                        return "Must define all directions (" + names + ") or define a 'default' value";
                    });
                }

                return DataResult.success(new DirectionalCtmProviders(defaultProvider.getOrThrow(), directions));
            }

            @Override
            public <T> RecordBuilder<T> encode(DirectionalCtmProviders input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                if (input.defaultProvider().isPresent()) {
                    prefix = prefix.add("default", CtmProvider.Type.CODEC.encodeStart(ops, input.defaultProvider().get()));
                }

                for (Map.Entry<Direction, CtmProvider.Type> entry : input.directions().entrySet()) {
                    prefix = prefix.add(entry.getKey().getSerializedName(), CtmProvider.Type.CODEC.encodeStart(ops, entry.getValue()));
                }

                return prefix;
            }

            // Absent reads as a successful null so a missing key and an invalid one stay distinguishable.
            private <T> DataResult<CtmProvider.Type> read(DynamicOps<T> ops, MapLike<T> input, String key) {
                T value = input.get(key);

                if (value == null) {
                    return DataResult.success(null);
                }

                return CtmProvider.Type.CODEC.parse(ops, value)
                    .mapError((error) -> "Invalid '" + key + "' ctm texture: " + error);
            }
        };

        private static final Codec<DirectionalCtmProviders> FULL_DIRECTIONAL_CODEC = FULL_DIRECTIONAL_MAP_CODEC.codec();

        // withAlternative would always encode through the first codec, so per direction textures would be written back
        // out as a single texture. Pick the form to encode from the value instead.
        public static final Codec<DirectionalCtmProviders> CODEC = new Codec<>() {
            @Override
            public <T> DataResult<Pair<DirectionalCtmProviders, T>> decode(DynamicOps<T> ops, T input) {
                DataResult<Pair<DirectionalCtmProviders, T>> single = NON_DIRECTIONAL_CODEC.decode(ops, input);

                if (single.isSuccess()) {
                    return single;
                }

                DataResult<Pair<DirectionalCtmProviders, T>> directional = FULL_DIRECTIONAL_CODEC.decode(ops, input);

                if (directional.isSuccess()) {
                    return directional;
                }

                String singleError = single.error().orElseThrow().message();
                String directionalError = directional.error().orElseThrow().message();

                return DataResult.error(() -> "Not a valid ctm texture, as one texture for every direction: " +
                    singleError + ", as a texture per direction: " + directionalError);
            }

            @Override
            public <T> DataResult<T> encode(DirectionalCtmProviders input, DynamicOps<T> ops, T prefix) {
                return input.directions().isEmpty() && input.defaultProvider().isPresent() ?
                    NON_DIRECTIONAL_CODEC.encode(input, ops, prefix) :
                    FULL_DIRECTIONAL_CODEC.encode(input, ops, prefix);
            }
        };

        public DirectionalCtmProviders(CtmProvider.Type provider) {
            this(Optional.of(provider), Map.of());
        }

        public ConnectedTextureMap resolve(MaterialStorage materials) {
            var textures = new ConnectedTextureMap();

            for (Direction direction : Direction.values()) {
                CtmProvider.Type type = directions().get(direction);

                if (type == null) {
                    type = defaultProvider.orElseThrow();
                }

                textures.quads.put(direction, type.build(materials));
            }

            return textures;
        }
    }
}
