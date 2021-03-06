package com.grim3212.assorted.decor.client.model;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.grim3212.assorted.decor.AssortedDecor;
import com.mojang.datafixers.util.Pair;

import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.client.model.obj.OBJModel;

public class ColorizerOBJModel implements IModelGeometry<ColorizerOBJModel> {

	private final OBJModelCopy objModel;

	private ColorizerOBJModel(ResourceLocation objModelLocation) {
		this.objModel = OBJModelCopy.loadModel(defaultSettings(objModelLocation));
	}

	private OBJModel.ModelSettings defaultSettings(ResourceLocation loc) {
		return new OBJModel.ModelSettings(loc, true, true, true, true, null);
	}

	@Nonnull
	@Override
	public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
		Set<RenderMaterial> ret = new HashSet<>();
		ret.addAll(this.objModel.getTextures(owner, modelGetter, missingTextureErrors));
		return ret;
	}

	@Nullable
	@Override
	public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform transform, ItemOverrideList overrides, ResourceLocation name) {
		IBakedModel bakedColorizer = this.objModel.bake(owner, bakery, spriteGetter, transform, overrides, name);
		return new ColorizerOBJBakedModel(bakedColorizer, objModel, owner, spriteGetter.apply(owner.resolveTexture("particle")), bakery, spriteGetter, transform, overrides, name);
	}

	public enum Loader implements IModelLoader<ColorizerOBJModel> {
		INSTANCE;

		public static final ResourceLocation LOCATION = new ResourceLocation(AssortedDecor.MODID, "models/colorizer_obj");

		@Override
		public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
		}

		@Nonnull
		@Override
		public ColorizerOBJModel read(JsonDeserializationContext ctx, JsonObject modelContents) {
			if (!modelContents.has("model"))
				throw new UnsupportedOperationException("Model location not found for a ColorizerOBJModel");
			ResourceLocation model = new ResourceLocation(modelContents.get("model").getAsString());
			return new ColorizerOBJModel(model);
		}
	}
}
