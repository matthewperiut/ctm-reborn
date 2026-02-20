package earth.terrarium.athena.api.client.models;

import net.minecraft.world.level.block.Rotation;

public record AthenaQuad(int sprite, float left, float right, float top, float bottom, Rotation rotation, float depth, boolean cull) {

    public AthenaQuad(int sprite, float left, float right, float top, float bottom, Rotation rotation, float depth) {
        this(sprite, left, right, top, bottom, rotation, depth, depth == 0);
    }

    public static AthenaQuad withSprite(int sprite) {
        return withRotation(sprite, Rotation.NONE);
    }

    public static AthenaQuad withRotation(int sprite, Rotation rotation) {
        return new AthenaQuad(sprite, 0, 1, 1, 0, rotation, 0f);
    }

    public static AthenaQuad square(int sprite, float depth) {
        return new AthenaQuad(sprite, 0, 1, 1, 0, Rotation.NONE, depth);
    }
}
