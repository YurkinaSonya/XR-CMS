package xr.templateservice.data.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import xr.templateservice.data.dto.model.assets.AssetRefDto;
import xr.templateservice.data.dto.model.blueprints.EnvelopeDto;
import xr.templateservice.data.dto.model.blueprints.ManifestDto;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CreateTemplatePackageRequest {
    private String ingestId;
    private EnvelopeDto envelope;
    private ManifestDto manifest;
    private List<AssetRefDto> assets;
}