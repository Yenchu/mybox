package mybox.service;

import java.util.List;

import mybox.backend.filecruiser.FileCruiserUtil;
import mybox.backend.filecruiser.Resource;
import mybox.model.keystone.Endpoint;
import mybox.model.keystone.Endpoints;
import mybox.model.keystone.Services;
import mybox.rest.RestClient;
import mybox.rest.RestClientFactory;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EndpointServiceImpl extends AbstractKeystoneService implements	EndpointService {

	private static final Logger log = LoggerFactory.getLogger(EndpointServiceImpl.class);

	protected RestClient restClient = RestClientFactory.getRestClient();
	
	protected String fileServiceUrl;
	
	protected String userPortalUrl;

	public Services getServices() {
		String token = getAdminToken();
		String url = getUserServiceUrl(Resource.SERVICES);
		String[] headers = FileCruiserUtil.getHeaders(token);
		Services services = restClient.get(Services.class, url, headers);
		return services;

	}

	public Endpoints getEndpoints() {
		String token = getAdminToken();
		String url = getUserServiceUrl(Resource.ENDPOINTS);
		String[] headers = FileCruiserUtil.getHeaders(token);
		Endpoints endpoints = restClient.get(Endpoints.class, url, headers);
		return endpoints;
	}
	
	public String getFileServiceUrl() {
		if (StringUtils.isNotBlank(fileServiceUrl)) {
			return fileServiceUrl;
		}
		
		String fileServiceName = systemProp.getFileServiceName();
		fileServiceUrl = getServiceUrl(fileServiceName);
		return fileServiceUrl;
	}
	
	public String getUserPortalUrl() {
		if (StringUtils.isNotBlank(userPortalUrl)) {
			return userPortalUrl;
		}
		
		String userPortal = systemProp.getUserPortalName();
		userPortalUrl = getServiceUrl(userPortal);
		return userPortalUrl;
	}

	private String getServiceUrl(String serviceName) {
		String serviceId = null;
		Services servicesHolder = getServices();
		List<mybox.model.keystone.Service> services = servicesHolder.getServices();
		for (mybox.model.keystone.Service service: services) {
			if (service.getName().equals(serviceName)) {
				serviceId = service.getId();
				break;
			}
		}
		
		if (serviceId == null) {
			throw new NullPointerException("Can not find file service name!");
		}
		
		Endpoints endpointsHolder = getEndpoints();
		List<Endpoint> endpoints = endpointsHolder.getEndpoints();
		
		String serviceUrl = null;
		for (Endpoint endpoint: endpoints) {
			if (endpoint.getServiceId().equals(serviceId)) {
				serviceUrl = endpoint.getUrl();
				break;
			}
		}
		
		if (serviceUrl == null) {
			throw new NullPointerException("Can not service url!");
		}
		
		if (serviceUrl.endsWith("/")) {
			serviceUrl = serviceUrl.substring(0, serviceUrl.length() - 1);
		}
		return serviceUrl;
	}
}
