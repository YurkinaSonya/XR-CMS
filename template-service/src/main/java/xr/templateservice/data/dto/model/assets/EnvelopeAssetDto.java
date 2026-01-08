package xr.templateservice.data.dto.model.assets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EnvelopeAssetDto {
    private String id;
    private String type;
    private String file;
}
