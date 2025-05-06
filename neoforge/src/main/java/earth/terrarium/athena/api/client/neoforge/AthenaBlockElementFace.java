package earth.terrarium.athena.api.client.neoforge;

import com.mojang.math.Quadrant;
import earth.terrarium.athena.api.client.models.AthenaQuad;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

public class AthenaBlockElementFace {

    public static BlockElementFace of(AthenaQuad quad, Direction direction, Vector3f start, Vector3f end) {
        return new BlockElementFace(
                quad.cull() ? direction : null,
                -1,
                "",
                getUVs(start, end, direction),
                switch (quad.rotation()) {
                    case NONE -> Quadrant.R0;
                    case CLOCKWISE_90 -> Quadrant.R90;
                    case CLOCKWISE_180 -> Quadrant.R180;
                    case COUNTERCLOCKWISE_90 -> Quadrant.R270;
                }
        );
    }

    private static BlockElementFace.UVs getUVs(Vector3f from, Vector3f to, Direction direction) {
        float[] uvs = switch (direction) {
            case UP -> new float[] { from.x(), to.z(), to.x(), from.z() };
            case DOWN -> new float[]{ from.x(), 16 - from.z(), to.x(), 16 - to.z() };
            case NORTH -> new float[] { 16 - from.x, 16 - to.y, 16 - to.x, 16 - from.y };
            case SOUTH -> new float[]{ to.x, 16 - to.y, from.x, 16 - from.y };
            case WEST -> new float[]{ to.z(), 16.0F - to.y(), from.z(), 16.0F - from.y() };
            case EAST -> new float[]{16.0F - from.z, 16.0F - to.y(), 16.0F - to.z, 16.0F - from.y()};
        };
        return new BlockElementFace.UVs(uvs[0], uvs[1], uvs[2], uvs[3]);
    }
}
