package earth.terrarium.athena.impl.client.models.ctm;

import earth.terrarium.athena.api.client.models.AthenaQuad;
import earth.terrarium.athena.api.client.utils.CtmState;
import net.minecraft.world.level.block.Rotation;

import java.util.List;

public record FourSliceCtmProvider(
        int particle, int center, int vertical, int horizontal, int empty
) implements CtmProvider {

    private int getTexture(boolean first, boolean second, boolean diagonal) {
        if (first && second) {
            return diagonal ? empty : center;
        } else if (first) {
            return vertical;
        } else if (second) {
            return horizontal;
        }
        return particle;
    }

    @Override
    public List<AthenaQuad> get(CtmState state, float depth) {
        if (state.allTrue()) {
            return List.of(AthenaQuad.square(empty, depth));
        }

        return List.of(
                new AthenaQuad(getTexture(state.up(), state.left(), state.upLeft()), 0, 0.5f, 1f, 0.5f, Rotation.NONE, depth),
                new AthenaQuad(getTexture(state.up(), state.right(), state.upRight()), 0.5f, 1f, 1f, 0.5f, Rotation.NONE, depth),
                new AthenaQuad(getTexture(state.down(), state.left(), state.downLeft()), 0, 0.5f, 0.5f, 0f, Rotation.NONE, depth),
                new AthenaQuad(getTexture(state.down(), state.right(), state.downRight()), 0.5f, 1f, 0.5f, 0f, Rotation.NONE, depth)
        );
    }
}
