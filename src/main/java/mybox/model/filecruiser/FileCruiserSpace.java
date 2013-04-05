package mybox.model.filecruiser;

import mybox.model.Space;
import mybox.model.keystone.Project;

public class FileCruiserSpace extends Space {
	
	private Project project;
	
	public String toString() {
		StringBuilder buf = new StringBuilder();
		buf.append(super.toString());
		buf.append(", project=").append(project);
		return buf.toString();
	}

	@Override
	public String getId() {
		return project.getId();
	}

	@Override
	public String getName() {
		return project.getName();
	}

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}
}
