/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package net.enderturret.tailslegacy.common.client.duck;

public interface TResourceLocation {

	public String t$getNamespace();
	public String t$getPath();

	public TResourceLocation t$withPath(String path);

	public int t$compareTo(TResourceLocation other);
	public int t$compareNamespaced(TResourceLocation other);
}