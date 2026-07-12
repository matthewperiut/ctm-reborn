package earth.terrarium.athena.impl.internal;

import earth.terrarium.athena.api.client.models.AthenaBlockModel;
import earth.terrarium.athena.api.client.utils.AthenaUnbakedModelLoader;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

public class AthenaUnbakedModel implements BlockStateModel.UnbakedRoot {

    private final AthenaBlockModel model;
    private final AthenaUnbakedModelLoader loader;

    public AthenaUnbakedModel(AthenaBlockModel model, AthenaUnbakedModelLoader loader) {
        this.model = model;
        this.loader = loader;
    }

    @Override
    public void resolveDependencies(@NonNull Resolver resolver) {

    }

    @Override
    public @NotNull BlockStateModel bake(@NonNull BlockState state, @NonNull ModelBaker baker) {
        return this.loader.bake(this, baker);
    }

    @Override
    public @NotNull Object visualEqualityGroup(@NonNull BlockState blockState) {
        return this;
    }

    public AthenaBlockModel getModel() {
        return model;
    }

    public AthenaUnbakedModelLoader getLoader() {
        return loader;
    }
}
