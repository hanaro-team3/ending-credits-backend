package com.hanaro.endingcredits.endingcreditsapi.domain.will.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinalMessageDto {
    private String name;
    private String relation;
    private String message;
}
