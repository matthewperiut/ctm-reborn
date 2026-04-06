package earth.terrarium.athena.impl.platform;

import earth.terrarium.athena.api.client.models.AthenaModelFactory;
import earth.terrarium.athena.api.client.neoforge.AthenaUnbakedModel;
import earth.terrarium.athena.api.client.utils.AthenaUnbakedModelLoader;
import net.minecraft.resources.Identifier;

public class ModelLoaderServiceNeoForgeImpl implements ModelLoaderService {

    @Override
    public AthenaUnbakedModelLoader register(Identifier type, AthenaModelFactory factory) {
        return new AthenaUnbakedModelLoader(type, factory, AthenaUnbakedModel::new);
    }
}
