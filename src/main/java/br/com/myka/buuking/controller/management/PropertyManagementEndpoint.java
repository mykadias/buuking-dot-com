package br.com.myka.buuking.controller.management;

import br.com.myka.buuking.model.request.PropertyRequest;
import br.com.myka.buuking.model.response.PropertyResponse;
import br.com.myka.buuking.service.PropertyService;
import io.opentelemetry.instrumentation.annotations.SpanAttribute;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/management/properties")
@RequiredArgsConstructor
@Tag(name = "Property Management", description = "Operations related to Properties")
@Transactional
public class PropertyManagementEndpoint implements CrudOperation<PropertyRequest, PropertyResponse, UUID> {

    private final PropertyService propertyService;

    @Override
    @GetMapping
    @Operation(summary = "Get all properties", description = "Retrieves a list of all properties.")
    public List<PropertyResponse> findAll() {
        return propertyService.getAll();
    }

    @Override
    @WithSpan
    @GetMapping("/{identifier}")
    @Operation(summary = "Get property by id", description = "Retrieves property given its id.")
    public PropertyResponse findById(@SpanAttribute @PathVariable @Valid @NotNull UUID identifier) {
        return propertyService.findById(identifier);
    }

    @Override
    @PostMapping
    @Operation(summary = "Insert property")
    public PropertyResponse insert(@Valid @NotNull @RequestBody PropertyRequest propertyRequest) {
        return propertyService.save(propertyRequest);
    }

    @Override
    @WithSpan
    @PutMapping("/{identifier}")
    @Operation(summary = "Update property", description = "Update property given its id")
    public PropertyResponse update(@SpanAttribute @PathVariable @Valid @NotNull UUID identifier,
                                   @Valid @NotNull @RequestBody PropertyRequest propertyRequest) {
        return propertyService.update(identifier, propertyRequest);
    }

    @Override
    @WithSpan
    @DeleteMapping("/{identifier}")
    @Operation(summary = "Delete property", description = "Delete property by its id")
    public void delete(@SpanAttribute @PathVariable @Valid @NotNull UUID identifier) {
        propertyService.delete(identifier);
    }

}
