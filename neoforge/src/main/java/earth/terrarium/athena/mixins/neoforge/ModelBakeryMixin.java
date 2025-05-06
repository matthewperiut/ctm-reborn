package earth.terrarium.athena.mixins.neoforge;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import earth.terrarium.athena.api.client.models.neoforge.FactoryManagerImpl;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {

    @WrapOperation(
            method = "method_68018",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/renderer/block/model/BlockStateModel$UnbakedRoot;bake(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/client/resources/model/ModelBaker;)Lnet/minecraft/client/renderer/block/model/BlockStateModel;"
            )
    )
    private static BlockStateModel stitch$loadModel(
            BlockStateModel.UnbakedRoot instance,
            BlockState blockState,
            ModelBaker baker, Operation<BlockStateModel> original
    ) {
        for (ResourceLocation type : FactoryManagerImpl.getTypes()) {
            BlockStateModel.UnbakedRoot model = FactoryManagerImpl.get(type).loadModel(blockState);
            if (model != null) {
                instance = model;
                break;
            }
        }
        return original.call(instance, blockState, baker);
    }
}
