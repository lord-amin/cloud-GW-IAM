package com.tiddev.authorization.client.controller.exception;

import java.util.Arrays;

public enum GeneralCodes implements ExceptionCode {
    SUCCESS("0"),
    FAILED("1"),
    INTERNAL_ERROR("2"),
    VALIDATION_ERROR("3"),
    AUTHENTICATION_REQUIRED("4"),
    EXPIRED_TOKEN("5"),
    ILLEGAL_ACCESS_EXCEPTION("6"),
    NOT_READABLE_EXCEPTION_CODE("7"),
    ENCRYPTION_ERROR("8"),
    DECRYPTION_ERROR("9"),
    AUTHENTICATION_SERVER_OUT_OF_SERVICE("10"),
    LOG_ERROR_DUPLICATE_FIELD_DETECT("11"),
    LOG_ERROR_IN_FIELD_NAMES("12"),
    LOG_ERROR_SEQUENCE_DOSE_NOT_EXIST("13"),
    LOG_ERROR_GENERAL_ERROR("14"),
    LOG_ERROR_LOG_TABLE_DOSE_NOT_EXIST("15"),
            ;

    private String value;

    private static String PREFIX = "100";

    GeneralCodes() {

    }

    GeneralCodes(String value) {
        if (value.equals("0")) {
            this.value = value;
        } else {
            this.value = ExceptionCode.removeLeftZeroPadding(value);
        }
    }

    public static GeneralCodes getByValue(String value) {
        return Arrays.stream(values())
                .filter(generalExceptionCode -> ExceptionCode.removeLeftZeroPadding(generalExceptionCode.value).equals(ExceptionCode.removeLeftZeroPadding(value)))
                .findAny()
                .orElse(null);
    }

    public static GeneralCodes getByPrefixValue(String prefixValue) {
        return Arrays.stream(values())
                .filter(generalExceptionCode -> ExceptionCode.removeLeftZeroPadding(generalExceptionCode.getPrefixValue()).equals(ExceptionCode.removeLeftZeroPadding(prefixValue)))
                .findAny()
                .orElse(null);
    }

    public static boolean contains(ExceptionCode exceptionCode) {
        return getByPrefixValue(exceptionCode.getPrefixValue()) != null;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String getPrefixValue() {
        return getPrefix() + ExceptionCode.padLeftZeros(value, LENGTH);
    }

    @Override
    public String getText() {
        return this.name();
    }

    @Override
    public String getPrefix() {
        return PREFIX;
    }

    public String getTitle() {
        return toString();
    }

}