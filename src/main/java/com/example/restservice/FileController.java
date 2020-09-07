package com.example.restservice;

import com.example.model.FileModel;
import com.example.util.FileHandlerUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

@RestController
public class FileController {

	@Value(value="${server.uploadDir}")
	private String uploadDir;
	
	@Value("classpath:static/userlist.json")
    private Resource userfile;
	
	private Map<String,Object> userlist = new HashMap<String,Object>();
	
	private String rootDir = System.getProperty("user.dir");
	
	/**
	 * Load mock users from userlist.json file
	 */
	private void loadUserFromFile(){
		try {
			File file = userfile.getFile();
			String str = FileUtils.readFileToString(file,"UTF-8");
			JSONObject json = new JSONObject(str);
			JSONArray list = json.getJSONArray("list");
			for(int i=0;i<list.length();i++){
				JSONObject user = list.getJSONObject(i);
				userlist.put(user.getString("name"), user);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Load files contains in the upload folder
	 * @param request
	 * @param folderPath
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/files")
	public ResponseEntity<Object> getFiles(HttpServletRequest request,
			@PathParam(value = "folderPath")String folderPath) throws Exception{
		
		if(userlist.size()==0)
			loadUserFromFile();
		
		ArrayList<Object> scanFiles = new ArrayList<Object>();
		LinkedList<File> queueFiles = new LinkedList<File>();
		
		File directory = new File(rootDir+uploadDir);
		if(!directory.isDirectory()){
			throw new Exception('"' + folderPath + '"' + " input path is not a Directory , please input the right path of the Directory.");
		}
		else{
			File [] files = directory.listFiles();
			File tmp = null;
 			for(int i = 0; i < files.length; i ++){
				if(files[i].isDirectory()){
					queueFiles.add(files[i]);
				}else{
					tmp = files[i];
					FileModel m = new FileModel();
					m.setKey(scanFiles.size()+1);
					m.setName(tmp.getName());
					m.setUsername(tmp.getParentFile().getName());
					m.setPath(tmp.getPath());
					scanFiles.add(m);
				}
			}
			
			while(!queueFiles.isEmpty()){
				File headDirectory = queueFiles.removeFirst();
				File [] currentFiles = headDirectory.listFiles();
				for(int j = 0; j < currentFiles.length; j ++){
					if(currentFiles[j].isDirectory()){
						queueFiles.add(currentFiles[j]);
					}else{
						tmp = currentFiles[j];
						FileModel m = new FileModel();
						m.setKey(scanFiles.size()+1);
						m.setName(tmp.getName());
						m.setPath(tmp.getPath());
						m.setUsername(tmp.getParentFile().getName());
						scanFiles.add(m);
					}
				}
			}
		}
		
		return new ResponseEntity<>(scanFiles,HttpStatus.OK);
	}
	
	/**
	 * Authenticate by username, return Failed 
	 * if username is not "public" or does NOT exist in userlist.json
	 * @param request
	 * @param username
	 * @param files
	 * @return
	 */
	@PostMapping("upload")
    public ResponseEntity<Object> upload(MultipartHttpServletRequest request,
    		@RequestParam(value="username",required=false)String username,
    		@RequestParam(value="files",required=false)List<MultipartFile> files) {
		
		Map<String,Object> resp = new HashMap<String,Object>();
		resp.put("status", HttpStatus.OK);
		
		//Secure upload - validate if the user exists in json
		if(!userlist.containsKey(username)&&!"public".equals(username)){
			resp.put("status", HttpStatus.UNAUTHORIZED);
			resp.put("msg", "User ["+username+"] is not authorized to upload a file.");
			return new ResponseEntity<>(resp,HttpStatus.UNAUTHORIZED);
		}
		
		List<String> result = new ArrayList<String>();
		StringBuilder path = new StringBuilder(rootDir+uploadDir);
		path.append(File.separator);
		path.append(username);
		
		for(MultipartFile file : files) {
			try {
				String r = FileHandlerUtil.upload(file.getInputStream(), path.toString(), file.getOriginalFilename());
				if(r==null){
					resp.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
					result.add("Failed to upload : " + file.getOriginalFilename());
					break;
				}else{
					result.add("Uploaded : " + file.getOriginalFilename());
				}
				System.out.println(r);
			} catch (Exception e) {
				resp.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
				resp.put("msg", e.getMessage());
				e.printStackTrace();
			}
		}
		
		resp.put("result", result);
		
        return new ResponseEntity<>(resp,HttpStatus.OK);
    }
	
	@PostMapping("delete")
    public ResponseEntity<Object> delete(
    		@RequestParam(value="username",required=false)String username,
    		@RequestParam(value="owner",required=false)String owner,
    		@RequestParam(value="path",required=false)String path) {
		
		Map<String,Object> resp = new HashMap<String,Object>();
		resp.put("status", HttpStatus.OK);
		
		//Secure upload - validate if the user exists in json
		if(!userlist.containsKey(username)||(!"public".equals(owner)&&!owner.equals(username))){
			resp.put("status", HttpStatus.UNAUTHORIZED);
			resp.put("msg", "User ["+username+"] is not authorized to upload a file.");
			return new ResponseEntity<>(resp,HttpStatus.UNAUTHORIZED);
		}
		
		String msg = FileHandlerUtil.delete(path)?"File deleted Successfully":"Failed to delete";
		resp.put("msg", msg);
        return new ResponseEntity<>(resp,HttpStatus.OK);
    }
	
}
