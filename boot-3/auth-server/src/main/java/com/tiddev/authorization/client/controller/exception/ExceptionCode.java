package com.tiddev.authorization.client.controller.exception;

public interface ExceptionCode {

    Integer LENGTH = 4;

    /**
     * this method retrieve pure VALUE of error code
     *
     * @return
     */
    String getValue();

    /**
     * this method retrieve VALUE with prefix of error code
     *
     * @return
     */
    String getPrefixValue();

    /**
     * this method retrieve TEXT of error code
     *
     * @return
     */
    String getText();

    /**
     * Set Prefix for each ErrorCode enum that implement this Class
     * Notice : all Classes implement this class Must have unique prefix
     *
     * @return String Prefix
     */
    String getPrefix();



    static String padLeftZeros(String str, int n) {
        return String.format("%1$" + n + "s", str).replace(' ', '0');
    }

    static String removeLeftZeroPadding(String value) {
        while (value.startsWith("0")) {
            value = value.substring(1);
        }
        return value;
    }
}