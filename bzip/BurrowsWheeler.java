public class BurrowsWheeler {
	private static final int R = 256; // Radix of a byte.

	// apply Burrows-Wheeler encoding, reading from standard input and writing
	// to standard output
	public static void encode() {
		String s = BinaryStdIn.readString();
		int n = s.length();
		CircularSuffixArray csa = new CircularSuffixArray(s);
		int first;
		for (first = 0; i < csa.length(); i++)
			if (csa.index(i) == 0)
				break;
		BinaryStdOut.write(first);
		for (int i = 0; i < csa.length(); i++)
			BinaryStdOut.write(s.charAt((csa.index(i) + n - 1) % n));
		BinaryStdOut.close();
	}

	// apply Burrows-Wheeler decoding, reading from standard input and writing
	// to standard output
	// Use index counting to find chars in the first column of the CSA. Then
	// iterate through the string in the order in which chars come from to
	// emulate ordering the last column of the CSA into the row given by
	// <code>first</code>.
	public static void decode() {
		int first = BinaryStdIn.readInt();
		String t = BinaryStdIn.readString();
		int[] count = new int[R + 1], next = new int[t.length()];
		for (int i = 0; i < t.length(); i++)
			count[t.charAt(i) + 1]++;
		for (int i = 1; i < R + 1; i++)
			count[i] += count[i - 1];
		for (int i = 0; i < t.length(); i++)
			next[count[t.charAt(i)]++] = i;
		for (int i = next[first]; i != first; i = next[i])
			BinaryStdOut.write(t.charAt(i));
		BinaryStdOut.write(t.charAt(first));
		BinaryStdOut.close();
	}

	// if args[0] is '-', apply Burrows-Wheeler encoding
	// if args[0] is '+', apply Burrows-Wheeler decoding
	public static void main(String[] args) {
		if (args.length != 1)
			throw new IllegalArgumentException("Expected + or -\n");
		else if (args[0].equals("+"))
			decode();
		else if (args[0].equals("-"))
			encode();
		else {
			String msg = "Unknown argument: " + args[0] + "\n";
			throw new IllegalArgumentException(msg);
		}
	}
}
