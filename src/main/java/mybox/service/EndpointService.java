package mybox.service;

import mybox.model.keystone.Endpoints;
import mybox.model.keystone.Services;

public interface EndpointService {
	
	public Services getServices();

	public Endpoints getEndpoints();
	
	public String getFileServiceUrl();
	
	public String getUserPortalUrl();

}