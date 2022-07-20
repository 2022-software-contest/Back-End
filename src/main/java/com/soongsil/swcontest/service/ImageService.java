package com.soongsil.swcontest.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.soongsil.swcontest.dto.response.UploadImageResponseDto;
import com.soongsil.swcontest.entity.Image;
import com.soongsil.swcontest.entity.UserInfo;
import com.soongsil.swcontest.exception.imageServiceException.UploadFailException;
import com.soongsil.swcontest.exception.imageServiceException.UserDoesNotHaveImageException;
import com.soongsil.swcontest.exception.userServiceException.UserNotFoundException;
import com.soongsil.swcontest.repository.ImageRepository;
import com.soongsil.swcontest.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ImageService {
    private final AmazonS3Client amazonS3Client;
    private final UserInfoRepository userInfoRepository;
    private final ImageRepository imageRepository;

    private final String baseUrl = "https://taewoon-s3.s3.ap-northeast-2.amazonaws.com/";
    private final String dirName = "image/";

    @Value("${cloud.aws.s3.bucket}")
    private String S3Bucket;

    public UploadImageResponseDto uploadImage(String email, List<MultipartFile> multipartFiles) {
        List<String> fileNameList = new ArrayList<>();
        UserInfo user = userInfoRepository.findByEmail(email);
        if (user==null) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
        }

        multipartFiles.forEach(file -> {
            String fileName = createFileName(file.getOriginalFilename());
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.setContentLength(file.getSize());
            objectMetadata.setContentType(file.getContentType());

            String storeUrl = baseUrl + dirName + fileName;

            try(InputStream inputStream = file.getInputStream()) {
                amazonS3Client.putObject(new PutObjectRequest(S3Bucket, dirName+fileName, inputStream, objectMetadata)
                        .withCannedAcl(CannedAccessControlList.PublicRead));
                Image image = new Image(null, storeUrl, user);
                imageRepository.save(image);
            } catch(Exception e) {
                throw new UploadFailException("파일 업로드에 실패했습니다.");
            }

            fileNameList.add(storeUrl);
        });

        return new UploadImageResponseDto(fileNameList);
    }

    public void deleteFile(String email, List<String> imageUrls) {
        UserInfo user = userInfoRepository.findByEmail(email);
        if (user==null) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
        }

        List<Image> images = user.getImages();
        imageUrls.forEach(imageUrl -> {
            boolean findFlag = false;
            for(Image tmp : images) {
                if (tmp.getImageUrl().equals(imageUrl)) {
                    findFlag = true;
                    String shortImageUrl = imageUrl.substring(baseUrl.length());
                    amazonS3Client.deleteObject(new DeleteObjectRequest(S3Bucket, shortImageUrl));
                    imageRepository.delete(tmp);
                    break;
                }
            }
            if (!findFlag) {
                throw new UserDoesNotHaveImageException("유저가 해당 이미지를 가지고 있지 않습니다.");
            }
        });
    }

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(getFileExtension(fileName));
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }
}
