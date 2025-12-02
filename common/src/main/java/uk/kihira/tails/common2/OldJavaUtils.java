package uk.kihira.tails.common2;

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