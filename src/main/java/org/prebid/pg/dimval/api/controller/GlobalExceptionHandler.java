package org.prebid.pg.dimval.api.controller;

import org.prebid.pg.dimval.api.AppExc;
import org.prebid.pg.dimval.api.Constants;
import org.prebid.pg.dimval.api.dto.ErrorDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status, WebRequest request
    ) {
        LOG.error("Required parameter {} is missing", ex.getParameterName());

        return new ResponseEntity<>(
                ErrorDto.builder()
                        .code(Constants.INVALID_REQUEST)
                        .message(String.format("Required parameter %s is missing", ex.getParameterName()))
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(AppExc.class)
    public ResponseEntity<ErrorDto> customHandleAppExc(AppExc e) {
        LOG.error("AppException={}, {}", e.getMessage(), e.getCode());
        if (e.getCode() == null) {
            return new ResponseEntity<>(
                    ErrorDto.builder().code(e.getCode()).message(e.getMessage()).build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
        if (e.getCode().equals(Constants.DATA_ERROR)) {
            return new ResponseEntity<>(
                    ErrorDto.builder().code(e.getCode()).message(e.getMessage()).build(), HttpStatus.NOT_FOUND
            );
        }
        if (e.getCode().equals(Constants.INVALID_REQUEST)) {
            return new ResponseEntity<>(
                    ErrorDto.builder().code(e.getCode()).message(e.getMessage()).build(), HttpStatus.BAD_REQUEST
            );
        }
        if (e.getCode().equals(Constants.SERVER_ERROR)) {
            return new ResponseEntity<>(
                    ErrorDto.builder().code(e.getCode()).message(e.getMessage()).build(),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
        return new ResponseEntity<>(
                ErrorDto.builder().code(e.getCode()).message(e.getMessage()).build(), HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> customHandleExc(Exception e) {
        if (e != null) {
            LOG.error("CatchAllException=", e);
        }
        return new ResponseEntity<>(
                ErrorDto.builder().code(Constants.SERVER_ERROR).message("Unexpected server error").build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

}
