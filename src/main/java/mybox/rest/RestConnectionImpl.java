package mybox.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mybox.exception.Error;
import mybox.exception.ErrorException;

public class RestConnectionImpl implements RestConnection {

	private static final Logger log = LoggerFactory.getLogger(RestConnectionImpl.class);

	protected static final String encoding = "UTF-8";

	protected boolean isHttps;

	protected int httpsPort;

	public RestConnectionImpl() {
		this(false);
	}

	public RestConnectionImpl(boolean isHttps) {
		this(isHttps, 443);
	}

	public RestConnectionImpl(boolean isHttps, int httpsPort) {
		this.isHttps = isHttps;
		this.httpsPort = httpsPort;
	}

	public RestResponse<String> get(String url, String... headers) {
		return get(url, null, headers);
	}

	public RestResponse<String> get(String url, Map<String, String> queryStr, String... headers) {
		url = addQueryStringToUrl(url, queryStr);
		HttpGet httpMethod = new HttpGet(url);
		return execute(httpMethod, url, headers);
	}

	public RestResponse<InputStream> getStream(String url, String... headers) {
		return getStream(url, null, headers);
	}

	public RestResponse<InputStream> getStream(String url, Map<String, String> queryStr, String... headers) {
		url = addQueryStringToUrl(url, queryStr);
		HttpGet httpMethod = new HttpGet(url);
		return executeStream(httpMethod, url, headers);
	}

	public RestResponse<String> delete(String url, String... headers) {
		return delete(url, null, headers);
	}

	public RestResponse<String> delete(String url, Map<String, String> queryStr, String... headers) {
		url = addQueryStringToUrl(url, queryStr);
		HttpDelete httpMethod = new HttpDelete(url);
		return execute(httpMethod, url, headers);
	}

	public RestResponse<String> post(String url, String... headers) {
		return post(url, "", headers);
	}

	public RestResponse<String> post(String url, String body, String... headers) {
		HttpPost httpMethod = new HttpPost(url);
		RestResponse<String> restResponse = execute(httpMethod, url, body, headers);
		return restResponse;
	}

	public RestResponse<String> post(String url, List<String> formParams, String... headers) {
		HttpPost httpMethod = new HttpPost(url);
		RestResponse<String> restResponse = execute(httpMethod, url, formParams, headers);
		return restResponse;
	}

	public RestResponse<String> post(String url, InputStream content, long contentLength, String... headers) {
		HttpPost httpMethod = new HttpPost(url);
		RestResponse<String> restResponse = execute(httpMethod, url, content, contentLength, headers);
		return restResponse;
	}

	public RestResponse<String> put(String url, String body, String... headers) {
		HttpPut httpMethod = new HttpPut(url);
		RestResponse<String> restResponse = execute(httpMethod, url, body, headers);
		return restResponse;
	}
	
	public RestResponse<String> put(String url, List<String> formParams, String... headers) {
		HttpPut httpMethod = new HttpPut(url);
		RestResponse<String> restResponse = execute(httpMethod, url, formParams, headers);
		return restResponse;
	}

	public RestResponse<String> put(String url, InputStream content, long contentLength, String... headers) {
		HttpPut httpMethod = new HttpPut(url);
		RestResponse<String> restResponse = execute(httpMethod, url, content, contentLength, headers);
		return restResponse;
	}
	
	public RestResponse<String> patch(String url, String body, String... headers) {
		HttpPatch httpMethod = new HttpPatch(url);
		RestResponse<String> restResponse = execute(httpMethod, url, body, headers);
		return restResponse;
	}
	
	public RestResponse<String> patch(String url, List<String> formParams, String... headers) {
		HttpPatch httpMethod = new HttpPatch(url);
		RestResponse<String> restResponse = execute(httpMethod, url, formParams, headers);
		return restResponse;
	}

	public RestResponse<String> patch(String url, InputStream content, long contentLength, String... headers) {
		HttpPatch httpMethod = new HttpPatch(url);
		RestResponse<String> restResponse = execute(httpMethod, url, content, contentLength, headers);
		return restResponse;
	}

