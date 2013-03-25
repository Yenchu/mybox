package mybox.rest;

import java.io.IOException;
import java.io.InputStream;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.util.EntityUtils;

public class HttpClientInputStream extends InputStream {

	private final HttpClient httpclient;
	
	private final HttpEntity entity;

	private final InputStream source;

	public HttpClientInputStream(HttpClient httpclient, HttpEntity entity) throws IOException {
		this.httpclient = httpclient;
		this.entity = entity;
		this.source = entity.getContent();
	}

	@Override
	public void close() throws IOException {
		if (source != null) {
			source.close();
		}
		
		if (entity != null) {
			EntityUtils.consume(entity);
		}
		
		if (httpclient != null) {
			httpclient.getConnectionManager().shutdown();
		}
	}

	@Override
	public int read() throws IOException {
		if (source == null) {
			return -1;
		}
		return source.read();
	}

	@Override
	public int read(byte[] bytes) throws IOException {
		if (source == null) {
			return -1;
		}
		return source.read(bytes);
	}

	@Override
	public int read(byte[] bytes, int i, int i1) throws IOException {
		if (source == null) {
			return -1;
		}
		return source.read(bytes, i, i1);
	}

	@Override
	public long skip(long l) throws IOException {
		if (source == null) {
			return -1;
		}
		return source.skip(l);
	}

	@Override
	public int available() throws IOException {
		if (source == null) {
			return 0;
		}
		return source.available();
	}

	@Override
	public synchronized void mark(int i) {
		if (source != null) {
			source.mark(i);
		}
	}

	@Override
	public synchronized void reset() throws IOException {
		if (source != null) {
			source.reset();
		}
	}

	@Override
	public boolean markSupported() {
		if (source != null) {
			return source.markSupported();
		}
		return false;
	}
}
