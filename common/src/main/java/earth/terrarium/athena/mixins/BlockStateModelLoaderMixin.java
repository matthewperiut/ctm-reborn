package earth.terrarium.athena.mixins;

import com.google.gson.JsonElement;
import com.llamalad7.mixinextras.sugar.Local;
import earth.terrarium.athena.impl.loading.AthenaResourceLoader;
import net.minecraft.client.resources.model.BlockStateModelLoader;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.Executor;
import java.util.function.Function;

@Mixin(BlockStateModelLoader.class)
public class BlockStateModelLoaderMixin {

    @Inject(method = "method_65721", at = @At("HEAD"))
    private static void onBlockStatesLoad(Function<?, ?> function, Executor executor, Map<?, ?> map, CallbackInfoReturnable<CompletionStage<?>> cir) {
        AthenaResourceLoader.clearBlockstateData();
    }

    @Inject(method = "method_65720", at = @At(value = "INVOKE", target = "Lcom/mojang/serialization/Codec;parse(Lcom/mojang/serialization/DynamicOps;Ljava/lang/Object;)Lcom/mojang/serialization/DataResult;"))
    private static void onBlockModelLoad(
            Map.Entry<?, ?> entry, Function<?, ?> function, CallbackInfoReturnable<BlockStateModelLoader.LoadedModels> cir,
            @Local ResourceLocation id, @Local JsonElement jsonObject
    ) {
        AthenaResourceLoader.addBlockstateData(id, jsonObject);
    }

}