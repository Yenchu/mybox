package mybox.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpUtil {
	
	private static final Logger log = LoggerFactory.getLogger(HttpUtil.class);

	public static String[] encodedHeaders(String... headers) {
		String[] encodedHeaders = null;
		if (headers != null && headers.length > 0) {
			encodedHeaders = new String[headers.length];
			encodeHeaders(encodedHeaders, headers);
		}
		return encodedHeaders;
	}
	
	public static void encodeHeaders(String[] encodedHeaders, String... headers) {
		if (headers.length % 2 != 0) {
			throw new IllegalArgumentException("Headers must have an even number of elements.");
		}

		for (int i = 0; i < headers.length; i += 2) {
			String header = headers[i];
			String value = headers[i + 1];
			if (value == null) {
				log.info("Header {} doesn't have value!", header);
				continue;
			}
			encodedHeaders[i] = header;
			encodedHeaders[i + 1] = encode(value);
		}
	}
	
	public static String encodeQueryString(String[] qryStr) {
		if (qryStr.length % 2 != 0) {
			throw new IllegalArgumentException("Query string must have an even number of elements.");
		}

		StringBuilder buf = new StringBuilder();
		boolean firstTime = true;
		for (int i = 0; i < qryStr.length; i += 2) {
			if (qryStr[i + 1] != null) {
				if (firstTime) {
					firstTime = false;
				} else {
					buf.append("&");
				}
				String name = encode(qryStr[i]);
				String value = encode(qryStr[i + 1]);
				buf.append(name).append("=").append(value);
			}
		}
		return buf.toString();
	}
	
	public static String encode(String str) {
		try {
			return URLEncoder.encode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(), e);
			return str;
		}
	}

	public static String decode(String str) {
		try {
			return URLDecoder.decode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error(e.getMessage(), e);
			return str;
		}
	}
}
