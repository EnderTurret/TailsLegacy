package uk.kihira.tails.forge.mixin.client;

import java.util.Collection;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.util.EnumFacing;

import uk.kihira.tails.forge.client.render.ModelPartCubeExtensions;

@Mixin(ModelBox.class)
public abstract class MixinModelPartCube implements ModelPartCubeExtensions {

	@Shadow
	@Final
	@Mutable
	private TexturedQuad[] quadList;

	@Override
	public void tails$setHiddenFaces(Collection<EnumFacing> faces) {
		if (quadList.length != 6) throw new IllegalStateException("Already set hidden faces!");
		if (faces.isEmpty()) return;

		final TexturedQuad[] array = new TexturedQuad[6 - faces.size()];
		int filled = 0;
		for (int i = 0; i < 6; i++) {
			final EnumFacing dir;
			switch (i) {
				case 0: dir = EnumFacing.EAST; break;
				case 1: dir = EnumFacing.WEST; break;
				case 2: dir = EnumFacing.DOWN; break;
				case 3: dir = EnumFacing.UP; break;
				case 4: dir = EnumFacing.NORTH; break;
				case 5: dir = EnumFacing.SOUTH; break;
				default: dir = null; break;
			};
			if (faces.contains(dir)) continue;
			array[filled++] = quadList[i];
		}

		quadList = array;
	}
}