package br.com.myka.buuking.controller.management;

import br.com.myka.buuking.model.request.GuestRequest;
import br.com.myka.buuking.model.response.GuestResponse;
import br.com.myka.buuking.service.GuestService;
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
@RequestMapping("/management/guests")
@RequiredArgsConstructor
@Tag(name = "Guest Management", description = "Operations related to Guests")
@Transactional
public class GuestManagementEndpoint implements CrudOperation<GuestRequest, GuestResponse, UUID> {

    private final GuestService guestService;

    @Override
    @GetMapping
    @Operation(summary = "Get all guests", description = "Retrieves a list of all guests.")
    public List<GuestResponse> findAll() {
        return guestService.getAll();
    }

    @Override
    @WithSpan
    @GetMapping("/{identifier}")
    @Operation(summary = "Get guest by id", description = "Retrieves guest given its id.")
    public GuestResponse findById(@SpanAttribute @PathVariable @Valid @NotNull UUID identifier) {
        return guestService.findById(identifier);
    }

    @Override
    @PostMapping
    @Operation(summary = "Insert guest")
    public GuestResponse insert(@Valid @NotNull @RequestBody GuestRequest guestRequest) {
        return guestService.save(guestRequest);
    }

    @Override
    @WithSpan
    @PutMapping("/{identifier}")
    @Operation(summary = "Update guest", description = "Update guest given its id")
    public GuestResponse update(@SpanAttribute @PathVariable @Valid @NotNull UUID identifier,
                                   @Valid @NotNull @RequestBody GuestRequest guestRequest) {
        return guestService.update(identifier, guestRequest);
    }

    @Override
    @WithSpan
    @DeleteMapping("/{identifier}")
    @Operation(summary = "Delete guest", description = "Delete guest by its id")
    public void delete(@SpanAttribute @PathVariable @Valid @NotNull UUID identifier) {
        guestService.delete(identifier);
    }

}
