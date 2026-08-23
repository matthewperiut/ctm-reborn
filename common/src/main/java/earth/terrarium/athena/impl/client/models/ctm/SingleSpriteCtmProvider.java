package earth.terrarium.athena.impl.client.models.ctm;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.DataResult;
import earth.terrarium.athena.api.client.models.AthenaQuad;
import earth.terrarium.athena.api.client.utils.CtmState;
import earth.terrarium.athena.impl.client.models.materials.MaterialStorage;
import net.minecraft.client.resources.model.sprite.Material;

import java.util.List;

public record SingleSpriteCtmProvider(
        int sprite
) implements CtmProvider {
    public static final Codec<SingleSpriteCtmProvider.Type> CODEC = Material.CODEC.xmap(Type::new, Type::material);

    @Override
    public List<AthenaQuad> get(CtmState state, float depth) {
        return List.of(AthenaQuad.square(this.sprite, depth));
    }

    public record Type(Material material) implements CtmProvider.Type {
        @Override
        public <T> DataResult<T> encode(DynamicOps<T> ops, T prefix) {
            return CODEC.encode(this, ops, prefix);
        }

        @Override
        public CtmProvider build(MaterialStorage materials) {
            return new SingleSpriteCtmProvider(materials.put(material));
        }
    }
}
