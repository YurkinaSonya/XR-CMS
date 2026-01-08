package xr.templateservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.ObjectMapper;
import xr.templateservice.data.dto.model.ValidationIssueDto;
import xr.templateservice.data.dto.response.ErrorResponse;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final ObjectMapper mapper;

    private String extractRequestId(HttpServletRequest request) {
        String rid = request.getHeader("X-Request-ID");
        return (rid == null || rid.isBlank()) ? null : rid;
    }

    @ExceptionHandler(TemplateValidationException.class)
    public ResponseEntity<ErrorResponse> handleBlueprintValidation(
            TemplateValidationException ex,
            HttpServletRequest request
    ) {
        List<Object> details;
        if (ex.getIssues() != null) {
            details = (List<Object>) ex.getIssues();
        } else {
            details = List.of(ex.getIssues());
        }

        ErrorResponse body = new ErrorResponse(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                HttpStatus.UNPROCESSABLE_ENTITY.getReasonPhrase(),
                ex.getMessage(),
                Instant.now(),
                extractRequestId(request),
                details
        );

        return ResponseEntity.unprocessableEntity().body(body);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        log.debug("Malformed JSON request", ex);

        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Malformed JSON request: " + ex.getMostSpecificCause().getMessage(),
                Instant.now(),
                extractRequestId(request),
                null
        );

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<ValidationIssueDto> issues = ex.getBindingResult().getFieldErrors().stream()
                .map(f -> new ValidationIssueDto(
                        "FIELD_INVALID",
                        f.getField(),
                        f.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Request validation failed",
                Instant.now(),
                extractRequestId(request),
                new ArrayList<>(issues)
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        log.warn("Data integrity violation", ex);
        ErrorResponse body = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                "Database integrity violation: " + ex.getMostSpecificCause().getMessage(),
                Instant.now(),
                extractRequestId(request),
                null
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception caught by GlobalExceptionHandler", ex);

        ErrorResponse body = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Internal server error",
                Instant.now(),
                extractRequestId(request),
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}