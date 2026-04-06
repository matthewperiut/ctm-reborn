package earth.terrarium.athena.fabric.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.minecraft.resources.Identifier;

public class AthenaFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModelLoadingPlugin.register(new AthenaModelLoadingPlugin());
        CustomUnbakedBlockStateModel.register(
                Identifier.fromNamespaceAndPath("athena", "athena"),
                AthenaCustomUnbakedBlockStateModel.CODEC
        );
    }
}
