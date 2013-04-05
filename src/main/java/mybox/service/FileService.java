package mybox.service;

import java.io.InputStream;
import java.util.List;

import mybox.model.ChunkedUploadParams;
import mybox.model.CopyParams;
import mybox.model.CreateParams;
import mybox.model.DeleteParams;
import mybox.model.DeltaPage;
import mybox.model.DeltaParams;
import mybox.model.EntryParams;
import mybox.model.FileEntry;
import mybox.model.Link;
import mybox.model.LinkParams;
import mybox.model.LoginParams;
import mybox.model.MetadataEntry;
import mybox.model.MetadataParams;
import mybox.model.MoveParams;
import mybox.model.Params;
import mybox.model.PathParams;
import mybox.model.RevisionParams;
import mybox.model.SearchParams;
import mybox.model.Space;
import mybox.model.ThumbnailParams;
import mybox.model.UploadParams;
import mybox.model.User;
import mybox.to.FileOperationResponse;

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
	
	public List<FileOperationResponse> delete(DeleteParams params);

	public List<FileOperationResponse> move(MoveParams params);
	
	public List<FileOperationResponse> copy(CopyParams params);
	
}