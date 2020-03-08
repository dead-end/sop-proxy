package de.dead.end.sop.proxy;

/**
 * 
 * @author dead-end
 *
 */
public class ParserException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * The constructor is called with the line and the line number which causes that
	 * problem.
	 */
	public ParserException(final int count, final String line, final String format, Object... args) {
		super(String.format("line: %d '%s' - %s", count, line, String.format(format, args)));
	}
}
