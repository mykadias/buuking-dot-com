package br.com.myka.buuking.model.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AvailablePropertiesResponse {
    private List<PropertyResponse> properties;
}
