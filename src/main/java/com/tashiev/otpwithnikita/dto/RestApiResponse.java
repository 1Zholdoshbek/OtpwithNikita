package com.tashiev.otpwithnikita.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestApiResponse {

    private Boolean success;
    private String message;

    public RestApiResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
