package earth.terrarium.athena.impl.client.models;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
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

    // The section materials are keyed by index, and which indices are valid depends on the width and height, so the
    // dimensions are dispatched on to build the codec that reads them.
    public static final MapCodec<GiantBlockModel> CODEC = Dimensions.CODEC.dispatchMap(
        GiantBlockModel::dimensions,
        (dimensions) -> Materials.codec(dimensions).xmap(
            (materials) -> new GiantBlockModel(dimensions, materials),
            GiantBlockModel::materials
        )
    );

    public static final AthenaModelType TYPE = new AthenaModelType(CODEC);

    private final Dimensions dimensions;
    private final Materials materials;

    public GiantBlockModel(Dimensions dimensions, Materials materials) {
        this.dimensions = dimensions;
        this.materials = materials;
    }

    public Dimensions dimensions() {
        return this.dimensions;
    }

    public Materials materials() {
        return this.materials;
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
        textures.put(0, getter.apply(materials.particle()));

        for (var entry : materials.sections().int2ObjectEntrySet()) {
            textures.put(entry.getIntKey(), getter.apply(entry.getValue()));
        }

        return textures;
    }

    public record Dimensions(int width, int height) {
        public static final MapCodec<Dimensions> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("width").forGetter(Dimensions::width),
            ExtraCodecs.POSITIVE_INT.fieldOf("height").forGetter(Dimensions::height)
        ).apply(instance, Dimensions::new));

        public int sections() {
            return this.width * this.height;
        }
    }

    public record Materials(
        Material particle,
        Int2ObjectMap<Material> sections
    ) {
        public static MapCodec<Materials> codec(Dimensions dimensions) {
            int sections = dimensions.sections();

            // The section keys share the object with "particle", so they are read by index rather than through a map
            // codec, which would try to parse "particle" as a section index and fail.
            MapCodec<Materials> baseCodec = new MapCodec<>() {
                @Override
                public <T> Stream<T> keys(DynamicOps<T> ops) {
                    return Stream.concat(
                        Stream.of("particle"),
                        IntStream.rangeClosed(1, sections).mapToObj(String::valueOf)
                    ).map(ops::createString);
                }

                @Override
                public <T> DataResult<Materials> decode(DynamicOps<T> ops, MapLike<T> input) {
                    return Material.CODEC.fieldOf("particle").decode(ops, input).flatMap((particle) -> {
                        Int2ObjectMap<Material> materials = new Int2ObjectArrayMap<>(sections);

                        // The pre-codec parser read every index from 1 to width * height and threw if one was missing,
                        // so a partially defined model is still rejected rather than baking to a missing texture.
                        for (int index = 1; index <= sections; index++) {
                            int section = index;
                            T value = input.get(String.valueOf(index));

                            if (value == null) {
                                return DataResult.error(() -> "Missing ctm texture for section " + section + " of " + sections);
                            }

                            DataResult<Material> material = Material.CODEC.parse(ops, value);

                            if (material.isError()) {
                                String message = material.error().orElseThrow().message();
                                return DataResult.error(() -> "Invalid ctm texture for section " + section + ": " + message);
                            }

                            materials.put(section, material.getOrThrow());
                        }

                        return DataResult.success(new Materials(particle, materials));
                    });
                }

                @Override
                public <T> RecordBuilder<T> encode(Materials input, DynamicOps<T> ops, RecordBuilder<T> prefix) {
                    prefix = Material.CODEC.fieldOf("particle").encode(input.particle(), ops, prefix);

                    for (var entry : input.sections().int2ObjectEntrySet()) {
                        prefix = prefix.add(String.valueOf(entry.getIntKey()), Material.CODEC.encodeStart(ops, entry.getValue()));
                    }

                    return prefix;
                }
            };

            return baseCodec.fieldOf("ctm_textures");
        }
    }
}
