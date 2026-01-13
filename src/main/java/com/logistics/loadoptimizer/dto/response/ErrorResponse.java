package com.logistics.loadoptimizer.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private String error;
    private String message;
    private List<String> details;
    private LocalDateTime timestamp;

    public static ErrorResponse of(String error, String message) {
        return ErrorResponse.builder()
            .error(error)
            .message(message)
            .timestamp(LocalDateTime.now())
            .build();
    }

    public static ErrorResponse of(String error, String message, List<String> details) {
        return ErrorResponse.builder()
            .error(error)
            .message(message)
            .details(details)
            .timestamp(LocalDateTime.now())
            .build();
    }
}
