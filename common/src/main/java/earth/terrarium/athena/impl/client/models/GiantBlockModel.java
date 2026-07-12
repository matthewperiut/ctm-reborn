package earth.terrarium.athena.impl.client.models;

import com.mojang.serialization.*;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import earth.terrarium.athena.api.client.models.AthenaBlockModel;
import earth.terrarium.athena.api.client.models.AthenaModelType;
import earth.terrarium.athena.api.client.models.AthenaQuad;
import earth.terrarium.athena.api.client.utils.AppearanceAndTintGetter;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GiantBlockModel implements AthenaBlockModel {

    // TODO Is there an existing utility for this?
    public static final MapCodec<GiantBlockModel> CODEC = new MapCodec<>() {
        @Override
        public <T> RecordBuilder<T> encode(GiantBlockModel input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            prefix = Dimensions.CODEC.encode(input.dimensions, ops, prefix);
            prefix = Materials.codec(input.dimensions).encode(input.materials, ops, prefix);

            return prefix;
        }

        @Override
        public <T> DataResult<GiantBlockModel> decode(DynamicOps<T> ops, MapLike<T> input) {
            return Dimensions.CODEC.decode(ops, input)
                .flatMap((dimensions) ->
                    Materials.codec(dimensions).decode(ops, input).map((materials) ->
                        new GiantBlockModel(dimensions, materials)
                    )
                );
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            // We can't predict the keys accurately given that they require the width and height, which we do not have here. Thus, we only include the keys that are guaranteed.
            return Stream.concat(
                Dimensions.CODEC.keys(ops),
                Stream.of(
                    "particle",
                    "1",
                    "2"
                ).map(ops::createString)
            );
        }

        @Override
        public String toString() {
            return "AthenaGiantBlockModelMapCodec";
        }
    };

    public static final AthenaModelType TYPE = new AthenaModelType(CODEC);

    private final Dimensions dimensions;
    private final Materials materials;

    public GiantBlockModel(Dimensions dimensions, Materials materials) {
        this.dimensions = dimensions;
        this.materials = materials;
    }

    @Override
    public AthenaModelType type() {
        return TYPE;
    }

    @Override
    public List<AthenaQuad> getQuads(AppearanceAndTintGetter level, BlockState blockState, BlockPos pos, Direction direction) {
        int width = dimensions.width();
        int height = dimensions.height();
        int x = Math.abs(pos.getX());
        int y = Math.abs(pos.getY());
        int z = Math.abs(pos.getZ());

        return switch (direction.getAxis()) {
            case X -> {
                if (direction.getAxisDirection() == Direction.AxisDirection.POSITIVE) {
                    z = Math.abs(width - z % width - 1);
                }
                yield List.of(AthenaQuad.withSprite(1 + (z % width) + (y % height) * width));
            }
            case Z -> {
                if (direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE) {
                    x = Math.abs(width - x % width - 1);
                }
                yield List.of(AthenaQuad.withSprite(1 + (x % width) + (y % height) * width));
            }
            default -> {
                if (direction.getAxisDirection() == Direction.AxisDirection.NEGATIVE) {
                    z = Math.abs(width - z % width - 1);
                }
                yield List.of(AthenaQuad.withSprite(1 + (x % width) + (z % height) * width));
            }
        };
    }

    @Override
    public Map<Direction, List<AthenaQuad>> getDefaultQuads(Direction direction) {
        Map<Direction, List<AthenaQuad>> quads = new HashMap<>(Direction.values().length);
        for (Direction dir : Direction.values()) {
            quads.put(dir, List.of(AthenaQuad.withSprite(0)));
        }
        return quads;
    }

    @Override
    public Int2ObjectMap<Material.Baked> getTextures(Function<Material, Material.Baked> getter) {
        Int2ObjectMap<Material.Baked> textures = new Int2ObjectArrayMap<>();
        textures.put(0, getter.apply(materials.particle));

        for (Map.Entry<Integer, Material> entry : materials.pixels().entrySet()) {
            textures.put(entry.getKey().intValue(), getter.apply(entry.getValue()));
        }

        return textures;
    }

    public record Dimensions(int width, int height) {
        public static final MapCodec<Dimensions> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("width").forGetter(Dimensions::width),
            ExtraCodecs.POSITIVE_INT.fieldOf("height").forGetter(Dimensions::height)
        ).apply(instance, Dimensions::new));
    }

    public record Materials(
        Material particle,
        Map<Integer, Material> pixels
    ) {
        private static Keyable pixelKeys(Dimensions dimensions) {
            return Keyable.forStrings(() -> IntStream
                .range(1, dimensions.width * dimensions.height + 1)
                .mapToObj(String::valueOf)
            );
        }

        public static MapCodec<Materials> codec(Dimensions dimensions) {
            MapCodec<Materials> baseCodec = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                Material.CODEC.fieldOf("particle").forGetter(Materials::particle),
                Codec.simpleMap(
                    Codec.STRING.xmap(Integer::parseInt, String::valueOf),
                    Material.CODEC,
                    pixelKeys(dimensions)
                ).forGetter(Materials::pixels)
            ).apply(instance, Materials::new));

            return baseCodec.fieldOf("ctm_textures");
        }
    }
}
