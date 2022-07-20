package com.soongsil.swcontest.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class DeleteImageRequestDto {
    private List<@Pattern(regexp = "https://taewoon-s3.s3.ap-northeast-2.amazonaws.com/image/[0-9A-Za-z-]{36}\\.[0-9A-Za-z]+",
    message = "정확한 이미지 url을 정확히 넣어주세요.") String> imageUrls;
}
