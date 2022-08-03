package com.soongsil.swcontest.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class RegisterPillTimeRequestDto {
    @NotBlank
    private String pillName;
    @NotBlank
    private String pillCategory;
    private List<SpecificTime> eatTime;
    @NotBlank
    private int dateYear;
    @NotBlank
    private int dateMonth;
    @NotBlank
    private int dateDay;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class SpecificTime {
        @NotBlank
        private int hour;
        @NotBlank
        private int minutes;
        @NotBlank
        private int sec;
    }
}
