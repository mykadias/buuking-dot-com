package br.com.myka.buuking.converter;

import br.com.myka.buuking.entity.Guest;
import br.com.myka.buuking.model.request.GuestRequest;
import br.com.myka.buuking.model.response.GuestResponse;
import org.mapstruct.Mapper;

@Mapper
public interface GuestConverter extends Converter<Guest, GuestRequest, GuestResponse> {
}
