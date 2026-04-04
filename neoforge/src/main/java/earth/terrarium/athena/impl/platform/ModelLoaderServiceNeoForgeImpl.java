package earth.terrarium.athena.impl.platform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import earth.terrarium.athena.api.client.models.AthenaModelFactory;
import earth.terrarium.athena.api.client.neoforge.AthenaUnbakedModel;
import earth.terrarium.athena.api.client.utils.AthenaUnbakedModelLoader;
import net.minecraft.Optionull;
import net.minecraft.resources.Identifier;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ModelLoaderServiceNeoForgeImpl implements ModelLoaderService {

    public static final Codec<AthenaUnbakedModelLoader> CODEC = Identifier.CODEC.comapFlatMap(
            it -> Optionull.mapOrElse(ModelLoaderServiceNeoForgeImpl.get(it), DataResult::success, () -> DataResult.error(() -> "Unknown loader: " + it)),
            AthenaUnbakedModelLoader::id
    );

    private static final Map<Identifier, AthenaUnbakedModelLoader> FACTORIES = new HashMap<>();

    @Override
    public void register(Identifier type, AthenaModelFactory factory) {
        if (FACTORIES.containsKey(type)) {
            throw new IllegalArgumentException("Factory already registered for type: " + type);
        }
        FACTORIES.put(type, new AthenaUnbakedModelLoader(type, factory, AthenaUnbakedModel::new));
    }

    public static AthenaUnbakedModelLoader get(Identifier type) {
        return FACTORIES.get(type);
    }

    public static Collection<Identifier> getTypes() {
        return FACTORIES.keySet();
    }
}
