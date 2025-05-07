package earth.terrarium.athena.api.client.models.neoforge;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import earth.terrarium.athena.api.client.models.AthenaModelFactory;
import earth.terrarium.athena.api.client.neoforge.AthenaUnbakedModel;
import earth.terrarium.athena.api.client.utils.AthenaUnbakedModelLoader;
import net.minecraft.Optionull;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class FactoryManagerImpl {

    public static final Codec<AthenaUnbakedModelLoader> CODEC = ResourceLocation.CODEC.comapFlatMap(
            it -> Optionull.mapOrElse(FactoryManagerImpl.get(it), DataResult::success, () -> DataResult.error(() -> "Unknown loader: " + it)),
            AthenaUnbakedModelLoader::id
    );

    private static final Map<ResourceLocation, AthenaUnbakedModelLoader> FACTORIES = new HashMap<>();

    public static void register(ResourceLocation type, AthenaModelFactory factory) {
        if (FACTORIES.containsKey(type)) {
            throw new IllegalArgumentException("Factory already registered for type: " + type);
        }
        FACTORIES.put(type, new AthenaUnbakedModelLoader(type, factory, AthenaUnbakedModel::new));
    }

    public static AthenaUnbakedModelLoader get(ResourceLocation type) {
        return FACTORIES.get(type);
    }

    public static Collection<ResourceLocation> getTypes() {
        return FACTORIES.keySet();
    }
}