	protected RestResponse<String> execute(HttpRequestBase httpMethod, String url, String... headers) {
		HttpClient httpclient = getHttpClient();
		RestResponse<String> restResponse = null;
		try {
			addHeaders(httpMethod, headers);
			HttpResponse response = httpclient.execute(httpMethod);
			restResponse = getRestResponse(response);
		} catch (IOException e) {
			StringBuilder buf = new StringBuilder().append("Connecting to ").append(url).append(" got exception: ").append(e.getMessage());
			String msg = buf.toString();
			log.error(msg, e);
			throw new ErrorException(Error.formatError(msg));
		} finally {
			releaseConnection(httpclient);
		}
		return restResponse;
	}

	protected RestResponse<String> execute(HttpEntityEnclosingRequestBase httpMethod, String url, String body,
			String... headers) {
		HttpClient httpclient = getHttpClient();
		RestResponse<String> restResponse = null;
		try {
			addHeaders(httpMethod, headers);

			if (body != null) {
				StringEntity entity = new StringEntity(body);
				httpMethod.setEntity(entity);
			}

			HttpResponse response = httpclient.execute(httpMethod);
			restResponse = getRestResponse(response);
		} catch (IOException e) {
			StringBuilder buf = new StringBuilder().append("Connecting to ").append(url).append(" got exception: ").append(e.getMessage());
			String msg = buf.toString();
			log.error(msg, e);
			throw new ErrorException(Error.formatError(msg));
		} finally {
			releaseConnection(httpclient);
		}
		return restResponse;
	}

	protected RestResponse<String> execute(HttpEntityEnclosingRequestBase httpMethod, String url,
			List<String> formParams, String... headers) {
		HttpClient httpclient = getHttpClient();
		RestResponse<String> restResponse = null;
		try {
			addHeaders(httpMethod, headers);

			if (formParams != null && formParams.size() > 0) {
				int size = formParams.size();
				if (size % 2 != 0) {
					throw new IllegalArgumentException("Params must be in pairs!");
				}

				List<NameValuePair> nvps = new ArrayList<NameValuePair>();
				for (int i = 0; i < size; i += 2) {
					String value = formParams.get(i + 1);
					if (value != null) {
						String name = formParams.get(i);
						nvps.add(new BasicNameValuePair(name, value));
					}
				}
				UrlEncodedFormEntity entity = new UrlEncodedFormEntity(nvps, "UTF-8");
				httpMethod.setEntity(entity);
			}

			HttpResponse response = httpclient.execute(httpMethod);
			restResponse = getRestResponse(response);
		} catch (IOException e) {
			StringBuilder buf = new StringBuilder().append("Connecting to ").append(url).append(" got exception: ").append(e.getMessage());
			String msg = buf.toString();
			log.error(msg, e);
			throw new ErrorException(Error.formatError(msg));
		} finally {
			releaseConnection(httpclient);
		}
		return restResponse;
	}

	protected RestResponse<String> execute(HttpEntityEnclosingRequestBase httpMethod, String url, InputStream is,
			long length, String... headers) {
		HttpClient httpclient = getHttpClient();
		RestResponse<String> restResponse = null;
		try {
			addHeaders(httpMethod, headers);

			InputStreamEntity entity = new InputStreamEntity(is, length);
			entity.setContentEncoding("application/octet-stream");
			entity.setChunked(false);
			httpMethod.setEntity(entity);

			HttpResponse response = httpclient.execute(httpMethod);
			restResponse = getRestResponse(response);
		} catch (IOException e) {
			StringBuilder buf = new StringBuilder().append("Connecting to ").append(url).append(" got exception: ").append(e.getMessage());
			String msg = buf.toString();
			log.error(msg, e);
			throw new ErrorException(Error.formatError(msg));
		} finally {
			releaseConnection(httpclient);
		}
		return restResponse;
	}

