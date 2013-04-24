public class MoveToFront {
	private static final int R = 256;

	// apply move-to-front encoding, reading from standard input and writing to
	// standard output
	public static void encode() {
		char[] chars = radixList();
		char count, ch;
		while (!BinaryStdIn.isEmpty()) {
			ch = BinaryStdIn.readChar();
			for (count = 0; ch != chars[count]; count++);
			BinaryStdOut.write(count);
			for (; count > 1; count--)
				chars[count] = chars[count - 1];
			chars[count] = ch;
		}
		BinaryStdOut.close();
	}

	// apply move-to-front decoding, reading from standard input and writing to
	// standard output
	public static void decode() {
		char[] chars = radixList();
		char count, ch;
		while (!BinaryStdIn.isEmpty()) {
			count = BinaryStdIn.readChar();
			ch = chars[count];
			BinaryStdOut.write(ch);
			for (; count > 1; count--)
				chars[count] = chars[count - 1];
			chars[count] = ch;
		}
		BinaryStdOut.close();
	}

	// Return an array list of elements of language with radix R in order.
	private static char[] radixList() {
		char[] rl = new char[R];
		for (char i = 0; i < R; rl[i] = i++);
		return rl;
	}

	// if args[0] is '-', apply move-to-front encoding
	// if args[0] is '+', apply move-to-front decoding
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
