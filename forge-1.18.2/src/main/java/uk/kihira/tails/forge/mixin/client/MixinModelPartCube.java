package uk.kihira.tails.forge.mixin.client;

import java.util.Collection;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.core.Direction;

import uk.kihira.tails.forge.client.render.ModelPartCubeExtensions;

@Mixin(ModelPart.Cube.class)
public abstract class MixinModelPartCube implements ModelPartCubeExtensions {

	@Shadow
	@Final
	@Mutable
	private ModelPart.Polygon[] polygons;

	@Override
	public void tails$setHiddenFaces(Collection<Direction> faces) {
		if (polygons.length != 6) throw new IllegalStateException("Already set hidden faces!");
		if (faces.isEmpty()) return;

		final ModelPart.Polygon[] array = new ModelPart.Polygon[6 - faces.size()];
		int filled = 0;
		for (int i = 0; i < 6; i++) {
			final Direction dir = switch (i) {
				case 0 -> Direction.EAST;
				case 1 -> Direction.WEST;
				case 2 -> Direction.DOWN;
				case 3 -> Direction.UP;
				case 4 -> Direction.NORTH;
				case 5 -> Direction.SOUTH;
				default -> null;
			};
			if (faces.contains(dir)) continue;
			array[filled++] = polygons[i];
		}

		polygons = array;
	}
}