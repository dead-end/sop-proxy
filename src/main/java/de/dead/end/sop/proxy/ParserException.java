package de.dead.end.sop.proxy;

/**
 * 
 * @author dead-end
 *
 */
public class ParserException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
	 * The constructor is called with the parser, which contains the line and the
	 * line number which causes that problem.
	 */
	public ParserException(final Parser parser, final String format, final Object... args) {
		super(String.format("line: %d '%s' - %s", parser.getCount(), parser.getLine(), String.format(format, args)));
	}
}
