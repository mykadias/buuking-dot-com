package br.com.myka.buuking.converter;

import br.com.myka.buuking.entity.Payment;
import br.com.myka.buuking.model.request.PaymentRequest;
import org.mapstruct.Mapper;

@Mapper
public interface PaymentConverter extends ConverterSourceToTarget<PaymentRequest, Payment> {
}
