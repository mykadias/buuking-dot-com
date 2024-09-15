package br.com.myka.buuking.service;

import br.com.myka.buuking.converter.PropertyConverter;
import br.com.myka.buuking.entity.Property;
import br.com.myka.buuking.exception.PropertyNotFoundException;
import br.com.myka.buuking.exception.ValidationException;
import br.com.myka.buuking.model.request.PropertyRequest;
import br.com.myka.buuking.model.response.PropertyResponse;
import br.com.myka.buuking.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PropertyService {

    private final PropertyRepository propertyRepository;

    private final PropertyConverter propertyConverter;

    public List<PropertyResponse> getAll() {
        return propertyRepository.findAll().stream().map(propertyConverter::convert).toList();
    }

    public PropertyResponse save(PropertyRequest propertyRequest) {
        var property = propertyConverter.convert(propertyRequest);
        if (!propertyRepository.findAllByNameIgnoreCase(property.getName()).isEmpty()) {
            throw new ValidationException("The name %s for property already exists".formatted(property.getName()));
        }
        return propertyConverter.convert(propertyRepository.save(propertyConverter.convert(propertyRequest)));
    }

    public PropertyResponse findById(UUID id) {
        return propertyRepository.findById(id).map(propertyConverter::convert).orElse(null);
    }

    public PropertyResponse update(UUID id, PropertyRequest propertyRequest) {
        var property = propertyRepository.findById(id).orElseThrow(() -> new PropertyNotFoundException(id));
        property.setName(propertyRequest.getName());
        if (propertyRepository.findAllByNameIgnoreCase(property.getName()).stream()
                .anyMatch(item -> !item.getId().equals(id))) {
            throw new ValidationException("The name %s for property already exists".formatted(property.getName()));
        }
        return propertyConverter.convert(propertyRepository.save(property));
    }

    public void delete(UUID id) {
        propertyRepository.deleteById(id);
    }

    public Property getProperty(UUID propertyId) {
        return propertyRepository.findById(propertyId).orElseThrow(() -> new PropertyNotFoundException(propertyId));
    }
}
