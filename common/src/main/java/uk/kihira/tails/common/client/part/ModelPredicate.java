package uk.kihira.tails.common.client.part;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

import uk.kihira.tails.common.client.duck.TailsModelPart;

public interface ModelPredicate {

	public static final ModelPredicate TRUE = (root, part) -> true;
	public static final ModelPredicate FALSE = (root, part) -> false;

	public boolean test(TailsModelPart root, TailsModelPart part);

	public static final class SinglePath implements ModelPredicate {

		private final TailsModelPart allowed;

		public SinglePath(TailsModelPart root, PartPath path) {
			allowed = path.traverse(root);
		}

		@Override
		public boolean test(TailsModelPart root, TailsModelPart part) {
			return allowed == part;
		}
	}

	public static final class MultiPath implements ModelPredicate {

		private final Set<TailsModelPart> allowed = Collections.newSetFromMap(new IdentityHashMap<>());

		public MultiPath(TailsModelPart root, PartPath... paths) {
			for (PartPath path : paths)
				allowed.add(path.traverse(root));
		}

		@Override
		public boolean test(TailsModelPart root, TailsModelPart part) {
			return allowed.contains(part);
		}
	}
}