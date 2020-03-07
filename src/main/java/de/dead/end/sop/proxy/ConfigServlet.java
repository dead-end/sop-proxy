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
			writeStatus(response, Status.OK, "Ready to run!");
		}

		else if (SELECT_LIST.equals(path)) {
			processList(response, proxyTargets);
		}

		else if (path.startsWith(SELECT_GET)) {
			processTarget(response, path, proxyTargets);

		} else {
			writeStatus(response, Status.ERROR, "Unknown selector : " + path);
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
			writeStatus(response, Status.NOT_FOUND, "Unknown proxy id: " + id);
		} else {
			writeResult(response, Status.OK, proxyTarget.toJsonObjectBuilder().build().toString());
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

		writeResult(response, Status.OK, arrayBuilder.build().toString());
	}

	/**
	 * The method creates a json object for the status and writes the result to the
	 * client.
	 */
	private void writeStatus(final HttpServletResponse response, final Status status, final String msg) throws IOException {

		final JsonObjectBuilder builder = Json.createObjectBuilder();

		builder.add("status", status.toString());
		builder.add("msg", msg);

		writeResult(response, status, builder.build().toString());
	}

	/**
	 * The method writes the result to the response. It maps the Status to a http
	 * status and sets the content type and encoding.
	 */
	private void writeResult(final HttpServletResponse response, final Status status, final String result) throws IOException {

		switch (status) {
		case OK:
			response.setStatus(HttpServletResponse.SC_OK);

		case ERROR:
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			break;

		case NOT_FOUND:
			response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			break;

		default:
			writeResult(response, Status.ERROR, "Unknown status: " + status);
			return;
		}

		response.setContentType("text/json");
		response.setCharacterEncoding("utf-8");

		response.getWriter().append(result);
	}

	/**
	 * 
	 * @author dead-end
	 *
	 */
	private static enum Status {
		OK, ERROR, NOT_FOUND
	}
}