package earth.terrarium.athena.neoforge.client;

//public class AthenaGeometryLoader implements UnbakedModelLoader<AthenaGeometryLoader.Unbaked> {
//
//    @Override
//    public @NotNull Unbaked read(@NotNull JsonObject json, @NotNull JsonDeserializationContext context) throws JsonParseException {
//        String id = GsonHelper.getAsString(json, DefaultModels.MODID + ":loader");
//        ResourceLocation loaderId = ResourceLocation.tryParse(id);
//        if (loaderId == null) throw new JsonParseException("Invalid loader id: " + id);
//        AthenaUnbakedModelLoader loader = FactoryManagerImpl.get(loaderId);
//        if (loader == null) throw new JsonParseException("Unknown loader: " + loaderId);
//        return new Unbaked(loader, json);
//    }
//
//    public record Unbaked(AthenaUnbakedModelLoader loader, JsonObject json) implements UnbakedModel {
//
//        @Override
//        public @NotNull BakedModel bake(@NotNull TextureSlots slots, @NotNull ModelBaker baker, @NotNull ModelState state, boolean bl, boolean bl2, @NotNull ItemTransforms arg4) {
//            return loader.loadModel(json).bake(baker);
//        }
//
//        @Override
//        public void resolveDependencies(@NotNull Resolver arg) {
//
//        }
//    }
//}