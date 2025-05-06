package earth.terrarium.athena.api.client.utils;

import com.google.gson.JsonObject;
import earth.terrarium.athena.api.client.models.AthenaBlockModel;
import earth.terrarium.athena.api.client.models.AthenaModelFactory;
import earth.terrarium.athena.impl.loading.AthenaResourceLoader;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

public class AthenaUnbakedModelLoader {

    private final ResourceLocation id;
    private final AthenaModelFactory factory;
    private final Function<Supplier<AthenaBlockModel>, BlockStateModel.UnbakedRoot> loader;

    public AthenaUnbakedModelLoader(ResourceLocation id, AthenaModelFactory factory, Function<Supplier<AthenaBlockModel>, BlockStateModel.UnbakedRoot> loader) {
        this.id = id;
        this.factory = factory;
        this.loader = loader;
    }

    public @Nullable BlockStateModel.UnbakedRoot loadModel(BlockState state) {
        var id = state.getBlock().builtInRegistryHolder().key().location();
        JsonObject json = AthenaResourceLoader.getData(this.id, id);
        return this.loadModel(json);
    }

    public BlockStateModel.UnbakedRoot loadModel(JsonObject json) {
        if (json != null) {
            return this.loader.apply(this.factory.create(json));
        }
        return null;
    }
}
