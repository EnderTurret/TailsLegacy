package uk.kihira.tails.client;

import java.util.EnumSet;
import java.util.stream.Collectors;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import net.neoforged.fml.ModLoader;

import uk.kihira.tails.client.api.RegisterPartRenderersEvent;
import uk.kihira.tails.client.texture.TripleTintTexture;
import uk.kihira.tails.common.ResourceManagerWrapperImpl;
import uk.kihira.tails.common2.client.TailsClientPlatform;
import uk.kihira.tails.common2.client.api.PartRendererRegistrar;
import uk.kihira.tails.common2.client.duck.TResourceLocation;
import uk.kihira.tails.common2.client.duck.TailsModelPart;
import uk.kihira.tails.common2.client.model.TailsCubeDefinition;
import uk.kihira.tails.common2.client.model.TailsPartDefinition;
import uk.kihira.tails.common2.client.part.Part;
import uk.kihira.tails.common2.client.part.PartRegistry;
import uk.kihira.tails.mixin.client.CubeDefinitionAccess;
import uk.kihira.tails.mixin.client.PartDefinitionAccess;

public final class TailsClientPlatformImpl implements TailsClientPlatform {

	@Override
	public TailsModelPart bake(TailsPartDefinition part, int textureWidth, int textureHeight) {
		return (TailsModelPart) (Object) makePartDefinition(part).bake(textureWidth, textureHeight);
	}

	private static PartDefinition makePartDefinition(TailsPartDefinition part) {
		final PartDefinition ret = PartDefinitionAccess.tails$new(
				part.cubes.stream().map(TailsClientPlatformImpl::makeCubeDefinition).toList(),
				PartPose.offsetAndRotation(part.xOffset, part.yOffset, part.zOffset, part.xRot, part.yRot, part.zRot));

		final PartDefinitionAccess access = (PartDefinitionAccess) ret;

		// Recursion :concern:
		for (var entry : part.children.entrySet())
			access.tails$children().put(entry.getKey(), makePartDefinition(entry.getValue()));

		return ret;
	}

	private static CubeDefinition makeCubeDefinition(TailsCubeDefinition cube) {
		return CubeDefinitionAccess.tails$new(null,
				cube.u, cube.v,
				cube.x, cube.y, cube.z,
				cube.sizeX, cube.sizeY, cube.sizeZ,
				new CubeDeformation(cube.growX, cube.growY, cube.growZ),
				cube.mirror, 1, 1, cube.visibleFaces.stream()
				.map(dir -> Direction.values()[dir.ordinal()])
				.collect(Collectors.toCollection(() -> EnumSet.noneOf(Direction.class))));
	}

	@Override
	public boolean hasTexture(TResourceLocation id) {
		return Minecraft.getInstance().getTextureManager()
				.getTexture((ResourceLocation) (Object) id, MissingTextureAtlasSprite.getTexture()) != MissingTextureAtlasSprite.getTexture();
	}

	@Override
	public void registerTripleTintTexture(TResourceLocation id, Part part, Part.SubType subType, Part.PartTexture texture, int[] tints) {
		Minecraft.getInstance().getTextureManager().register((ResourceLocation) (Object) id, new TripleTintTexture(
				(ResourceLocation) (Object) part.getId().t$withPath(texture.path()),
				tints[0], tints[1], tints[2], texture.tintingStrategy()
				));
	}

	@Override
	public void releaseTexture(TResourceLocation id) {
		try {
			Minecraft.getInstance().getTextureManager().release((ResourceLocation) (Object) id);
		} catch (Exception ignored) {}
	}

	public static void reloadParts(ResourceManager manager) {
		PartRegistry.MANAGER.reload(new ResourceManagerWrapperImpl(manager));
	}

	@Override
	public void fireRegisterPartRenderersEvent(PartRendererRegistrar registrar) {
		ModLoader.postEvent(new RegisterPartRenderersEvent(registrar));
	}
}