package earth.terrarium.athena.impl.internal;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.data.AtlasIds;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public class BlockStateModelMaterialInfo {

    private final Int2ObjectMap<Material.Baked> materials;

    private @Nullable Material.Baked particleMaterial = null;
    private int materialFlags = -1;

    public BlockStateModelMaterialInfo(Int2ObjectMap<Material.Baked> materials) {
        this.materials = materials;
    }

    public Material.Baked getParticle() {
        if (this.particleMaterial == null) {
            if (this.materials.containsKey(0)) {
                this.particleMaterial = this.materials.get(0);
            } else {
                this.particleMaterial = new Material.Baked(
                        Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS).missingSprite(),
                        false
                );
            }
        }
        return this.particleMaterial;
    }

    public @BakedQuad.MaterialFlags int getFlags() {
        if (this.materialFlags == -1) {
            this.materialFlags = 0;
            for (var material : this.materials.values()) {
                var contents = material.sprite().contents();

                if (material.forceTranslucent() || contents.computeTransparency(0f, 0f, 1f, 1f).hasTranslucent()) {
                    this.materialFlags |= BakedQuad.FLAG_TRANSLUCENT;
                }
                if (contents.isAnimated()) {
                    this.materialFlags |= BakedQuad.FLAG_ANIMATED;
                }
            }
        }
        return this.materialFlags;
    }
}
