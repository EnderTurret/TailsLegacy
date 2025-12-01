package uk.kihira.tails.client;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.PartDefinition;

import uk.kihira.tails.common2.client.TailsClientPlatform;
import uk.kihira.tails.common2.client.duck.TailsModelPart;
import uk.kihira.tails.common2.client.model.TailsCubeDefinition;
import uk.kihira.tails.common2.client.model.TailsPartDefinition;
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
				cube.mirror, 1, 1, cube.visibleFaces);
	}
}