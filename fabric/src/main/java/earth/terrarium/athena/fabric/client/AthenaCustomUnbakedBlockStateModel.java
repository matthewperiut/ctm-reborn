package earth.terrarium.athena.fabric.client;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import earth.terrarium.athena.api.client.models.FactoryManager;
import earth.terrarium.athena.impl.client.DefaultModels;
import net.fabricmc.fabric.api.client.model.loading.v1.CustomUnbakedBlockStateModel;
import net.minecraft.Optionull;
import net.minecraft.client.renderer.block.dispatch.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public record AthenaCustomUnbakedBlockStateModel(
        @NotNull BlockStateModel.UnbakedRoot root
) implements CustomUnbakedBlockStateModel {

    private static final MapCodec<JsonObject> OBJECT_CODEC = MapCodec.assumeMapUnsafe(ExtraCodecs.JSON.comapFlatMap(
            it -> it instanceof JsonObject object ? DataResult.success(object) : DataResult.error(() -> "Expected JsonObject"),
            Function.identity()
    ));
    public static final MapCodec<AthenaCustomUnbakedBlockStateModel> CODEC = Codec.mapPair(
            FactoryManager.codec().fieldOf(DefaultModels.MODID + ":loader"), OBJECT_CODEC
    ).flatXmap(
            it -> Optionull.mapOrElse(
                    it.getFirst().loadModel(it.getSecond()),
                    root -> DataResult.success(new AthenaCustomUnbakedBlockStateModel(root)),
                    () -> DataResult.error(() -> "Failed to load model: " + it.getSecond())
            ),
            _ -> DataResult.error(() -> "AthenaCustomUnbakedBlockStateModel cannot be encoded")
    );

    @Override
    public @NotNull MapCodec<? extends CustomUnbakedBlockStateModel> codec() {
        return CODEC;
    }

    @Override
    public @NotNull BlockStateModel bake(@NotNull ModelBaker baker) {
        return root.bake(Blocks.AIR.defaultBlockState(), baker);
    }

    @Override
    public void resolveDependencies(@NotNull ResolvableModel.Resolver arg) {

    }
}
