package mybox.model.keystone;

import com.google.gson.annotations.SerializedName;

public class Endpoint {
	//, {"links": {"self": "http://localhost:5000/v3/endpoints/e78ae2da9a464232bfc62f0066c5440c"}, 
	//"url": "http://10.90.0.97:8080/", "region": "regionOne", "interface": "public", "service_id": "4f775abe26244e0c8920204c8d0c41f1", "id": "e78ae2da9a464232bfc62f0066c5440c"}]
	
	private String id;
	
	private String name;

	@SerializedName("service_id")
	private String serviceId;

	private String url;
	
	private String region;
	
	@SerializedName("interface")
	private String interfaceType;
	
	private Links links;

	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("id=").append(id);
		buf.append(", name=").append(name);
		buf.append(", serviceId=").append(serviceId);
		buf.append(", url=").append(url);
		buf.append(", region=").append(region);
		buf.append(", interface=").append(interfaceType);
		buf.append(", links=").append(links);
		return buf.toString();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public String getInterfaceType() {
		return interfaceType;
	}

	public void setInterfaceType(String interfaceType) {
		this.interfaceType = interfaceType;
	}

	public Links getLinks() {
		return links;
	}

	public void setLinks(Links links) {
		this.links = links;
	}
}
