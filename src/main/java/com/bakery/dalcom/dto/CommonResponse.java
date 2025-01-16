package com.bakery.dalcom.dto;

public class CommonResponse<T> {
    private boolean isSuccess;
    private String message;
    private T payload;

    public CommonResponse(boolean isSuccess, String message, T payload) {
        this.isSuccess = isSuccess;
        this.message = message;
        this.payload = payload;
    }

    // Static factory methods for success and error responses
    public static <T> CommonResponse<T> success(T payload) {
        return new CommonResponse<>(true, "Success", payload);
    }

    public static <T> CommonResponse<T> error(String message) {
        return new CommonResponse<>(false, message, null);
    }

    public boolean getIsSuccess() {
        return isSuccess;
    }
    public void setIsSuccess(boolean isSuccess) {
        this.isSuccess = isSuccess;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public T getPayload() {
        return payload;
    }
    public void setPayload(T payload) {
        this.payload = payload;
    }
}
