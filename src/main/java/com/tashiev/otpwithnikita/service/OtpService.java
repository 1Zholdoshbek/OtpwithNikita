package com.tashiev.otpwithnikita.service;

import com.tashiev.otpwithnikita.dto.OtpData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class OtpService {
    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${otp.expiry.minutes:3}")
    private int otpExpiryMinutes;

    @Value("${otp.max.attempts:3}")
    private int maxAttempts;

    @Value("${otp.block.hours:1}")
    private int blockHours;

    private static final String OTP_KEY_PREFIX = "otp:";
    private static final String BLOCK_KEY_PREFIX = "otp:block:";
    private static final String ATTEMPTS_KEY_PREFIX = "otp:attempts:";

    public OtpService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveOtp(String phoneNumber, String otpCode) {
        if (isUserBlocked(phoneNumber)) {
            throw new RuntimeException("Превышено количество попыток. Повторите через " + blockHours + " час");
        }

        // Проверка количества попыток отправки OTP
        String attemptsKey = ATTEMPTS_KEY_PREFIX + phoneNumber;
        Integer attempts = (Integer) redisTemplate.opsForValue().get(attemptsKey);
        int currentAttempts = (attempts == null) ? 1 : attempts + 1;

        if (currentAttempts > maxAttempts) {
            String blockKey = BLOCK_KEY_PREFIX + phoneNumber;
            redisTemplate.opsForValue().set(blockKey, true);
            redisTemplate.expire(blockKey, blockHours, TimeUnit.HOURS);
            throw new RuntimeException("Превышено количество попыток. Повторите через " + blockHours + " час");
        }

        redisTemplate.opsForValue().set(attemptsKey, currentAttempts);
        redisTemplate.expire(attemptsKey, blockHours, TimeUnit.HOURS);

        OtpData otpData = OtpData.builder()
                .phoneNumber(phoneNumber)
                .otpCode(otpCode)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes))
                .failedAttempts(0)
                .build();

        String otpKey = OTP_KEY_PREFIX + phoneNumber;
        redisTemplate.opsForValue().set(otpKey, otpData);
        redisTemplate.expire(otpKey, otpExpiryMinutes, TimeUnit.MINUTES);

        log.info("OTP сохранен для номера телефона: {}", phoneNumber);
    }

    public boolean verifyOtp(String phoneNumber, String otpCode) {
        if (isUserBlocked(phoneNumber)) {
            throw new RuntimeException("Превышено количество попыток. Повторите через " + blockHours + " час");
        }

        String otpKey = OTP_KEY_PREFIX + phoneNumber;
        Object storedValue = redisTemplate.opsForValue().get(otpKey);

        OtpData otpData = null;
        if (storedValue instanceof OtpData) {
            otpData = (OtpData) storedValue;
        } else if (storedValue instanceof Map) {
            Map<String, Object> storedMap = (Map<String, Object>) storedValue;
            otpData = convertMapToOtpData(storedMap);
        }

        if (otpData != null &&
                otpCode.equals(otpData.getOtpCode()) &&
                !otpData.isVerified() &&
                LocalDateTime.now().isBefore(otpData.getExpiresAt())) {

            otpData.setVerified(true);
            redisTemplate.opsForValue().set(otpKey, otpData);

            String attemptsKey = ATTEMPTS_KEY_PREFIX + phoneNumber;
            redisTemplate.delete(attemptsKey);

            log.info("OTP успешно проверен для номера телефона: {}", phoneNumber);
            return true;
        }

        String attemptsKey = ATTEMPTS_KEY_PREFIX + phoneNumber;
        Integer attempts = (Integer) redisTemplate.opsForValue().get(attemptsKey);
        int currentAttempts = (attempts == null) ? 1 : attempts + 1;

        redisTemplate.opsForValue().set(attemptsKey, currentAttempts);

        if (currentAttempts >= maxAttempts) {
            String blockKey = BLOCK_KEY_PREFIX + phoneNumber;
            redisTemplate.opsForValue().set(blockKey, true);
            redisTemplate.expire(blockKey, blockHours, TimeUnit.HOURS);

            log.warn("Пользователь заблокирован на {} час(ов) для номера: {}", blockHours, phoneNumber);
        }

        log.warn("Не удалось проверить OTP для номера телефона: {}", phoneNumber);
        return false;
    }

    private OtpData convertMapToOtpData(Map<String, Object> storedMap) {
        return OtpData.builder()
                .phoneNumber((String) storedMap.get("phoneNumber"))
                .otpCode((String) storedMap.get("otpCode"))
                .createdAt(convertToLocalDateTime(storedMap.get("createdAt")))
                .expiresAt(convertToLocalDateTime(storedMap.get("expiresAt")))
                .isVerified(Boolean.TRUE.equals(storedMap.get("verified")))
                .failedAttempts(storedMap.get("failedAttempts") != null
                        ? ((Number) storedMap.get("failedAttempts")).intValue()
                        : 0)
                .build();
    }

    private LocalDateTime convertToLocalDateTime(Object dateTime) {
        if (dateTime instanceof String) {
            return LocalDateTime.parse((String) dateTime);
        } else if (dateTime instanceof LocalDateTime) {
            return (LocalDateTime) dateTime;
        }
        return null;
    }

    public boolean canSendNewOtp(String phoneNumber) {
        if (isUserBlocked(phoneNumber)) {
            return false;
        }

        String otpKey = OTP_KEY_PREFIX + phoneNumber;
        Object storedValue = redisTemplate.opsForValue().get(otpKey);

        OtpData otpData = null;
        if (storedValue instanceof OtpData) {
            otpData = (OtpData) storedValue;
        } else if (storedValue instanceof Map) {
            Map<String, Object> storedMap = (Map<String, Object>) storedValue;
            otpData = convertMapToOtpData(storedMap);
        }

        return otpData == null || LocalDateTime.now().isAfter(otpData.getExpiresAt());
    }

    private boolean isUserBlocked(String phoneNumber) {
        String blockKey = BLOCK_KEY_PREFIX + phoneNumber;

        return Boolean.TRUE.equals(redisTemplate.hasKey(blockKey));
    }
}