package com.tashiev.otpwithnikita.controller;

import com.tashiev.otpwithnikita.dto.RestApiResponse;
import com.tashiev.otpwithnikita.endpoint.UserEndpoint;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class OtpController {
    private final UserEndpoint userEndpoint;

    public OtpController(UserEndpoint userEndpoint) {
        this.userEndpoint = userEndpoint;
    }

    @PostMapping("/send-otp")
    @Operation(summary = "Отправка OTP кода", description = "Отправляет одноразовый пароль (OTP) на указанный номер телефона для верификации пользователя")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "OTP успешно отправлен"),
            @ApiResponse(responseCode = "400", description = "Ошибка отправки OTP")
    })
    public ResponseEntity<RestApiResponse> sendOtp(
            @RequestParam @NotBlank @Size(min = 12, max = 15, message = "Номер телефона должен содержать от 12 до 15 символов") String phoneNumber) {
        return userEndpoint.generateOTP(phoneNumber);
    }
    @PostMapping("/verify-otp")
    @Operation(summary = "Проверка кода для верификации", description = "Проверяет корректность OTP кода")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Пользователь успешно верифицирован"),
            @ApiResponse(responseCode = "400", description = "Ошибка верификации")
    })

    public ResponseEntity<RestApiResponse> verifyOtp(
            @RequestParam @NotBlank(message = "Номер телефона обязателен для ввода")
            @Size(min = 12, max = 15, message = "Номер телефона должен содержать от 12 до 15 символов (пример: 996777112233)")
            String phoneNumber,
            @RequestParam @NotBlank(message = "OTP код обязателен для ввода")
            @Size(min = 6, max = 6, message = "OTP код должен содержать ровно 6 символов")
            String otpCode) {
        return userEndpoint.verifyOtp(phoneNumber, otpCode);
    }

}
