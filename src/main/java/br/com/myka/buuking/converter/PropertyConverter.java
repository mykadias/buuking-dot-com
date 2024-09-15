package br.com.myka.buuking.converter;

import br.com.myka.buuking.entity.Property;
import br.com.myka.buuking.model.request.PropertyRequest;
import br.com.myka.buuking.model.response.PropertyResponse;
import org.mapstruct.Mapper;

@Mapper
public interface PropertyConverter extends Converter<Property, PropertyRequest, PropertyResponse> {
}
