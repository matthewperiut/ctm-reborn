package earth.terrarium.athena.api.client.models;

import com.mojang.serialization.MapCodec;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AthenaModelAttributes {

    public static final AthenaModelAttributes EMPTY = new AthenaModelAttributes(null);

    public static final MapCodec<AthenaModelAttributes> TINT_CODEC = TintProvider.CODEC
        .optionalFieldOf("tint")
        .xmap(
            (tint) -> tint.<AthenaModelAttributes>map(AthenaModelAttributes::new).orElse(EMPTY),
            (attributes) -> Optional.ofNullable(attributes.getTint())
        );

    private final TintProvider tint;

    public AthenaModelAttributes(@Nullable TintProvider tint) {
        this.tint = tint;
    }

    public TintProvider getTint() {
        return this.tint;
    }
}
