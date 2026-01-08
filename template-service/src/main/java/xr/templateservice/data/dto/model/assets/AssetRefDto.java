package xr.templateservice.data.dto.model.assets;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tools.jackson.databind.JsonNode;

@Getter
@Setter
@AllArgsConstructor
public class AssetRefDto {
    private String id;
    private String type;
    private String url;
    private String sha256;
    private JsonNode meta;
}