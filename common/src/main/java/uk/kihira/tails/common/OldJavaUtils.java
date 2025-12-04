/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2025 EnderTurret
 *
 * See LICENSE for full License
 */

package uk.kihira.tails.common;

public class OldJavaUtils {

	public static int compare3IntArray(int[] a, int[] b) {
		int tmp = Integer.compare(a[0], b[0]);
		if (tmp != 0) return tmp;

		tmp = Integer.compare(a[1], b[1]);
		if (tmp != 0) return tmp;

		tmp = Integer.compare(a[2], b[2]);
		if (tmp != 0) return tmp;

		return 0;
	}
}