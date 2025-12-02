package uk.kihira.tails.neoforge.common.platform;

import net.minecraft.util.RandomSource;

import uk.kihira.tails.common.client.duck.TailsRandomSource;

public final class TailsRandomSourceImpl implements TailsRandomSource {

	private final RandomSource random;

	public TailsRandomSourceImpl(RandomSource random) {
		this.random = random;
	}

	@Override
	public int t$nextInt(int bound) { return random.nextInt(bound); }

	@Override
	public float t$nextFloat() { return random.nextFloat(); }

	@Override
	public Object t$unwrap() { return random; }
}