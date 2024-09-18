package br.com.myka.buuking.controller.management;

import br.com.myka.buuking.model.request.RoomRequest;
import br.com.myka.buuking.model.response.RoomResponse;
import br.com.myka.buuking.service.RoomService;
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
@RequestMapping("/management/rooms")
@RequiredArgsConstructor
@Tag(name = "Room Management", description = "Operations related to Rooms")
@Transactional
public class RoomManagementEndpoint implements CrudOperation<RoomRequest, RoomResponse, UUID> {

    private final RoomService roomService;

    @Override
    @GetMapping
    @Operation(summary = "Get all Rooms", description = "Retrieves a list of all rooms.")
    public List<RoomResponse> findAll() {
        return roomService.getAll();
    }

    @Override
    @WithSpan
    @GetMapping("/{identifier}")
    @Operation(summary = "Get room by id", description = "Get all room by identifier")
    public RoomResponse findById(@SpanAttribute @PathVariable @Valid @NotNull UUID identifier) {
        return roomService.getRoomById(identifier);
    }

    @Override
    @PostMapping
    @Operation(summary = "Insert a room")
    public RoomResponse insert(@Valid @NotNull @RequestBody RoomRequest request) {
        return roomService.save(request);
    }

    @Override
    @WithSpan
    @PutMapping("/{identifier}")
    @Operation(summary = "Update room", description = "Update room given its id")
    public RoomResponse update(@SpanAttribute @PathVariable @Valid @NotNull UUID identifier,
                               @Valid @NotNull @RequestBody RoomRequest request) {
        return roomService.update(identifier, request);
    }

    @Override
    @WithSpan
    @DeleteMapping("/{identifier}")
    @Operation(summary = "Delete room", description = "Delete room by its id")
    public void delete(@SpanAttribute @PathVariable @Valid @NotNull UUID identifier) {
        roomService.deleteRoom(identifier);
    }
}
