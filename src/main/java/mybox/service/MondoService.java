package mybox.service;

import java.util.List;

import mybox.mondo.model.Group;
import mybox.mondo.to.MondoUser;


public interface MondoService extends FileService {

	public List<Group> getGroups(MondoUser user);
	
}