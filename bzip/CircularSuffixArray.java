public class CircularSuffixArray {
	private static final int R = 256;
	private final int n;
	private int[] order;

	// circular suffix array of s
	public CircularSuffixArray(String s) {
		n = s.length();
		order = new int[n];
		sort(s);
	}

	// length of s
	public int length() { return n; }

	// returns index of ith sorted suffix
	public int index(int i) { return order[i]; }

	// LSD Radix sort the circular suffixes of s
	private void sort(String s) {
		int[] count = new int[R + 1], aux = new int[n];
		for (int d = 0; d < n; d++)
			order[d] = d;
		for (int d = 0; d < n; d++) {
			for (int i = 0; i < R + 1; i++)
				count[i] = 0;
			for (int i = 0; i < R + 1; i++)
				count[s.charAt((d + order[i]) % n) + 1]++;
			for (int i = 1; i < R + 1; i++)
				count[i] += count[i - 1];
			for (int i = 0; i < n; i++)
				aux[count[s.charAt((d + order[i]) % n)]++] = order[i];
			for (int i = 0; i < n; i++)
				order[i] = aux[i];
		}
	}
}
