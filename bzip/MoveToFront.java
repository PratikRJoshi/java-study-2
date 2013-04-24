public class MoveToFront {
	private static final int R = 256;

	// apply move-to-front encoding, reading from standard input and writing to
	// standard output
	public static void encode() {
		Node first = radixList(), prev, oldfirst;
		while (!BinaryStdIn.isEmpty()) {
			char ch = BinaryStdIn.readChar(), count = 1;
			if (first.value != ch) {
				for (prev = oldfirst = first; prev.next.value != ch; count++)
					prev = prev.next;
				first = prev.next;
				prev.next = prev.next.next;
				first.next = oldfirst;
			}
			BinaryStdOut.write(count);
		}
		BinaryStdOut.close();
	}

	// apply move-to-front decoding, reading from standard input and writing to
	// standard output
	public static void decode() {
		Node first = radixList(), prev, oldfirst;
		while (!BinaryStdIn.isEmpty()) {
			char ch = BinaryStdIn.readChar();
			if (ch != 0) {
				prev = oldfirst = first;
				for (char count = 1; count < ch; count++)
					prev = prev.next;
				first = prev.next; // prev.next != null because count < ch < R.
				prev.next = prev.next.next;
				first.next = oldfirst;
			}
			BinaryStdOut.write(first.value);
		}
		BinaryStdOut.close();
	}

	private static class Node {
		private char value;
		private Node next;
		private Node(char i) { value = i; }
	}

	// Return a linked list of elements of language with radix R in order.
	private static Node radixList() {
		Node first = new Node('\0'), last = first;
		for (char i = 1; i < R; i++) {
			last.next = new Node(i);
			last = last.next;
		}
		return first;
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
