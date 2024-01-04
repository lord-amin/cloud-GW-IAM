package com.tiddev.authorization.client.controller.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class MessageSourceService {

    private static final Logger logger = LoggerFactory.getLogger(MessageSourceService.class);

    private final MessageSource exceptionMessageSource;

    private final MessageSource validationMessageSource;

    public MessageSourceService(@Qualifier("genericExceptionMessageSource") MessageSource exceptionMessageSource,
                                @Qualifier("genericValidationMessageSource") MessageSource validationMessageSource) {
        this.exceptionMessageSource = exceptionMessageSource;
        this.validationMessageSource = validationMessageSource;
    }

    public ResponseDto<Object,Object> resolveDescription(BaseException ex) {
        ResponseDto<Object,Object> responseDto = new ResponseDto<>(ex);
        String faMessage = ex.getExceptionCode().toString();
        String enMessage = ex.getExceptionCode().toString();
        try {
            faMessage = exceptionMessageSource.getMessage(ex.getExceptionCode().toString(), null, new Locale("fa"));
            enMessage = exceptionMessageSource.getMessage(ex.getExceptionCode().toString(), null, new Locale("en"));

        } catch (NoSuchMessageException e) {
            logger.info("message not found", e);
        }

        Map<String, String> desc = new HashMap<>();

        desc.put("fa", faMessage);
        desc.put("en", enMessage);
        responseDto.getExceptionDetail().setDescription(desc);

        return responseDto;
    }

    public ResponseDto<Object,Object> resolveDescription(BusinessException ex) {
        ResponseDto<Object,Object> responseDto = new ResponseDto<>(ex);
        String faMessage = ex.getExceptionCode().toString();
        String enMessage = ex.getExceptionCode().toString();
        try {
            faMessage = exceptionMessageSource.getMessage(ex.getExceptionCode().toString(), null, new Locale("fa"));
            enMessage = exceptionMessageSource.getMessage(ex.getExceptionCode().toString(), null, new Locale("en"));

        } catch (NoSuchMessageException e) {
            logger.info("message not found", e);
        }

        Map<String, String> desc = new HashMap<>();

        desc.put("fa", faMessage);
        desc.put("en", enMessage);
        responseDto.getExceptionDetail().setDescription(desc);

        return responseDto;
    }

    public ResponseDto<Object,Object> resolveDescription(ValidationException ex) {

        ResponseDto<Object,Object> responseDto = new ResponseDto<>(ex);
        new HashMap<String, String>();

        StringBuilder faMessage = new StringBuilder();
        StringBuilder enMessage = new StringBuilder();

        ex
                .getMetadata()
                .forEach((s, o) -> {
                    String fa = getValidationMessage((String) o, new Locale("fa"));
                    String en = getValidationMessage((String) o, new Locale("en"));
                    faMessage.append(fa).append(";");
                    enMessage.append(en).append(";");
                    ex.getMetadata().put(s, fa);
                });
        Map<String, String> desc = new HashMap<>();

        desc.put("fa", faMessage.toString());
        desc.put("en", enMessage.toString());
        responseDto.getExceptionDetail().setDescription(desc);
        return responseDto;

    }

    private String getValidationMessage(String key, Locale locale) {
        key = key.replace("{", "");
        key = key.replace("}", "");
        try {
            return validationMessageSource.getMessage(key, null, locale);
        } catch (NoSuchMessageException e) {
            return key;
        }
    }
}