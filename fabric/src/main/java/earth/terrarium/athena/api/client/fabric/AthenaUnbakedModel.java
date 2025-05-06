package earth.terrarium.athena.api.client.fabric;

import earth.terrarium.athena.api.client.models.AthenaBlockModel;
import net.minecraft.client.renderer.block.model.BlockStateModel;
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
    public void resolveDependencies(Resolver resolver) {

    }

    @Override
    public @NotNull BlockStateModel bake(BlockState state, ModelBaker baker) {
        return new AthenaBakedModel(this.model.get(), material -> baker.sprites().get(material, () -> "Athena: ?"));
    }

    @Override
    public @NotNull Object visualEqualityGroup(BlockState blockState) {
        return this;
    }
}
