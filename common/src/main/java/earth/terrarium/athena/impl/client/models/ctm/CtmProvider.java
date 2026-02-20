package earth.terrarium.athena.impl.client.models.ctm;

import earth.terrarium.athena.api.client.models.AthenaQuad;
import earth.terrarium.athena.api.client.utils.CtmState;

import java.util.List;

public interface CtmProvider {

    List<AthenaQuad> get(CtmState state, float depth);
}