package br.com.myka.buuking.converter;

import br.com.myka.buuking.entity.BuukingEntity;
import br.com.myka.buuking.model.request.BuukingRequest;
import br.com.myka.buuking.model.response.BuukingResponse;

/**
 * Converter interface
 *
 * @param <E>   as entity to be converted
 * @param <REQ> as request model object
 * @param <RES> as response model object
 */
public interface Converter<E extends BuukingEntity, REQ extends BuukingRequest, RES extends BuukingResponse> {
    E convert(REQ request);

    RES convert(E entity);
}
