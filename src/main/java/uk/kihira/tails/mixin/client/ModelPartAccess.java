package uk.kihira.tails.mixin.client;

import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.model.geom.ModelPart;

@Mixin(ModelPart.class)
public interface ModelPartAccess {

	@Accessor("children")
	public Map<String, ModelPart> tails$children();
}