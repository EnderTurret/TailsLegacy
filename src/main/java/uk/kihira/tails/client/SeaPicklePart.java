package uk.kihira.tails.client;

import uk.kihira.tails.common.part.Part;
import uk.kihira.tails.common.part.PartInfo;
import uk.kihira.tails.common.part.PartType;

public class SeaPicklePart extends Part {

	public SeaPicklePart(Builder builder) {
		super(builder);
	}

	@Override
	public PartInfo makeDefaultPartInfo(int subType) {
		// Red sea pickles are kind of weird, which is why we're overriding this to make all tints white.
		return new PartInfo(id, subType, 0, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, null);
	}
}