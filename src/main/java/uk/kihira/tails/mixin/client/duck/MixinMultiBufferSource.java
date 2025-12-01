package uk.kihira.tails.mixin.client.duck;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import uk.kihira.tails.common2.client.duck.TailsBuffer;
import uk.kihira.tails.common2.client.duck.TailsBufferSource;
import uk.kihira.tails.common2.client.duck.TailsEntity;

@Mixin(MultiBufferSource.class)
@SuppressWarnings("cast")
public interface MixinMultiBufferSource extends TailsBufferSource {

	@Override
	public default TailsBuffer t$getEntityBuffer(TailsEntity entity, ResourceLocation texture) {
		final LivingEntity living = (LivingEntity) entity.t$unwrap();
		final boolean visible = !living.isInvisible();
		final boolean visibleToPlayer = !visible && !living.isInvisibleTo(Minecraft.getInstance().player);
		final boolean glowing = Minecraft.getInstance().shouldEntityAppearGlowing(living);

		final RenderType renderType;
		if (visibleToPlayer)
			renderType = RenderType.itemEntityTranslucentCull(texture);
		else if (visible)
			renderType = RenderType.entityCutoutNoCull(texture);
		else
			renderType = glowing ? RenderType.outline(texture) : null;

		return (TailsBuffer) ((MultiBufferSource) (Object) this).getBuffer(renderType);
	}
}