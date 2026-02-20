package earth.terrarium.athena.impl.client.models.ctm;

import earth.terrarium.athena.api.client.models.AthenaQuad;
import earth.terrarium.athena.api.client.utils.CtmState;

import java.util.List;

public record SingleSpriteCtmProvider(
        int sprite
) implements CtmProvider {

    @Override
    public List<AthenaQuad> get(CtmState state, float depth) {
        return List.of(AthenaQuad.square(this.sprite, depth));
    }
}
