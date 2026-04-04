package earth.terrarium.athena.impl.platform;

import earth.terrarium.athena.api.client.fabric.AthenaUnbakedModel;
import earth.terrarium.athena.api.client.models.AthenaModelFactory;
import earth.terrarium.athena.api.client.utils.AthenaUnbakedModelLoader;
import net.minecraft.resources.Identifier;

import java.util.HashMap;
import java.util.Map;

public class ModelLoaderServiceFabricImpl implements ModelLoaderService {

    public static final Map<Identifier, AthenaUnbakedModelLoader> LOADERS = new HashMap<>();

    @Override
    public void register(Identifier type, AthenaModelFactory factory) {
        LOADERS.put(type, new AthenaUnbakedModelLoader(type, factory, AthenaUnbakedModel::new));
    }
}
