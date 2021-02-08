package org.prebid.pg.dimval.api.controller;

import org.apache.tomcat.util.bcel.Const;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.prebid.pg.dimval.api.AppExc;
import org.prebid.pg.dimval.api.Constants;
import org.prebid.pg.dimval.api.dto.ErrorDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

class GlobalExceptionHandlerTest {

    SoftAssertions softAssertions;

    @BeforeEach
    public void setup() {

        softAssertions = new SoftAssertions();
    }

    @Test
    void shouldReturnProperHttpStatusAndBodyOnMissingServletRequestParameter() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        WebRequest webRequest = mock(WebRequest.class);

        HttpHeaders headers = new HttpHeaders();
        HttpStatus status = HttpStatus.BAD_REQUEST;

        String paramName = "param-name";
        ErrorDto errorDto = ErrorDto.builder()
                .code(Constants.INVALID_REQUEST)
                .message(String.format("Required parameter %s is missing", paramName))
                .build();

        MissingServletRequestParameterException missingServletRequestParameterException = new MissingServletRequestParameterException(paramName, "param-type");
        softAssertions.assertThat(handler.handleMissingServletRequestParameter(
                missingServletRequestParameterException, headers, status, webRequest))
                .hasFieldOrPropertyWithValue("statusCode", HttpStatus.BAD_REQUEST)
                .hasFieldOrPropertyWithValue("body", errorDto);
        softAssertions.assertAll();
    }

    @Test
    void shouldReturnProperHttpStatusAndBodyOnAppExc() {
        String code = null;
        AppExc appExc = null;
        ErrorDto errorDto = null;
        String msg = "error message";
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        code = Constants.DATA_ERROR;
        appExc = new AppExc(code, msg);
        errorDto = ErrorDto.builder().code(code).message(msg).build();
        softAssertions.assertThat(handler.customHandleAppExc(appExc))
                .hasFieldOrPropertyWithValue("statusCode", NOT_FOUND)
                .hasFieldOrPropertyWithValue("body", errorDto);
        softAssertions.assertAll();

        code = Constants.INVALID_REQUEST;
        appExc = new AppExc(code, msg);
        errorDto = ErrorDto.builder().code(code).message(msg).build();
        softAssertions.assertThat(handler.customHandleAppExc(appExc))
                .hasFieldOrPropertyWithValue("statusCode", BAD_REQUEST)
                .hasFieldOrPropertyWithValue("body", errorDto);
        softAssertions.assertAll();


        code = Constants.SERVER_ERROR;
        appExc = new AppExc(code, msg);
        errorDto = ErrorDto.builder().code(code).message(msg).build();
        softAssertions.assertThat(handler.customHandleAppExc(appExc))
                .hasFieldOrPropertyWithValue("statusCode", INTERNAL_SERVER_ERROR)
                .hasFieldOrPropertyWithValue("body", errorDto);
        softAssertions.assertAll();

        code = "ABCD";
        appExc = new AppExc(code, msg);
        errorDto = ErrorDto.builder().code(code).message(msg).build();
        softAssertions.assertThat(handler.customHandleAppExc(appExc))
                .hasFieldOrPropertyWithValue("statusCode", INTERNAL_SERVER_ERROR)
                .hasFieldOrPropertyWithValue("body", errorDto);
        softAssertions.assertAll();

        code = null;
        msg = null;
        appExc = new AppExc(code, msg);
        errorDto = ErrorDto.builder().code(code).message(msg).build();
        softAssertions.assertThat(handler.customHandleAppExc(appExc))
                .hasFieldOrPropertyWithValue("statusCode", INTERNAL_SERVER_ERROR)
                .hasFieldOrPropertyWithValue("body", errorDto);
        softAssertions.assertAll();

    }

    @Test
    void shouldReturnProperHttpStatusAndBodyOnExc() {
        Exception exc = new Exception();
        String msg = "Unexpected server error";
        GlobalExceptionHandler handler = new GlobalExceptionHandler();

        ErrorDto errorDto = ErrorDto.builder().code(Constants.SERVER_ERROR).message(msg).build();
        softAssertions.assertThat(handler.customHandleExc(exc))
                .hasFieldOrPropertyWithValue("statusCode", INTERNAL_SERVER_ERROR)
                .hasFieldOrPropertyWithValue("body", errorDto);
        softAssertions.assertAll();

        errorDto = ErrorDto.builder().code(Constants.SERVER_ERROR).message(msg).build();
        softAssertions.assertThat(handler.customHandleExc(null))
                .hasFieldOrPropertyWithValue("statusCode", INTERNAL_SERVER_ERROR)
                .hasFieldOrPropertyWithValue("body", errorDto);
        softAssertions.assertAll();

    }
}