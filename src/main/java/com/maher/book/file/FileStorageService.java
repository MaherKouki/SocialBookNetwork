package com.maher.book.file;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.io.File.separator;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileStorageService {


    @Value("${application.file.upload.photos-output-path}")
    private String fileUploadPath = "./uploads";


    public String saveFile(@NonNull  MultipartFile sourceFile,
                           @NonNull Integer userId) {
         final String fileUploadSubPath  = "users" + separator + userId;
         return uploadFile(sourceFile , fileUploadSubPath);
    }

    private String uploadFile(@NonNull MultipartFile sourceFile,
                              @NonNull String fileUploadSubPath) {
        final String finalUploadPath = fileUploadPath + separator + fileUploadSubPath;
        File targetFolder = new File(finalUploadPath);
        if(!targetFolder.exists()){
            boolean folderCreated = targetFolder.mkdirs();
            if(!folderCreated){
                log.warn("failed to create the target folder");
                return null;
            }
        }

        final String fileExtension = getFileExtension(sourceFile.getOriginalFilename());
        String targetFilePath = finalUploadPath + separator + System.currentTimeMillis() + "." + fileExtension;
        Path targetPath = Paths.get(targetFilePath);
        try{
            Files.write(targetPath , sourceFile.getBytes());
            log.info("file saved to " + targetFilePath);
            return targetFilePath;
        }catch (IOException e){
            log.error("File was not saved as a message",e);
        }
        return null;
    }

    private String getFileExtension(String filename) {
        if(filename == null || filename.isEmpty()){
            return "";
        }
        int lastDotIndex = filename.lastIndexOf(".");
        if(lastDotIndex == -1){
            return "";
        }
        return filename.substring(lastDotIndex +1).toLowerCase();
    }
}
