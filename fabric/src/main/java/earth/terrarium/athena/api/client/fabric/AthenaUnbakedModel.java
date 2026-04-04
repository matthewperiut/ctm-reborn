package earth.terrarium.athena.api.client.fabric;

import earth.terrarium.athena.api.client.models.AthenaBlockModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.function.Supplier;

public class AthenaUnbakedModel implements BlockStateModel.UnbakedRoot {

    private final Supplier<AthenaBlockModel> model;

    public AthenaUnbakedModel(Supplier<AthenaBlockModel> model) {
        this.model = model;
    }

    @Override
    public void resolveDependencies(@NonNull Resolver resolver) {

    }

    @Override
    public @NotNull BlockStateModel bake(@NonNull BlockState state, @NonNull ModelBaker baker) {
        return new AthenaBakedModel(this.model.get(), material -> baker.materials().get(material, () -> "Athena: ?"));
    }

    @Override
    public @NotNull Object visualEqualityGroup(@NonNull BlockState blockState) {
        return this;
    }
}
