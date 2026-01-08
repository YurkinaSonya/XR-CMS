package xr.templateservice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xr.templateservice.data.dto.request.CreateTemplatePackageRequest;
import xr.templateservice.data.dto.response.CreateTemplatePackageResponse;
import xr.templateservice.exception.TemplateValidationException;
import xr.templateservice.service.TemplatePackageService;

@RestController
@RequestMapping("/blueprints")
public class TemplatePackageController {

    private final TemplatePackageService packageService;

    public TemplatePackageController(TemplatePackageService packageService) {
        this.packageService = packageService;
    }

    @PostMapping("/packages")
    public ResponseEntity<CreateTemplatePackageResponse> createPackage(
            @RequestBody CreateTemplatePackageRequest request
    ) {
        CreateTemplatePackageResponse response = packageService.createPackage(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @ExceptionHandler(TemplateValidationException.class)
    public ResponseEntity<?> handleValidation(TemplateValidationException ex) {
        return ResponseEntity.unprocessableEntity().body(
                new Object() {
                    public final String message = ex.getMessage();
                    public final Object issues = ex.getIssues();
                }
        );
    }
}