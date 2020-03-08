package de.dead.end.sop.proxy;

/**
 * 
 * @author dead-end
 *
 */
public interface Constants {

	//
	// json-keys: the keys for the config servlet response
	//
	final static String JSON_ID = "id";

	final static String JSON_URL = "url";

	final static String JSON_DATA = "data";

	//
	// property-key: server port
	//
	final static String CFG_PORT = "port";

	//
	// property-key: csv file and delimiter
	//
	final static String CFG_CSV_FILE = "csv.file";

	final static String CFG_CSV_DELIM = "csv.delim";

	//
	// property-key: context path for the uri
	//
	final static String CFG_PROXY_CTX = "proxy.ctx";

	//
	// property-key: directory with the data and context path for the uri
	//
	final static String CFG_APP_PATH = "app.path";

	final static String CFG_APP_CTX = "app.ctx";

	//
	// property-key: context path for the uri
	//
	final static String CFG_CONFIG_CTX = "config.ctx";

	//
	// servlet-param-key: proxy target url and prefix
	//
	final static String KEY_PROXY_TO = "proxyTo";

	final static String KEY_PREFIX = "prefix";

	//
	// https protocol prefix
	//
	final static String HTTP_PREFIX = "http://";

	final static String HTTPS_PREFIX = "https://";
}
