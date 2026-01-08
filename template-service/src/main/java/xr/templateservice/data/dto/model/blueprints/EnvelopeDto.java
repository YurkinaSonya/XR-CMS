package xr.templateservice.data.dto.model.blueprints;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import xr.templateservice.data.dto.model.assets.EnvelopeAssetDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class EnvelopeDto {
    private String blueprintId;
    private String version;
    private List<String> capabilities;
    private List<String> targets;
    private EnvelopeIndexDto index;
    private List<EnvelopeAssetDto> assets;
}
