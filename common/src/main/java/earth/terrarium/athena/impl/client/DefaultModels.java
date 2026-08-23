package earth.terrarium.athena.impl.client;

import earth.terrarium.athena.api.client.models.AthenaModelType;
import earth.terrarium.athena.api.client.models.FactoryManager;
import earth.terrarium.athena.api.client.utils.ModelConnectionCondition;
import earth.terrarium.athena.impl.client.models.*;
import net.minecraft.resources.Identifier;

public class DefaultModels {

    /**
     * The namespace Athena resource packs are written against. This fork keeps reading and registering it so that
     * existing packs, and blockstate files using the {@code athena:loader} key, keep working unchanged.
     */
    public static final String MODID = "athena";

    /**
     * The id this fork is loaded under. Model types are registered under this namespace as well as {@link #MODID}.
     */
    public static final String MOD_ID = "ctm_reborn";

    private static Identifier id(String namespace, String name) {
        return Identifier.fromNamespaceAndPath(namespace, name);
    }

    private static void register(String name, AthenaModelType type) {
        FactoryManager.register(id(MODID, name), type);
        FactoryManager.register(id(MOD_ID, name), type);
    }

    public static void init() {
        register("ctm", ConnectedBlockModel.TYPE);
        register("carpet_ctm", ConnectedCarpetBlockModel.TYPE);
        register("pane_ctm", PaneConnectedBlockModel.TYPE);
        // "giant" is the original name and "mural" the current one; both stay registered so older packs keep loading.
        register("giant", GiantBlockModel.TYPE);
        register("mural", GiantBlockModel.TYPE);
        register("pillar", PillarBlockModel.TYPE);
        register("limited_pillar", LimitedPillarBlockModel.TYPE);
        register("pane_pillar", PanePillarBlockModel.TYPE);

        ModelConnectionCondition.CONDITION_TYPES.put("not", ModelConnectionCondition.Not.CODEC);
        ModelConnectionCondition.CONDITION_TYPES.put("and", ModelConnectionCondition.And.CODEC);
        ModelConnectionCondition.CONDITION_TYPES.put("or", ModelConnectionCondition.Or.CODEC);
        ModelConnectionCondition.CONDITION_TYPES.put("xor", ModelConnectionCondition.Xor.CODEC);
        ModelConnectionCondition.CONDITION_TYPES.put("state", ModelConnectionCondition.State.CODEC);
        ModelConnectionCondition.CONDITION_TYPES.put("tag", ModelConnectionCondition.Tag.CODEC);
        ModelConnectionCondition.CONDITION_TYPES.put("sameBlock", ModelConnectionCondition.SameBlock.CODEC);
        ModelConnectionCondition.CONDITION_TYPES.put("sameState", ModelConnectionCondition.SameState.CODEC);
        // Registered so that a condition that was read as never-connecting can also be written back out.
        ModelConnectionCondition.CONDITION_TYPES.put("false", ModelConnectionCondition.False.CODEC);
    }
}
