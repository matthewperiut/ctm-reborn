package earth.terrarium.athena.fabric.client;

import earth.terrarium.athena.api.client.models.FactoryManager;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class AthenaModelLoadingPlugin implements ModelLoadingPlugin {

    @Override
    public void initialize(@NonNull Context context) {
        FactoryManager.loaders().forEach(loader ->
                context.modifyBlockModelBeforeBake().register((model, ctx) ->
                        Objects.requireNonNullElse(loader.loadModel(ctx.state()), model)
                )
        );
    }
}
