package uk.kihira.tails.client.model.tail;

import java.util.List;

import com.mojang.math.Vector3f;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

import uk.kihira.tails.client.model.PartConfiguration;
import uk.kihira.tails.client.model.PartModel;
import uk.kihira.tails.client.render.RenderContext;
import uk.kihira.tails.client.render.part.PartRenderer;

class BeeAbdomenModel extends PartModel {

	private final ModelPart root;
	private final ModelPart abdomen;
	private final ModelPart stinger;

	public BeeAbdomenModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		final PartDefinition abdomenDef = rootDef.addOrReplaceChild("abdomen", CubeListBuilder.create()
				.texOffs(0, 18).addBox(-1F, -6F, -9.5F, 2F, 2F, 2F)
				.texOffs(12, 12).addBox(-2F, -7F, -7.5F, 4F, 4F, 2F)
				.texOffs(0, 12).addBox(-2F, -7F, 0.5F, 4F, 4F, 2F)
				.texOffs(0, 0).addBox(-3F, -8F, -5.5F, 6F, 6F, 6F)
				, PartPose.offsetAndRotation(0F, 23F - 15, -5F + 7.2F, -0.8727F, 0F, 0F));

		abdomenDef.addOrReplaceChild("stinger", CubeListBuilder.create()
				.texOffs(0, 0).addBox(0F, -6F, 2.5F, 0F, 2F, 2F), PartPose.ZERO);

		root = rootDef.bake(32, 32);
		abdomen = root.getChild("abdomen");
		stinger = abdomen.getChild("stinger");

		config = new PartConfiguration(abdomen, List.of(abdomen), (info, poseStack, partialTick, entity) -> {
			poseStack.scale(1.25f, 1.25f, 1.25f);
		});
	}

	@Override
	public void setupPartPreviewAnim(RenderContext ctx, PartRenderer renderer) {
		ctx.poseStack().translate(0.15, 0.2, 0);
		ctx.poseStack().mulPose(Vector3f.YP.rotationDegrees(-35));
	}

	@Override
	public void render(RenderContext ctx) {
		stinger.visible = ctx.info().getSubType().id().equals("with_stinger");

		ctx.poseStack().pushPose();

		ctx.poseStack().scale(1.25f, 1.25f, 1.25f);

		ctx.render(root);

		ctx.poseStack().popPose();
	}
}