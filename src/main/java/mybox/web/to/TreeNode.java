package mybox.web.to;

import java.util.ArrayList;
import java.util.List;

public class TreeNode {
	
	private String id;

	private String title;
	
	private Boolean isFolder;
	
	private List<TreeNode> children;
	
	private Boolean isLazy;

	public TreeNode() {
	}
	
	public TreeNode(String title) {
		this(title, false);
	}
	
	public TreeNode(String title, Boolean isFolder) {
		this.title = title;
		this.isFolder = isFolder;
	}
	
	public TreeNode(String id, String title, Boolean isFolder) {
		this.id = id;
		this.title = title;
		this.isFolder = isFolder;
	}
	
	public void addChild(TreeNode child) {
		if (children == null) {
			children = new ArrayList<TreeNode>();
		}
		children.add(child);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Boolean getIsFolder() {
		return isFolder;
	}

	public void setIsFolder(Boolean isFolder) {
		this.isFolder = isFolder;
	}

	public List<TreeNode> getChildren() {
		return children;
	}

	public void setChildren(List<TreeNode> children) {
		this.children = children;
	}

	public Boolean getIsLazy() {
		return isLazy;
	}

	public void setIsLazy(Boolean isLazy) {
		this.isLazy = isLazy;
	}
}
