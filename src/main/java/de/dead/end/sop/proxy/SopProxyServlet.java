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

	private final boolean trustAll;

	public SopProxyServlet(final boolean useSsl, final boolean trustAll) {

		this.useSsl = useSsl;

		this.trustAll = trustAll;
	}

	@Override
	protected HttpClient newHttpClient() {

		if (useSsl) {
			final SslContextFactory contextFactory = new SslContextFactory.Client();

			//
			// Disabling the truststore can be a security issue. This is only recommended
			// for test stages.
			//
			contextFactory.setTrustAll(trustAll);

			return new HttpClient(contextFactory);
		}

		return super.newHttpClient();
	}
}
