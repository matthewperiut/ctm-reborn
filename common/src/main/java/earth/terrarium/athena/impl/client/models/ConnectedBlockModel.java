package earth.terrarium.athena.impl.client.models;

import com.google.gson.JsonObject;
import earth.terrarium.athena.api.client.models.AthenaBlockModel;
import earth.terrarium.athena.api.client.models.AthenaModelAttributes;
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
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Supplier;

public class ConnectedBlockModel implements AthenaBlockModel {

    public static final AthenaModelFactory FACTORY = new Factory();

    private final MaterialStorage materials;
    private final ConnectedTextureMap textures;
    private final BiPredicate<BlockState, BlockState> connectTo;
    private final AthenaModelAttributes attributes;

    public ConnectedBlockModel(MaterialStorage materials, ConnectedTextureMap textures, BiPredicate<BlockState, BlockState> connectTo, AthenaModelAttributes attributes) {
        this.materials = materials;
        this.textures = textures;
        this.connectTo = connectTo;
        this.attributes = attributes;
    }

    @Override
    public List<AthenaQuad> getQuads(AppearanceAndTintGetter level, BlockState state, BlockPos pos, Direction direction) {
        if (CtmUtils.checkRelative(level, state, pos, direction)) {
            return List.of();
        }

        return this.textures.getQuads(
                direction,
                CtmState.from(level, state, pos, direction, CtmUtils.check(level, state, pos, direction, this.connectTo)),
                0f
        );
    }

    @Override
    public Map<Direction, List<AthenaQuad>> getDefaultQuads(Direction direction) {
        if (direction == null) return Object2ObjectMaps.emptyMap();
        return Object2ObjectMaps.singleton(direction, this.textures.getQuads(direction, CtmState.ALL_FALSE, 0f));
    }

    @Override
    public Int2ObjectMap<Material.Baked> getTextures(Function<Material, Material.Baked> getter) {
        return this.materials.resolve(getter);
    }

    @Override
    public @Nullable AthenaModelAttributes getAttributes() {
        return this.attributes;
    }

    private static class Factory implements AthenaModelFactory {

        @Override
        public Supplier<AthenaBlockModel> create(JsonObject json) {
            var materials = new MaterialStorage();
            var textures = ConnectedTextureMap.of(materials, List.of(Direction.values()), GsonHelper.getNonNull(json, "ctm_textures"));
            BiPredicate<BlockState, BlockState> conditions = CtmUtils.parseCondition(json);
            var attributes = AthenaModelAttributes.fromJson(json);
            return () -> new ConnectedBlockModel(materials, textures, conditions, attributes);
        }
    }
}
