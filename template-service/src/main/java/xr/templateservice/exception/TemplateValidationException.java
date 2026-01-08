package xr.templateservice.exception;

import lombok.Getter;

import java.util.List;

@Getter
public class TemplateValidationException extends RuntimeException {

    private final List<?> issues;

    public TemplateValidationException(String message) {
        super(message);
        this.issues = List.of();
    }

    public TemplateValidationException(String message, List<?> issues) {
        super(message);
        this.issues = issues;
    }
}