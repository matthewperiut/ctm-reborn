package earth.terrarium.athena.impl.client.models.ctm;

import com.mojang.serialization.Codec;
import earth.terrarium.athena.api.client.models.AthenaQuad;
import earth.terrarium.athena.api.client.utils.CtmState;
import earth.terrarium.athena.impl.client.models.materials.MaterialStorage;

import java.util.List;
import java.util.function.Function;

public interface CtmProvider {
    List<AthenaQuad> get(CtmState state, float depth);

    sealed interface Type permits FourSliceCtmProvider.Type, FourtySevenSliceCtmProvider.Type, SingleSpriteCtmProvider.Type {
        Codec<Type> CODEC = FourSliceCtmProvider.CODEC.<Type>xmap(Function.identity(), FourSliceCtmProvider.Type.class::cast)
            .withAlternative(FourtySevenSliceCtmProvider.CODEC)
            .withAlternative(SingleSpriteCtmProvider.CODEC);

        CtmProvider build(MaterialStorage materials);
    }
}
