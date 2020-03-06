package de.dead.end.sop.proxy;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.proxy.ProxyServlet;
import org.eclipse.jetty.util.ssl.SslContextFactory;

/**
 * 
 * @author dead-end
 *
 */
public class SopProxyServlet extends ProxyServlet.Transparent {
	private static final long serialVersionUID = 1L;

	private final boolean useSsl;

	public SopProxyServlet(final boolean useSsl) {
		this.useSsl = useSsl;
	}

	@Override
	protected HttpClient newHttpClient() {

		if (useSsl) {
			final SslContextFactory contextFactory = new SslContextFactory.Client();
			return new HttpClient(contextFactory);
		}

		return super.newHttpClient();
	}
}
