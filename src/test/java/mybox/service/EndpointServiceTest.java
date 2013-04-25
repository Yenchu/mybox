package mybox.service;

import java.util.List;

import mybox.SpringUnitTest;
import mybox.model.keystone.Endpoint;
import mybox.model.keystone.Endpoints;
import mybox.model.keystone.Service;
import mybox.model.keystone.Services;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class EndpointServiceTest extends SpringUnitTest {

	private static final Logger log = LoggerFactory.getLogger(EndpointServiceTest.class);
	
	@Autowired
	private EndpointService endpointService;
	
	@Test
	public void getServices() {
		Services servicesHolder = endpointService.getServices();
		List<Service> services = servicesHolder.getServices();
		for (Service service: services) {
			log.debug("service: {}", service);
		}
	}

	@Test
	public void getEndpoints() {
		Endpoints endpointsHolder = endpointService.getEndpoints();
		List<Endpoint> endpoints = endpointsHolder.getEndpoints();
		for (Endpoint endpoint: endpoints) {
			log.debug("endpoint: {}", endpoint);
		}
	}
	
	@Test
	public void getFileServiceUrl() {
		String url = endpointService.getFileServiceUrl();
		log.debug("url: {}", url);
	}
}
