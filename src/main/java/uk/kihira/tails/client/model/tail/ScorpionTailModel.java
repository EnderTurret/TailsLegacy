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

public class ScorpionTailModel extends PartModel {

	private final ModelPart root;

	public ScorpionTailModel() {
		final PartDefinition rootDef = new MeshDefinition().getRoot();

		rootDef.addOrReplaceChild("tail", CubeListBuilder.create()
				.texOffs(0, 14).addBox(-1F, -4F, -8F, 2F, 2F, 2F)
				.texOffs(11, 11).addBox(-2F, -5F, -6F, 4F, 3F, 3F)
				.texOffs(11, 5).addBox(-2F, -6F, -3F, 4F, 3F, 3F)
				.texOffs(0, 0).addBox(-2F, -11F, -2F, 4F, 5F, 3F)
				.texOffs(0, 8).addBox(-2F, -14F, -3F, 4F, 3F, 3F)
				.texOffs(14, 0).addBox(-2F, -15F, -5F, 4F, 3F, 2F)
				.texOffs(13, 17).addBox(-1F, -14F, -6F, 2F, 2F, 1F)
				.texOffs(7, 17).addBox(-1F, -13F, -7F, 2F, 2F, 1F)
				, PartPose.offset(0F, 24F - 21, 0F + 8));

		root = rootDef.bake(32, 32);

		config = new PartConfiguration(root, List.of(root), (info, poseStack, partialTick, entity) -> {
			poseStack.scale(1.1f, 1.1f, 1.1f);
		});
	}

	@Override
	public void setupPartPreviewAnim(RenderContext ctx, PartRenderer renderer) {
		ctx.poseStack().translate(-0.3, 0.8, 0);
		ctx.poseStack().mulPose(Vector3f.YP.rotationDegrees(140));
	}

	@Override
	public void render(RenderContext ctx) {
		ctx.poseStack().pushPose();

		ctx.poseStack().scale(1.1f, 1.1f, 1.1f);

		ctx.render(root);

		ctx.poseStack().popPose();
	}
}