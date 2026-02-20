package earth.terrarium.athena.impl.client.models.ctm;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.serialization.DataResult;
import earth.terrarium.athena.api.client.models.AthenaQuad;
import earth.terrarium.athena.api.client.utils.CtmState;
import earth.terrarium.athena.impl.client.models.materials.MaterialStorage;
import net.minecraft.core.Direction;
import net.minecraft.util.GsonHelper;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ConnectedTextureMap {

    private static final String SLICE_47_KEY = "[$index]";
    private static final String DEFAULT_KEY = "default";

    private final EnumMap<Direction, CtmProvider> quads = new EnumMap<>(Direction.class);

    public ConnectedTextureMap() {

    }

    public ConnectedTextureMap(CtmProvider provider) {
        for (Direction direction : Direction.values()) {
            this.quads.put(direction, provider);
        }
    }

    public List<AthenaQuad> getQuads(Direction direction, CtmState state, float depth) {
        var provider = quads.get(direction);
        if (provider == null) {
            throw new IllegalStateException("No CTM provider for direction " + direction);
        }
        return provider.get(state, depth);
    }

    public Map<Direction, List<AthenaQuad>> getDefaultQuads(Direction direction, CtmState state, float depth) {
        return Map.of(direction, getQuads(direction, state, depth));
    }

    public static ConnectedTextureMap of(MaterialStorage materials, Iterable<Direction> directions, JsonElement json) {
        // if the json has a "particle" property always make it index 0
        if (json instanceof JsonObject object && object.has("particle")) {
            materials.put(GsonHelper.getAsString(object, "particle"));
        }

        return resolve(materials, json).mapOrElse(ConnectedTextureMap::new, error -> {
            if (json instanceof JsonObject object) {
                var textures = new ConnectedTextureMap();
                for (Direction direction : directions) {
                    if (object.has(direction.getName()) || object.has(DEFAULT_KEY)) {
                        var entry = object.has(direction.getName()) ? object.get(direction.getName()) : object.get(DEFAULT_KEY);
                        textures.quads.put(
                                direction,
                                resolve(materials, entry).getOrThrow(JsonSyntaxException::new)
                        );
                    } else {
                        throw new JsonSyntaxException("Missing CTM provider for direction " + direction.getName() + " and no default provided or legacy found");
                    }
                }
                return textures;
            }
            throw new JsonSyntaxException("Not a valid CTM provider: " + json);
        });
    }

    private static DataResult<CtmProvider> resolve(MaterialStorage materials, JsonElement json) {
        if (json instanceof JsonObject object) {
            if (object.has("center") || object.has("vertical") || object.has("horizontal") || object.has("empty")) {
                return DataResult.success(new FourSliceCtmProvider(
                        materials.put(GsonHelper.getAsString(object, "particle")),
                        materials.put(GsonHelper.getAsString(object, "center")),
                        materials.put(GsonHelper.getAsString(object, "vertical")),
                        materials.put(GsonHelper.getAsString(object, "horizontal")),
                        materials.put(GsonHelper.getAsString(object, "empty"))
                ));
            }
        } else if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isString()) {
            var texture = json.getAsString();
            if (texture.contains(SLICE_47_KEY)) {
                int[] sprites = new int[47];
                for (int i = 0; i < sprites.length; i++) {
                    sprites[i] = materials.put(texture.replace(SLICE_47_KEY, String.valueOf(i)));
                }
                return DataResult.success(new FourtySevenSliceCtmProvider(sprites));
            } else {
                return DataResult.success(new SingleSpriteCtmProvider(materials.put(texture)));
            }
        }

        return DataResult.error(() -> "Not a valid CTM provider: " + json);
    }
}
