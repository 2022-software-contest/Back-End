package com.soongsil.swcontest.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
public class FirebaseRequestDto {
    private String targetToken;
    private String title;
    private String body;
}
