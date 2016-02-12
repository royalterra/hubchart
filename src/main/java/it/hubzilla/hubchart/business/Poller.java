package it.hubzilla.hubchart.business;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import it.hubzilla.hubchart.UrlException;

public class Poller {
	
	/* POLL USING HTTP CLIENT */
	public String getJsonResponseFromUrl(String url) throws UrlException {
		CloseableHttpClient httpclient = null;
		try {
			// TRUST-ALL CERTIFICATES HTTP CLIENT:
			HttpClientBuilder clientBuilder = HttpClientBuilder.create();
			SSLContextBuilder sslBuilder = new SSLContextBuilder();
			//ONLY SELF SIGNED: sslBuilder.loadTrustMaterial(KeyStore.getInstance(KeyStore.getDefaultType()), new TrustSelfSignedStrategy());
			sslBuilder.loadTrustMaterial(null, new TrustStrategy() {
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			});
			clientBuilder.setSslcontext(sslBuilder.build());
			httpclient = clientBuilder.build();
		} catch (Exception e) {
			// NORMAL HTTP CLIENT:
			httpclient = HttpClients.createDefault();
		}

		HttpPost httpget = new HttpPost(url);
		ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
			public String handleResponse(final HttpResponse response)
					throws ClientProtocolException, IOException {
				int status = response.getStatusLine().getStatusCode();
				if (status >= 200 && status < 300) {
					HttpEntity entity = response.getEntity();
					return entity != null ? EntityUtils.toString(entity) : null;
				} else {
					throw new ClientProtocolException(
							"Unexpected response status: " + status);
				}
			}
		};
		String responseBody = null;
		try {
			responseBody = httpclient.execute(httpget, responseHandler);
		} catch (ClientProtocolException e) {
			throw new UrlException(e.getClass().getSimpleName()+": "+e.getMessage(), e);
		} catch (IOException e) {
			throw new UrlException(e.getClass().getSimpleName()+": "+e.getMessage(), e);
		}
		return responseBody;
	}
}
