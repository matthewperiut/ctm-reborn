package earth.terrarium.athena.api.client.data;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import earth.terrarium.athena.api.client.models.AthenaBlockModel;
import earth.terrarium.athena.api.client.models.FactoryManager;
import earth.terrarium.athena.api.client.utils.AthenaUnbakedModelLoader;
import earth.terrarium.athena.impl.client.DefaultModels;
import earth.terrarium.athena.impl.internal.AthenaUnbakedModel;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;

import java.nio.file.Path;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class AthenaModelProvider implements DataProvider {

    // Writes the loader key alongside the model's own fields, which is what the resource loader keys off when it
    // decides whether a file belongs to a given model type.
    private static final Codec<AthenaUnbakedModel> CODEC = FactoryManager.codec().dispatch(
        DefaultModels.MODID + ":loader",
        AthenaUnbakedModel::loader,
        AthenaUnbakedModelLoader::codec
    );

    private final PackOutput output;
    private final Map<Block, AthenaUnbakedModel> models = new HashMap<>();

    protected AthenaModelProvider(PackOutput output) {
        this.output = output;
    }

    public abstract void gather();

    /**
     * Adds a model, resolving the loader to write it under from the model's own type. A type registered under more
     * than one id - {@code giant} and {@code mural} share one - resolves to the lowest id; use
     * {@link #add(Block, Identifier, AthenaBlockModel)} to pick.
     */
    public void add(Block block, AthenaBlockModel model) {
        var loader = FactoryManager.loaders()
            .stream()
            .filter((candidate) -> candidate.type() == model.type())
            .min(Comparator.comparing((candidate) -> candidate.id().toString()))
            .orElseThrow(() -> new IllegalArgumentException("No Athena loader is registered for the model added for " + block));

        this.models.put(block, new AthenaUnbakedModel(model, loader));
    }

    /**
     * Adds a model written under a specific loader id.
     */
    public void add(Block block, Identifier loader, AthenaBlockModel model) {
        var registered = FactoryManager.get(loader);

        if (registered == null) {
            throw new IllegalArgumentException("No Athena loader is registered as " + loader);
        }

        this.models.put(block, new AthenaUnbakedModel(model, registered));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        gather();

        ImmutableList.Builder<CompletableFuture<?>> futuresBuilder = new ImmutableList.Builder<>();
        // These are read by a client resource reload listener, so they belong to the resource pack, not the data pack.
        Path outputFolder = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK);

        for (var entry : models.entrySet()) {
            var id = entry.getKey().builtInRegistryHolder().key().identifier();

            Path path = outputFolder
                .resolve(id.getNamespace())
                .resolve(DefaultModels.MODID)
                .resolve(id.getPath() + ".json");

            futuresBuilder.add(DataProvider.saveStable(cache, CODEC, entry.getValue(), path));
        }

        return CompletableFuture.allOf(futuresBuilder.build().toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "CTM Reborn Models";
    }
}
