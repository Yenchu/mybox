package mybox.dropbox;

import static mybox.dropbox.DropboxUtil.getChunkedUploadUrl;
import static mybox.dropbox.DropboxUtil.getCommitChunkedUploadUrl;
import static mybox.dropbox.DropboxUtil.getSignedHeaders;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import mybox.common.to.ChunkedUploadParams;
import mybox.dropbox.model.ChunkedUploadResponse;
import mybox.dropbox.model.MetadataEntry;
import mybox.dropbox.to.DropboxUser;
import mybox.rest.JsonConverter;
import mybox.rest.RestResponse;
import mybox.rest.RestResponseHandler;
import mybox.service.RestService;

import org.apache.http.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ChunkedUploader {

	private static final Logger log = LoggerFactory.getLogger(ChunkedUploader.class);
	
	private RestService restService;
	
	private ChunkedUploadResponseHandler restResponseHandler = new ChunkedUploadResponseHandler();

	public ChunkedUploader(RestService restService) {
		this.restService = restService;
	}

	public MetadataEntry upload(ChunkedUploadParams params) throws IOException, InterruptedException {
		DropboxUser user = DropboxUtil.getUser(params);
		InputStream stream = params.getContent();
		long targetLength = params.getLength();
		int chunkSize = params.getChunkSize();
		String[] headers = getSignedHeaders(user);
		
		String uploadId = null;
		long offset = 0;
		while (offset < targetLength) {
			int nextChunkSize = (int) Math.min(chunkSize, targetLength - offset);
			
			//* for streaming: read byte by byte
			ByteArrayOutputStream bos = new ByteArrayOutputStream(nextChunkSize);
			int i = 0;
			for(int next = stream.read(); next != -1; next = stream.read()) {
			    bos.write(next);
			    if (++i >= nextChunkSize) {
			    	break;
			    }
			}
			bos.flush();
			byte[] lastChunk = bos.toByteArray();
			int bytesRead = lastChunk.length;
			
			//* for non-streaming
			//byte[] lastChunk = new byte[nextChunkSize];
			//int bytesRead = stream.read(lastChunk);
			
			if (bytesRead < lastChunk.length) {
				throw new IllegalStateException("InputStream ended after " + (offset + bytesRead) + " bytes, expecting " + lastChunk.length + " bytes.");
			}

			ChunkedUploadResponse resp = upload(headers, new ByteArrayInputStream(lastChunk), lastChunk.length, offset, uploadId);
			offset = resp.getOffset();
			uploadId = resp.getUploadId();
		}
		
		String root = params.getRoot();
		String path = params.getPath();
		List<String> qryStr = params.getParamList();
		MetadataEntry entry = commit(headers, root, path, qryStr, uploadId);
		return entry;
	}

	protected ChunkedUploadResponse upload(String[] headers, InputStream is, long length, long offset, String uploadId) {
		String[] params = null;
		if (offset == 0) {
			params = new String[0];
		} else {
			params = new String[] { "upload_id", uploadId, "offset", Long.toString(offset) };
		}

		String url = getChunkedUploadUrl(params);
		log.debug("Chunked upload url: {}", url);
		
		ChunkedUploadResponse resp = restService.put(restResponseHandler, url, is, length, headers);
		return resp;
	}
	
	protected MetadataEntry commit(String[] headers, String root, String path, List<String> qryStrList, String uploadId) {
		qryStrList.add("upload_id");
		qryStrList.add(uploadId);
		String[] qryStr = qryStrList.toArray(new String[qryStrList.size()]);
		
		String url = getCommitChunkedUploadUrl(root, path, qryStr);
		log.debug("Commit chunked upload url: {}", url);
		
		MetadataEntry entry = restService.post(MetadataEntry.class, url, headers);
		return entry;
	}
	
	protected class ChunkedUploadResponseHandler extends RestResponseHandler<ChunkedUploadResponse, String> {

		@Override
		public ChunkedUploadResponse handle(RestResponse<String> restResponse) {
			// 400 The offset parameter does not match up with what the server expects. The body of the error response will be JSON similar to the above, indicating the correct offset to upload.
			//{"expires": "Tue, 02 Oct 2012 07:30:44 +0000", "upload_id": "uxOxcvD_kj-6RuYFQXYfpw", "offset": 0, "error": "Submitted input out of alignment: got [4194304] expected [0]"}
			checkResponse(restResponse, HttpStatus.SC_BAD_REQUEST);
			ChunkedUploadResponse resp = JsonConverter.fromJson(restResponse.getBody(), ChunkedUploadResponse.class);
			return resp;
		}
	}
}
