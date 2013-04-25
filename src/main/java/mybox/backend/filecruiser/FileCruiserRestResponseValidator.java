package mybox.backend.filecruiser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import mybox.exception.Error;
import mybox.exception.ErrorException;
import mybox.json.JsonConverter;
import mybox.model.filecruiser.Fault;
import mybox.rest.RestResponseValidator;

public class FileCruiserRestResponseValidator extends RestResponseValidator {
	
	private static final Logger log = LoggerFactory.getLogger(FileCruiserRestResponseValidator.class);

	/*
	{
    "fault": {
        "details": "'ascii' codec can't encode characters in position 0-1: ordinal not in range(128)", 
        "service": "monga"
    }
	}
	 */
	protected void throwException(int statusCode, String content) {
		Error error = null;
		if (content != null && !"".equals(content)) {
			try {
				Fault fault = JsonConverter.fromJson(content, Fault.class, true);
				error = new Error();
				error.setCode(statusCode);
				error.setMessage(fault.getDetails());
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		if (error == null) {
			error = new Error(statusCode);
			error.setMessage(content);
		}
		throw new ErrorException(error);
	}
}
