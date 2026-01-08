package xr.templateservice.data.dto.model.blueprints;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import xr.templateservice.data.dto.model.entities.ParamPathDto;
import xr.templateservice.data.dto.model.entities.RepeatableDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class EnvelopeIndexDto {
    private List<ParamPathDto> paramPaths;
    private List<RepeatableDto> repeatables;
}