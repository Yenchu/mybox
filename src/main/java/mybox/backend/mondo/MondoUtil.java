package mybox.backend.mondo;

import java.util.LinkedHashMap;
import java.util.Map;

import mybox.model.mondo.MondoUser;
import mybox.util.HttpUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MondoUtil {

	private static final Logger log = LoggerFactory.getLogger(MondoUtil.class);
	
	public static String encodeUrl(String str) {
		String encodedStr = HttpUtil.encode(str);
		encodedStr = encodedStr.replace("%2F", "/").replace("+", "%20").replace("*", "%2A");
		return encodedStr;
	}
	
	public static String[] getSignedHeaders(MondoUser user, String... headers) {
		String[] signedHeaders = null;
		if (headers != null && headers.length > 0) {
			signedHeaders = new String[headers.length + 2];
			HttpUtil.encodeHeaders(signedHeaders, headers);
		} else {
			signedHeaders = new String[2];
		}

		int idx = signedHeaders.length - 2;
		signedHeaders[idx] = HttpHeader.AUTH_TOKEN.value();
		signedHeaders[idx + 1] = user.getToken();
		return signedHeaders;
	}
	
	public static Map<String, String> getQueryString(String... params) {
		if (params == null || params.length <= 0) {
			return null;
		}
		if (params.length % 2 != 0) {
			throw new IllegalArgumentException("Query string must have an even number of elements.");
		}
		
		Map<String, String> qryStr = new LinkedHashMap<String, String>();
		for (int i = 0; i < params.length; i += 2) {
			String name = params[i];
			String value = params[i + 1];
			qryStr.put(name, value);
		}
		return qryStr;
	}
}