	protected RestResponse<InputStream> executeStream(HttpRequestBase httpMethod, String url, String... headers) {
		HttpClient httpclient = getHttpClient();
		RestResponse<InputStream> restResponse = null;
		try {
			addHeaders(httpMethod, headers);
			HttpResponse response = httpclient.execute(httpMethod);
			restResponse = getRestResponseAsStream(httpclient, response);
		} catch (IOException e) {
			StringBuilder buf = new StringBuilder().append("Connecting to ").append(url).append(" got exception: ").append(e.getMessage());
			String msg = buf.toString();
			log.error(msg, e);
			releaseConnection(httpclient);
			throw new ErrorException(Error.formatError(msg));
		}
		//* connection is released by calling HttpComponentInputStream.close()
		/*finally {
			releaseConnection(httpclient);
		}*/
		return restResponse;
	}

	protected HttpClient getHttpClient() {
		HttpClient httpclient = new DefaultHttpClient();
		if (isHttps) {
			try {
				SSLSocketFactory sf = new SSLSocketFactory(new TrustSelfSignedStrategy(),
						SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
				Scheme https = new Scheme("https", httpsPort, sf);
				httpclient.getConnectionManager().getSchemeRegistry().register(https);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new ErrorException(Error.formatError(e.getMessage()));
			}
		}
		return httpclient;
	}

	protected void releaseConnection(HttpClient httpclient) {
		httpclient.getConnectionManager().shutdown();
	}

	protected String addQueryStringToUrl(String url, Map<String, String> queryParameters) {
		if (queryParameters == null || queryParameters.size() <= 0) {
			return url;
		}

		boolean hasQryStrAlready = false;
		if (url.endsWith("?")) {
			hasQryStrAlready = true;
		}

		StringBuilder queryString = new StringBuilder();
		for (Map.Entry<String, String> entry : queryParameters.entrySet()) {
			if (hasQryStrAlready) {
				queryString.append("&");
			} else {
				queryString.append("?");
				hasQryStrAlready = true;
			}
			queryString.append(entry.getKey()).append("=").append(entry.getValue());
		}
		url += queryString.toString();
		return url;
	}

	protected void addHeaders(AbstractHttpMessage httpMethod, String... headers) {
		if (headers == null || headers.length <= 0) {
			return;
		}

		if (headers.length % 2 != 0) {
			throw new IllegalArgumentException("Headers must be in pairs!");
		}

		for (int index = 0, len = headers.length; index < len; index = index + 2) {
			httpMethod.addHeader(headers[index], headers[index + 1]);
		}
	}

	protected RestResponse<String> getRestResponse(HttpResponse response) throws IOException {
		RestResponse<String> restResponse = new RestResponse<String>();
		extractHeaders(response, restResponse);

		HttpEntity responseEntity = response.getEntity();
		String responseBody = null;
		if (responseEntity != null) {
			responseBody = EntityUtils.toString(responseEntity, encoding);
		}/* else {
			responseBody = "";
		}*/
		restResponse.setBody(responseBody);
		return restResponse;
	}

	protected RestResponse<InputStream> getRestResponseAsStream(HttpClient httpclient, HttpResponse response)
			throws IOException {
		RestResponse<InputStream> restResponse = new RestResponse<InputStream>();
		extractHeaders(response, restResponse);

		HttpEntity responseEntity = response.getEntity();
		HttpClientInputStream hcis = new HttpClientInputStream(httpclient, responseEntity);
		restResponse.setBody(hcis);
		return restResponse;
	}

	protected void extractHeaders(HttpResponse response, RestResponse<?> restResponse) {
		Header[] headers = response.getAllHeaders();
		for (Header header : headers) {
			String name = header.getName();
			String value = header.getValue();
			restResponse.addHeader(name, value);
		}

		StatusLine statusLine = response.getStatusLine();
		int statusCode = statusLine.getStatusCode();
		restResponse.setStatusCode(statusCode);
	}

	public boolean isHttps() {
		return isHttps;
	}

	public void setHttps(boolean isHttps) {
		this.isHttps = isHttps;
	}

	public int getHttpsPort() {
		return httpsPort;
	}

	public void setHttpsPort(int httpsPort) {
		this.httpsPort = httpsPort;
	}
}
