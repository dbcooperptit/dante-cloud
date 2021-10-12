/*
 * Copyright (c) 2019-2021 Gengwei Zheng(herodotus@aliyun.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Project Name: eurynome-cloud
 * Module Name: eurynome-cloud-security
 * File Name: SecurityGlobalExceptionHandler.java
 * Author: gengwei.zheng
 * Date: 2021/05/07 11:28:07
 */

package cn.herodotus.eurynome.security.exception;

import cn.herodotus.eurynome.assistant.exception.HerodotusExceptionHandler;
import cn.herodotus.eurynome.assistant.exception.platform.PlatformException;
import cn.herodotus.eurynome.common.domain.Result;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.ClientAuthenticationException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>Description: 统一异常处理器 </p>
 *
 * @author : gengwei.zheng
 * @date : 2019/11/18 8:12
 */
@RestControllerAdvice
public class SecurityGlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(SecurityGlobalExceptionHandler.class);

    @ExceptionHandler({Exception.class, PlatformException.class})
    public static Result<String> exception(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Result<String> result = resolveException(ex, request.getRequestURI());
        response.setStatus(result.getStatus());
        return result;
    }

    /**
     * Rest Template 错误处理
     *
     * @param ex       错误
     * @param request  请求
     * @param response 响应
     * @return {@link Result<String>}
     * <p>
     * {@see :https://www.baeldung.com/spring-rest-template-error-handling}
     */
    @ExceptionHandler({HttpClientErrorException.class, HttpServerErrorException.class})
    public static Result<String> restTemplateException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Result<String> result = resolveException(ex, request.getRequestURI());
        response.setStatus(result.getStatus());
        return result;
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public static Result<String> validationMethodArgumentException(MethodArgumentNotValidException ex, HttpServletRequest request, HttpServletResponse response) {
        return validationBindException(ex, request, response);
    }

    @ExceptionHandler({BindException.class})
    public static Result<String> validationBindException(BindException ex, HttpServletRequest request, HttpServletResponse response) {
        Result<String> result = resolveException(ex, request.getRequestURI());

        BindingResult bindingResult = ex.getBindingResult();
        FieldError fieldError = bindingResult.getFieldError();
        //返回第一个错误的信息
        if (ObjectUtils.isNotEmpty(fieldError)) {
            result.validation(fieldError.getDefaultMessage(), fieldError.getCode(), fieldError.getField());
        }

        response.setStatus(result.getStatus());
        return result;
    }

    /**
     * 统一异常处理
     * AuthenticationException
     *
     * @param ex       错误
     * @param request  请求
     * @param response 响应
     * @return {@link Result<String>}
     */
    @ExceptionHandler({AuthenticationException.class})
    @ResponseBody
    public static Result<String> authenticationException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Result<String> result = resolveException(ex, request.getRequestURI());
        response.setStatus(result.getStatus());
        return result;
    }

    /**
     * OAuth2Exception
     *
     * @param ex       错误
     * @param request  请求
     * @param response 响应
     * @return {@link Result<String>}
     */
    @ExceptionHandler({OAuth2Exception.class, ClientAuthenticationException.class})
    @ResponseBody
    public static Result<String> oauth2Exception(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        Result<String> result = resolveException(ex, request.getRequestURI());
        response.setStatus(result.getStatus());
        return result;
    }

    public static Result<String> resolveException(Exception ex, String path) {
        return HerodotusExceptionHandler.resolveException(ex, path);
    }

    /**
     * 静态解析认证异常
     *
     * @param exception 错误信息
     * @return {@link Result<String>}
     */
    public static Result<String> resolveOauthException(Exception exception, String path) {

        Exception reason;

        if (exception instanceof OAuth2Exception) {
            OAuth2Exception aex = (OAuth2Exception) exception;
            reason = OAuth2Exception.create(aex.getOAuth2ErrorCode(), aex.getMessage());
        } else if (exception instanceof InsufficientAuthenticationException) {
            reason = (Exception) exception.getCause();
            log.debug("[Herodotus] |- InsufficientAuthenticationException cause content is [{}]", reason.getClass().getSimpleName());
        } else {
            reason = exception;
        }

        return resolveException(reason, path);
    }
}