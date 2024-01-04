package com.tiddev.authorization.client.controller.exception;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Set;

@Configuration("Generic-Exception-Config")
public class GenericExceptionConfig {

    private final ReloadableResourceBundleMessageSource exceptionMessageSource;
    private final ReloadableResourceBundleMessageSource validationMessageSource;

    public GenericExceptionConfig(@Qualifier("exceptionMessageSource") ReloadableResourceBundleMessageSource exceptionMessageSource,
                                  @Qualifier("validationMessageSource") ReloadableResourceBundleMessageSource validationMessageSource) {
        this.exceptionMessageSource = exceptionMessageSource;
        this.validationMessageSource = validationMessageSource;
    }

    @Bean("genericExceptionMessageSource")
    public MessageSource exceptionMessageSource() {
        Set<String> basenameSet = exceptionMessageSource.getBasenameSet();
        basenameSet.add("classpath:Generic_Exception_Message");

        exceptionMessageSource.setBasenames(basenameSet.toArray(new String[0]));
        return exceptionMessageSource;
    }

    @Bean(name="genericValidationMessageSource")
    public MessageSource validatorMessageSource() {
        Set<String> basenameSet = validationMessageSource.getBasenameSet();
        basenameSet.add("classpath:Generic_ValidationMessagesBase");

        validationMessageSource.setBasenames(basenameSet.toArray(new String[0]));
        return validationMessageSource;
    }
}