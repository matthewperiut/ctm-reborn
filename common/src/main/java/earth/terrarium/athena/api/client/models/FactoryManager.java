package earth.terrarium.athena.api.client.models;

import earth.terrarium.athena.impl.platform.ModelLoaderService;
import net.minecraft.resources.Identifier;

public class FactoryManager {

    private static final ModelLoaderService SERVICE = ModelLoaderService.create();

    /**
     * Registers a new model factory, which will be used to create models for the given json.
     * @param id The id of the model factory
     * @param factory The factory to use
     */
    public static void register(Identifier id, AthenaModelFactory factory) {
        SERVICE.register(id, factory);
    }
}
