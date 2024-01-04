package com.tiddev.authorization.client.controller.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class ExceptionControllerAdvice {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MessageSourceService messageSourceService;

    public ExceptionControllerAdvice(MessageSourceService messageSourceService) {
        this.messageSourceService = messageSourceService;
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handle(Exception ex) {
        GeneralCodes infrastructureExceptionCode = GeneralCodes.INTERNAL_ERROR;
        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        Map<String, Object> metaData = new HashMap<>();

        if (ex instanceof AccessDeniedException) {
            infrastructureExceptionCode = GeneralCodes.ILLEGAL_ACCESS_EXCEPTION;
            httpStatus = HttpStatus.FORBIDDEN;
        }
        if (ex instanceof MethodArgumentNotValidException) {
            infrastructureExceptionCode = GeneralCodes.VALIDATION_ERROR;
            httpStatus = HttpStatus.BAD_REQUEST;
            String defaultMessage = ((MethodArgumentNotValidException) ex).getAllErrors().get(0).getDefaultMessage();
            metaData.put("message ", defaultMessage);
        }
        if (ex instanceof HttpMessageConversionException) {
            infrastructureExceptionCode = GeneralCodes.ILLEGAL_ACCESS_EXCEPTION;
            httpStatus = HttpStatus.BAD_REQUEST;

        } else {
            logger.error("exception occur: ", ex);
        }
        ResponseDto<Object, Object> responseDto = messageSourceService.resolveDescription(BusinessExceptionBuilder.newInstance()
                .withExceptionCode(infrastructureExceptionCode)
                .withMetadata(metaData)
                .build());
        logger.info("Controller HttpStatusCode:{} {}", httpStatus.value(), httpStatus.getReasonPhrase());

        return new ResponseEntity<ResponseDto<?, ?>>(responseDto, httpStatus);
    }

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<ResponseDto<?, ?>> handle(BaseException ex) {
        ExceptionCode exceptionCode = ex.getExceptionCode();
        logger.info("Controller HttpStatusCode:{}", exceptionCode);

        ResponseDto<Object, Object> responseDto = messageSourceService.resolveDescription(ex);
        return new ResponseEntity<>(responseDto, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ResponseDto<?, ?>> handle(BusinessException ex) {
        ExceptionCode exceptionCode = ex.getExceptionCode();
        logger.info("Controller HttpStatusCode:{}", exceptionCode);

        ResponseDto<Object, Object> responseDto = messageSourceService.resolveDescription(ex);
        return new ResponseEntity<>(responseDto, HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseDto<?, ?>> handle(ConstraintViolationException ex) {


        ValidationException validationException = new ValidationException();
        Map<String, Object> metadata = ex
                .getConstraintViolations()
                .stream()
                .map(ConstraintViolation.class::cast)
                .collect(Collectors.toMap(constraintViolation ->
                                ((PathImpl) constraintViolation
                                        .getPropertyPath())
                                        .getLeafNode()
                                        .asString(),
                        ConstraintViolation::getMessage));

        validationException.setMetadata(metadata);
        messageSourceService.resolveDescription(validationException);

        logger.info("Controller HttpStatusCode:{} {}", HttpStatus.NOT_ACCEPTABLE, ex.getCause());

        return new ResponseEntity<>(new ResponseDto<>(validationException), HttpStatus.NOT_ACCEPTABLE);
    }


    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ResponseDto<?, ?>> handle(ValidationException ex) {
        logger.info("Controller HttpStatusCode:{} {}", HttpStatus.NOT_ACCEPTABLE, ex.getExceptionCode());

        ResponseDto<Object, Object> responseDto = messageSourceService.resolveDescription(ex);

        return new ResponseEntity<>(responseDto, HttpStatus.NOT_ACCEPTABLE);
    }
}