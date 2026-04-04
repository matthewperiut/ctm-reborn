package earth.terrarium.athena.api.client.fabric;

import earth.terrarium.athena.impl.platform.ModelLoaderServiceFabricImpl;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class AthenaModelLoadingPlugin implements ModelLoadingPlugin {

    @Override
    public void initialize(@NonNull Context context) {
        ModelLoaderServiceFabricImpl.LOADERS.forEach((_, loader) ->
                context.modifyBlockModelBeforeBake().register((model, ctx) ->
                        Objects.requireNonNullElse(loader.loadModel(ctx.state()), model)
                )
        );
    }
}
