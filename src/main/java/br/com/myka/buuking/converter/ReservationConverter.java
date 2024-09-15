package br.com.myka.buuking.converter;

import br.com.myka.buuking.entity.Reservation;
import br.com.myka.buuking.model.request.ReservationRequest;
import br.com.myka.buuking.model.response.ReservationResponse;
import org.mapstruct.Mapper;

@Mapper
public interface ReservationConverter extends Converter<Reservation, ReservationRequest, ReservationResponse> {
}
