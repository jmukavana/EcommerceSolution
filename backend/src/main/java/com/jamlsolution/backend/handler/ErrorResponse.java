package com.jamlsolution.backend.handler;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ErrorResponse {
   String message;
   private String code;
   private List<ValidationError> errors;

    @Data
    @Builder
    public static class ValidationError {
        private String field;
        private String code;
        private String message;
    }

}
