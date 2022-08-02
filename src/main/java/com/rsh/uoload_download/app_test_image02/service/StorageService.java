package com.rsh.uoload_download.app_test_image02.service;

import java.awt.image.ImagingOpException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.sun.xml.bind.v2.runtime.output.FastInfosetStreamWriterOutput;

@Service
public class StorageService {
	
	@Value("${application.bucket.name}")
	private String bucketName;
	
	@Autowired
	private AmazonS3 s3Client;
	
	public String uploadFile(MultipartFile file) {
		File fileObj = convertMultiPartFile(file);
		String fileName = System.currentTimeMillis()+"_"+file.getOriginalFilename();
		
		s3Client.putObject(new PutObjectRequest(bucketName, fileName, fileObj));
		
		fileObj.delete();
		
		return "File upload : " + fileName;
	}
	
	
	public byte[] downloadFile(String fileName) {
		S3Object s3Object = s3Client.getObject(bucketName, fileName);
		S3ObjectInputStream inputStream = s3Object.getObjectContent();
		
		try {
			byte[] content = IOUtils.toByteArray(inputStream);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		return null;
	}
	
	public String deleteFile(String fileName) {
		s3Client.deleteObject(bucketName, fileName);
		
		return fileName + " removed.";
	}
	
	private File convertMultiPartFile(MultipartFile file) {
		File convertFile = new File(file.getOriginalFilename());
		try(FileOutputStream fileOutputStream = new FileOutputStream(convertFile)) {
			fileOutputStream.write(file.getBytes());
		}catch(IOException exception) {
			System.out.println("exception = "+exception);
		}
		return convertFile;
	}

}
