package mybox.to;

/**
 * This is used to generate response for jQuery File Upload.
 */
public class UploadResponse {

	/*jsono.put("name", item.getName());
    jsono.put("size", item.getSize());
    jsono.put("url", "upload?getfile=" + item.getName());
    jsono.put("thumbnail_url", "upload?getthumb=" + item.getName());
    jsono.put("delete_url", "upload?delfile=" + item.getName());
    jsono.put("delete_type", "GET");*/

	private String error;
	
	private String name;
	
	private Long size;
	
	private String url;
	
	private String thumbnail_url;
	
	private String delete_url;
	
	private String delete_type;

	public UploadResponse() {
	}

	public UploadResponse(String name, Long size) {
		this.name = name;
		this.size = size;
	}
	
	public UploadResponse(String name, Long size, String error) {
		this.name = name;
		this.size = size;
		this.error = error;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}
	
	public UploadResponse(String error) {
		this.error = error;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getSize() {
		return size;
	}

	public void setSize(Long size) {
		this.size = size;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getThumbnail_url() {
		return thumbnail_url;
	}

	public void setThumbnail_url(String thumbnailUrl) {
		this.thumbnail_url = thumbnailUrl;
	}

	public String getDelete_url() {
		return delete_url;
	}

	public void setDelete_url(String deleteUrl) {
		this.delete_url = deleteUrl;
	}

	public String getDelete_type() {
		return delete_type;
	}

	public void setDelete_type(String deleteType) {
		this.delete_type = deleteType;
	}
}
