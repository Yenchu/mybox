package mybox.service.mock;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import mybox.model.Permission;
import mybox.model.filecruiser.FileCruiserUser;
import mybox.model.filecruiser.SharedFile;
import mybox.model.filecruiser.SharingFile;
import mybox.model.keystone.User;
import mybox.service.FileCruiserServiceImpl;
import mybox.to.Params;
import mybox.to.PathParams;

//@Service
public class FileCruiserServiceMock extends FileCruiserServiceImpl {

	private static final Logger log = LoggerFactory.getLogger(FileCruiserServiceMock.class);
	
	private List<SharedFile> sharedFiles = new ArrayList<SharedFile>();
	
	public SharedFile share(Params params, SharingFile sharingFile) {
		FileCruiserUser user = (FileCruiserUser) params.getUser();
		String toUseId = sharingFile.getUserId();
		User toUser = userService.getUser(toUseId);
		
		SharedFile sharedFile = new SharedFile();
		sharedFile.setId(UUID.randomUUID().toString().replaceAll("-", ""));
		sharedFile.setFilePath(sharingFile.getFilePath());
		sharedFile.setIsDir(sharingFile.getIsDir());
		sharedFile.setPermission(Permission.getPermission(Permission.WRITE));
		
		sharedFile.setFromUserName(user.getName());
		sharedFile.setToUserId(UUID.randomUUID().toString().replaceAll("-", ""));
		sharedFile.setToUserName(toUser.getName());
		sharedFiles.add(sharedFile);
		return sharedFile;
	}
	
	public List<SharedFile> getShares(PathParams params) {
		if (sharedFiles.size() < 1) {
			SharedFile sharedFile = new SharedFile();
			sharedFile.setId(UUID.randomUUID().toString().replaceAll("-", ""));
			sharedFile.setFilePath("/dev/folder001");
			sharedFile.setIsDir(false);
			sharedFile.setNotation(Permission.WRITE);
			sharedFile.setPermission(Permission.getPermission(sharedFile.getNotation()));
			sharedFile.setFromUserName("dev001");
			sharedFile.setToUserId(UUID.randomUUID().toString().replaceAll("-", ""));
			sharedFile.setToUserName("user001");
			sharedFiles.add(sharedFile);
		}
		return sharedFiles;
	}
}
