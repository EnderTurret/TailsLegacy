/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.neoforge.common.platform;

import net.minecraft.util.RandomSource;

import net.enderturret.tailslegacy.common.client.duck.TailsRandomSource;

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