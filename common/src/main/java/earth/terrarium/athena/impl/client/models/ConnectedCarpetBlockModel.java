package earth.terrarium.athena.impl.client.models;

import com.google.gson.JsonObject;
import earth.terrarium.athena.api.client.models.AthenaBlockModel;
import earth.terrarium.athena.api.client.models.AthenaModelFactory;
import earth.terrarium.athena.api.client.models.AthenaQuad;
import earth.terrarium.athena.api.client.utils.AppearanceAndTintGetter;
import earth.terrarium.athena.api.client.utils.CtmState;
import earth.terrarium.athena.api.client.utils.CtmUtils;
import earth.terrarium.athena.impl.client.models.ctm.ConnectedTextureMap;
import earth.terrarium.athena.impl.client.models.materials.MaterialStorage;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConnectedCarpetBlockModel implements AthenaBlockModel {

    public static final AthenaModelFactory FACTORY = new Factory();

    private static final float PIXELS_15 = 15f / 16f;
    private static final float PIXELS_1 = 1f / 16f;

    private static final List<AthenaQuad> SIDE = List.of(new AthenaQuad(0, 0, 1f, PIXELS_1, 0, Rotation.NONE, 0));
    private static final List<AthenaQuad> CENTER_TOP = List.of(new AthenaQuad(1, 0, 1, 1, 0, Rotation.NONE, PIXELS_15));
    private static final List<AthenaQuad> CENTER_BOTTOM = List.of(new AthenaQuad(1, 0, 1, 1, 0, Rotation.NONE, PIXELS_1));

    private final MaterialStorage materials;
    private final ConnectedTextureMap textures;
    private final BiPredicate<BlockState, BlockState> connectTo;

    public ConnectedCarpetBlockModel(MaterialStorage materials, ConnectedTextureMap textures, BiPredicate<BlockState, BlockState> connectTo) {
        this.materials = materials;
        this.textures = textures;
        this.connectTo = connectTo;
    }

    @Override
    public List<AthenaQuad> getQuads(AppearanceAndTintGetter level, BlockState blockState, BlockPos pos, Direction direction) {
        if (direction.getAxis().isHorizontal()) {
            return SIDE;
        }

        return this.textures.getQuads(
                direction,
                CtmState.from(level, blockState, pos, direction, CtmUtils.check(level, blockState, pos, direction, connectTo)),
                direction == Direction.UP ? PIXELS_15 : PIXELS_1
        );
    }

    @Override
    public Map<Direction, List<AthenaQuad>> getDefaultQuads(Direction direction) {
        if (direction == null) return Object2ObjectMaps.emptyMap();
        if (direction.getAxis().isHorizontal()) return Object2ObjectMaps.singleton(direction, SIDE);
        return this.textures.getDefaultQuads(direction, CtmState.ALL_FALSE, direction == Direction.UP ? PIXELS_15 : PIXELS_1);
    }

    @Override
    public Int2ObjectMap<Material.Baked> getTextures(Function<Material, Material.Baked> getter) {
        return this.materials.resolve(getter);
    }

    private static class Factory implements AthenaModelFactory {

        @Override
        public Supplier<AthenaBlockModel> create(JsonObject json) {
            var materials = new MaterialStorage();
            var textures = ConnectedTextureMap.of(materials, List.of(Direction.values()), GsonHelper.getNonNull(json, "ctm_textures"));
            BiPredicate<BlockState, BlockState> conditions = CtmUtils.parseCondition(json);
            return () -> new ConnectedCarpetBlockModel(materials, textures, conditions);
        }
    }
}
