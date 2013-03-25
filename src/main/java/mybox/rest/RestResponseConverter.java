package mybox.rest;

public abstract class RestResponseConverter<T, E> {

	public abstract E convert(RestResponse<T> restResponse);
	
}
