package uk.kihira.tails.mixin.client.duck;

import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import uk.kihira.tails.common2.client.duck.TResourceLocation;
import uk.kihira.tails.common2.client.duck.TailsBuffer;
import uk.kihira.tails.common2.client.duck.TailsBufferSource;
import uk.kihira.tails.common2.client.duck.TailsEntity;

@Mixin(MultiBufferSource.class)
@SuppressWarnings("cast")
public interface MixinMultiBufferSource extends TailsBufferSource {

	@Override
	public default TailsBuffer t$getEntityBuffer(TailsEntity entity, TResourceLocation texture) {
		final LivingEntity living = (LivingEntity) entity.t$unwrap();
		final ResourceLocation tex = (ResourceLocation) (Object) texture;
		final boolean visible = !living.isInvisible();
		final boolean visibleToPlayer = !visible && !living.isInvisibleTo(Minecraft.getInstance().player);
		final boolean glowing = Minecraft.getInstance().shouldEntityAppearGlowing(living);

		final RenderType renderType;
		if (visibleToPlayer)
			renderType = RenderType.itemEntityTranslucentCull(tex);
		else if (visible)
			renderType = RenderType.entityCutoutNoCull(tex);
		else
			renderType = glowing ? RenderType.outline(tex) : null;

		return (TailsBuffer) ((MultiBufferSource) (Object) this).getBuffer(renderType);
	}
}