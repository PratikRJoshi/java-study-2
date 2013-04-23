public class BurrowsWheeler {

	// apply Burrows-Wheeler encoding, reading from standard input and writing
	// to standard output
	public static void encode() {
		String s = BinaryStdIn.readString();
		int n = s.length();
		CircularSuffixArray csa = new CircularSuffixArray(s);
		BinaryStdOut.write(findFirst(csa));
		for (int i = 0; i < csa.length(); i++)
			BinaryStdOut.write(s.charAt((csa.index(i) + n - 1) % n));
		BinaryStdOut.close();
	}

	// Find the location in the CircularSuffixArray sorted order of the original
	// string
	private static int findFirst(CircularSuffixArray csa) {
		for (int i = 0; i < csa.length(); i++)
			if (csa.index(i) == 0)
				return i;
		throw new IllegalArgumentException("Couldn't find first");
	}

	// apply Burrows-Wheeler decoding, reading from standard input and writing
	// to standard output
	public static void decode();

	// if args[0] is '-', apply Burrows-Wheeler encoding
	// if args[0] is '+', apply Burrows-Wheeler decoding
	public static void main(String[] args);
}
