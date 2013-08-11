package mybox.service;

import java.io.InputStream;
import java.util.List;

import mybox.model.FileEntry;
import mybox.model.Link;
import mybox.model.MetadataEntry;
import mybox.model.Space;
import mybox.model.User;
import mybox.to.ChunkedUploadParams;
import mybox.to.CopyParams;
import mybox.to.CreateParams;
import mybox.to.DeleteParams;
import mybox.to.EntryParams;
import mybox.to.FileOperationResponse;
import mybox.to.LinkParams;
import mybox.to.LoginParams;
import mybox.to.MetadataParams;
import mybox.to.MoveParams;
import mybox.to.Params;
import mybox.to.PathParams;
import mybox.to.RevisionParams;
import mybox.to.SearchParams;
import mybox.to.ThumbnailParams;
import mybox.to.UploadParams;

public interface FileService {

	public User auth(LoginParams params);
	
	public Space getDefaultSpace(Params params);
	
	public Space getSpace(Params params, String spaceId);

	public Space getSpace(PathParams params);
	
	public MetadataEntry getFiles(PathParams params);
	
	public MetadataEntry getFiles(MetadataParams params);
	
	public MetadataEntry getFolders(MetadataParams params);

	public FileEntry download(EntryParams params);
	
	public InputStream getThumbnail(ThumbnailParams params);
	
	public MetadataEntry upload(UploadParams params);
	
	public MetadataEntry upload(UploadParams params, boolean isPut);
	
	public MetadataEntry chunkedUpload(ChunkedUploadParams params);
	
	public List<MetadataEntry> getRevisions(RevisionParams params);
	
	public MetadataEntry restore(EntryParams params);
	
	public Link link(LinkParams params);
	
	public Link media(PathParams params);
	
	public List<MetadataEntry> search(SearchParams params);

	public FileOperationResponse createFolder(CreateParams params);
	
	public List<FileOperationResponse> delete(DeleteParams params);

	public List<FileOperationResponse> move(MoveParams params);
	
	public List<FileOperationResponse> copy(CopyParams params);
	
}