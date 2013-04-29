public class CircularSuffixArray {
	private static final int R = 256, CUTOFF = 15;
	private final int n;
	private int[] order;

	// circular suffix array of s
	public CircularSuffixArray(String s) {
		n = s.length();
		order = new int[n];
		for (int i = 0; i < n; i++)
			order[i] = i;
		sort(s, 0, n - 1, 0);
	}

	// length of s
	public int length() { return n; }

	// returns index of ith sorted suffix
	public int index(int i) { return order[i]; }

	private char charAt(String s, int suffix, int offset) {
		return s.charAt((suffix + offset) % n);
	}

	// 3-way String Quicksort circular suffixes of string s from lo to hi
	// starting at index offset. Code adapted from
	// http://algs4.cs.princeton.edu/51radix/Quick3string.java.html
	private void sort(String s, int lo, int hi, int offset) {
		if (hi - lo <= CUTOFF) {
			insertion(s, lo, hi, offset);
			return;
		}
		int lt = lo, gt = hi, piv = charAt(s, order[lo], offset), eq = lo + 1;
		while (eq <= gt) {
			int t = charAt(s, order[eq], offset);
			if      (t < piv) exch(lt++, eq++);
			else if (t > piv) exch(eq, gt--);
			else              eq++;
		}
		sort(s, lo, lt - 1, offset);
		if (piv >= 0)
			sort(s, lt, gt, offset + 1);
		sort(s, gt + 1, hi, offset);
	}

	private void exch(int i, int j) {
		int tmp = order[i];
		order[i] = order[j];
		order[j] = tmp;
	}

	// Insertion sort starting at index offset. Code adapted from
	// http://algs4.cs.princeton.edu/51radix/Quick3string.java.html
	private void insertion(String s, int lo, int hi, int offset) {
		for (int i = lo; i <= hi; i++)
			for (int j = i; j > lo && less(s, j, j - 1, offset); j--)
				exch(j, j - 1);
	}

	// Is suffix i less than suffix j, starting at offset
	private boolean less(String s, int i, int j, int offset) {
		int oi = order[i], oj = order[j];
		for (; offset < n; offset++) {
			int ival = charAt(s, oi, offset), jval = charAt(s, oj, offset);
			if (ival < jval)
				return true;
			else if (ival > jval)
				return false;
		}
		return false;
	}

	public static void main(String[] args) {
		int SCREEN_WIDTH = 80;
		String s = args[0];
		int n = s.length();
		StdOut.printf("String length: %d\n", n);
		CircularSuffixArray csa = new CircularSuffixArray(s);
		for (int i = 0; i < n; i++) {
			StdOut.printf("%3d  ", i);
			for (int j = 0; j < (SCREEN_WIDTH - 5) && j < n; j++)
				StdOut.print(s.charAt((j + csa.index(i)) % n));
			StdOut.println();
		}
	}
}
