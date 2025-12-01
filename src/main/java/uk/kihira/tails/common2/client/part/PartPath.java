package uk.kihira.tails.common2.client.part;

import net.minecraft.client.model.geom.ModelPart;

public final class PartPath {

	private final String[] path;

	public PartPath(String[] path) {
		this.path = path;
	}

	public PartPath(String path) {
		this(path.split("\\."));
	}

	public ModelPart traverse(ModelPart root) {
		for (String segment : path)
			root = root.getChild(segment);

		return root;
	}
}