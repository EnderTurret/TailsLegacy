package uk.kihira.tails.common.client.duck;

import java.util.Random;

public interface TailsRandomSource {

	public int t$nextInt(int bound);
	public float t$nextFloat();

	public Object t$unwrap();

	public static final class Java implements TailsRandomSource {

		private final Random rand;

		public Java(Random rand) {
			this.rand = rand;
		}

		@Override
		public int t$nextInt(int bound) { return rand.nextInt(bound); }

		@Override
		public float t$nextFloat() { return rand.nextFloat(); }

		@Override
		public Object t$unwrap() {
			return rand;
		}
	}
}