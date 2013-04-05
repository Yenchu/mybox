package mybox.web.controller.advice;

import javax.servlet.http.HttpServletRequest;

import mybox.exception.Error;
import mybox.exception.ErrorException;
import mybox.json.JsonConverter;
import mybox.util.WebUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ControllerExceptionHandler {
	
	private static final Logger log = LoggerFactory.getLogger(ControllerExceptionHandler.class);

	@ExceptionHandler(ErrorException.class)
	@ResponseBody
	public ResponseEntity<String> handleFaultException(ErrorException e, HttpServletRequest request) {
		Error error = e.getError();
		log.warn("Got error when handling request {} from {}: {}", request.getRequestURI(), WebUtil.getUserAddress(request), error);
		return handleResponse(request, error);
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseBody
	public ResponseEntity<String> handleException(Exception e, HttpServletRequest request) {
		log.error(e.getMessage(), e);
		log.error("Got exception when handling request {} from {}: {}", request.getRequestURI(), WebUtil.getUserAddress(request), e.getMessage());
		Error error = Error.internalServerError(e.getMessage());
		return handleResponse(request, error);
	}
	
	protected ResponseEntity<String> handleResponse(HttpServletRequest request, Error fault) {
		String body = JsonConverter.toJson(fault);
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		ResponseEntity<String> responseEntity = new ResponseEntity<String>(body, headers, HttpStatus.valueOf(fault.getCode()));
		return responseEntity;
	}
}
