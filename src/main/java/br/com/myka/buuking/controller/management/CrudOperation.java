package br.com.myka.buuking.controller.management;

import br.com.myka.buuking.model.request.BuukingRequest;
import br.com.myka.buuking.model.response.BuukingResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Basic crud operations contracts
 *
 * @param <REQ> Request
 * @param <RES> Response
 * @param <ID>  Main entity identifier
 */
public interface CrudOperation<REQ extends BuukingRequest, RES extends BuukingResponse, ID> {

    List<RES> findAll();

    RES findById(@Valid @NotNull ID identifier);

    RES insert(@Valid @NotNull @RequestBody REQ request);

    RES update(@Valid @NotNull ID identifier, @Valid @NotNull @RequestBody REQ request);

    void delete(@Valid @NotNull ID identifier);
}
