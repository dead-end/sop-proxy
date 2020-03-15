package de.dead.end.sop.proxy;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.PathResource;

/**
 * 
 * @author dead-end
 *
 */
public class SopProxy {

	/**
	 * The method checks if the system properties for the trust store are set. If
	 * not and there is a configuration in the properties file, they are set. We do
	 * not overwrite system properties.
	 */
	private void loadTrustStore(final Configs configs) {

		//
		// Trust store file
		//
		if (System.getProperty(Constants.SYS_TRUST_STORE_FILE) == null) {
			final String store = configs.getString(Constants.CFG_TRUST_STORE_FILE);
			if (!"".equals(store)) {
				System.setProperty(Constants.SYS_TRUST_STORE_FILE, store);
			}
		}

		//
		// Trust store password.
		//
		if (System.getProperty(Constants.SYS_TRUST_STORE_PWD) == null) {
			final String pwd = configs.getString(Constants.CFG_TRUST_STORE_PWD);
			if (!"".equals(pwd)) {
				System.setProperty(Constants.SYS_TRUST_STORE_PWD, pwd);
			}
		}
	}

	/**
	 * The method creates a proxy servlet and a servlet holder for the host.
	 */
	private ServletHolder getProxyServletHolder(final String host, final String prefix, final boolean trustAll) {

		final boolean useSsl = host.startsWith(Constants.HTTPS_PREFIX);

		//
		// From the doc:
		//
		// Holds the name, params and some state of a javax.servlet.Servlet instance. It
		// implements the ServletConfig interface. This class will organize the loading
		// of the servlet when needed or requested.
		//
		final ServletHolder servletHolder = new ServletHolder(new SopProxyServlet(useSsl, trustAll));

		servletHolder.setInitParameter(Constants.KEY_PROXY_TO, host);
		servletHolder.setInitParameter(Constants.KEY_PREFIX, prefix);

		return servletHolder;
	}

	/**
	 * The method adds the proxy target servlets to the servlet context handler.
	 */
	private void addProxyServlets(final ServletContextHandler servletContextHandler, final Configs configs, final Map<String, ProxyTarget> proxyTargets) throws IOException {

		final String proxyCtx = configs.getContext(Constants.CFG_PROXY_CTX);

		final boolean trustAll = configs.getBoolean(Constants.CFG_TRUST_ALL);

		for (final ProxyTarget target : proxyTargets.values()) {

			final String prefix = String.format("%s/%s", proxyCtx, target.getId());

			final String context = String.format("%s/%s/*", proxyCtx, target.getId());

			final ServletHolder servletHolder = getProxyServletHolder(target.getUrl(), prefix, trustAll);

			servletContextHandler.addServlet(servletHolder, context);
		}
	}

	/**
	 * The method adds theconfig servlet to the servlet context handler.
	 */
	private void addConfigServlet(final ServletContextHandler servletContextHandler, final Configs configs, final Map<String, ProxyTarget> proxyTargets) {

		final String context = String.format("%s/*", configs.getContext(Constants.CFG_CONFIG_CTX));

		final ServletHolder servletHolder = new ServletHolder(new ConfigServlet(proxyTargets));

		servletContextHandler.addServlet(servletHolder, context);
	}

	/**
	 * The method creates a context handler for the config servlet and the proxy
	 * servlets.
	 */
	private ContextHandler getServletHandler(final Configs configs) throws Exception {

		//
		// Read the csv file with the target proxy configurations.
		//
		final Map<String, ProxyTarget> proxyTargets = new ProxyTargetReader().process(configs);

		//
		// Create the servlet context handler
		//
		final ServletContextHandler servletContextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);

		servletContextHandler.setContextPath("/");

		addConfigServlet(servletContextHandler, configs, proxyTargets);
		addProxyServlets(servletContextHandler, configs, proxyTargets);

		return servletContextHandler;
	}

	/**
	 * The method returns a context handler for the resource handler of the app.
	 */
	private ContextHandler getAppHandler(final Configs configs) {

		final ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(true);
		resourceHandler.setWelcomeFiles(new String[] { "index.html" });

		final Path appBaseDir = configs.getPath(Constants.CFG_APP_PATH);

		final ContextHandler ctxHandler = new ContextHandler();
		ctxHandler.setContextPath(configs.getContext(Constants.CFG_APP_CTX));
		ctxHandler.setBaseResource(new PathResource(appBaseDir));
		ctxHandler.setHandler(resourceHandler);

		return ctxHandler;
	}

	/**
	 * The method does the processing of the application.
	 */
	public void process(final Configs configs) throws Exception {

		//
		// The context handler collection is filled with the context handlers.
		//
		final ContextHandlerCollection ctxHandlers = new ContextHandlerCollection();

		ctxHandlers.addHandler(getAppHandler(configs));
		ctxHandlers.addHandler(getServletHandler(configs));

		//
		// Create the server at the port
		//
		final Server server = new Server(configs.getInt(Constants.CFG_PORT));

		server.setHandler(ctxHandlers);

		server.start();
	}

	/**
	 * The main method checks the args (which is the properties file) and starts the
	 * application.
	 */
	public static void main(final String[] args) throws Exception {

		if (args.length != 1) {
			System.err.println("Usage: SopProxy <properties-file>");
			System.exit(1);
		}

		final Configs configs = new Configs(args[0]);

		final SopProxy sopProxy = new SopProxy();

		sopProxy.loadTrustStore(configs);
		sopProxy.process(configs);
	}
}
