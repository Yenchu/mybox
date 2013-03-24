package mybox.service;

import java.io.InputStream;
import java.util.List;

import mybox.common.to.ChunkedUploadParams;
import mybox.common.to.CopyParams;
import mybox.common.to.CreateParams;
import mybox.common.to.DeleteParams;
import mybox.common.to.DeltaParams;
import mybox.common.to.EntryParams;
import mybox.common.to.LinkParams;
import mybox.common.to.LoginParams;
import mybox.common.to.MetadataParams;
import mybox.common.to.MoveParams;
import mybox.common.to.Params;
import mybox.common.to.PathParams;
import mybox.common.to.RevisionParams;
import mybox.common.to.SearchParams;
import mybox.common.to.Space;
import mybox.common.to.ThumbnailParams;
import mybox.common.to.UploadParams;
import mybox.common.to.User;
import mybox.dropbox.model.DeltaPage;
import mybox.dropbox.model.FileEntry;
import mybox.dropbox.model.Link;
import mybox.dropbox.model.MetadataEntry;
import mybox.web.to.FileOperationResponse;


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
	
	public MetadataEntry chunkedUpload(ChunkedUploadParams params);
	
	public DeltaPage<MetadataEntry> delta(DeltaParams params);
	
	public List<MetadataEntry> getRevisions(RevisionParams params);
	
	public MetadataEntry restore(EntryParams params);
	
	public Link link(LinkParams params);
	
	public Link media(PathParams params);
	
	public List<MetadataEntry> search(SearchParams params);

	public FileOperationResponse createFolder(CreateParams params);
	
	public List<FileOperationResponse> delete(DeleteParams delParams);

	public List<FileOperationResponse> move(MoveParams moveParams);
	
	public List<FileOperationResponse> copy(CopyParams params);
	
}