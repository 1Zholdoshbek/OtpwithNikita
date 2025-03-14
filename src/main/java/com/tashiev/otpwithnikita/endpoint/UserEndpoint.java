package com.tashiev.otpwithnikita.endpoint;


import com.tashiev.otpwithnikita.annotation.Endpoint;
import com.tashiev.otpwithnikita.dto.RestApiResponse;
import com.tashiev.otpwithnikita.service.OtpService;
import com.tashiev.otpwithnikita.service.SmsService;
import com.tashiev.otpwithnikita.utils.GenerateOtp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

@Slf4j
@Endpoint
public class UserEndpoint {
    private final SmsService smsService;
    private final OtpService otpService;

    public UserEndpoint(SmsService smsService, OtpService otpService) {
        this.smsService = smsService;
        this.otpService = otpService;
    }


    public ResponseEntity<RestApiResponse> generateOTP(String phoneNumber) {
        if (!otpService.canSendNewOtp(phoneNumber)) {
            log.warn("Запрос на отправку нового OTP для {} отклонен: слишком часто", phoneNumber);
            RestApiResponse otpResponse = new RestApiResponse(false, "Пожалуйста, подождите, прежде чем запрашивать новый OTP.");
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(otpResponse);
        }

        String otpCode = GenerateOtp.generateOtpCode(6);
        otpService.saveOtp(phoneNumber, otpCode);

        Map<String, String> result = smsService.sendOtp(phoneNumber, otpCode, System.currentTimeMillis());
        String smsStatus = result.get("status");

        if ("11".equals(smsStatus) || "0".equals(smsStatus)) {
            log.info("OTP успешно отправлен на номер телефона: {}", phoneNumber);
            RestApiResponse otpResponse = new RestApiResponse(true, "OTP успешно отправлен");
            return ResponseEntity.ok(otpResponse);
        } else {
            log.error("Не удалось отправить OTP на номер телефона: {}", phoneNumber);
            RestApiResponse otpResponse = new RestApiResponse(false, "Не удалось отправить OTP");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(otpResponse);
        }
    }

    public ResponseEntity<RestApiResponse> verifyOtp(String phoneNumber, String otpCode) {
        boolean isValid = otpService.verifyOtp(phoneNumber, otpCode);
        if (isValid) {
            log.info("OTP успешно проверен для номера телефона: {}", phoneNumber);
            RestApiResponse otpResponse = new RestApiResponse(true, "OTP verified successfully");
            log.info("Response Body: {}", otpResponse);
            return ResponseEntity.ok(otpResponse);
        } else {
            log.warn("Ошибка проверки OTP для номера телефона: {}", phoneNumber);
            RestApiResponse otpResponse = new RestApiResponse(false, "Invalid or expired OTP");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(otpResponse);
        }
    }

}
