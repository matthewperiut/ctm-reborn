package earth.terrarium.athena.impl.client.models;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Keyable;
import com.mojang.serialization.MapCodec;
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
        private static Keyable sectionKeys(Dimensions dimensions) {
            return Keyable.forStrings(() -> IntStream
                .rangeClosed(1, dimensions.sections())
                .mapToObj(String::valueOf)
            );
        }

        private static Codec<Integer> sectionKeyCodec(Dimensions dimensions) {
            int sections = dimensions.sections();

            return Codec.STRING.comapFlatMap(
                (key) -> {
                    int index;

                    try {
                        index = Integer.parseInt(key);
                    } catch (NumberFormatException e) {
                        return DataResult.error(() -> "Not a valid section index: " + key);
                    }

                    return index >= 1 && index <= sections ?
                        DataResult.success(index) :
                        DataResult.error(() -> "Section index " + key + " is outside of 1.." + sections);
                },
                String::valueOf
            );
        }

        public static MapCodec<Materials> codec(Dimensions dimensions) {
            MapCodec<Materials> baseCodec = RecordCodecBuilder.mapCodec((instance) -> instance.group(
                Material.CODEC.fieldOf("particle").forGetter(Materials::particle),
                Codec.simpleMap(
                    sectionKeyCodec(dimensions),
                    Material.CODEC,
                    sectionKeys(dimensions)
                ).xmap(
                    (map) -> (Int2ObjectMap<Material>) new Int2ObjectArrayMap<Material>(map),
                    (map) -> map
                ).forGetter(Materials::sections)
            ).apply(instance, Materials::new));

            // The pre-codec parser read every index from 1 to width * height and threw if one was missing, so a
            // partially defined model is still rejected rather than baking to a missing texture.
            return baseCodec.fieldOf("ctm_textures").validate((materials) -> {
                for (int index = 1; index <= dimensions.sections(); index++) {
                    if (!materials.sections().containsKey(index)) {
                        int missing = index;
                        return DataResult.error(() -> "Missing ctm texture for section " + missing + " of " + dimensions.sections());
                    }
                }

                return DataResult.success(materials);
            });
        }
    }
}
