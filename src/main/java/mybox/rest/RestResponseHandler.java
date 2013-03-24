package mybox.rest;

import java.io.IOException;
import java.io.InputStream;

import mybox.json.Json;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class RestResponseHandler<T, E> {
	
	private static final Logger log = LoggerFactory.getLogger(RestResponseHandler.class);
	
	public abstract T handle(RestResponse<E> restResponse);
	
	public static <E> void checkResponse(RestResponse<E> restResponse, int... validStatusCodes) {
		if (restResponse == null) {
			String msg = "The rest response is empty!";
			throw new FaultException(Fault.internalServerError(msg));
		}
		
		log.debug("Get {} rest response: {}", restResponse.getStatusCode(), (InputStream.class.isAssignableFrom(restResponse.getBody().getClass()) ? "" : restResponse.getBody().toString()));
		checkStatusCode(restResponse, validStatusCodes);
	}
	
	protected static <E> void checkStatusCode(RestResponse<E> restResponse, int... validStatusCodes) {
		int statusCode = restResponse.getStatusCode();
		if (validStatusCodes != null && validStatusCodes.length > 0) {
			for (int validStatusCode: validStatusCodes) {
				if (statusCode == validStatusCode) {
					return;
				}
			}
		}
		
		if (statusCode > HttpStatus.SC_MULTI_STATUS || statusCode < HttpStatus.SC_OK) {
			String content = null;
			E body = restResponse.getBody();
			if (InputStream.class.isAssignableFrom(body.getClass())) {
				InputStream is = (InputStream) body;
				try {
					content = IOUtils.toString(is, "UTF-8");
				} catch (IOException e) {
					log.error(e.getMessage(), e);
					throw new FaultException(Fault.internalServerError(e.getMessage()));
				}
			} else {
				content = body.toString();
			}
			throwFaultException(statusCode, content);
		}
	}

	protected static void throwFaultException(int statusCode, String content) {
		Fault fault = null;
		if (content != null && !"".equals(content)) {
			try {
				fault = Json.fromJson(content, Fault.class);
				fault.setCode(statusCode);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		if (fault == null) {
			fault = new Fault(statusCode);
			fault.setMessage(content);
		}
		throw new FaultException(fault);
	}
}
