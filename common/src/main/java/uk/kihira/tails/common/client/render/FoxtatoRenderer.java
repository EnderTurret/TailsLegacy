package uk.kihira.tails.common.client.render;

import java.util.ArrayList;
import java.util.List;

import org.jetbrains.annotations.ApiStatus.Internal;

import uk.kihira.tails.common.TailsMath;
import uk.kihira.tails.common.client.duck.FakeTailsEntity;
import uk.kihira.tails.common.client.duck.TailsBufferSource;
import uk.kihira.tails.common.client.duck.TailsEntity;
import uk.kihira.tails.common.client.duck.TailsPoseStack;
import uk.kihira.tails.common.client.part.ClientPartInfo;
import uk.kihira.tails.common.client.part.PartRegistry;

@Internal
public final class FoxtatoRenderer {

	private final List<ClientPartInfo> parts;

	public FoxtatoRenderer(List<ClientPartInfo> parts) {
		this.parts = parts;
	}

	public FoxtatoRenderer() {
		this(new ArrayList<>());
		parts.add(new ClientPartInfo(new int[]{-5480951, -6594259, -5197647}, PartRegistry.FLUFFY_TAIL));
		parts.add(new ClientPartInfo(new int[]{-5480951, 0xFF000000, -5197647}, PartRegistry.FOX_EARS));
	}

	public void render(TailsPoseStack poseStack, TailsBufferSource buffers, int x, int y, int z, float partialTicks, int packedLight, int packedOverlay) {
		final TailsEntity fakeEntity = FakeTailsEntity.getInstance();

		poseStack.t$push();

		poseStack.t$scale(0.5F, 0.5F, 0.5F);

		poseStack.t$translate(0, 2F, 0.2F);

		// TODO: Rework to allow additional parts.
		parts.get(0).getRenderer().render(poseStack, fakeEntity, null, parts.get(0), buffers, x, y, z, partialTicks, packedLight, packedOverlay, 0xFF);

		poseStack.t$translate(0, -0.7, -0.3F);
		poseStack.t$rotateY(TailsMath.PI);

		parts.get(1).getRenderer().render(poseStack, fakeEntity, null, parts.get(1), buffers, x, y, z, partialTicks, packedLight, packedOverlay, 0xFF);

		poseStack.t$pop();
	}
}