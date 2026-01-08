package xr.templateservice.data.dto.model.blueprints;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import tools.jackson.databind.JsonNode;

@Getter
@Setter
@AllArgsConstructor
public class ManifestDto {
    @JsonProperty("$schema")
    private String schema;

    private String id;
    private String name;
    private String version;

    private JsonNode nodes;
    private JsonNode components;
    private JsonNode params;
    private JsonNode bindings;
    private JsonNode repeatables;
    private JsonNode signals;
    private JsonNode ext;
}
