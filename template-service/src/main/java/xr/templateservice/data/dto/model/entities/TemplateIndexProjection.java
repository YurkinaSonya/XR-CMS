package xr.templateservice.data.dto.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class TemplateIndexProjection {
    private List<ParamPathDto> paramPaths;
    private List<RepeatableDto> repeatables;
}