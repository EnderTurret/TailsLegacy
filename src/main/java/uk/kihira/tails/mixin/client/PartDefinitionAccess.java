package uk.kihira.tails.mixin.client;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

@Mixin(PartDefinition.class)
public interface PartDefinitionAccess {

	@Invoker("<init>")
	public static PartDefinition tails$new(List<CubeDefinition> cubes, PartPose partPose) {
		return null;
	}

	@Accessor("cubes")
	public List<CubeDefinition> tails$cubes();

	@Accessor("partPose")
	public PartPose tails$partPose();

	@Accessor("children")
	public Map<String, PartDefinition> tails$children();
}