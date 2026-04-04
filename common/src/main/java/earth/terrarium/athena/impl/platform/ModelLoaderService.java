package earth.terrarium.athena.impl.platform;

import earth.terrarium.athena.api.client.models.AthenaModelFactory;
import net.minecraft.resources.Identifier;

@PlatformService
public interface ModelLoaderService {

    void register(Identifier type, AthenaModelFactory factory);

    static ModelLoaderService create() {
        throw new AssertionError("Failed to create ModelLoaderService, no implementation found");
    }
}
