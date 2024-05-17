package com.web.back.model.responses;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CustomResponse<T> {
    T data;
    boolean error;
    int statusCode;
    String message;

    public CustomResponse<T> badRequest(String errorMessage) {
        return new CustomResponse<>(null, true, 400, errorMessage);
    }

    public CustomResponse<T> badRequest(T value, String errorMessage) {
        return new CustomResponse<>(value, true, 400, errorMessage);
    }

    public CustomResponse<T> forbidden() {
        return new CustomResponse<>(null, true, 403, "Forbidden");
    }

    public CustomResponse<T> forbidden(String errorMessage) {
        return new CustomResponse<>(null, true, 403, errorMessage);
    }

    public CustomResponse<T> internalError(String errorMessage) {
        return new CustomResponse<>(null, true, 500, errorMessage);
    }

    public CustomResponse<T> ok(T object) {
        return new CustomResponse<>(object, false, 200, "OK");
    }

    public CustomResponse<T> ok(T object, String message) {
        return new CustomResponse<>(object, false, 200, message);
    }
}
