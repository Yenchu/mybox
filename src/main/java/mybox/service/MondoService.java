package mybox.service;

import java.util.List;

import mybox.model.mondo.Group;
import mybox.model.mondo.MondoUser;

public interface MondoService extends FileService {

	public List<Group> getGroups(MondoUser user);
	
}