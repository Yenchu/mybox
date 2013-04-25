package mybox.model.filecruiser;

import java.util.List;

import mybox.model.filecruiser.DeltaEntry;

import com.google.gson.annotations.SerializedName;

public class DeltaPage {
	/*"cursor": 1366186795254, 
    "entries": [
        {
            "action": "Upload File", 
            "delta": "Create", 
            "is_dir": false, 
            "path": "a", 
            "size": 1
        }
    ], 
    "has_more": false*/

	private String cursor;;
	
	private boolean reset;
	
	@SerializedName("has_more")
	private boolean hasMore;
	
	private List<DeltaEntry> entries;

	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append("\ncursor=").append(cursor);
		buf.append(", reset=").append(reset);
		buf.append(", hasMore=").append(hasMore);
		if (entries != null) {
			for (DeltaEntry entry: entries) {
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

	public List<DeltaEntry> getEntries() {
		return entries;
	}

	public void setEntries(List<DeltaEntry> entries) {
		this.entries = entries;
	}
}
