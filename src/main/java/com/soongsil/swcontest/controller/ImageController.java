package com.soongsil.swcontest.controller;

import com.soongsil.swcontest.dto.request.DeleteImageRequestDto;
import com.soongsil.swcontest.dto.response.UploadImageResponseDto;
import com.soongsil.swcontest.security.AuthInfo;
import com.soongsil.swcontest.security.Authenticated;
import com.soongsil.swcontest.service.ImageService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ImageController {
    private final ImageService imageService;

    @ApiOperation("처방전 사진 올리기 다중 첨부 가능")
    @PostMapping("/v1/uploadImage")
    public UploadImageResponseDto uploadImage(@Authenticated AuthInfo authInfo, @RequestPart("image") List<MultipartFile> multipartFiles) {
        return imageService.uploadImage(authInfo.getEmail(), multipartFiles);
    }

    @ApiOperation("올린 처방전 삭제하기 다중 삭제 가능")
    @PostMapping("/v1/deleteImage")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@Authenticated AuthInfo authInfo,@Valid @RequestBody DeleteImageRequestDto deleteImageRequestDto) {
        imageService.deleteImage(authInfo.getEmail(), deleteImageRequestDto.getImageUrls());
    }
}
