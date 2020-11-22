package com.upload.example;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Acl.Role;
import com.google.cloud.storage.Acl.User;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;


@WebServlet(
	    name = "UploadToStorage",
	    urlPatterns = {"/gcs"}
	)
@MultipartConfig(maxFileSize = 1024 * 1024 * 10)//10 MB
public class UploadToStorage extends HttpServlet{

	private static Storage storage = null;

	private static final String BUCKET_NAME = "[PROJECT_ID].appspot.com";
	private static final String FILE_NAME = "testimage.jpg";

	// [START init]
	static {
		storage = StorageOptions.getDefaultInstance().getService();
	}
	// [END init]

	// [START doPost]
	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

		Part filePart = req.getPart("file");
		uploadFile(filePart, BUCKET_NAME);

	}
	// [END doPost]

	// [START uploadFile]

	/**
	 * Uploads a file to Google Cloud Storage to the bucket specified in the
	 * BUCKET_NAME environment variable, appending a timestamp to end of the
	 * uploaded filename.
	 */
	@SuppressWarnings("deprecation")
	public String uploadFile(Part filePart, final String bucketName) throws IOException {

		final String fileName = filePart.getSubmittedFileName();

		// the inputstream is closed by default, so we don't need to close it here
		BlobInfo blobInfo = storage.create(BlobInfo.newBuilder(bucketName, fileName)
				.setContentType("image/jpeg")
				// Modify access list to allow all users with link to read file
				.setAcl(new ArrayList<>(Arrays.asList(Acl.of(User.ofAllUsers(), Role.READER)))).build(),
				filePart.getInputStream());
		// return the public download link
		return blobInfo.getMediaLink();
	}
	// [END uploadFile]

}
