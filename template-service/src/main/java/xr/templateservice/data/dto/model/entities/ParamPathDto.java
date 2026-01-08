package xr.templateservice.data.dto.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ParamPathDto {
    private String path;
    private String type;
    private Boolean required;
    private List<String> allowedSources;
}
