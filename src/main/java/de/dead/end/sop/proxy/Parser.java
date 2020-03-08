package de.dead.end.sop.proxy;

/**
 * 
 * @author dead-end
 *
 */
public class Parser {

	//
	// The delimiter is immutable
	//
	private final String delimiter;

	private int count = 0;

	private String line;

	private String[] array;

	/**
	 * The constructor is called with the delimiter
	 */
	public Parser(final String delimiter) {
		super();
		this.delimiter = delimiter;
	}

	public int getCount() {
		return count;
	}

	public String getLine() {
		return line;
	}

	public String[] getArray() {
		return array;
	}

	/**
	 * The method does the parsing of the line into an array. It returns true if the
	 * parsing was possible.
	 */
	public boolean parse(final String line) {

		if (line == null) {
			return false;
		}

		this.line = line;

		this.count++;

		this.array = line.split(delimiter);

		//
		// Trim the strings of the array.
		//
		for (int i = 0; i < this.array.length; i++) {
			this.array[i] = this.array[i].trim();
		}

		return true;
	}
}
