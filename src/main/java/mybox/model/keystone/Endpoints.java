package mybox.model.keystone;

import java.util.List;

public class Endpoints {
	//{"endpoints": 
	//[{"links": {"self": "http://localhost:5000/v3/endpoints/4fa8aa82e15d44c3970141fdf2b56635"}, "url": "http://10.90.0.99:8080/", "region": "regionOne", "interface": "public", "service_id": "27bf59de818a4b8298be3d38aa1c39fc", "id": "4fa8aa82e15d44c3970141fdf2b56635"} 
	//, {"links": {"self": "http://localhost:5000/v3/endpoints/837bd1d82d2d4f94bce2b20548bf7ab1"}, "url": "ws://10.90.0.98:12345/", "region": "regionOne", "interface": "public", "service_id": "d1040aa7837943268cca0e78d34fe99f", "id": "837bd1d82d2d4f94bce2b20548bf7ab1"}
	//, {"links": {"self": "http://localhost:5000/v3/endpoints/919a9cbe884f41328b63dbe515da339d"}, "url": "http://10.90.0.95:7000/v1/", "region": "regionOne", "interface": "public", "service_id": "5136a8c3b77e42dba328ebc5dc082726", "id": "919a9cbe884f41328b63dbe515da339d"}
	//, {"links": {"self": "http://localhost:5000/v3/endpoints/e78ae2da9a464232bfc62f0066c5440c"}, 
	//"url": "http://10.90.0.97:8080/", "region": "regionOne", "interface": "public", "service_id": "4f775abe26244e0c8920204c8d0c41f1", "id": "e78ae2da9a464232bfc62f0066c5440c"}]
	//, "links": {"self": "http://localhost:5000/v3/endpoints", "previous": null, "next": null}}
	
	private List<Endpoint> endpoints;

	private Links links;
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("endpoints=").append(endpoints);
		buf.append(", links=").append(links);
		return buf.toString();
	}
	
	public List<Endpoint> getEndpoints() {
		return endpoints;
	}

	public void setEndpoints(List<Endpoint> endpoints) {
		this.endpoints = endpoints;
	}

	public Links getLinks() {
		return links;
	}

	public void setLinks(Links links) {
		this.links = links;
	}
}
