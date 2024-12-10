package com.maher.book.file;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Service
@RequiredArgsConstructor
public class FileStorageService {


    @Value("${application.file.upload.photos-output-path}")
    private String fileUploadPath = "./uploads";


    public String saveFile(@NonNull  MultipartFile sourceFile,
                           @NonNull Integer bookId,
                           @NonNull Integer userId) {
         final String fileUploadSubPath  = "users" + File.separator + userId;
         return uploadFile(sourceFile , fileUploadSubPath);
    }

    private String uploadFile(@NonNull MultipartFile sourceFile,
                              @NonNull String fileUploadSubPath) {
        return null;
    }
}
