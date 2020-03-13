package de.dead.end.sop.proxy;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author dead-end
 *
 */
public class ConfigServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private final static String SELECT_LIST = "/list";

	private final static String SELECT_GET = "/get/";

	private final Map<String, ProxyTarget> proxyTargets;

	/**
	 * The servlet is created with a proxy target map.
	 */
	public ConfigServlet(final Map<String, ProxyTarget> proxyTargets) {
		this.proxyTargets = proxyTargets;
	}

	/**
	 * The method processes get requests and returns json objects as an response.
	 */
	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {

		final String path = request.getPathInfo();

		if (path == null || "".equals(path) || "/".equals(path)) {
			writeStatus(response, HttpServletResponse.SC_OK, "Ready to run!");
		}

		else if (SELECT_LIST.equals(path)) {
			processList(response, proxyTargets);
		}

		else if (path.startsWith(SELECT_GET)) {
			processTarget(response, path, proxyTargets);

		} else {
			writeStatus(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, String.format("Unknown selector: %s", path));
		}
	}

	/**
	 * The method extracts the proxy target id from the request, and tries to get
	 * the object from the proxy target map. If the proxy target was found it will
	 * be returned as a json object. Otherwise the method creates a status object
	 * with an error message.
	 */
	private void processTarget(final HttpServletResponse response, final String path, final Map<String, ProxyTarget> proxyTargets) throws IOException {

		final String id = path.substring(SELECT_GET.length());
		final ProxyTarget proxyTarget = proxyTargets.get(id);

		if (proxyTarget == null) {
			writeStatus(response, HttpServletResponse.SC_NOT_FOUND, String.format("Unknown proxy id: %s", id));
		} else {
			writeJsonResult(response, HttpServletResponse.SC_OK, proxyTarget.toJsonObjectBuilder().build().toString());
		}
	}

	/**
	 * The method creates a list of the proxy targets as a json array and writes the
	 * result to the client.
	 */
	private void processList(final HttpServletResponse response, final Map<String, ProxyTarget> proxyTargets) throws IOException {

		final JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();

		for (final Entry<String, ProxyTarget> entry : proxyTargets.entrySet()) {
			arrayBuilder.add(entry.getValue().toJsonObjectBuilder());
		}

		writeJsonResult(response, HttpServletResponse.SC_OK, arrayBuilder.build().toString());
	}

	/**
	 * The method creates a json object for the status and writes the result to the
	 * client.
	 */
	private void writeStatus(final HttpServletResponse response, final int status, final String msg) throws IOException {

		final JsonObjectBuilder builder = Json.createObjectBuilder();

		builder.add("status", getStatus(status));
		builder.add("msg", msg);

		writeJsonResult(response, status, builder.build().toString());
	}

	/**
	 * The method writes the json result to the response.
	 */
	private void writeJsonResult(final HttpServletResponse response, final int status, final String json) throws IOException {

		response.setStatus(status);

		response.setContentType("text/json");
		response.setCharacterEncoding("utf-8");

		response.getWriter().append(json);
	}

	/**
	 * The method returns a string representation of the status.
	 */
	private String getStatus(final int status) {

		if (status == HttpServletResponse.SC_OK) {
			return "OK";
		}

		if (status == HttpServletResponse.SC_INTERNAL_SERVER_ERROR) {
			return "ERROR";
		}

		if (status == HttpServletResponse.SC_NOT_FOUND) {
			return "NOT-FOUND";
		}

		return String.format("UNKNOWN-%d", status);
	}
}