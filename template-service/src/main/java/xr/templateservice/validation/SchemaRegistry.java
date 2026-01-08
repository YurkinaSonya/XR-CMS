package xr.templateservice.validation;

import com.fasterxml.jackson.databind.jsonschema.JsonSchema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class SchemaRegistry {

    private final ObjectMapper mapper;
    private final JsonSchemaFactory factory;
    private final Map<String, JsonSchema> cache = new ConcurrentHashMap<>();

    public SchemaRegistry(ObjectMapper mapper) {
        this.mapper = mapper;
        this.factory = JsonSchemaFactory.builder(JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7)).build();
    }

    public JsonSchema getSchema(String schemaUri) {
        return cache.computeIfAbsent(schemaUri, this::loadSchema);
    }

    private JsonSchema loadSchema(String schemaUri) {
        try {
            JsonNode schemaNode;
            if (schemaUri.startsWith("classpath:")) {
                String path = schemaUri.substring("classpath:".length());
                try (InputStream in = getClass().getResourceAsStream(path)) {
                    if (in == null) throw new IllegalArgumentException("Schema not found in classpath: " + path);
                    schemaNode = mapper.readTree(in);
                }
            } else if (schemaUri.startsWith("http://") || schemaUri.startsWith("https://")) {
                URL url = new URL(schemaUri);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout((int) Duration.ofSeconds(5).toMillis());
                conn.setReadTimeout((int) Duration.ofSeconds(10).toMillis());
                conn.setRequestProperty("Accept", "application/json");
                try (InputStream in = conn.getInputStream()) {
                    schemaNode = mapper.readTree(in);
                }
            } else {
                try (InputStream in = getClass().getResourceAsStream(schemaUri)) {
                    if (in == null) throw new IllegalArgumentException("Unknown schema URI scheme: " + schemaUri);
                    schemaNode = mapper.readTree(in);
                }
            }

            return factory.getSchema(schemaNode);
        } catch (Exception e) {
            log.error("Failed to load JSON Schema from {}", schemaUri, e);
            throw new RuntimeException("Failed to load JSON Schema: " + schemaUri, e);
        }
    }
}