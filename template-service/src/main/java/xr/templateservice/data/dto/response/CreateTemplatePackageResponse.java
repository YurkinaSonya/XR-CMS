package xr.templateservice.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import xr.templateservice.data.dto.model.entities.TemplateIndexProjection;
import xr.templateservice.data.dto.model.ValidationIssueDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CreateTemplatePackageResponse {
    private String packageId;
    private String blueprintId;
    private String version;
    private String status;
    private List<ValidationIssueDto> warnings;
    private TemplateIndexProjection index;
}
