package com.ttop.spring.stock.error;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    
    private final LocalDateTime timestamp = LocalDateTime.now();
    private int status;
    private String code;
    private String message;
    private List<FieldError> errors;



    private ErrorResponse(final ErrorCode code, final List<FieldError> errors) {
        this.status = code.getHttpStatus().value();
        this.code = code.getCode();
        this.message = code.getDetail();
        this.errors = errors;
    }

    private ErrorResponse(final ErrorCode code) {
        this.status = code.getHttpStatus().value();
        this.code = code.getCode();
        this.message = code.getDetail();
        this.errors = new ArrayList<>();
    }


    public static ErrorResponse of(final ErrorCode code, final BindingResult bindingResult) {
        return new ErrorResponse(code, FieldError.of(bindingResult));
    }

    public static ErrorResponse of(final ErrorCode code) {
        return new ErrorResponse(code);
    }

    public static ErrorResponse of(final ErrorCode code, final List<FieldError> errors) {
        return new ErrorResponse(code, errors);
    }

    public static ErrorResponse of(MethodArgumentTypeMismatchException e) {
        final String value = e.getValue() == null ? "" : e.getValue().toString();
        final List<ErrorResponse.FieldError> errors = ErrorResponse.FieldError.of(e.getName(), value, e.getErrorCode());
        return new ErrorResponse(ErrorCode.INVALID_TYPE_VALUE, errors);
    }

    public static ErrorResponse of(MissingServletRequestParameterException e) {
        final List<ErrorResponse.FieldError> errors = ErrorResponse.FieldError.of(e.getParameterName(), "", "No given parameter");
        return new ErrorResponse(ErrorCode.NO_REQUEST_PARAMETER, errors);
    }




    public ResponseEntity<ErrorResponse> toResponseEntity() {
        return ResponseEntity
                .status(HttpStatus.valueOf(status))
                .body(this);
    }


    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class FieldError {
        private String field;
        private String value;
        private String reason;

        private FieldError(final String field, final String value, final String reason) {
            this.field = field;
            this.value = value;
            this.reason = reason;
        }

        public static List<FieldError> of(final String field, final String value, final String reason) {
            List<FieldError> fieldErrors = new ArrayList<>();
            fieldErrors.add(new FieldError(field, value, reason));
            return fieldErrors;
        }

        private static List<FieldError> of(final BindingResult bindingResult) {
            final List<org.springframework.validation.FieldError> fieldErrors = bindingResult.getFieldErrors();
            return fieldErrors.stream()
                    .map(error -> new FieldError(
                            error.getField(),
                            error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                            error.getDefaultMessage()))
                    .collect(Collectors.toList());
        }
    }


}