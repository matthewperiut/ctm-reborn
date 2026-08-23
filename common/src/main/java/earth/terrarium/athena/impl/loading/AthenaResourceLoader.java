package earth.terrarium.athena.impl.loading;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import earth.terrarium.athena.impl.client.DefaultModels;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AthenaResourceLoader extends SimpleJsonResourceReloadListener<@NotNull JsonElement> {

    private static final String KEY = DefaultModels.MODID + ":loader";
    private static final String FORK_KEY = DefaultModels.MOD_ID + ":loader";

    public static final AthenaResourceLoader INSTANCE = new AthenaResourceLoader();

    private final Map<Identifier, JsonObject> blockstateData = new ConcurrentHashMap<>();
    private final Map<Identifier, JsonElement> data = new HashMap<>();

    public AthenaResourceLoader() {
        super(ExtraCodecs.JSON, FileToIdConverter.json("athena"));
    }

    public static void clearBlockstateData() {
        INSTANCE.blockstateData.clear();
    }

    public static void addBlockstateData(Identifier stateId, JsonElement data) {
        if (!(data instanceof JsonObject object)) return;
        if (!object.has(KEY) && !object.has(FORK_KEY)) return;
        INSTANCE.blockstateData.put(stateId, object);
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> object, @NotNull ResourceManager manager, @NotNull ProfilerFiller profiler) {
        this.data.clear();
        this.data.putAll(object);
    }

    public static JsonObject getData(Identifier modelType, Identifier modelId) {
        var modelData = INSTANCE.data.get(modelId);
        if (modelData != null) {
            return checkObject(modelType, modelData);
        }
        var blockstateData = INSTANCE.blockstateData.get(modelId);
        if (blockstateData != null) {
            return checkObject(modelType, blockstateData);
        }
        return null;
    }

    private static JsonObject checkObject(Identifier modelType, JsonElement data) {
        if (data instanceof JsonObject object) {
            String type = GsonHelper.getAsString(object, KEY, GsonHelper.getAsString(object, FORK_KEY, ""));
            if (modelType.toString().equals(type)) {
                return object;
            }
        }
        return null;
    }
}
