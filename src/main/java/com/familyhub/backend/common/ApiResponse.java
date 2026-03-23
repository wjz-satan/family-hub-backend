package com.familyhub.backend.common;

public record ApiResponse<T>(
        int code,
        String message,
        T data,
        long timestamp,
        String traceId
) {
    public static <T> ApiResponse<T> success(T data, String traceId) {
        return new ApiResponse<>(200, "success", data, System.currentTimeMillis(), traceId);
    }
}
