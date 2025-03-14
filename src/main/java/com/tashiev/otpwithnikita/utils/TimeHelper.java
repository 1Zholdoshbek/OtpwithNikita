package com.tashiev.otpwithnikita.utils;


import java.time.format.DateTimeFormatter;

public class TimeHelper {

    public static final String TIME_FORMAT = "HH:mm";
    public static final String EXAM_TIME_FORMAT = "HH:mm:ss";
    public static final String DATE_FORMAT = "dd.MM.yyyy";
    public static final String DATE_FORMAT_V2 = "dd/MM/yyyy";
    public static final String DATE_FORMAT_V3 = "dd-MM-yyyy";
    public static final String DATE_REVERSE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "dd.MM.yyyy HH:mm";
    public static final String DATE_TIME_SS_FORMAT = "dd.MM.yyyy HH:mm:ss";
    public static final String DATE_TIME_SCHEDULE_FORMAT = "yyyy-MM-dd HH:mm";
    public static final String DATE_TIME_REVERSE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_PIN_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSSSS";

    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_FORMAT);
    public static final DateTimeFormatter EXAM_TIME_FORMATTER = DateTimeFormatter.ofPattern(EXAM_TIME_FORMAT);
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    public static final DateTimeFormatter DATE_FORMATTER_V2 = DateTimeFormatter.ofPattern(DATE_FORMAT_V2);
    public static final DateTimeFormatter DATE_FORMATTER_V3 = DateTimeFormatter.ofPattern(DATE_FORMAT_V3);
    public static final DateTimeFormatter DATE_REVERSE_FORMATTER = DateTimeFormatter.ofPattern(DATE_REVERSE_FORMAT);
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
    public static final DateTimeFormatter DATE_TIME_SS_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_SS_FORMAT);
    public static final DateTimeFormatter DATE_TIME_SCHEDULE_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_SCHEDULE_FORMAT);
    public static final DateTimeFormatter DATE_TIME_REVERSE_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_REVERSE_FORMAT);
    public static final DateTimeFormatter DATE_TIME_PIN_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PIN_FORMAT);

    public static final DateTimeFormatter ISO_LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
}