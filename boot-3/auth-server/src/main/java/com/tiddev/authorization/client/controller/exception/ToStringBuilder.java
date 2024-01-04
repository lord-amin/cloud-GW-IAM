package com.tiddev.authorization.client.controller.exception;

public interface ToStringBuilder {
    ToStringBuilder append(String var1, Object var2);

    ToStringBuilder leftEncryptedAppend(String var1, Object var2);

    ToStringBuilder rightEncryptedAppend(String var1, Object var2);

    ToStringBuilder encryptedAppend(String var1, Object var2);

    ToStringBuilder semiEncryptedAppend(String var1, Object var2);

    ToStringBuilder middleEncryptedAppend(String var1, Object var2);
}