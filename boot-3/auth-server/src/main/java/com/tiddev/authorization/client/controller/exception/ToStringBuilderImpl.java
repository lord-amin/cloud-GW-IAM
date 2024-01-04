package com.tiddev.authorization.client.controller.exception;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public class ToStringBuilderImpl implements ToStringBuilder {
    private static final String SPACE = "  ";
    private static final String NULL_TEXT = "null";
    private static final String COMMA = ",";
    private static final String L_BRACKET = "[";
    private static final String R_BRACKET = "]";
    private static final String ENTER = "\n";
    private static final String EQUAL = "=";
    private static final String PACKAGE_START_NAME = "com.tiddev";
    private static final String ERROR_TEXT = "error has been occurred for this field.";
    private StringBuilder text = new StringBuilder();
    private Object toStringObject;

    public ToStringBuilderImpl(Object toStringObject) {
        this.toStringObject = toStringObject;
    }

    public final ToStringBuilder append(String key, Object obj) {
        try {
            if (key.equals("superClass")) {
                String name = this.toStringObject.getClass().getSuperclass().getName();
                if (name.contains("com.tiddev")) {
                    StringBuilder result = new StringBuilder();
                    String s = (String) obj;
                    String[] splittedString = s.split("\n");

                    for (int i = 0; i < splittedString.length; ++i) {
                        if (i == 0) {
                            if (!splittedString[i].equals("[") && splittedString[i].length() > 1 && splittedString[i].startsWith("[")) {
                                result.append(splittedString[i].substring(1));
                                result.append("\n");
                            }
                        } else if (i == splittedString.length - 1) {
                            if (!splittedString[i].equals("]") && splittedString[i].length() > 1 && splittedString[i].endsWith("]")) {
                                result.append(splittedString[i].substring(0, splittedString[i].length() - 1));
                                result.append("\n");
                            }
                        } else {
                            result.append(splittedString[i]);
                            result.append("\n");
                        }
                    }

                    StringBuilder builder = new StringBuilder(result);
                    builder.append(this.text);
                    this.text = builder;
                }
            } else {
                this.text.append("  ");
                this.text.append(key);
                this.text.append("=");
                if (obj == null) {
                    this.text.append("null");
                    this.text.append("\n");
                } else {
                    Iterator iterator;
                    Object o;
                    if (obj instanceof Iterable) {
                        Iterable iterable = (Iterable) obj;
                        iterator = iterable.iterator();
                        if (!iterator.hasNext()) {
                            this.text.append("[");
                            this.text.append("]");
                            this.text.append("\n");
                        }

                        while (iterator.hasNext()) {
                            o = iterator.next();
                            this.text.append(o == null ? "null" : o.toString());
                            if (iterator.hasNext()) {
                                this.text.append(",");
                            } else {
                                this.text.append("\n");
                            }
                        }
                    } else if (obj.getClass().isArray()) {
                        if (obj instanceof int[]) {
                            this.text.append(Arrays.toString((int[]) obj));
                        } else if (obj instanceof long[]) {
                            this.text.append(Arrays.toString((long[]) obj));
                        } else if (obj instanceof short[]) {
                            this.text.append(Arrays.toString((short[]) obj));
                        } else if (obj instanceof boolean[]) {
                            this.text.append(Arrays.toString((boolean[]) obj));
                        } else if (obj instanceof char[]) {
                            this.text.append(Arrays.toString((char[]) obj));
                        } else if (obj instanceof byte[]) {
                            this.text.append(Arrays.toString((byte[]) obj));
                        } else if (obj instanceof float[]) {
                            this.text.append(Arrays.toString((float[]) obj));
                        } else if (obj instanceof double[]) {
                            this.text.append(Arrays.toString((double[]) obj));
                        } else if (obj instanceof Object[]) {
                            this.text.append(Arrays.toString((Object[]) obj));
                        }

                        this.text.append("\n");
                    } else if (obj instanceof Map) {
                        Map map = (Map) obj;
                        this.text.append("[");
                        iterator = map.keySet().iterator();

                        while (iterator.hasNext()) {
                            o = iterator.next();
                            Object value = map.get(o);
                            this.text.append("\n");
                            this.text.append("  ");
                            this.text.append(o.toString());
                            this.text.append("=");
                            if (value == null) {
                                this.text.append("null");
                            } else {
                                this.text.append(value.toString());
                            }
                        }

                        this.text.append("]");
                        this.text.append("\n");
                    } else {
                        this.text.append(obj.toString());
                        this.text.append("\n");
                    }
                }
            }
        } catch (Exception var8) {
            this.text.append("error has been occurred for this field.");
            this.text.append("\n");
        }

        return this;
    }

    public final ToStringBuilder leftEncryptedAppend(String key, Object obj) {
        try {
            this.text.append("  ");
            this.text.append(key);
            this.text.append("=");
            if (obj != null) {
                String s;
                if (obj instanceof String) {
                    s = (String) obj;
                } else if (obj.getClass().isPrimitive()) {
                    s = String.valueOf(obj);
                } else {
                    s = obj.toString();
                }

                if (s.length() >= 2) {
                    s = "*SEMI_ENCRYPTED:".concat(s.substring(s.length() / 2, s.length()));
                }

                this.text.append(s);
                this.text.append("\n");
            } else {
                this.text.append("null");
                this.text.append("\n");
            }
        } catch (Exception var4) {
            this.text.append("error has been occurred for this field.");
            this.text.append("\n");
        }

        return this;
    }

    public final ToStringBuilder rightEncryptedAppend(String key, Object obj) {
        try {
            this.text.append("  ");
            this.text.append(key);
            this.text.append("=");
            if (obj != null) {
                String s;
                if (obj instanceof String) {
                    s = (String) obj;
                } else if (obj.getClass().isPrimitive()) {
                    s = String.valueOf(obj);
                } else {
                    s = obj.toString();
                }

                if (s.length() >= 2) {
                    s = "*SEMI_ENCRYPTED:".concat(s.substring(0, s.length() / 2));
                }

                this.text.append(s);
                this.text.append("\n");
            } else {
                this.text.append("null");
                this.text.append("\n");
            }
        } catch (Exception var4) {
            this.text.append("error has been occurred for this field.");
            this.text.append("\n");
        }

        return this;
    }

    public final ToStringBuilder encryptedAppend(String key, Object obj) {
        try {
            this.text.append("  ");
            this.text.append(key);
            this.text.append("=");
            if (obj != null) {
                this.text.append("ENCRYPTED VALUE");
                this.text.append("\n");
            } else {
                this.text.append("null");
                this.text.append("\n");
            }
        } catch (Exception var4) {
            this.text.append("error has been occurred for this field.");
            this.text.append("\n");
        }

        return this;
    }

    public final String toString() {
        StringBuilder builder = new StringBuilder("[");
        builder.append("\n");
        builder.append(this.text);
        builder.append("]");
        this.text = builder;
        return this.text.toString();
    }

    public ToStringBuilder semiEncryptedAppend(String key, Object obj) {
        try {
            this.text.append("  ");
            this.text.append(key);
            this.text.append("=");
            if (obj != null) {
                String s;
                if (obj instanceof String) {
                    s = (String) obj;
                    if (s.length() > 2) {
                        s = "*SEMIENCRYPTED:".concat(s.length() > 10 ? s.substring(0, s.length() - 5) : s.substring(0, s.length() / 2));
                    } else {
                        s = "ENCRYPTED";
                    }
                } else {
                    s = "ENCRYPTED";
                }

                this.text.append(s);
                this.text.append("\n");
            } else {
                this.text.append("null");
                this.text.append("\n");
            }
        } catch (Exception var4) {
            this.text.append("error has been occurred for this field.");
            this.text.append("\n");
        }

        return this;
    }

    public ToStringBuilder middleEncryptedAppend(String key, Object obj) {
        try {
            this.text.append("  ");
            this.text.append(key);
            this.text.append("=");
            if (obj != null) {
                String s;
                if (obj instanceof String) {
                    s = (String) obj;
                    if (s.length() > 2) {
                        int third = s.length() / 3;
                        s = "*SEMIENCRYPTED:".concat(s.substring(0, third)).concat(StringUtils.repeat("*", third)).concat(s.substring(s.length() - third));
                    } else {
                        s = "ENCRYPTED";
                    }
                } else {
                    s = "ENCRYPTED";
                }

                this.text.append(s);
                this.text.append("\n");
            } else {
                this.text.append("null");
                this.text.append("\n");
            }
        } catch (Exception var5) {
            this.text.append("error has been occurred for this field.");
            this.text.append("\n");
        }

        return this;
    }


}