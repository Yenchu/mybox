package mybox.rest;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mybox.exception.Error;
import mybox.exception.ErrorException;
import mybox.json.JsonConverter;

public class RestResponseValidator {
	
	private static final Logger log = LoggerFactory.getLogger(RestResponseValidator.class);
	
	public <T> void validateResponse(RestResponse<T> restResponse, int... validStatusCodes) {
		if (restResponse == null) {
			String msg = "The rest response is empty!";
			throw new ErrorException(Error.internalServerError(msg));
		}
		
		T body = restResponse.getBody();
		if (body != null) {
			log.debug("Get {} rest response: {}", restResponse.getStatusCode(), (InputStream.class.isAssignableFrom(body.getClass()) ? "" : body.toString()));
		} else {
			log.debug("Get {} rest response", restResponse.getStatusCode());
		}
		validateStatusCode(restResponse, validStatusCodes);
	}
	
	protected <T> void validateStatusCode(RestResponse<T> restResponse, int... validStatusCodes) {
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
			T body = restResponse.getBody();
			if (body != null) {
				if (InputStream.class.isAssignableFrom(body.getClass())) {
					InputStream is = (InputStream) body;
					try {
						content = IOUtils.toString(is, "UTF-8");
					} catch (IOException e) {
						log.error(e.getMessage(), e);
						throw new ErrorException(Error.internalServerError(e.getMessage()));
					}
				} else {
					content = body.toString();
				}
			}
			throwException(statusCode, content);
		}
	}

	protected void throwException(int statusCode, String content) {
		Error error = null;
		if (content != null && !"".equals(content)) {
			try {
				error = JsonConverter.fromJson(content, Error.class);
				error.setCode(statusCode);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		if (error == null) {
			error = new Error(statusCode);
			error.setTitle("Error");
			error.setMessage(content);
		}
		throw new ErrorException(error);
	}
}
