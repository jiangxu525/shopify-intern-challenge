package com.example.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;

public class FileHandlerUtil {
	/**
	 * upload singe file
	 *
	 * @param inputStream file input stream
	 * @param path        file path like image/
	 * @param filename    file name like test.jpg
	 * @return returns the file path if the upload is completed，otherwise returns null
	 * @throws Exception 
	 */
	public static String upload(InputStream inputStream, String path, String filename) throws Exception {
		//create folder
		createDirIfNotExists(path);
		//save files
		File uploadFile = new File(path, filename);
		try {
			FileUtils.copyInputStreamToFile(inputStream, uploadFile);
		} catch (IOException e) {
			throw new Exception("Failed to upload!");
		}
		return uploadFile.getPath();
	}
	
	/**
	 * create file path
	 */
	private static void createDirIfNotExists(String path) {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
	}
	
	/**
	 * delete a file
	 *
	 * @param path file path like /upload/image/test.jpg
	 * @return successfully deleting returns true, otherwise returns false
	 */
	public static boolean delete(String path) {
		File file = new File(path);
		return file.exists() == true ? file.delete() : true;
	}
	
	/**
	 * get the root file directory like http:// + ip + port
	 *
	 * @param request
	 * @return
	 */
	public static String getServerIPPort(HttpServletRequest request) {
		//+ ":" + request.getServerPort()
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
	}
}
