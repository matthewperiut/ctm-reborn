package earth.terrarium.athena.api.client.models;

import com.google.gson.JsonObject;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class AthenaModelAttributes {

    public static final AthenaModelAttributes EMPTY = new AthenaModelAttributes(null);

    private final TintProvider tint;

    public AthenaModelAttributes(@Nullable TintProvider tint) {
        this.tint = tint;
    }

    public TintProvider getTint() {
        return this.tint;
    }

    @ApiStatus.Internal
    public static AthenaModelAttributes fromJson(JsonObject json) {
        var tint = TintProvider.fromJson(json);
        return new AthenaModelAttributes(tint);
    }
}
