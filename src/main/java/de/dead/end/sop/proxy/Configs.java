package de.dead.end.sop.proxy;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * 
 * @author dead-end
 *
 */
public class Configs {

	private final Properties props;

	/**
	 * The constructor loads the configurations from a property file.
	 */
	public Configs(final String filename) throws IOException {

		try (final InputStream is = new FileInputStream(filename)) {
			this.props = new Properties();
			this.props.load(is);
		}
	}

	/**
	 * The method returns a String property from the configuration file.
	 */
	public String getString(final String key) {
		ensure(key);

		return this.props.getProperty(key);
	}

	/**
	 * The method returns an int property from the configuration file.
	 */
	public int getInt(final String key) {
		ensure(key);

		return Integer.parseInt(this.props.getProperty(key));
	}

	/**
	 * The method returns a Path property from the configuration file.
	 */
	public Path getPath(final String key) {
		ensure(key);

		return Paths.get(this.props.getProperty(key));
	}

	/**
	 * The method returns a context property from the configuration file. A context
	 * has to start with a '/'.
	 */
	public String getContext(final String key) {
		ensure(key);

		final String ctx = this.props.getProperty(key);
		if (ctx.startsWith("/")) {
			return ctx;
		}

		return "/" + ctx;
	}

	/**
	 * The method ensures that the properties file contains a given key.
	 */
	private void ensure(final String key) {

		if (!props.containsKey(key)) {
			throw new IllegalArgumentException(String.format("Key not found: %s", key));
		}
	}
}
