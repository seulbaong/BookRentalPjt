package com.office.library.book.admin.util;

import java.io.File;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UploadFileService {
	public String upload(MultipartFile file) {
		
		boolean result = false;
		
		String fileOriName = file.getOriginalFilename();
		String fileExtension = fileOriName.substring(fileOriName.lastIndexOf("."), fileOriName.length());
		String uploadDir = "C:\\library\\upload\\";
		//현재 저장되어있는 원래 이름을 찾아서 확장자만 똑 뗌
		UUID uuid = UUID.randomUUID();
		String uniqueName = uuid.toString().replaceAll("-", "");
		//랜덤으로 이상하고 긴 이름을 가져올건데 UUID는 항상 - 를 포함하고 있어서 지우고 가져옴
		File saveFile = new File(uploadDir + "\\" + uniqueName + fileExtension);
		//경로, UUID에서 따온 이름, 확장자 합쳐서 새로운 이름으로 만듦
		if(!saveFile.exists()) {
			//만약 지시하는 경로에 파일이 없다면
			saveFile.mkdirs();
			//만들어라!
		}
		try {
			file.transferTo(saveFile);
			result = true;
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		if(result) {
			System.out.println("[UploadFileService] FILE UPLOAD SUCCESS!!");
			return uniqueName + fileExtension;
		} else {
			System.out.println("[UploadFileService] FILE UPLOAD FAIL!!");
			
			return null;
		}
	}
}
