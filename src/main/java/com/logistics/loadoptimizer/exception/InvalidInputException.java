package com.logistics.loadoptimizer.exception;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class InvalidInputException extends RuntimeException {
    private final List<String> details;

    public InvalidInputException(String message) {
        super(message);
        this.details = Collections.emptyList();
    }

    public InvalidInputException(String message, List<String> details) {
        super(message);
        this.details = details;
    }
}
