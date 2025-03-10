package com.knameless.OwlBank.controller;

import com.knameless.OwlBank.utils.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalResponseHandlerTest {

    private GlobalResponseHandler globalResponseHandler;
    private MethodParameter methodParameter;
    private ServerHttpRequest serverHttpRequest;
    private ServerHttpResponse serverHttpResponse;
    private HttpMessageConverter<?> httpMessageConverter;

    @BeforeEach
    void setUp() {
        globalResponseHandler = new GlobalResponseHandler();
        methodParameter = mock(MethodParameter.class);
        serverHttpRequest = mock(ServerHttpRequest.class);
        serverHttpResponse = mock(ServerHttpResponse.class);
        httpMessageConverter = mock(HttpMessageConverter.class);
    }

    @Test
    void supports_ShouldReturnTrue_WhenReturnTypeIsNotApiResponse() {
        when(methodParameter.getParameterType()).thenReturn((Class) String.class);
        assertTrue(globalResponseHandler.supports(methodParameter, (Class<? extends HttpMessageConverter<?>>) httpMessageConverter.getClass()));
    }

    @Test
    void supports_ShouldReturnFalse_WhenReturnTypeIsApiResponse() {
        when(methodParameter.getParameterType()).thenReturn((Class) ApiResponse.class);
        assertFalse(globalResponseHandler.supports(methodParameter, (Class<? extends HttpMessageConverter<?>>) httpMessageConverter.getClass()));
    }

    @Test
    void beforeBodyWrite_ShouldWrapNonApiResponseInApiResponse() {
        String responseBody = "Test Response";
        Object result = globalResponseHandler.beforeBodyWrite(
                (Object) responseBody, methodParameter, MediaType.APPLICATION_JSON, (Class<? extends HttpMessageConverter<?>>) httpMessageConverter.getClass(), serverHttpRequest, serverHttpResponse);

        assertTrue(result instanceof ApiResponse);
        assertEquals(responseBody, ((ApiResponse<?>) result).getData());
    }

    @Test
    void beforeBodyWrite_ShouldNotWrapIfAlreadyApiResponse() {
        ApiResponse<String> responseBody = ApiResponse.success("Test Response");
        Object result = globalResponseHandler.beforeBodyWrite(
                (Object) responseBody, methodParameter, MediaType.APPLICATION_JSON, (Class<? extends HttpMessageConverter<?>>) httpMessageConverter.getClass(), serverHttpRequest, serverHttpResponse);

        assertSame(responseBody, result);
    }
}
