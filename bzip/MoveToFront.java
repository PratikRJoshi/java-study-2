public class MoveToFront {
	// apply move-to-front encoding, reading from standard input and writing to
	// standard output
	public static void encode();

	// apply move-to-front decoding, reading from standard input and writing to
	// standard output
	public static void decode();

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
