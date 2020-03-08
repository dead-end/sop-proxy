package de.dead.end.sop.proxy;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 
 * @author dead-end
 *
 */
public class ProxyTargetReader {

	private final Pattern patternId = Pattern.compile("^[-_a-zA-Z0-9]*$");

	/**
	 * The method parses the csv file and creates a map with proxy targets.
	 */
	public Map<String, ProxyTarget> process(final Configs configs) throws Exception {

		final String csvfile = configs.getString(Constants.CFG_CSV_FILE);

		final Map<String, ProxyTarget> proxyTargets = new HashMap<>();

		try (final BufferedReader br = new BufferedReader(new FileReader(csvfile))) {

			String[] header = null;

			final Parser parser = new Parser(configs.getString(Constants.CFG_CSV_DELIM));

			while (parser.parse(br.readLine())) {

				//
				// Process header
				//
				if (header == null) {

					header = parser.getArray();
					if (header.length < 2) {
						throw new ParserException(parser, "Not enought elements in header array: %d", header.length);
					}

					continue;
				}

				//
				// Ensure that all arrays have the same length
				//
				if (parser.getArray().length != header.length) {
					throw new ParserException(parser, "Header length: %d current length: %d", header.length, parser.getArray().length);
				}

				//
				// Create the proxy target.
				//
				final String id = getId(parser, proxyTargets);

				final String url = getUrl(parser);

				final ProxyTarget target = getProxyTarget(parser, id, url, header);

				//
				// Register the proxy target
				//
				proxyTargets.put(target.getId(), target);
			}
		}

		return proxyTargets;
	}

	/**
	 * The method creates a ProxyTarget with the id and the url. It extracts the key
	 * / value pairs from the array.
	 */
	private ProxyTarget getProxyTarget(final Parser parser, final String id, final String url, final String[] header) {

		final ProxyTarget target = new ProxyTarget(id, url);

		for (int i = 2; i < parser.getArray().length; i++) {
			target.add(header[i], parser.getArray(i));
		}

		return target;
	}

	/**
	 * The method extracts and validates the proxy target id.
	 */
	private String getId(final Parser parser, Map<String, ProxyTarget> proxyTargets) throws ParserException {

		final String id = parser.getArray(0);

		if (!patternId.matcher(id).matches()) {
			throw new ParserException(parser, "Id has invalid characters: %s", id);
		}

		if (proxyTargets.containsKey(id)) {
			throw new ParserException(parser, "Duplicate id: %s", id);
		}

		return id;
	}

	/**
	 * The method extracts and validates the proxy target url.
	 */
	private String getUrl(final Parser parser) throws ParserException {

		final String url = parser.getArray(1);

		if (!url.startsWith(Constants.HTTP_PREFIX) && !url.startsWith(Constants.HTTPS_PREFIX)) {
			throw new ParserException(parser, "Url has not protocol: %s", url);
		}

		return url;
	}
}
