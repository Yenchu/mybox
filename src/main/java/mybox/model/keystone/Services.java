package mybox.model.keystone;

import java.util.List;

public class Services {

	//{"services": [{"id": "27bf59de818a4b8298be3d38aa1c39fc", "type": "admin_portal", "name": "admin_portal", "links": {"self": "http://localhost:5000/v3/services/27bf59de818a4b8298be3d38aa1c39fc"}, "description": null}
	//, {"id": "4f775abe26244e0c8920204c8d0c41f1", "type": "user_portal", "name": "user_portal", "links": {"self": "http://localhost:5000/v3/services/4f775abe26244e0c8920204c8d0c41f1"}, "description": null}
	//, {"id": "5136a8c3b77e42dba328ebc5dc082726", "type": "fileop", "name": "fileop", "links": {"self": "http://localhost:5000/v3/services/5136a8c3b77e42dba328ebc5dc082726"}, "description": null}
	//, {"id": "d1040aa7837943268cca0e78d34fe99f", "type": "notify_server", "name": "notify_server", "links": {"self": "http://localhost:5000/v3/services/d1040aa7837943268cca0e78d34fe99f"}, "description": null}], "links": {"self": "http://localhost:5000/v3/services", "previous": null, "next": null}}

	private List<Service> services;
	
	private Links links;
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("services=").append(services);
		buf.append(", links=").append(links);
		return buf.toString();
	}

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}

	public Links getLinks() {
		return links;
	}

	public void setLinks(Links links) {
		this.links = links;
	}
}
