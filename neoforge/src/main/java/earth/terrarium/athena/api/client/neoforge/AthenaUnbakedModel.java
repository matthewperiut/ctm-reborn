package earth.terrarium.athena.api.client.neoforge;

import earth.terrarium.athena.api.client.models.AthenaBlockModel;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class AthenaUnbakedModel implements BlockStateModel.UnbakedRoot {

    private final Supplier<AthenaBlockModel> model;

    public AthenaUnbakedModel(Supplier<AthenaBlockModel> model) {
        this.model = model;
    }

    @Override
    public void resolveDependencies(@NotNull Resolver arg) {

    }

    @Override
    public @NotNull BlockStateModel bake(@NotNull BlockState state, @NotNull ModelBaker baker) {
        return new AthenaBakedModel(this.model.get(), material -> baker.materials().get(material, () -> "Athena: ?"));
    }

    @Override
    public @NotNull Object visualEqualityGroup(@NotNull BlockState arg) {
        return this;
    }
}
