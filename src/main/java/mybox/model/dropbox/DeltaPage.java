package mybox.model.dropbox;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class DeltaPage<MD> {

	//{"reset": true, "cursor": "AphVPJMhVDtlCn9BMIL_nQdrYKEXnx5uoSHeTDlHOwmdeJxjUD02kYGxSpTBVncB-wzbeAYGBiYGCODXz0stV0jLz0lJLdJPyU_mko9Zw4AALCAxzt9m90EcABECDpA", "has_more": true, 
	//"entries": [
	//["/photos", {"revision": 127417582, "rev": "7983cee003d2da0", "thumb_exists": false, "bytes": 0, "modified": "Tue, 13 Oct 2009 06:00:30 +0000", "path": "/Photos", "is_dir": true, "icon": "folder_photos", "root": "dropbox", "size": "0 bytes"}], 
	//["/public", {"revision": 127417583, "rev": "7983cef003d2da0", "thumb_exists": false, "bytes": 0, "modified": "Tue, 13 Oct 2009 06:00:30 +0000", "path": "/Public", "is_dir": true, "icon": "folder_public", "root": "dropbox", "size": "0 bytes"}], ["/photos/how to use the photos folder.rtf", {"revision": 127417584, "rev": "7983cf0003d2da0", "thumb_exists": false, "bytes": 945, "modified": "Tue, 13 Oct 2009 06:00:30 +0000", "client_mtime": "Tue, 13 Oct 2009 06:00:30 +0000", "path": "/Photos/How to use the Photos folder.rtf", "is_dir": false, "icon": "page_white_text", "root": "dropbox", "mime_type": "application/rtf", "size": "945 bytes"}], ["/photos/sample album", {"revision": 127417585, "rev": "7983cf1003d2da0", "thumb_exists": false, "bytes": 0, "modified": "Tue, 13 Oct 2009 06:00:30 +0000", "path": "/Photos/Sample Album", "is_dir": true, "icon": "folder", "root": "dropbox", "size": "0 bytes"}], ["/photos/sample album/boston city flow.jpg", {"revision": 127417586, "rev": "7983cf2003d2da0", "thumb_exists": true, "bytes": 339773, "modified": "Tue, 13 Oct 2009 06:00:30 +0000", "client_mtime": "Tue, 13 Oct 2009 06:00:30 +0000", "path": "/Photos/Sample Album/Boston City Flow.jpg", "is_dir": false, "icon": "page_white_picture", "root": "dropbox", "mime_type": "image/jpeg", "size": "331.8 KB"}], ["/photos/sample album/pensive parakeet.jpg", {"revision": 127417587, "rev": "7983cf3003d2da0", "thumb_exists": true, "bytes": 480098, "modified": "Tue, 13 Oct 2009 06:00:30 +0000", "client_mtime": "Tue, 13 Oct 2009 06:00:30 +0000", "path": "/Photos/Sample Album/Pensive Parakeet.jpg", "is_dir": false, "icon": "page_white_picture", "root": "dropbox", "mime_type": "image/jpeg", "size": "468.8 KB"}], ["/photos/sample album/costa rican frog.jpg", {"revision": 127417588, "rev": "7983cf4003d2da0", "thumb_exists": true, "bytes": 354633, "modified": "Tue, 13 Oct 2009 06:00:30 +0000", "client_mtime": "Tue, 13 Oct 2009 06:00:30 +0000", "path": "/Photos/Sample Album/Costa Rican Frog.jpg", "is_dir": false, "icon": "page_white_picture", "root": "dropbox", "mime_type": "image/jpeg", "size": "346.3 KB"}], ["/public/how to use the public folder.rtf", {"revision": 127417589, "rev": "7983cf5003d2da0", "thumb_exists": false, "bytes": 1072, "modified": "Tue, 13 Oct 2009 06:00:30 +0000", "client_mtime": "Tue, 13 Oct 2009 06:00:30 +0000", "path": "/Public/How to use the Public folder.rtf", "is_dir": false, "icon": "page_white_text", "root": "dropbox", "mime_type": "application/rtf", "size": "1 KB"}], ["/getting started.rtf", {"revision": 127417590, "rev": "7983cf6003d2da0", "thumb_exists": false, "bytes": 1268, "modified": "Tue, 13 Oct 2009 06:00:30 +0000", "client_mtime": "Tue, 13 Oct 2009 06:00:30 +0000", "path": "/Getting Started.rtf", "is_dir": false, "icon": "page_white_text", "root": "dropbox", "mime_type": "application/rtf", "size": "1.2 KB"}], ["/iphone intro.pdf", {"revision": 127417607, "rev": "7983d07003d2da0", "thumb_exists": false, "bytes": 176640, "modified": "Tue, 13 Oct 2009 06:00:32 +0000", "client_mtime": "Tue, 13 Oct 2009 06:00:32 +0000", "path": "/iPhone intro.pdf", "is_dir": false, "icon": "page_white_acrobat", "root": "dropbox", "mime_type": "application/pdf", "size": "172.5 KB"}], ["/new folder", {"revision": 127417610, "rev": "7983d0a003d2da0", "thumb_exists": false, "bytes": 0, "modified": "Mon, 20 Aug 2012 09:41:09 +0000", "path": "/New folder", "is_dir": true, "icon": "folder", "root": "dropbox", "size": "0 bytes"}], ["/doc", {"revision": 127417633, "rev": "7983d21003d2da0", "thumb_exists": false, "bytes": 0, "modified": "Wed, 05 Sep 2012 15:59:04 +0000", "path": "/doc", "is_dir": true, "icon": "folder_user", "root": "dropbox", "size": "0 bytes"}], ["/new folder/doc", {"revision": 127417647, "rev": "7983d2f003d2da0", "thumb_exists": false, "bytes": 0, "modified": "Tue, 11 Sep 2012 15:42:27 +0000", "path": "/New folder/doc", "is_dir": true, "icon": "folder_user", "root": "dropbox", "size": "0 bytes"}], ["/new folder/hadoop.pdf", {"revision": 127417653, "rev": "7983d35003d2da0", "thumb_exists": false, "bytes": 560933, "modified": "Tue, 18 Sep 2012 02:46:14 +0000", "client_mtime": "Tue, 18 Sep 2012 02:46:14 +0000", "path": "/New folder/hadoop.pdf", "is_dir": false, "icon": "page_white_acrobat", "root": "dropbox", "mime_type": "application/pdf", "size": "547.8 KB"}], ["/new folder/spring-data-hadoop-reference.pdf", {"revision": 127417654, "rev": "7983d36003d2da0", "thumb_exists": false, "bytes": 331527, "modified": "Tue, 18 Sep 2012 02:47:58 +0000", "client_mtime": "Wed, 05 Sep 2012 03:02:20 +0000", "path": "/New folder/spring-data-hadoop-reference.pdf", "is_dir": false, "icon": "page_white_acrobat", "root": "dropbox", "mime_type": "application/pdf", "size": "323.8 KB"}], ["/photos/penguins.jpg", {"revision": 127417657, "rev": "7983d39003d2da0", "thumb_exists": true, "bytes": 777835, "modified": "Wed, 19 Sep 2012 05:27:45 +0000", "client_mtime": "Wed, 19 Sep 2012 05:27:45 +0000", "path": "/Photos/Penguins.jpg", "is_dir": false, "icon": "page_white_picture", "root": "dropbox", "mime_type": "image/jpeg", "size": "759.6 KB"}], ["/photos/hydrangeas.jpg", {"revision": 127417658, "rev": "7983d3a003d2da0", "thumb_exists": true, "bytes": 595284, "modified": "Wed, 19 Sep 2012 05:46:03 +0000", "client_mtime": "Wed, 19 Sep 2012 05:46:03 +0000", "path": "/Photos/Hydrangeas.jpg", "is_dir": false, "icon": "page_white_picture", "root": "dropbox", "mime_type": "image/jpeg", "size": "581.3 KB"}], ["/photos/tulips.jpg", {"revision": 127417659, "rev": "7983d3b003d2da0", "thumb_exists": true, "bytes": 620888, "modified": "Wed, 19 Sep 2012 05:50:39 +0000", "client_mtime": "Wed, 19 Sep 2012 05:50:39 +0000", "path": "/Photos/Tulips.jpg", "is_dir": false, "icon": "page_white_picture", "root": "dropbox", "mime_type": "image/jpeg", "size": "606.3 KB"}], ["/photos/lighthouse.jpg", {"revision": 127417676, "rev": "7983d4c003d2da0", "thumb_exists": true, "bytes": 561276, "modified": "Thu, 20 Sep 2012 06:22:29 +0000", "client_mtime": "Thu, 20 Sep 2012 06:22:29 +0000", "path": "/Photos/Lighthouse.jpg", "is_dir": false, "icon": "page_white_picture", "root": "dropbox", "mime_type": "image/jpeg", "size": "548.1 KB"}], ["/photos/chrysanthemum - \u8907\u88fd.jpg", {"revision": 127417677, "rev": "7983d4d003d2da0", "thumb_exists": true, "bytes": 879394, "modified": "Thu, 20 Sep 2012 06:22:30 +0000", "client_mtime": "Thu, 20 Sep 2012 06:22:30 +0000", "path": "/Photos/Chrysanthemum - \u8907\u88fd.jpg", "is_dir": false, "icon": "page_white_picture", "root": "dropbox", "mime_type": "image/jpeg", "size": "858.8 KB"}], ["/photos/koala.jpg", {"revision": 127417686, "rev": "7983d56003d2da0", "thumb_exists": true, "bytes": 780831, "modified": "Fri, 21 Sep 2012 09:15:48 +0000", "client_mtime": "Fri, 21 Sep 2012 09:15:48 +0000", "path": "/Photos/Koala.jpg", "is_dir": false, "icon": "page_white_picture", "root": "dropbox", "mime_type": "image/jpeg", "size": "762.5 KB"}], ["/photos/jellyfish.jpg", {"revision": 127417687, "rev": "7983d57003d2da0", "thumb_exists": true, "bytes": 775702, "modified": "Mon, 01 Oct 2012 07:10:52 +0000", "client_mtime": "Mon, 01 Oct 2012 07:10:52 +0000", "path": "/Photos/Jellyfish.jpg", "is_dir": false, "icon": "page_white_picture", "root": "dropbox", "mime_type": "image/jpeg", "size": "757.5 KB"}], ["/costa rican frog.jpg", {"revision": 127417694, "rev": "7983d5e003d2da0", "thumb_exists": true, "bytes": 354633, "modified": "Wed, 03 Oct 2012 08:22:44 +0000", "client_mtime": "Wed, 03 Oct 2012 08:22:03 +0000", "path": "/Costa Rican Frog.jpg", "is_dir": false, "icon": "page_white_picture", "root": "dropbox", "mime_type": "image/jpeg", "size": "346.3 KB"}], ["/mondo install 1.0.9.pdf", {"revision": 127417695, "rev": "7983d5f003d2da0", "thumb_exists": false, "bytes": 4491061, "modified": "Thu, 04 Oct 2012 07:11:20 +0000", "client_mtime": "Thu, 04 Oct 2012 07:11:20 +0000", "path": "/Mondo install 1.0.9.pdf", "is_dir": false, "icon": "page_white_acrobat", "root": "dropbox", "mime_type": "application/pdf", "size": "4.3 MB"}]
	//]}

	private String cursor;;
	
	private boolean reset;
	
	@SerializedName("has_more")
	private boolean hasMore;
	
	private List<DeltaEntry<MD>> entries;

	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("\ncursor=").append(cursor);
		buf.append(", reset=").append(reset);
		buf.append(", hasMore=").append(hasMore);
		if (entries != null) {
			for (DeltaEntry<MD> entry: entries) {
				buf.append("\n");
				buf.append("entry={").append(entry).append("}");
			}
		}
		return buf.toString();
	}
	
	public String getCursor() {
		return cursor;
	}

	public void setCursor(String cursor) {
		this.cursor = cursor;
	}

	public boolean isReset() {
		return reset;
	}

	public void setReset(boolean reset) {
		this.reset = reset;
	}

	public boolean isHasMore() {
		return hasMore;
	}

	public void setHasMore(boolean hasMore) {
		this.hasMore = hasMore;
	}

	public List<DeltaEntry<MD>> getEntries() {
		return entries;
	}

	public void setEntries(List<DeltaEntry<MD>> entries) {
		this.entries = entries;
	}
}
