package com.tashiev.otpwithnikita.utils;

import java.util.Random;

public class GenerateOtp {

    public static String generateOtpCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        System.out.println(sb);
        return sb.toString();
    }
}
