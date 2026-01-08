package xr.templateservice.data.dto.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ValidationIssueDto {
    private String code;
    private String path;
    private String message;
}
