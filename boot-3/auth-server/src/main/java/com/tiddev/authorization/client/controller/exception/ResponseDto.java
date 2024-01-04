package com.tiddev.authorization.client.controller.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@SuperBuilder(buildMethodName = "newInstance", setterPrefix = "with")
public class ResponseDto<R,CODE> implements Serializable {
    private CODE resultCode ;
    private String message;
    private ExceptionDetail exceptionDetail;
    private R result;

    public ResponseDto(R result) {
        this.result = result;
    }

    public ResponseDto(BusinessException e) {
        this.resultCode = (CODE) e.getExceptionCode().getPrefixValue();
        ExceptionDetail exceptionDetail = new ExceptionDetail();
        exceptionDetail.setKey(e.getExceptionCode().toString());
        exceptionDetail.setMetadata(e.getMetadata());
        this.exceptionDetail = exceptionDetail;
    }

    public ResponseDto(ValidationException e) {
        this.resultCode = (CODE) e.getExceptionCode().getPrefixValue();
        this.exceptionDetail = new ExceptionDetail(e.getExceptionCode().toString(), (Map)null, e.getMetadata());
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}